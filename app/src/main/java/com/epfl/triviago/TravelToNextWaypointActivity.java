package com.epfl.triviago;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

public class TravelToNextWaypointActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final float TARGET_REACHED_DIST_METERS = 10.f;
    private GoogleMap mMap;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private static final int INITIAL_MAP_ZOOM = 7;
    static final int MAP_MARKER_PADDING = 300; // offset from edges of the map in pixels

    // intent result
    public static final String INTENT_RESULT = "RESULT";


    LatLng currentLocationLatLgn;
    Marker currentLocationMarker;
    LatLng destinationWaypointLatLgn;
    Marker destinationWaypointMarker;
    List<LatLng> otherPlayerLatLgn = new ArrayList<LatLng>();
    List<String> otherPlayerNames = new ArrayList<String>();
    List<Marker> otherPlayerMarkers = new ArrayList<Marker>();

    IconGenerator iconGenerator;

    private DatabaseReference gameDb;
    private String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_to_next_waypoint);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.GoogleMap);
        mapFragment.getMapAsync(this);

        // Get location updates
        // check location permissions
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        fusedLocationClient = new FusedLocationProviderClient(this);
        locationCallback = getLocationCallback();

        // get the generator for player name icons
        iconGenerator = new IconGenerator(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras.containsKey(ChooseNextWaypoint.DEST_LAT_LNG)) {
            destinationWaypointLatLgn = intent.getParcelableExtra(ChooseNextWaypoint.DEST_LAT_LNG);
        }
        if(extras.containsKey(ChooseNextWaypoint.LATEST_USER_LOC)) {
            currentLocationLatLgn = intent.getParcelableExtra(ChooseNextWaypoint.LATEST_USER_LOC);
        }

        String gameName = extras.getString(ChooseNextWaypoint.INTENT_GAME_NAME);
        playerName = extras.getString(ChooseNextWaypoint.INTENT_PLAYER_NAME);

        // Initialize player names and LatLgn
        gameDb = FirebaseDatabase.getInstance().getReference().child(gameName);
        gameDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot userSnapshot: snapshot.child("Users").getChildren()){
                    String userName = userSnapshot.getKey();
                    if(!userName.equals(playerName)){
                        otherPlayerNames.add(userName);
                        otherPlayerLatLgn.add(new LatLng(
                                userSnapshot.child("latitude").getValue(Double.class),
                                userSnapshot.child("longitude").getValue(Double.class)
                                ));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TxGO", "Error writing to database");
            }
        });

        // TODO: Comment here if on simulator
        // Start the activity on the watch
        Intent intent_start = new Intent(this, WearService.class);
        intent_start.setAction(WearService.ACTION_SEND.STARTACTIVITY.name());
        intent_start.putExtra(WearService.ACTIVITY_TO_START, BuildConfig.W_compass_view);
        startService(intent_start);
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

    /**
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(currentLocationLatLgn!=null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLgn, INITIAL_MAP_ZOOM));
        }
    }

    /**
     * Callback for everytime the user location is updated
     * Here we update the user location on the map and center the map
     */
    private LocationCallback getLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Location location = locationResult.getLastLocation();

                currentLocationLatLgn = new LatLng(location.getLatitude(), location.getLongitude());

                if (mMap != null) {

                    // if not initialized then it's the first run
                    if (currentLocationMarker == null) {
                        currentLocationMarker = mMap.addMarker(
                                new MarkerOptions().position(currentLocationLatLgn).title("Wow, you're here !!!")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_my_location_black_36dp)));
                        destinationWaypointMarker = mMap.addMarker( new MarkerOptions().position(destinationWaypointLatLgn)
                                .title("You need to come here 🏆‍").icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_tour_black_36dp)));

                        // Zoom the map to show both postition and destination
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(currentLocationMarker.getPosition());
                        builder.include(destinationWaypointMarker.getPosition());
                        LatLngBounds bounds = builder.build();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, MAP_MARKER_PADDING));
                    }

                    currentLocationMarker.setPosition(currentLocationLatLgn);

                    // update location in firebase
                    gameDb.child("Users").child(playerName)
                            .child("latitude").setValue(currentLocationLatLgn.latitude);
                    gameDb.child("Users").child(playerName)
                            .child("longitude").setValue(currentLocationLatLgn.longitude);

                    // check the distance between current location and destination
                    Location currLoc = new Location("point A");
                    currLoc.setLatitude(currentLocationLatLgn.latitude);
                    currLoc.setLongitude(currentLocationLatLgn.longitude);
                    Location destLoc = new Location("point B");
                    destLoc.setLatitude(destinationWaypointLatLgn.latitude);
                    destLoc.setLongitude(destinationWaypointLatLgn.longitude);
                    float distance[] = new float[3];
                    Location.distanceBetween(currentLocationLatLgn.latitude,
                            currentLocationLatLgn.longitude, destinationWaypointLatLgn.latitude,
                            destinationWaypointLatLgn.longitude, distance);

                    TextView distanceDisplayTextview = findViewById(R.id.distanceToDestination);
                    String displayText = getString(R.string.distToDestText) + String.valueOf(Math.round(distance[0])) + " meters";
                    if(distance[0]>10000){ //display in KM if above 10km, such that it is more readable
                        displayText = getString(R.string.distToDestText) + String.valueOf(Math.round(distance[0]/1000)) + " km";
                    }
                    distanceDisplayTextview.setText(displayText);
                    if(distance[0]<TARGET_REACHED_DIST_METERS){
                        destinationReached();
                    }

                    double angleToDestination = SphericalUtil.computeHeading(
                            currentLocationLatLgn, destinationWaypointLatLgn);
                    ImageView compassArrowImgView = findViewById(R.id.compassArrowView);
                    compassArrowImgView.setPivotX(compassArrowImgView.getWidth()/2);
                    compassArrowImgView.setPivotY(compassArrowImgView.getHeight()/2);
                    compassArrowImgView.setRotation((float)angleToDestination);

                    // TODO: Comment here if on simulator
                    Intent intent = new Intent(TravelToNextWaypointActivity.this, WearService.class);
                    intent.setAction(WearService.ACTION_SEND.ANGLE_SEND.name());
                    intent.putExtra(BuildConfig.W_angle_key, (float) angleToDestination);
                    startService(intent);

                    // Now update other players' locations on the map
                    if (otherPlayerMarkers.isEmpty()) {
                        iconGenerator.setStyle(IconGenerator.STYLE_RED);
                        for (int i = 0; i < otherPlayerLatLgn.size(); i++) {
                            LatLng playerLatLgn = otherPlayerLatLgn.get(i);
                            String playerName = otherPlayerNames.get(i);
                            Marker playerMarker = mMap.addMarker(
                                    new MarkerOptions().position(playerLatLgn).title("Other player : " + playerName));

                            playerMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                                    iconGenerator.makeIcon(playerName)));
                            otherPlayerMarkers.add(playerMarker);
                        }
                    }
                    else{ // Other player location updates
                        for (int i = 0; i < otherPlayerLatLgn.size(); i++) {
                            // listen to changes for other player locations
                            int finalI = i;
                            gameDb.child("Users").child(otherPlayerNames.get(i))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot userSnapshot) {
                                    if(userSnapshot.exists()){
                                        otherPlayerLatLgn.set(finalI, new LatLng(
                                                userSnapshot.child("latitude").getValue(Double.class),
                                                userSnapshot.child("longitude").getValue(Double.class)
                                        ));
                                        otherPlayerMarkers.get(finalI).setPosition(
                                                otherPlayerLatLgn.get(finalI));
                                    }
                                    else { // if snapshot is empty : player disappeared !
                                        otherPlayerNames.remove(finalI);
                                        otherPlayerLatLgn.remove(finalI);
                                        otherPlayerMarkers.get(finalI).remove();
                                        otherPlayerMarkers.remove(finalI);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("TxGO", "Error writing to database");
                                }
                            });
                        }
                    }
                }
            }
        };
    }

    private void destinationReached(){
        Toast.makeText(this, "Destination reached !!! 🎇", Toast.LENGTH_SHORT).show();

        // TODO: Comment here if on simulator
        // Finish activity on watch
        Intent intent_stop = new Intent(this, WearService.class);
        intent_stop.setAction(WearService.ACTION_SEND.STOPACTIVITY.name());
        intent_stop.putExtra(WearService.ACTIVITY_TO_STOP, BuildConfig.W_compass_view);
        startService(intent_stop);

        // Finish this activity
        Intent intent = new Intent(TravelToNextWaypointActivity.this, ChooseNextWaypoint.class);
        intent.putExtra(INTENT_RESULT, true);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void moveToCurrentLocationButtonCallback(View view){
        mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocationLatLgn));
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

    private static double calculateAngle(double x1, double y1, double x2, double y2)
    {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        // Keep angle between 0 and 360
        angle = angle + Math.ceil( -angle / 360 ) * 360;

        return angle;
    }
}