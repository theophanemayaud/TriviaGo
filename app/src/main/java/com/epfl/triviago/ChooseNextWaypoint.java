package com.epfl.triviago;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

public class ChooseNextWaypoint extends AppCompatActivity implements OnMapReadyCallback {

    // Request codes for TravelToNextWaypointActivity
    private static final int REACH_DEST = 2;
    private static final int ASK_QUESTION = 3;

    private enum WaypointStatus {
        NOT_REACHED,
        REACHED,
        BAD_ANSWER
    };

    private final String TAG = this.getClass().getSimpleName();

    private List<String> spinnerValuesList = new ArrayList<>();
    private Spinner spinner;

    private GoogleMap mMap;
    private DatabaseReference mDatabase;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    LatLng currentLocation;
    private Marker mapMarker;
    private List<LatLng> waypointsLatLgn = new ArrayList<>();
    private List<Marker> waypointsMarkers = new ArrayList<>();
    private List<WaypointStatus> waypointsStatus = new ArrayList<>();

    private int selectedDestinationIndex;
    private int lastDestinationIndex = 0;

    private IconGenerator iconGenerator;

    public static final String DEST_LAT_LNG = "DestinationLatLgn";
    public static final String LATEST_USER_LOC = "LatestUserLocation";
    public static final String INTENT_GAME_NAME = "GAMENAME";

    private String gameName;

    // -------- Start : Lifecycle methods --------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_next_waypoint);

        // Initialising the spinner values, should be taken from activity input values or DB
        // and some placeholder waypoints values, should be taken from activity inputs or DB
        spinner = (Spinner) findViewById(R.id.chooseNextWaypointSpinner);

