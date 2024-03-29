package com.laubasthe.triviago;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

public class CreateWaypointsActivity<onMapLongClick> extends AppCompatActivity implements OnMapReadyCallback {
    //Variables for intents
    static final int RESULT_WAYPOINTS_CODE = 2131;
    static final String RESULT_WAYPOINTS_LIST_NAME = "waypoints_list_result";
    static final String RESULT_CATEG_LIST_NAME = "waypoints_category_list";
    private static final int NO_CATEG_SELECTION = 0;
    private static final int NO_WAYPOINT_SELECTION = -1;
    static final int LIST_POS_TO_LETTER_OFFSET = 65;

    //Variables for the map
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Marker currentLocationMarker;
    private int selectedWaypointListPosition = NO_WAYPOINT_SELECTION;

    //Useful variables
    private IconGenerator iconGenerator;
    private final ArrayList<LatLng> waypointsLatLgnList = new ArrayList<LatLng>();
    private final ArrayList<Marker> waypointsMarkersList = new ArrayList<Marker>();
    private final ArrayList<Integer> waypointsSelectedCategoryList = new ArrayList<Integer>();
    private Spinner spinner_category;

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

        //Create spinner with everything
        spinner_category = findViewById(R.id.categorySelectionSpinner);
        List<String> categories_name_list = TriviaQuestion.Categories;
        ArrayAdapter<String> adapter_category = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categories_name_list);
        spinner_category.setAdapter(adapter_category);
        spinner_category.setOnItemSelectedListener(getSpinnerAdapterCallback());
        spinner_category.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        new AlertDialog.Builder(CreateWaypointsActivity.this)
                .setTitle("Instructions")
                .setMessage("Long press anywhere on the map to add waypoints, then select a category for each.")
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, null)
                .setIcon(android.R.drawable.ic_dialog_map)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
    // ---------------- end lifecycle methods ------------------

    // -------------------------------- Begin of location things ---------
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

    // -------------------------------- End of location things -------------------------------

    // -------------------------------- Begin of map things (excluding geolocation) -------

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
                int listPosition = (int) (marker.getTag());
                updateSelectedWaypoint(listPosition);
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
            String waypointLetter = String.valueOf((char) (numberOfWaypoints + LIST_POS_TO_LETTER_OFFSET));

            Marker waypointMarker = mMap.addMarker(
                    new MarkerOptions().position(latLng).
                            title("Waypoint " + waypointLetter));
            waypointMarker.setTag(numberOfWaypoints); // item in list so that we can retrieve when clicked
            iconGenerator.setStyle(IconGenerator.STYLE_RED);
            waypointMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    iconGenerator.makeIcon(waypointLetter)));
            waypointsMarkersList.add(waypointMarker);
            waypointsLatLgnList.add(latLng);
            waypointsSelectedCategoryList.add(NO_CATEG_SELECTION);
            waypointMarker.showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            updateSelectedWaypoint(numberOfWaypoints); // list starts at 0 but size at 1
        }
    }
    // -------------------------------- End of map things (excluding geolocation) ----------------

    // -------------------------------- Begin of small other things ----------------
    private void updateSelectedWaypoint(int waypointListPosition) {
        if (waypointListPosition != NO_WAYPOINT_SELECTION) {
            selectedWaypointListPosition = waypointListPosition;
            String waypointLetter = String.valueOf((char) (waypointListPosition + LIST_POS_TO_LETTER_OFFSET));
            TextView selectedItemView = findViewById(R.id.selectedWaypointText);
            selectedItemView.setText("Waypoint " + waypointLetter + " category ➡");
            if (waypointsSelectedCategoryList.get(waypointListPosition) == NO_CATEG_SELECTION) {
                selectedItemView.setTextColor(getResources().getColor(R.color.button_end));
            } else {
                selectedItemView.setTextColor(getResources().getColor(R.color.teal_700));
            }

            spinner_category.setEnabled(true);
            spinner_category.setSelection(waypointsSelectedCategoryList.get(waypointListPosition));
        } else {
            selectedWaypointListPosition = NO_WAYPOINT_SELECTION;
            spinner_category.setEnabled(false);
            spinner_category.setSelection(NO_CATEG_SELECTION);
            TextView selectedItemView = findViewById(R.id.selectedWaypointText);
            selectedItemView.setText(R.string.no_waypoint_selected_textview);
            selectedItemView.setTextColor(Color.BLACK);
        }
    }

    private void onCategorySelected(int categoryNumber) {
        if (selectedWaypointListPosition != NO_WAYPOINT_SELECTION && categoryNumber != NO_CATEG_SELECTION) {
            waypointsSelectedCategoryList.set(selectedWaypointListPosition, categoryNumber);
            Marker waypointMarker = waypointsMarkersList.get(selectedWaypointListPosition);
            iconGenerator.setStyle(IconGenerator.STYLE_GREEN);
            String waypointLetter = String.valueOf((char) (selectedWaypointListPosition + LIST_POS_TO_LETTER_OFFSET));
            waypointMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    iconGenerator.makeIcon(waypointLetter)));
            TextView selectedItemView = findViewById(R.id.selectedWaypointText);
            selectedItemView.setTextColor(getResources().getColor(R.color.teal_700));
        }
    }

    // spinner selection callback
    private AdapterView.OnItemSelectedListener getSpinnerAdapterCallback() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onCategorySelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_waypoints, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // App Bar button actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveAllWaypointsMenuItem:
                if (waypointsSelectedCategoryList.size() == 0) {
                    Toast.makeText(this, R.string.no_waypoints_error_msg, Toast.LENGTH_SHORT).show();
                    break;
                }
                for (int i = 0; i < waypointsSelectedCategoryList.size(); i++) {
                    if (waypointsSelectedCategoryList.get(i) == NO_CATEG_SELECTION) {
                        Toast.makeText(this, "Not all waypoints have a category selected (check red waypoints) !", Toast.LENGTH_SHORT).show();
                        return super.onOptionsItemSelected(item);
                    }
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra(RESULT_WAYPOINTS_LIST_NAME, waypointsLatLgnList);
                returnIntent.putExtra(RESULT_CATEG_LIST_NAME, waypointsSelectedCategoryList);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                break;
            case R.id.delWaypntMenuButton:
                if (selectedWaypointListPosition != NO_WAYPOINT_SELECTION) {
                    waypointsMarkersList.get(selectedWaypointListPosition).remove();
                    waypointsMarkersList.remove(selectedWaypointListPosition);
                    waypointsLatLgnList.remove(selectedWaypointListPosition);
                    waypointsSelectedCategoryList.remove(selectedWaypointListPosition);
                    updateSelectedWaypoint(NO_WAYPOINT_SELECTION);

                    for (int i = 0; i < waypointsSelectedCategoryList.size(); i++) {
                        if (waypointsSelectedCategoryList.get(i) == NO_CATEG_SELECTION) {
                            iconGenerator.setStyle(IconGenerator.STYLE_RED);
                        } else {
                            iconGenerator.setStyle(IconGenerator.STYLE_GREEN);
                        }
                        Marker markerToUpdate = waypointsMarkersList.get(i);
                        String waypointLetter = String.valueOf((char) (i + LIST_POS_TO_LETTER_OFFSET));
                        markerToUpdate.setTitle("Waypoint " + waypointLetter);
                        markerToUpdate.setIcon(BitmapDescriptorFactory.fromBitmap(
                                iconGenerator.makeIcon(waypointLetter)));
                        markerToUpdate.setTag(i);
                    }
                } else {
                    Toast.makeText(this, "No waypoints selected for delete...", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // -------------------------------- End of small other things ----------------

}