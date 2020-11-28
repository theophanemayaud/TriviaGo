package com.epfl.triviago;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // amazing
    }


    public void clickedChooseNextWaypointButtonXmlCallback(View view) {
        Intent intentChooseNextWaypoint = new Intent(MainActivity.this, ChooseNextWaypoint.class);
        startActivity(intentChooseNextWaypoint);
    }
}