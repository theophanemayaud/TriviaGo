package com.epfl.triviago;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.service.autofill.CharSequenceTransformation;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SetUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // [START declare_database_ref]
    private DatabaseReference mDatabase;

    //Useful Variables
    String numPlayer = "2";
    boolean questionType = false; //FALSE = Multiple choice & TRUE = true/false
    String difficulty = "easy";
    String maxAttemps = "3";
    ArrayList<LatLng> waypointsLatLgnList;
    ArrayList<String> waypointsCategList;

    //UI Elements
    private View button_done;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //Views
        button_done = findViewById(R.id.button_done);
        button_done.setVisibility(View.GONE);

        //DatabaseReference myRef = database.getReference("message");
        //myRef.setValue("Hello, World!");

        // Spinner spinner_player = (Spinner) findViewById(R.id.spinner_players_num);
        // ArrayAdapter<CharSequence> adapter_player = ArrayAdapter.createFromResource(this,
        //         R.array.spinner_players, android.R.layout.simple_spinner_item);
        // adapter_player.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // spinner_player.setAdapter(adapter_player);

    }

    public void clickedWaypointButtonXmlCallback(View view) {
        Intent createWaypointsIntent = new Intent(SetUpActivity.this, CreateWaypointsActivity.class);
        startActivityForResult(createWaypointsIntent, CreateWaypointsActivity.RESULT_WAYPOINTS_CODE);

        button_done.setVisibility(View.VISIBLE);
    }


    public void clickedDoneButtonXmlCallback(View view) {
        TextView nameGame = findViewById(R.id.nameGame);
        String gameName = nameGame.getText().toString();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("Games").hasChild(gameName)) {
                    Toast.makeText(SetUpActivity.this, "Game name already exists, " +
                            "please choose another one", Toast.LENGTH_SHORT).show();
                } else {
                    //Default values
                    mDatabase.child("Games").child(gameName).child("NumPlayers").setValue("2");
                    mDatabase.child("Games").child(gameName).child("QuestionType").setValue("QCM");
                    mDatabase.child("Games").child(gameName).child("Difficulty").setValue("easy");
                    mDatabase.child("Games").child(gameName).child("MaxAttempts").setValue("3");

                    //Adjusted values
                    ToggleButton toggle = findViewById(R.id.button_questionType);
                    mDatabase.child("Games").child(gameName).child("NumPlayers").setValue(numPlayer);
                    if (toggle.isChecked()) {
                        questionType = true;
                        mDatabase.child("Games").child(gameName).child("QuestionType").setValue("true/false");
                    } else {
                        mDatabase.child("Games").child(gameName).child("QuestionType").setValue("QCM");
                    }
                    Toast.makeText(SetUpActivity.this, "" + questionType, Toast.LENGTH_SHORT).show();

                    mDatabase.child("Games").child(gameName).child("Difficulty").setValue(difficulty);
                    mDatabase.child("Games").child(gameName).child("MaxAttempts").setValue(maxAttemps);

                    Toast.makeText(SetUpActivity.this, "NEW", Toast.LENGTH_SHORT).show();

                    int size = waypointsLatLgnList.size();
                    for (int i=0; i<size; i++) {
                        LatLng waypoint = waypointsLatLgnList.get(i);
                        double waypointLat = waypoint.latitude;
                        double waypointLgn = waypoint.longitude;

                        String wayptIdx = String.valueOf(i);

                        mDatabase.child("Games").child(gameName).child("Waypoints").child(wayptIdx+"-Lat:").setValue(waypointLat);
                        mDatabase.child("Games").child(gameName).child("Waypoints").child(wayptIdx+"-Lgn:").setValue(waypointLgn);
                    }

                    //finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void clicked1pButtonXmlCallback(View view) {
        numPlayer = "1";
        findViewById(R.id.button_1p).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_2p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_3p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_4p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_5p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked2pButtonXmlCallback(View view) {
        numPlayer = "2";
        findViewById(R.id.button_1p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_2p).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_3p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_4p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_5p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked3pButtonXmlCallback(View view) {
        numPlayer = "3";
        findViewById(R.id.button_1p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_2p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_3p).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_4p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_5p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked4pButtonXmlCallback(View view) {
        numPlayer = "4";
        findViewById(R.id.button_1p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_2p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_3p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_4p).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_5p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked5pButtonXmlCallback(View view) {
        numPlayer = "5";
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
        maxAttemps = "1";
        findViewById(R.id.button_1atp).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_3atp).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_5atp).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked3AtpButtonXmlCallback(View view) {
        maxAttemps = "3";
        findViewById(R.id.button_1atp).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_3atp).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_5atp).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked5AtpButtonXmlCallback(View view) {
        maxAttemps = "5";
        findViewById(R.id.button_1atp).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_3atp).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_5atp).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

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
                waypointsCategList = (ArrayList<String>) data.getSerializableExtra(CreateWaypointsActivity.RESULT_CATEG_LIST_NAME);
            }
        }
    }
}