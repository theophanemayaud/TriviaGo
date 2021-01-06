package com.epfl.triviago;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinActivity extends AppCompatActivity {

    //Useful variables
    int max_players;
    int current_players;
    //boolean waiting = false;
    //String nameBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        //Getting the structural elements
        SeekBar seekbar = findViewById(R.id.seekBar2);
        TextView waiting_message = findViewById(R.id.waitingMessage);
        TextView progress_message = findViewById(R.id.progressMessage);

        //Initialisation of elements
        seekbar.setClickable(false);
        seekbar.setFocusable(false);
        seekbar.setEnabled(false);
        seekbar.setVisibility(View.GONE);
        waiting_message.setVisibility(View.GONE);
        progress_message.setVisibility(View.GONE);

    }

    public void clickedJoinButtonXmlCallback(View view) {
        //Getting the structural elements
        SeekBar seekbar = findViewById(R.id.seekBar2);
        TextView waiting_message = findViewById(R.id.waitingMessage);
        TextView progress_message = findViewById(R.id.progressMessage);
        LinearLayout enter_code = findViewById(R.id.enterGame);
        EditText name_text = findViewById(R.id.writeCode);
        String gameName = name_text.getText().toString();
        EditText username_text = findViewById(R.id.username);
        String username = username_text.getText().toString();


        //Check game name to see if it exists
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        DatabaseReference mData = data.getReference();


        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("Games").hasChild(gameName)) {
                    //nameBack = gameName;
                    //waiting = true;

                    //Add the player to the game
                    mData.child("Games").child(gameName).child("Users").setValue(username);

                    // Update the number of players in waiting room
                    max_players = snapshot.child("Games").child(gameName).child("NumPlayers").getValue(Integer.class);
                    current_players = snapshot.child("Games").child(gameName).child("WaitingRoom").child("Players").getValue(Integer.class);

                    if (current_players > max_players){
                        Toast.makeText(JoinActivity.this, "Game is full!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    if(current_players < max_players) {
                        current_players += 1;
                        mData.child("Games").child(gameName).child("WaitingRoom").child("Players").setValue(current_players);
                    }
                    if (current_players == max_players) {
                        int once = 0;
                        Intent intentChooseNextWaypoint = new Intent(JoinActivity.this, ChooseNextWaypoint.class);
                        intentChooseNextWaypoint.putExtra(ChooseNextWaypoint.INTENT_GAME_NAME, gameName);
                        startActivity(intentChooseNextWaypoint);
                    }
                    if (current_players > max_players) {
                        Toast.makeText(JoinActivity.this, "Game is full!", Toast.LENGTH_SHORT).show();
                    }

                    //Updating waiting interface
                    enter_code.setVisibility(View.GONE);

                    seekbar.setVisibility(View.VISIBLE);
                    waiting_message.setVisibility(View.VISIBLE);
                    progress_message.setVisibility(View.VISIBLE);

                    progress_message.setText(" "+current_players+"/"+max_players+"players");
                    seekbar.setMax(max_players);
                    seekbar.setProgress(current_players);

                    if (current_players == max_players) {
                        current_players += 1;
                        mData.child("Games").child(gameName).child("WaitingRoom").child("Players").setValue(current_players);
                        Intent intentChooseNextWaypoint = new Intent(JoinActivity.this, ChooseNextWaypoint.class);
                        intentChooseNextWaypoint.putExtra(ChooseNextWaypoint.INTENT_GAME_NAME, gameName);
                        intentChooseNextWaypoint.putExtra(ChooseNextWaypoint.INTENT_PLAYER_NAME, username);
                        Toast.makeText(JoinActivity.this, "Joining Game...", Toast.LENGTH_SHORT).show();
                        startActivity(intentChooseNextWaypoint);
                    }

                } else {
                    Toast.makeText(JoinActivity.this, "Game "+gameName+ " doesn't exist: " +
                            "create a new one or enter another name! ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }

    //@Override
    //protected void onDestroy() {
    //    super.onDestroy();
    //    FirebaseDatabase data = FirebaseDatabase.getInstance();
    //    DatabaseReference mData = data.getReference();
    //    if(waiting == true) {
    //        mData.addListenerForSingleValueEvent(new ValueEventListener() {
    //            @Override
    //            public void onDataChange(DataSnapshot snapshot) {
    //                if (snapshot.child("Games").hasChild(nameBack)) {
    //                    mData.child("Games").child(nameBack).child("WaitingRoom").child("Players").setValue(current_players);
    //                    Toast.makeText(JoinActivity.this, "DESTROYYYYYY!", Toast.LENGTH_SHORT).show();
    //                }
    //            }
    //            @Override
    //            public void onCancelled(DatabaseError error) {
    //                // Failed to read value
    //            }
    //        });
    //    }

}