        // get intent with difficulty, category and type
        Bundle b1 = getIntent().getExtras();
        gameName = b1.getString(INTENT_GAME_NAME);;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new onCreateGetDatabaseValues());

        // Obtain the SupportMapFragment and get notified
        // when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.GoogleMap);
        mapFragment.getMapAsync(this);

        // Get location updates and all
        fusedLocationClient = new FusedLocationProviderClient(this);
        locationCallback = getLocationCallback();

         iconGenerator = new IconGenerator(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    // -------- End : Lifecycle methods --------

    // -------- Start : Location and map related functions --------
    private LocationCallback getLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    LatLng updatedLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    if (mMap != null) {

                        if(currentLocation != updatedLocation){
                            if (mapMarker == null) {
                                // when null, camera view has never been set, and we want to show the marker title !
                                mapMarker = mMap.addMarker(
                                        new MarkerOptions().position(updatedLocation).title("Wow, you're here !!!")
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_my_location_black_36dp)));

                                // Move the camera if it was not set
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(updatedLocation, 5));
                                mapMarker.showInfoWindow();
                            }
                            mapMarker.setPosition(updatedLocation);

                        }
                        currentLocation = updatedLocation;

                        // Now add all markers of elements to go to
                        // if no waypoint markers, then we'll set all of them
                        if (waypointsMarkers.isEmpty()) {
                            iconGenerator.setStyle(IconGenerator.STYLE_BLUE);
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(currentLocation);
                            for (int i = 0; i < waypointsLatLgn.size(); i++) {
                                LatLng waypointLatLong = waypointsLatLgn.get(i);
                                String waypointLetter = String.valueOf((char)(i + CreateWaypointsActivity.LIST_POS_TO_LETTER_OFFSET));
                                Marker waypointMarker = mMap.addMarker(
                                        new MarkerOptions().position(waypointLatLong)
                                                .title("Waypoint " + waypointLetter));

                                waypointMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                                        iconGenerator.makeIcon(waypointLetter)));
                                waypointsMarkers.add(waypointMarker);
                                builder.include(waypointLatLong);
                            }
                            LatLngBounds bounds = builder.build();
                            mMap.moveCamera(
                                    CameraUpdateFactory.newLatLngBounds(bounds,
                                            TravelToNextWaypointActivity.MAP_MARKER_PADDING));
                        }
                    }
                }
            }
        };
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(5)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If the user really doesn't want to, we won't force them !!!
            stopLocationUpdates();
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // We have a map !
        mMap = googleMap;

        //Disable Map Toolbar:
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    // -------- End : Location related functions --------

    // -----------------------------------------------------------
    // -------- ⬇️ Start : sub activity related functions --------
    public void goToNextWaypointPressedCallback(View view) {
        if(waypointsLatLgn.isEmpty()){
            Toast.makeText(ChooseNextWaypoint.this, "There must be a waypoint !!!", Toast.LENGTH_SHORT).show();
        }
        else if(waypointsStatus.get(selectedDestinationIndex)==WaypointStatus.BAD_ANSWER){
            int waypointsLeftToDo = 0;
            for(int i=0; i<waypointsStatus.size(); i++){
                if(waypointsStatus.get(i)==WaypointStatus.NOT_REACHED){
                    waypointsLeftToDo++;
                }
            }
            if(waypointsLeftToDo==0){ // If no "free" waypoints left to do and it must be re-enabled
                waypointsStatus.set(selectedDestinationIndex, WaypointStatus.NOT_REACHED);
                Toast.makeText(ChooseNextWaypoint.this,
                        "Wait a little before trying again !",
                        Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(ChooseNextWaypoint.this,
                        "You can't go to the one you just tried, and failed at !",
                        Toast.LENGTH_LONG).show();
            }
        }
        else if(waypointsStatus.get(selectedDestinationIndex)==WaypointStatus.REACHED){
            Toast.makeText(ChooseNextWaypoint.this,
                    "You already got this one right !!!", Toast.LENGTH_SHORT).show();
        }
        else{
            // TODO : pass the number of attempts allowed and implement it in TriviaQuestionActivity
            Intent TravelToNextWaypointIntent = new Intent(ChooseNextWaypoint.this, TravelToNextWaypointActivity.class);
            TravelToNextWaypointIntent.putExtra( DEST_LAT_LNG, waypointsLatLgn.get(selectedDestinationIndex));
            if(currentLocation!=null) {
                TravelToNextWaypointIntent.putExtra(LATEST_USER_LOC, currentLocation);
            }
            startActivityForResult(TravelToNextWaypointIntent, REACH_DEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REACH_DEST:
                if (resultCode == RESULT_OK) {
                    boolean reached_waypoint = data.getExtras().getBoolean(TravelToNextWaypointActivity.INTENT_RESULT);
                    if (reached_waypoint) {
                        // launch triviaquestion
                        Intent intent = new Intent(ChooseNextWaypoint.this, TriviaQuestionActivity.class);
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        // Read from the database, and launch the intent
                        mDatabase.addListenerForSingleValueEvent(getListenerForStartingActivityWithDbValues(intent));

                        // reset previous waypoint to not reached if it was failed
                        if(waypointsStatus.get(lastDestinationIndex) == WaypointStatus.BAD_ANSWER){
                            waypointsStatus.set(lastDestinationIndex, WaypointStatus.NOT_REACHED);
                        }
                    }
                }
                break;
            case ASK_QUESTION:
                if (resultCode == RESULT_OK) {
                    boolean correct_answer = data.getExtras().getBoolean(TriviaQuestionActivity.INTENT_RESULT);
                    if (correct_answer) {
                        waypointsStatus.set(selectedDestinationIndex, WaypointStatus.REACHED);
                        // TODO make the icon green
                    } else {
                        waypointsStatus.set(selectedDestinationIndex, WaypointStatus.BAD_ANSWER);
                    }
                    boolean stillSomeWaypointsToDo=false;
                    for(int i = 0; i<waypointsLatLgn.size(); i++){
                        if(waypointsStatus.get(i)!=WaypointStatus.REACHED){
                            stillSomeWaypointsToDo=true; // There are still waypoints to go to
                        }
                    }
                    // If here, then there are no more waypoints to go to
                    // TODO implement actual finish and go to stats activity !
                    if(stillSomeWaypointsToDo==false){
                        Log.e(TAG, "User got to all waypoints !");
                        finish();
                    }
                    lastDestinationIndex = selectedDestinationIndex; //remember for next attempt
                }
                break;
        }
    }

    private ValueEventListener getListenerForStartingActivityWithDbValues(Intent intent) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean type_db;
                String diff_db;
                int cat_db;
                int max_attempts_db;

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                diff_db = snapshot.child("Games").child(gameName).child("Difficulty").getValue(String.class);
                String type_db_Str = snapshot.child("Games").child(gameName).child("QuestionType").getValue(String.class);
                type_db = type_db_Str != "QCM";
                cat_db = snapshot.child("Games").child(gameName).child("Waypoints")
                        .child(String.valueOf(selectedDestinationIndex)+"-Cat")
                        .getValue(Integer.class)+TriviaQuestion.TRIVIA_SPINNER_API_OFFSET;
                max_attempts_db = snapshot.child("Games").child(gameName).child("MaxAttempts").getValue(Integer.class);


                Log.e("TxGO", "Params are : type  = " + type_db + ", cat value = " + cat_db + ", diff value = " + diff_db);
                intent.putExtra(TriviaQuestionActivity.INTENT_QCM_TYPE, type_db);
                intent.putExtra(TriviaQuestionActivity.INTENT_CATEGORY, cat_db);
                intent.putExtra(TriviaQuestionActivity.INTENT_DIFFICULTY, diff_db);
                intent.putExtra(TriviaQuestionActivity.INTENT_MAX_ATTEMPTS, max_attempts_db);
                startActivityForResult(intent, ASK_QUESTION);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        };
    }

    // -------- ⬆️ End : sub activity related functions --------
    // ---------------------------------------------------------

    // -------- Start : Small diverse functions functions --------
    private AdapterView.OnItemSelectedListener getListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                // Get the currently selected State object from the spinner
                String itemLetter = String.valueOf((char)(pos + CreateWaypointsActivity.LIST_POS_TO_LETTER_OFFSET));

                // Just show the selection in textfield
                TextView selectedItemView = findViewById(R.id.selectedItemText);
                selectedItemView.setText("Waypoint " + itemLetter + " is currently selected");
                selectedItemView.setTextColor(Color.RED);

                // Show waypoint title on map and center map on it
                if (waypointsMarkers.isEmpty() == false) {
                    selectedDestinationIndex = pos;
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(waypointsLatLgn.get(pos)));
                    waypointsMarkers.get(pos).showInfoWindow();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        };
    }

    private class onCreateGetDatabaseValues implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.e("THEO-INFO", "We got data change");
            double wayptLat;
            double wayptLgn;
            long numWaypts = snapshot.child("Games").child(gameName).child("Waypoints").getChildrenCount();
            numWaypts = numWaypts/3;

            for(int i=0; i<numWaypts; i++){
                String wayptIdx = String.valueOf(i);
                wayptLat = snapshot.child("Games").child(gameName).child("Waypoints").child(wayptIdx+"-Lat").getValue(double.class);
                wayptLgn = snapshot.child("Games").child(gameName).child("Waypoints").child(wayptIdx+"-Lgn").getValue(double.class);
                waypointsLatLgn.add(new LatLng(wayptLat,wayptLgn));
                waypointsStatus.add(WaypointStatus.NOT_REACHED);
                String wayptLetter = String.valueOf((char)(i + CreateWaypointsActivity.LIST_POS_TO_LETTER_OFFSET));
                spinnerValuesList.add("Waypoint "+wayptLetter);
            }
            // By default first is selected
            selectedDestinationIndex = 0;

            // Link the spinner and it's adapter with listener for modifications
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    ChooseNextWaypoint.this, android.R.layout.simple_spinner_item,
                    spinnerValuesList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(getListener());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    }
    // -------- End : Small diverse functions functions --------
}