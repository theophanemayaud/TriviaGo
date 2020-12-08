package com.epfl.triviago;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TravelToNextWaypoint extends FragmentActivity implements OnMapReadyCallback {

    private static final float TARGET_REACHED_DIST_METERS = 10.f;
    private GoogleMap mMap;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private static final int INITIAL_MAP_ZOOM = 7;
    private static final int MAP_MARKER_PADDING = 100; // offset from edges of the map in pixels


    LatLng currentLocationLatLgn;
    Marker currentLocationMarker;
    LatLng destinationWaypointLatLgn;
    Marker destinationWaypointMarker;
    List<LatLng> otherPlayerLatLgn = new ArrayList<LatLng>();
    List<String> otherPlayerNames = new ArrayList<String>();
    List<Marker> otherPlayerMarkers = new ArrayList<Marker>();

    IconGenerator iconGenerator;


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

        // TODO initialize with firebase not those dumb values !
        // Initialize player names and LatLgn
        otherPlayerLatLgn.add(new LatLng(51.9265, 5.2205));
        otherPlayerNames.add("WW");
        otherPlayerLatLgn.add(new LatLng(49.9265, 4.13));
        otherPlayerNames.add("The Bat");
        otherPlayerLatLgn.add(new LatLng(48.13, 5.2205));
        otherPlayerNames.add("Arielle");
        otherPlayerLatLgn.add(new LatLng(52.13, 4.13));
        otherPlayerNames.add("Harry");

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras.containsKey(ChooseNextWaypoint.DEST_LAT_LNG)) {
            destinationWaypointLatLgn = intent.getParcelableExtra(ChooseNextWaypoint.DEST_LAT_LNG);
        }
        if(extras.containsKey(ChooseNextWaypoint.LATEST_USER_LOC)) {
            currentLocationLatLgn = intent.getParcelableExtra(ChooseNextWaypoint.LATEST_USER_LOC);
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

                    // IDEA add arrow position marker and rotate based on movement ?
                    // IDEA show movement history (add the trail from the start) ?
                    currentLocationMarker.setPosition(currentLocationLatLgn);

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

                    // TODO set distance to textView !
                    if(distance[0]<TARGET_REACHED_DIST_METERS){
                        destinationReached();
                    }

                    // TODO move next part to firebase listener
                    // Now update other players' locations on the map
                    if (otherPlayerMarkers.isEmpty()) {
                        iconGenerator.setStyle(IconGenerator.STYLE_RED);
                        for (int i = 0; i < otherPlayerLatLgn.size(); i++) {
                            LatLng playerLatLgn = otherPlayerLatLgn.get(i);
                            String playerName = otherPlayerNames.get(i);
                            Marker playerMarker = mMap.addMarker(
                                    new MarkerOptions().position(playerLatLgn).title(playerName));

                            playerMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                                    iconGenerator.makeIcon(playerName)));
                            otherPlayerMarkers.add(playerMarker);
                        }
                    }
                    else{ // Other player location updates
                        for (int i = 0; i < otherPlayerLatLgn.size(); i++) {
                            otherPlayerMarkers.get(i).setPosition(otherPlayerLatLgn.get(i));
                        }
                    }
                }
            }
        };
    }

    private void destinationReached(){
        Toast.makeText(this, "Destination reached !!! 🎇", Toast.LENGTH_SHORT).show();
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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



}