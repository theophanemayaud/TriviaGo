package com.epfl.triviago;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ChooseNextWaypoint extends AppCompatActivity implements OnMapReadyCallback {

    List<String> spinnerValuesList = new ArrayList<String>();
    Spinner spinner;

    private GoogleMap mMap;
    private Marker mapMarker;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_next_waypoint);

        // Initialising the spinner values, should be taken from activity input values
        spinner = (Spinner) findViewById(R.id.chooseNextWaypointSpinner);
        spinnerValuesList.add("item1");
        spinnerValuesList.add("item2");

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
//                for (Location location : locationResult.getLocations()) {
//                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//
//                    // Add a marker in the current location and move the camera
//                    if (mMap != null) {
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 5));
//
//                        if (mapMarker != null) {
//                            mapMarker.remove();
//                        }
//                        mapMarker = mMap.addMarker(
//                                new MarkerOptions().position(currentLocation).title("Wow, you're here !!!"));
//                        mapMarker.showInfoWindow();
//                    }
//                }
            }
        };
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(5)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
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

                // Now do something with it
                TextView selectedItemView = findViewById(R.id.selectedItemText);
                selectedItemView.setText(selected_item);
                selectedItemView.setTextColor(Color.RED);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        };
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        // We have a map !
        mMap = googleMap;

        // Add a marker in the default location and move the camera
        LatLng currentLocation = new LatLng( 1.2341, 31.34);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 5));

        if(mapMarker!=null){
            mapMarker.remove();
        }
        mapMarker = mMap.addMarker(
                new MarkerOptions().position(currentLocation).title("Placeholder location (you're not here !"));
        mapMarker.showInfoWindow();
    }
}