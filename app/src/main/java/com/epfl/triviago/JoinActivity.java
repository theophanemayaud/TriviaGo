package com.epfl.triviago;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinActivity extends AppCompatActivity {

    //Useful variables
    private int max_players;
    private int current_players;
    private String username;
    private String gameName;

    private DatabaseReference gameDb;

    private boolean userInGame = false;
    private boolean userAddedToDb = false;
    private boolean skippedFirstNewUser = false;

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
        gameName = name_text.getText().toString();
        if(gameName.isEmpty()){
            Toast.makeText(JoinActivity.this, R.string.empty_game_name, Toast.LENGTH_SHORT).show();
            return;
        }
        EditText username_text = findViewById(R.id.username);
        username = username_text.getText().toString();
        if(username.isEmpty()){
            Toast.makeText(JoinActivity.this,
                    R.string.empty_user_name, Toast.LENGTH_SHORT).show();
            return;
        }

        //Check game name to see if it exists
        gameDb = FirebaseDatabase.getInstance().getReference().child(gameName);
        gameDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot gameSnapshot) {
                if(!gameSnapshot.exists()){
                    Toast.makeText(JoinActivity.this, "Game "+gameName+ " doesn't exist: " +
                            "create a new one or enter another name! ", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update the number of players in waiting room
                max_players = gameSnapshot.child("Settings").child("NumPlayers").getValue(Integer.class);
                current_players = (int) gameSnapshot.child("Users").getChildrenCount();

                if (current_players >= max_players){
                    Toast.makeText(JoinActivity.this, "This game is full!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Add the player to the game if doesn't exist
                if(gameSnapshot.child("Users").hasChild(username)){
                    Toast.makeText(JoinActivity.this, "This username is taken !", Toast.LENGTH_SHORT).show();
                    return;
                }
                //add current player to db, as child of Users
                gameDb.child("Users").child(username)
                        .child("latitude").setValue(0); //default value to save player to db
                gameDb.child("Users").child(username)
                        .child("longitude").setValue(0);
                userAddedToDb = true;

                //Update waiting interface
                enter_code.setVisibility(View.GONE);
                seekbar.setVisibility(View.VISIBLE);
                waiting_message.setVisibility(View.VISIBLE);
                progress_message.setVisibility(View.VISIBLE);
                progress_message.setText(" "+current_players+"/"+max_players+" players");
                seekbar.setMax(max_players);
                seekbar.setProgress(current_players);

                gameDb.child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot usersSnapshot) {
                        if(userInGame==true){
                            return; //We are already in the game, or waiting to enter
                        }

                        current_players = (int) usersSnapshot.getChildrenCount();
                        if(current_players<max_players) {
                            if(skippedFirstNewUser==false){ //skip first fire of the callback as it is the user itself !
                                progress_message.setText(" "+current_players+"/"+max_players+" players");
                                seekbar.setProgress(current_players);
                                Toast.makeText(JoinActivity.this, "A new player just joined !", Toast.LENGTH_SHORT).show();
                            }
                            skippedFirstNewUser = true;
                        }
                        else{
                            userInGame=true;
                            long tStart = System.currentTimeMillis();
                            Intent intentChooseNextWaypoint = new Intent(JoinActivity.this, ChooseNextWaypoint.class);
                            intentChooseNextWaypoint.putExtra(ChooseNextWaypoint.INTENT_GAME_NAME, gameName);
                            intentChooseNextWaypoint.putExtra(ChooseNextWaypoint.INTENT_PLAYER_NAME, username);
                            intentChooseNextWaypoint.putExtra("time", tStart);
                            Toast.makeText(JoinActivity.this, "Joining Game...", Toast.LENGTH_SHORT).show();
                            startActivity(intentChooseNextWaypoint);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        Log.e(""+e.getClass(), "Error with database" + e.getDetails());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError e) {
                Log.e(""+e.getClass(), "Error with database" + e.getDetails());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // only remove user when pressing back button within this activity
        if(userInGame == false && userAddedToDb == true){
            gameDb.child("Users").child(username).removeValue();
        }
    }
}

