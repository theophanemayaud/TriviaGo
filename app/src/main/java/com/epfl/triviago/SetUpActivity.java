package com.epfl.triviago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SetUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final int DEFAULT_NUM_PLAYERS = 2;
    private static final int NUM_PLAYER_BUTTONS = 3;

    private String difficulty = "easy";
    private Integer maxAttemps = 3;
    private ArrayList<LatLng> waypointsLatLgnList;
    private ArrayList<Integer> waypointsCategList;

    private ArrayList<Button> playerNumberButtonList = new ArrayList<>();

    TextView playersNumberTextview;

    // ---------------------------------------------------------------------------------------------
    // -------- ⬇️ Start : Lifecycle methods -------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_setup);

        //Views
        playersNumberTextview = findViewById(R.id.playersNumberTextview);
        findViewById(R.id.button_done).setVisibility(View.GONE);

        playerNumberButtonList.add(findViewById(R.id.button_1_pnumber));
        playerNumberButtonList.add(findViewById(R.id.button_2_pnumber));
        playerNumberButtonList.add(findViewById(R.id.button_3_pnumber));

        setSelectedPlayerButtonState(DEFAULT_NUM_PLAYERS);
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

    // -------- ⬆️ End : Lifecycle methods ---------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // -------- ⬇️ Start : Utility functions -------------------------------------------------------

    private void setSelectedPlayerButtonState(int numberOfPlayers) {
        // go through button number list and set correct one selected (if value corresponds to one)
        boolean playerNumberInButtonList = false;
        for (int i = 1; i <= NUM_PLAYER_BUTTONS; i++) {
            int buttonNumPlayer = Integer.parseInt(playerNumberButtonList.get(i - 1).getText().toString());
            if (numberOfPlayers == buttonNumPlayer) {
                playerNumberButtonList.get(i - 1).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
                playerNumberInButtonList = true;
            } else {
                playerNumberButtonList.get(i - 1).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
            }
        }

        // if the player number is not in specific button numbers, we show the plus and minus buttons as activated
        activatePlusMinusButtons(!playerNumberInButtonList);

        playersNumberTextview.setText(String.valueOf(numberOfPlayers));
    }

    private void activatePlusMinusButtons(boolean activate) {
        Button plus_button = findViewById(R.id.button_plus_player);
        Button minus_button = findViewById(R.id.button_minus_player);

        if(activate){
            plus_button.setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
            minus_button.setBackgroundResource(R.drawable.buttonshape_difficulty_selected);

        }
        else{
            plus_button.setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
            minus_button.setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        }

    }

    // -------- ⬆️ End : Utility functions ---------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // -------- ⬇️ Start : XML Callbacks -----------------------------------------------------------

    public void clickedPlayerNumberButton(View view) {
        Button button = (Button)view;
        int playerNumber = Integer.parseInt(button.getText().toString());
        setSelectedPlayerButtonState(playerNumber);
    }

    public void clickedPlayerPlusMinusButton(View view){
        Button button = (Button)view;
        String buttonSign = button.getText().toString();
        int numPlayer = Integer.parseInt(playersNumberTextview.getText().toString());

        if(buttonSign.equals(getString(R.string.button_plus_player_sign))){
            setSelectedPlayerButtonState(numPlayer+1);
        }
        else if(buttonSign.equals(getString(R.string.button_minus_player_sign))){
            if (numPlayer == 1) {
                Toast.makeText(this, "You cannot have less than 1 player !", Toast.LENGTH_SHORT).show();
            }
            else{
                setSelectedPlayerButtonState(numPlayer-1);
            }
        }
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
                    int numPlayer = Integer.parseInt(playersNumberTextview.getText().toString());

                    // Set values in DB
                    ToggleButton toggle = findViewById(R.id.button_questionType);
                    gameDb.child("Settings").child("NumPlayers").setValue(numPlayer);
                    if (toggle.isChecked()) {
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

    // -------- ⬆️ End : XML Callbacks -----------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
}