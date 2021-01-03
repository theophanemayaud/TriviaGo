package com.epfl.triviago;

import androidx.appcompat.app.AppCompatActivity;

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

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = database.getReference();

    //Useful variables
    int max_players;
    int current_players;

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


        int num = 0;
        seekbar.setMax(num);
        seekbar.setProgress(0);

    }

    public void clickedJoinButtonXmlCallback(View view) {
        //Getting the structural elements
        SeekBar seekbar = findViewById(R.id.seekBar2);
        TextView waiting_message = findViewById(R.id.waitingMessage);
        TextView progress_message = findViewById(R.id.progressMessage);
        LinearLayout enter_code = findViewById(R.id.enterGame);
        EditText name_text = findViewById(R.id.writeCode);
        String gameName;
        gameName = name_text.getText().toString();

        //Check game name to see if it exists
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("Games").hasChild(gameName)) {

                    Toast.makeText(JoinActivity.this, "Joining Game...", Toast.LENGTH_SHORT).show();

                    // Update the number of players in waiting room
                    max_players = snapshot.child("Games").child(gameName).child("NumPlayers").getValue(Integer.class);
                    current_players = snapshot.child("Games").child(gameName).child("WaitingRoom").child("Players").getValue(Integer.class);
                    current_players += 1;   //PROBLEMMMMMMM !!!
                    mDatabase.child("Games").child(gameName).child("WaitingRoom").child("Players").setValue(current_players);

                    //Updating waiting interface
                    enter_code.setVisibility(View.GONE);
                    seekbar.setVisibility(View.VISIBLE);
                    waiting_message.setVisibility(View.VISIBLE);
                    progress_message.setVisibility(View.VISIBLE);

                    progress_message.setText(" "+current_players+"/3 players");
                    seekbar.setMax(max_players);
                    seekbar.setProgress(current_players);

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

}

