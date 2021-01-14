package com.epfl.triviago;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    // Request codes for TriviaActivity
    private static final int ASK_QUESTION = 1;
    private static Float counter = 0.f;

    private int mScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button starttrivia = findViewById(R.id.StartTrivia);
        starttrivia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TriviaQuestionActivity.class);
                // choose QCM or V/F
                intent.putExtra(TriviaQuestionActivity.INTENT_QCM_TYPE, false);
                intent.putExtra(TriviaQuestionActivity.INTENT_CATEGORY, 9);
                intent.putExtra(TriviaQuestionActivity.INTENT_DIFFICULTY, "hard");
                intent.putExtra(TriviaQuestionActivity.INTENT_MAX_ATTEMPTS, 3);
                startActivityForResult(intent, ASK_QUESTION);
            }
        });
    }

    public void clickedChooseNextWaypointButtonXmlCallback(View view) {
        Intent intentChooseNextWaypoint = new Intent(MainActivity.this, ChooseNextWaypoint.class);
        intentChooseNextWaypoint.putExtra(ChooseNextWaypoint.INTENT_GAME_NAME, "ABC");
        startActivity(intentChooseNextWaypoint);
    }

    public void clickedWelcomeActivityButtonXmlCallback(View view) {
        Intent intentWelcomeActivity = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(intentWelcomeActivity);
    }

    public void clickedGoToCreateWaypointsButtonXmlCallback(View view) {
        Intent createWaypointsIntent = new Intent(MainActivity.this, CreateWaypointsActivity.class);
        startActivityForResult(createWaypointsIntent, CreateWaypointsActivity.RESULT_WAYPOINTS_CODE);
    }

    public void clickedGoEndButtonXmlCallback(View view) {
        Intent i = new Intent(MainActivity.this, EndActivity.class);
        startActivity(i);
    }


    // ---------------------------------------- WearService ------

    public void clickedUpdateXmlCallback(View view) {
        counter +=10.f;
        Log.e(TAG, "Counter value : " + counter);

        Intent intent = new Intent(this, WearService.class);
        intent.setAction(WearService.ACTION_SEND.ANGLE_SEND.name());
        intent.putExtra(BuildConfig.W_angle_key, counter);
        startService(intent);
    }

    //TODO:Basile debug
    public void clickedWatchXmlCallback(View view) {
        counter = 0.f;
        Log.w(TAG, "Button to launch activity was pressed");
        Intent intent_start = new Intent(this, WearService.class);
        intent_start.setAction(WearService.ACTION_SEND.STARTACTIVITY.name());
        intent_start.putExtra(WearService.ACTIVITY_TO_START, BuildConfig.W_compass_view);
        startService(intent_start);
    }

    public void clickedQuitXmlCallback(View view) {
        Log.w(TAG, "Button to quit activity was pressed");
        Intent intent_stop = new Intent(this, WearService.class);
        intent_stop.setAction(WearService.ACTION_SEND.STOPACTIVITY.name());
        intent_stop.putExtra(WearService.ACTIVITY_TO_STOP, BuildConfig.W_compass_view);
        startService(intent_stop);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check which request we're responding to
        if (requestCode == CreateWaypointsActivity.RESULT_WAYPOINTS_CODE) {
            if (resultCode == RESULT_OK) {
                ArrayList<LatLng> waypointsLatLgnList = (ArrayList<LatLng>) data.getSerializableExtra(CreateWaypointsActivity.RESULT_WAYPOINTS_LIST_NAME);
                ArrayList<String> waypointsCategList = (ArrayList<String>) data.getSerializableExtra(CreateWaypointsActivity.RESULT_CATEG_LIST_NAME);
            }
        }
    }
}