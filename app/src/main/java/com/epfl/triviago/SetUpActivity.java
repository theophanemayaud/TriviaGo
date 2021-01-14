package com.epfl.triviago;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.service.autofill.CharSequenceTransformation;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class SetUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //Useful Variables
    int numPlayer = 2;
    boolean questionType = false; //FALSE = Multiple choice & TRUE = true/false
    String difficulty = "easy";
    Integer maxAttemps = 3;
    ArrayList<LatLng> waypointsLatLgnList;
    ArrayList<Integer> waypointsCategList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //Views
        findViewById(R.id.button_done).setVisibility(View.GONE);
    }

    public void clickedWaypointButtonXmlCallback(View view) {
        Intent createWaypointsIntent = new Intent(SetUpActivity.this, CreateWaypointsActivity.class);
        startActivityForResult(createWaypointsIntent, CreateWaypointsActivity.RESULT_WAYPOINTS_CODE);
    }

    public void clickedDoneButtonXmlCallback(View view) {
        TextView nameGame = findViewById(R.id.nameGame);
        String gameName = nameGame.getText().toString();
        if(gameName.isEmpty()){
            Toast.makeText(SetUpActivity.this, R.string.empty_game_name,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference gameDb = FirebaseDatabase.getInstance().getReference().child(gameName);
        gameDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot gameSnapshot) {
                if(gameSnapshot.exists()){
                    Toast.makeText(SetUpActivity.this, "Game name already exists, " +
                            "please choose another one", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    // Set values in DB
                    ToggleButton toggle = findViewById(R.id.button_questionType);
                    gameDb.child("Settings").child("NumPlayers").setValue(numPlayer);
                    if (toggle.isChecked()) {
                        questionType = true;
                        gameDb.child("Settings").child("QuestionType").setValue("true/false");
                    } else {
                        gameDb.child("Settings").child("QuestionType").setValue("QCM");
                    }
                    gameDb.child("Settings").child("Difficulty").setValue(difficulty);
                    gameDb.child("Settings").child("MaxAttempts").setValue(maxAttemps);

                    int size = waypointsLatLgnList.size();
                    for (int i=0; i<size; i++) {
                        LatLng waypoint = waypointsLatLgnList.get(i);
                        double waypointLat = waypoint.latitude;
                        double waypointLgn = waypoint.longitude;
                        int category = waypointsCategList.get(i);

                        gameDb.child("Waypoints").child(Integer.toString(i))
                                .child("latitude").setValue(waypointLat);
                        gameDb.child("Waypoints").child(Integer.toString(i))
                                .child("longitude").setValue(waypointLgn);
                        gameDb.child("Waypoints").child(Integer.toString(i))
                                .child("category").setValue(category);
                    }

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("name",gameName);
                    setResult(Activity.RESULT_OK,returnIntent);
                    Toast.makeText(SetUpActivity.this, "Game created!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TxGO", "Error writing to database");
                Toast.makeText(SetUpActivity.this, "Problem with database, please try again...", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void clicked1pButtonXmlCallback(View view) {
        numPlayer = 1;
        findViewById(R.id.button_1p).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_2p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_3p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_4p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_5p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked2pButtonXmlCallback(View view) {
        numPlayer = 2;
        findViewById(R.id.button_1p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_2p).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_3p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_4p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_5p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked3pButtonXmlCallback(View view) {
        numPlayer = 3;
        findViewById(R.id.button_1p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_2p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_3p).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_4p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_5p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked4pButtonXmlCallback(View view) {
        numPlayer = 4;
        findViewById(R.id.button_1p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_2p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_3p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_4p).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_5p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked5pButtonXmlCallback(View view) {
        numPlayer = 5;
        findViewById(R.id.button_1p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_2p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_3p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_4p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_5p).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
    }

    public void clickedEasyButtonXmlCallback(View view) {
        difficulty = "easy";
        findViewById(R.id.button_easy).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_medium).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_hard).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clickedMediumButtonXmlCallback(View view) {
        difficulty = "medium";
        findViewById(R.id.button_easy).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_medium).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_hard).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clickedHardButtonXmlCallback(View view) {
        difficulty = "hard";
        findViewById(R.id.button_easy).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_medium).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_hard).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
    }

    public void clicked1AtpButtonXmlCallback(View view) {
        maxAttemps = 1;
        findViewById(R.id.button_1atp).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_3atp).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_5atp).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked3AtpButtonXmlCallback(View view) {
        maxAttemps = 3;
        findViewById(R.id.button_1atp).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_3atp).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_5atp).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked5AtpButtonXmlCallback(View view) {
        maxAttemps = 5;
        findViewById(R.id.button_1atp).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_3atp).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_5atp).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Another interface callback
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == CreateWaypointsActivity.RESULT_WAYPOINTS_CODE) {
            if (resultCode == RESULT_OK) {
                waypointsLatLgnList = (ArrayList<LatLng>) data.getSerializableExtra(CreateWaypointsActivity.RESULT_WAYPOINTS_LIST_NAME);
                waypointsCategList = (ArrayList<Integer>) data.getSerializableExtra(CreateWaypointsActivity.RESULT_CATEG_LIST_NAME);
                findViewById(R.id.button_done).setVisibility(View.VISIBLE);
            }
            else {
                Toast.makeText(this, R.string.no_waypoints_error_msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}