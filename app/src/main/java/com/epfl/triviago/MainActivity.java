package com.epfl.triviago;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button starttrivia = findViewById(R.id.StartTrivia);
        starttrivia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TriviaQuestion.class);
                startActivity(intent);
            }
        });
    }


    public void clickedChooseNextWaypointButtonXmlCallback(View view) {
        Intent intentChooseNextWaypoint = new Intent(MainActivity.this, ChooseNextWaypoint.class);
        startActivity(intentChooseNextWaypoint);
    }

    public void clickedWelcomeActivityButtonXmlCallback(View view) {
        Intent intentWelcomeActivity = new Intent(MainActivity.this, ChooseNextWaypoint.class);
        startActivity(intentWelcomeActivity);
    }
}