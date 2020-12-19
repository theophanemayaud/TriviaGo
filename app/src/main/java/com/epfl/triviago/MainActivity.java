package com.epfl.triviago;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    // Request codes for TriviaActivity
    private static final int ASK_QUESTION = 1;

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
                intent.putExtra(TriviaQuestionActivity.INTENT_CATEGORY, 17);
                intent.putExtra(TriviaQuestionActivity.INTENT_DIFFICULTY, "hard");
                startActivityForResult(intent, ASK_QUESTION);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ASK_QUESTION:
                if (resultCode == RESULT_OK) {
                    boolean correct_answer = data.getExtras().getBoolean(TriviaQuestionActivity.INTENT_RESULT);
                    if (correct_answer) {
                        mScore += 1;
                    }
                    Log.e(TAG, "The score is : " + mScore);
                }
        }
    }

    public void clickedChooseNextWaypointButtonXmlCallback(View view) {
        Intent intentChooseNextWaypoint = new Intent(MainActivity.this, ChooseNextWaypoint.class);
        startActivity(intentChooseNextWaypoint);
    }

    public void clickedWelcomeActivityButtonXmlCallback(View view) {
        Intent intentWelcomeActivity = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(intentWelcomeActivity);
    }

    public void clickedGoToCreateWaypointsButtonXmlCallback(View view) {
        Intent createWaypointsIntent = new Intent(MainActivity.this, CreateWaypoints.class);
        startActivity(createWaypointsIntent);
    }
}