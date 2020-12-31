package com.epfl.triviago;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

public class ChooseNextWaypoint extends AppCompatActivity implements OnMapReadyCallback {

    List<String> spinnerValuesList = new ArrayList<String>();
    Spinner spinner;

    private GoogleMap mMap;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    LatLng currentLocation;
    private Marker mapMarker;
    List<LatLng> waypointsLatLgn = new ArrayList<LatLng>();
    List<Marker> waypointsMarkers = new ArrayList<Marker>();

    LatLng selectedDestinationLatLgn;

    IconGenerator iconGenerator;

    public static final String DEST_LAT_LNG = "DestinationLatLgn";
    public static final String LATEST_USER_LOC = "LatestUserLocation";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_next_waypoint);

        // TODO do these initializations from firebase ?
        // Initialising the spinner values, should be taken from activity input values or DB
        // and some placeholder waypoints values, should be taken from activity inputs or DB
        spinner = (Spinner) findViewById(R.id.chooseNextWaypointSpinner);
        waypointsLatLgn.add(new LatLng(50.9265, 5.2205));
        spinnerValuesList.add("Waypoint 0");
        waypointsLatLgn.add(new LatLng(50.9265, 4.13));
        spinnerValuesList.add("Waypoint 1");
        waypointsLatLgn.add(new LatLng(48.13, 5.2205));
        spinnerValuesList.add("Waypoint 2");
        waypointsLatLgn.add(new LatLng(48.13, 4.13));
        spinnerValuesList.add("Waypoint 3");
        // By default first is selected
        selectedDestinationLatLgn = waypointsLatLgn.get(0);

        // Link the spinner and it's adapter with listener for modifications
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerValuesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(getListener());

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
                            for (int i = 0; i < waypointsLatLgn.size(); i++) {
                                LatLng waypointLatLong = waypointsLatLgn.get(i);
                                Marker waypointMarker = mMap.addMarker(
                                        new MarkerOptions().position(waypointLatLong).title("Waypoint " + i));

                                waypointMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                                        iconGenerator.makeIcon(String.valueOf(i))));
                                waypointsMarkers.add(waypointMarker);
                            }
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

    private AdapterView.OnItemSelectedListener getListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                // Get the currently selected State object from the spinner
                String selected_item = adapterView.getItemAtPosition(pos).toString();

                // Just show the selection in textfield
                TextView selectedItemView = findViewById(R.id.selectedItemText);
                selectedItemView.setText("Waypoint " + pos + " is currently selected");
                selectedItemView.setTextColor(Color.RED);

                // Show waypoint title on map and center map on it
                if (waypointsMarkers.isEmpty() == false) {
                    LatLng selectedWaypointLatLng = waypointsLatLgn.get(pos);
                    selectedDestinationLatLgn = selectedWaypointLatLng;
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(selectedWaypointLatLng));
                    waypointsMarkers.get(pos).showInfoWindow();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        };
    }

    public void goToNextWaypointPressedCallback(View view) {
        Intent TravelToNextWaypointIntent = new Intent(ChooseNextWaypoint.this, TravelToNextWaypoint.class);
        TravelToNextWaypointIntent.putExtra( DEST_LAT_LNG, selectedDestinationLatLgn);
        if(currentLocation!=null) {
            TravelToNextWaypointIntent.putExtra(LATEST_USER_LOC, currentLocation);
        }
        startActivity(TravelToNextWaypointIntent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // We have a map !
        mMap = googleMap;

        //Disable Map Toolbar:
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }
}