package com.laubasthe.triviago;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChooseNextWaypoint extends AppCompatActivity implements OnMapReadyCallback {
    private final String TAG = this.getClass().getSimpleName();

    // Request codes for TravelToNextWaypointActivity
    private static final int REACH_DEST = 2;
    private static final int ASK_QUESTION = 3;
    private static final int SELF_MARKER_TAG = -1;

    //Database
    private DatabaseReference gameDb;

    //Keeps the start time
    private long start_time;

    //Variables for intents
    public static final String DEST_LAT_LNG = "DestinationLatLgn";
    public static final String LATEST_USER_LOC = "LatestUserLocation";
    public static final String INTENT_GAME_NAME = "name";
    public static final String INTENT_PLAYER_NAME = "player";
    public static final String INTENT_PLAYER_STATS_LIST = "list";

    //Variables for the map
    private GoogleMap mMap;

    private enum WaypointStatus {
        NOT_REACHED,
        REACHED,
        BAD_ANSWER
    }

    private int selectedDestinationIndex;
    private int lastDestinationIndex = 0;

    // Get location updates and all
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LatLng currentLocation;
    private Marker mapMarker;
    private final List<LatLng> waypointsLatLgn = new ArrayList<>();
    private final List<Marker> waypointsMarkers = new ArrayList<>();
    private final List<WaypointStatus> waypointsStatus = new ArrayList<>();
    private final List<Integer> waypointsAttemptsList = new ArrayList<>();
    private final List<String> spinnerValuesList = new ArrayList<>();

    // Refs to other items
    private Spinner spinner;
    private IconGenerator iconGenerator;

    private String gameName;
    private String playerName;
    private boolean userSentToEnd = false;

    // -------- Start : Lifecycle methods --------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_choose_next_waypoint);

        // Initialising the spinner values, should be taken from activity input values or DB
        // and some placeholder waypoints values, should be taken from activity inputs or DB
        spinner = findViewById(R.id.chooseNextWaypointSpinner);

        // get intent with game name and player name
        Bundle b1 = getIntent().getExtras();
        gameName = b1.getString(INTENT_GAME_NAME);
        playerName = b1.getString(INTENT_PLAYER_NAME);
        start_time = b1.getLong(JoinActivity.START_TIME_MS);

        // load game infos from DB
        gameDb = FirebaseDatabase.getInstance().getReference().child(gameName);
        gameDb.child("Waypoints").addListenerForSingleValueEvent(new onCreateGetDatabaseValues());

        // Obtain the SupportMapFragment and get notified
        // when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.GoogleMap);
        mapFragment.getMapAsync(this);

        locationCallback = getLocationCallback();

        fusedLocationClient = new FusedLocationProviderClient(this);

        iconGenerator = new IconGenerator(this);
        if(BuildConfig.DEBUG){
            this.findViewById(R.id.skipDebug).setVisibility(1);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //remove user from DB
        if (!userSentToEnd) {
            gameDb.child("Users").child(playerName).removeValue();
        }
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
                        if (currentLocation != updatedLocation) {
                            if (mapMarker == null) {
                                // when null, camera view has never been set, and we want to show the marker title !
                                mapMarker = mMap.addMarker(
                                        new MarkerOptions().position(updatedLocation).title("Wow, you're here !!!")
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_my_location_black_36dp)));
                                mapMarker.setTag(SELF_MARKER_TAG);

                                // Move the camera if it was not set
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(updatedLocation, 5));
                                mapMarker.showInfoWindow();
                            }
                            mapMarker.setPosition(updatedLocation);
                        }
                        currentLocation = updatedLocation;

                        // update location in firebase
                        gameDb.child("Users").child(playerName)
                                .child("latitude").setValue(currentLocation.latitude);
                        gameDb.child("Users").child(playerName)
                                .child("longitude").setValue(currentLocation.longitude);

                        // Now add all markers of elements to go to
                        // if no waypoint markers, then we'll set all of them
                        if (waypointsMarkers.isEmpty()) {
                            iconGenerator.setStyle(IconGenerator.STYLE_ORANGE);
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(currentLocation);
                            for (int i = 0; i < waypointsLatLgn.size(); i++) {
                                LatLng waypointLatLong = waypointsLatLgn.get(i);
                                String waypointLetter = String.valueOf((char) (i + CreateWaypointsActivity.LIST_POS_TO_LETTER_OFFSET));
                                Marker waypointMarker = mMap.addMarker(
                                        new MarkerOptions().position(waypointLatLong)
                                                .title("Waypoint " + waypointLetter));

                                waypointMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                                        iconGenerator.makeIcon(waypointLetter)));
                                waypointMarker.setTag(i);
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
        //Check if GPS is enabled on the device
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

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

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // We have a map !
        mMap = googleMap;

        //Disable Map Toolbar:
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.setOnMarkerClickListener(getOnMarkerClickListener());
    }

    private GoogleMap.OnMarkerClickListener getOnMarkerClickListener() {
        return marker -> {
            if ((int) marker.getTag() != SELF_MARKER_TAG) {
                int listPosition = (int) (marker.getTag());

                spinner.setSelection(listPosition); // fires callback of adapter
            }
            return false; //true to disable standard show title and center behavior
        };
    }
    // -------- End : Location and map related functions --------

    // -----------------------------------------------------------
    // -------- ⬇️ Start : sub activity related functions --------
    public void goToNextWaypointPressedCallback(View view) {
        if (waypointsLatLgn.isEmpty()) {
            Toast.makeText(ChooseNextWaypoint.this, "There must be a waypoint !!!", Toast.LENGTH_SHORT).show();
        } else if (waypointsStatus.get(selectedDestinationIndex) == WaypointStatus.BAD_ANSWER) {
            int waypointsLeftToDo = 0;
            for (int i = 0; i < waypointsStatus.size(); i++) {
                if (waypointsStatus.get(i) == WaypointStatus.NOT_REACHED) {
                    waypointsLeftToDo++;
                }
            }
            if (waypointsLeftToDo == 0) { // If no "free" waypoints left to do and it must be re-enabled
                waypointsStatus.set(selectedDestinationIndex, WaypointStatus.NOT_REACHED);
                Toast.makeText(ChooseNextWaypoint.this,
                        "Wait a little before trying again !",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ChooseNextWaypoint.this,
                        "You can't go to the one you just tried, and failed at !",
                        Toast.LENGTH_LONG).show();
            }
        } else if (waypointsStatus.get(selectedDestinationIndex) == WaypointStatus.REACHED) {
            Toast.makeText(ChooseNextWaypoint.this,
                    "You already got this one right !!!", Toast.LENGTH_SHORT).show();
        } else {
            Intent TravelToNextWaypointIntent = new Intent(ChooseNextWaypoint.this, TravelToNextWaypointActivity.class);
            TravelToNextWaypointIntent.putExtra(DEST_LAT_LNG, waypointsLatLgn.get(selectedDestinationIndex));
            TravelToNextWaypointIntent.putExtra(INTENT_GAME_NAME, gameName);
            TravelToNextWaypointIntent.putExtra(INTENT_PLAYER_NAME, playerName);
            if (currentLocation != null) {
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
                        // Read from the database, and launch the intent
                        gameDb.addListenerForSingleValueEvent(getListenerForStartingActivityWithDbValues(intent));

                        // reset previous waypoint to not reached if it was failed
                        if (waypointsStatus.get(lastDestinationIndex) == WaypointStatus.BAD_ANSWER) {
                            waypointsStatus.set(lastDestinationIndex, WaypointStatus.NOT_REACHED);
                            //Set marker color to red
                            iconGenerator.setStyle(IconGenerator.STYLE_ORANGE);
                            String waypointLetter = String.valueOf((char) (lastDestinationIndex +
                                    CreateWaypointsActivity.LIST_POS_TO_LETTER_OFFSET));
                            waypointsMarkers.get(lastDestinationIndex).setIcon(
                                    BitmapDescriptorFactory.fromBitmap(
                                            iconGenerator.makeIcon(waypointLetter))
                            );
                        }
                    }
                }
                break;
            case ASK_QUESTION:
                if (resultCode == RESULT_OK) {
                    int newAttempsNb = waypointsAttemptsList.get(selectedDestinationIndex)
                            + data.getIntExtra(TriviaQuestionActivity.INTENT_EFFECTIVE_ATTEMPTS, 0);
                    waypointsAttemptsList.set(selectedDestinationIndex, newAttempsNb);

                    boolean correct_answer = data.getExtras().getBoolean(TriviaQuestionActivity.INTENT_RESULT);
                    if (correct_answer) {
                        waypointsStatus.set(selectedDestinationIndex, WaypointStatus.REACHED);
                        iconGenerator.setStyle(IconGenerator.STYLE_GREEN);

                        //Set marker color to green
                        String waypointLetter = String.valueOf((char) (selectedDestinationIndex +
                                CreateWaypointsActivity.LIST_POS_TO_LETTER_OFFSET));
                        waypointsMarkers.get(selectedDestinationIndex).setIcon(
                                BitmapDescriptorFactory.fromBitmap(
                                        iconGenerator.makeIcon(waypointLetter))
                        );
                    } else {
                        waypointsStatus.set(selectedDestinationIndex, WaypointStatus.BAD_ANSWER);

                        //Set marker color to red
                        iconGenerator.setStyle(IconGenerator.STYLE_RED);
                        String waypointLetter = String.valueOf((char) (selectedDestinationIndex +
                                CreateWaypointsActivity.LIST_POS_TO_LETTER_OFFSET));
                        waypointsMarkers.get(selectedDestinationIndex).setIcon(
                                BitmapDescriptorFactory.fromBitmap(
                                        iconGenerator.makeIcon(waypointLetter))
                        );
                    }
                    boolean stillSomeWaypointsToDo = false;
                    for (int i = 0; i < waypointsLatLgn.size(); i++) {
                        if (waypointsStatus.get(i) != WaypointStatus.REACHED) {
                            stillSomeWaypointsToDo = true; // There are still waypoints to go to
                        }
                    }
                    // If here, then there are no more waypoints to go to
                    if (!stillSomeWaypointsToDo) {
                        int waypointAttemptsTotal = 0;
                        List<Float> waypointsRatesList = new ArrayList<>();
                        for (int i = 0; i < waypointsAttemptsList.size(); i++) {
                            waypointAttemptsTotal += waypointsAttemptsList.get(i);
                            waypointsRatesList.add(calcRate(
                                    waypointsAttemptsList.get(i), 1
                            ));
                        }

                        float playerSuccessRate = calcRate(
                                waypointAttemptsTotal,
                                waypointsLatLgn.size());
                        gameDb.child("Users").child(playerName)
                                .child("rate").setValue(playerSuccessRate);

                        Intent endIntent = new Intent(ChooseNextWaypoint.this,
                                EndActivity.class);
                        endIntent.putExtra(ChooseNextWaypoint.INTENT_GAME_NAME, gameName);
                        endIntent.putExtra(ChooseNextWaypoint.INTENT_PLAYER_NAME, playerName);
                        endIntent.putExtra(ChooseNextWaypoint.INTENT_PLAYER_STATS_LIST,
                                (Serializable) waypointsRatesList);
                        endIntent.putExtra(JoinActivity.START_TIME_MS, start_time);

                        Toast.makeText(ChooseNextWaypoint.this, "All done !!!",
                                Toast.LENGTH_SHORT).show();

                        startActivity(endIntent);
                        userSentToEnd = true;
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
            public void onDataChange(@NonNull DataSnapshot gameSnapshot) {
                boolean type_db;
                String diff_db;
                int cat_db;
                int max_attempts_db;

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                diff_db = gameSnapshot.child("Settings").child("Difficulty").getValue(String.class);
                String type_db_Str = gameSnapshot.child("Settings").child("QuestionType").getValue(String.class);
                type_db = type_db_Str.equals("QCM");
                cat_db = gameSnapshot.child("Waypoints")
                        .child(Integer.toString(selectedDestinationIndex)).child("category")
                        .getValue(Integer.class) + TriviaQuestion.TRIVIA_SPINNER_API_OFFSET;
                max_attempts_db = gameSnapshot.child("Settings").child("MaxAttempts").getValue(Integer.class);

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
                String itemLetter = String.valueOf((char) (pos + CreateWaypointsActivity.LIST_POS_TO_LETTER_OFFSET));

                // Just show the selection in textfield
                TextView selectedItemView = findViewById(R.id.selectedItemText);
                selectedItemView.setText("Waypoint " + itemLetter + " is currently selected");

                // Show waypoint title on map and center map on it
                if (!waypointsMarkers.isEmpty()) {
                    selectedDestinationIndex = pos;
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(waypointsLatLgn.get(pos)));
                    waypointsMarkers.get(pos).showInfoWindow();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // If nothing is selected
            }
        };
    }

    private class onCreateGetDatabaseValues implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot wayptsSnapshot) {
            double wayptLat;
            double wayptLgn;

            int i = 0;
            for (DataSnapshot oneWayptsSnapshot : wayptsSnapshot.getChildren()) {
                String wayptIdx = String.valueOf(i);
                wayptLat = oneWayptsSnapshot.child("latitude").getValue(double.class);
                wayptLgn = oneWayptsSnapshot.child("longitude").getValue(double.class);
                waypointsLatLgn.add(new LatLng(wayptLat, wayptLgn));
                waypointsStatus.add(WaypointStatus.NOT_REACHED);
                waypointsAttemptsList.add(0); //starts at 0 attempts !
                String wayptLetter =
                        String.valueOf((char) (i + CreateWaypointsActivity.LIST_POS_TO_LETTER_OFFSET));
                spinnerValuesList.add("Waypoint " + wayptLetter);
                i++;
            }

            // By default first waypoint is selected
            selectedDestinationIndex = 0;

            // Link the spinner and it's adapter with listener for modifications
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
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

    private float calcRate(int attempts, int minAttempts) {
        return minAttempts / (float) attempts;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, you must enable it for the game to work.")
                .setCancelable(false)
                .setPositiveButton("Enable", (dialog, id) -> startActivity(
                        new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("Exit", (dialog, id) -> {
                    finish();
                    dialog.cancel();
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    // -------- End : Small diverse functions functions --------

    // DEBUG TODO : remove this from prod
    public void skipButtonCB(View view) {
        if(BuildConfig.DEBUG){
            int waypointAttemptsTotal = 0;
            List<Float> waypointsRatesList = new ArrayList<>();
            for (int i = 0; i < waypointsAttemptsList.size(); i++) {
                waypointAttemptsTotal += 2;
                waypointsRatesList.add(calcRate(
                        2, 1
                ));
            }

            float playerSuccessRate = calcRate(
                    waypointAttemptsTotal,
                    waypointsLatLgn.size());
            gameDb.child("Users").child(playerName)
                    .child("rate").setValue(playerSuccessRate);

            Intent endIntent = new Intent(ChooseNextWaypoint.this,
                    EndActivity.class);
            endIntent.putExtra(ChooseNextWaypoint.INTENT_GAME_NAME, gameName);
            endIntent.putExtra(ChooseNextWaypoint.INTENT_PLAYER_NAME, playerName);
            endIntent.putExtra(ChooseNextWaypoint.INTENT_PLAYER_STATS_LIST,
                    (Serializable) waypointsRatesList);
            endIntent.putExtra(JoinActivity.START_TIME_MS, start_time);

            Toast.makeText(ChooseNextWaypoint.this, "All done !!!",
                    Toast.LENGTH_SHORT).show();

            startActivity(endIntent);
            userSentToEnd = true;
            finish();
        }
    }
}