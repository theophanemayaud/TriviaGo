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
import android.view.Menu;
import android.view.MenuItem;
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

public class CreateWaypoints<onMapLongClick> extends AppCompatActivity implements OnMapReadyCallback {
    private static final int NO_CATEGORY=-1;
    private static final int LIST_POS_TO_LETTER_OFFSET=65;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Marker currentLocationMarker;

    private IconGenerator iconGenerator;

    private List<LatLng> waypointsLatLgnList = new ArrayList<LatLng>();
    private List<Marker> waypointsMarkersList = new ArrayList<Marker>();
    private List<Integer> waypointsSelectedCategoryList = new ArrayList<Integer>();

    private int selectedWaypointListPosition = -1; // default -1 to check if none yet selected

    // ---------------- start lifecycle methods ------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_waypoints);

        // Obtain the SupportMapFragment and get notified
        // when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.GoogleMap);
        mapFragment.getMapAsync(this);

        // Get location updates and all
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
    // ---------------- end lifecycle methods ------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_waypoints, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveAllWaypointsMenuItem:
                if(waypointsSelectedCategoryList.size()==0){
                    Toast.makeText(this, "Create at least 1 waypoint !", Toast.LENGTH_SHORT).show();
                    break;
                }
                for(int i=0; i<waypointsSelectedCategoryList.size(); i++){
                    if(waypointsSelectedCategoryList.get(i)==NO_CATEGORY){
                        Toast.makeText(this, "Not all waypoints have a category selected (check red waypoints) !", Toast.LENGTH_SHORT).show();
                        return super.onOptionsItemSelected(item);
                    }
                }
                // TODO : really save all
                Intent intent = new Intent(CreateWaypoints.this, ChooseNextWaypoint.class);
                startActivity(intent);
                break;
//            case R.id.action_validate:
//                editUser();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // -------------------------------- Begin of location things ---------
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

                        if (currentLocationMarker == null) {
                            // when null, camera view has never been set, and we want to show the marker title !
                            currentLocationMarker = mMap.addMarker(
                                    new MarkerOptions().position(updatedLocation).title("Wow, you're here !!!")
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_my_location_black_36dp)));
                            currentLocationMarker.setTag(-1); // indicates it is not a waypoint
                            // Move the camera if it was not set
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(updatedLocation, 5));
                            currentLocationMarker.showInfoWindow();
                        }
                        currentLocationMarker.setPosition(updatedLocation);
                    }
                }
            }
        };
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

    // -------------------------------- End of location things ---------

    // -------------------------------- Begin of map things (excluding geolocation)-----------------

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // We have a map !
        mMap = googleMap;

        //Disable Map Toolbar:
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.setOnMapLongClickListener(new MyOnMapLongClickListener(googleMap));

        mMap.setOnMarkerClickListener(getOnMarkerClickListener());

    }

    private GoogleMap.OnMarkerClickListener getOnMarkerClickListener() {
        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int listPosition = (int)(marker.getTag());
                if (listPosition >= 0) { // check if it's a waypoint marker
                    //Using position get Value from arraylist
                    updateSelectedWaypoint(listPosition);
                }
                return false; //true to disable standard show title and center behavior
            }
        };
    }

    private class MyOnMapLongClickListener implements GoogleMap.OnMapLongClickListener {
        private final GoogleMap googleMap;
        public MyOnMapLongClickListener(GoogleMap googleMap) {
            this.googleMap = googleMap;
        }
        @Override
        public void onMapLongClick(LatLng latLng) {
            int numberOfWaypoints = waypointsMarkersList.size();
            String waypointLetter = String.valueOf((char)(numberOfWaypoints + LIST_POS_TO_LETTER_OFFSET));

            Marker waypointMarker = mMap.addMarker(
                    new MarkerOptions().position(latLng).
                            title("Waypoint " +  waypointLetter ));
            waypointMarker.setTag(numberOfWaypoints); // item in list so that we can retrieve when clicked
            iconGenerator.setStyle(IconGenerator.STYLE_RED);
            waypointMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    iconGenerator.makeIcon(waypointLetter)));
            waypointsMarkersList.add(waypointMarker);
            waypointsLatLgnList.add(latLng);
            waypointsSelectedCategoryList.add(NO_CATEGORY);
            updateSelectedWaypoint(numberOfWaypoints); // list starts at 0 but size at 1
        }
    }

    // -------------------------------- End of map things (excluding geolocation) ----------------

    // -------------------------------- Begin of small other things ----------------
    private void updateSelectedWaypoint(int waypointListPosition){
        selectedWaypointListPosition = waypointListPosition;
        String waypointLetter = String.valueOf((char)(waypointListPosition + LIST_POS_TO_LETTER_OFFSET));
        TextView selectedItemView = findViewById(R.id.selectedWaypointText);
        selectedItemView.setText("Waypoint " + waypointLetter + " is currently selected");
        selectedItemView.setTextColor(Color.RED);
        if(waypointsSelectedCategoryList.get(waypointListPosition)!=NO_CATEGORY){
            // TODO update category in dropdown
        }
    }

    private void onCategorySelected(int categoryNumber){ // TODO call this function when spinner/dropdown changes
        waypointsSelectedCategoryList.set(selectedWaypointListPosition, categoryNumber);
        Marker waypointMarker = waypointsMarkersList.get(selectedWaypointListPosition);
        iconGenerator.setStyle(IconGenerator.STYLE_GREEN);
        String waypointLetter = String.valueOf((char)(selectedWaypointListPosition + LIST_POS_TO_LETTER_OFFSET));
        waypointMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                iconGenerator.makeIcon(waypointLetter)));
    }

    private void onDeleteButtonXmlButtonPressed(){ //TODO implement delete !

    }
    // -------------------------------- End of small other things ----------------

}