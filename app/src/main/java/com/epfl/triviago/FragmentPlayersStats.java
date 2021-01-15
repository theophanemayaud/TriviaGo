package com.epfl.triviago;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;

public class FragmentPlayersStats extends Fragment {

    // TODO private what doesn't need public
    //Data from intent
    String gameName;
    String playerName;
    long total_players;

    //Views
    TextView player1;
    TextView player2;
    TextView player3;
    TextView player4;
    TextView player5;

    public static FragmentPlayersStats getInstance() {
        FragmentPlayersStats fragmentPlayersStats = new FragmentPlayersStats();
        return  fragmentPlayersStats;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_players_stats, container, false);

        //Retrieve data from EndActivity
        SharedPreferences prefs = getActivity().getSharedPreferences(EndActivity.MyPREFERENCES , MODE_PRIVATE);
        gameName = prefs.getString(ChooseNextWaypoint.INTENT_GAME_NAME, null);
        playerName = prefs.getString(ChooseNextWaypoint.INTENT_PLAYER_NAME, null);

        //Getting the views and setting appearance
        player1 = view.findViewById(R.id.player1);
        player1.setTextAppearance(getActivity(), R.style.fontForEndGame);
        player2 = view.findViewById(R.id.player2);
        player2.setTextAppearance(getActivity(), R.style.fontForEndGame);
        player3 = view.findViewById(R.id.player3);
        player3.setTextAppearance(getActivity(), R.style.fontForEndGame);
        player4 = view.findViewById(R.id.player4);
        player4.setTextAppearance(getActivity(), R.style.fontForEndGame);
        player5 = view.findViewById(R.id.player5);
        player5.setTextAppearance(getActivity(), R.style.fontForEndGame);

        createLayout(view);
        return  view;
    }

    public void createLayout(View view) {
        // load game infos from DB
        DatabaseReference usersDb;
        usersDb = FirebaseDatabase.getInstance().getReference().child(gameName).child("Users");
        usersDb.addValueEventListener(new ValueEventListener() {

            int index =0;
            String player_name;
            float score;

            @Override
            public void onDataChange(DataSnapshot usersSnapshot) {
                total_players = usersSnapshot.getChildrenCount();

                for(DataSnapshot ds: usersSnapshot.getChildren()) {
                    player_name = ds.getKey();
                    if(ds.child("rate").exists()) {
                        score = ds.child("rate").getValue(Float.class); // TODO fix when new player finishes it also re-adds to list some original players
                        if (index==0){
                            player1.setText(player_name+":    "+String.format("%.2f", score*100)+"%   correct!");
                            if (player_name.equals(playerName)) {
                                player1.setTextColor(getResources().getColor(R.color.button_end));
                            }
                        }
                        if (index==1){
                            player2.setText(player_name+":    "+String.format("%.2f", score*100)+"%   correct!");
                            if (player_name.equals(playerName)) {
                                player1.setTextColor(getResources().getColor(R.color.button_end));
                            }
                        }
                        if (index==2){
                            player3.setText(player_name+":    "+String.format("%.2f", score*100)+"%   correct!");
                            if (player_name.equals(playerName)) {
                                player1.setTextColor(getResources().getColor(R.color.button_end));
                            }
                        }
                        if (index==3){
                            player4.setText(player_name+":    "+String.format("%.2f", score*100)+"%   correct!");
                            if (player_name.equals(playerName)) {
                                player1.setTextColor(getResources().getColor(R.color.button_end));
                            }
                        }
                        if (index==4){
                            player5.setText(player_name+":    "+String.format("%.2f", score*100)+"%   correct!!");
                            if (player_name.equals(playerName)) {
                                player1.setTextColor(getResources().getColor(R.color.button_end));
                            }
                        }
                        index+=1;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TxGO", "Error writing to database");
            }
        });

        if(total_players == (long) 1) {
            player1.setVisibility(View.VISIBLE);
            player2.setVisibility(View.GONE);
            player3.setVisibility(View.GONE);
            player4.setVisibility(View.GONE);
            player5.setVisibility(View.GONE);
        }
        if(total_players == 2) {
            player1.setVisibility(View.VISIBLE);
            player2.setVisibility(View.VISIBLE);
            player3.setVisibility(View.GONE);
            player4.setVisibility(View.GONE);
            player5.setVisibility(View.GONE);
        }
        if(total_players == 3) {
            player1.setVisibility(View.VISIBLE);
            player2.setVisibility(View.VISIBLE);
            player3.setVisibility(View.VISIBLE);
            player4.setVisibility(View.GONE);
            player5.setVisibility(View.GONE);
        }
        if(total_players == 4) {
            player1.setVisibility(View.VISIBLE);
            player2.setVisibility(View.VISIBLE);
            player3.setVisibility(View.VISIBLE);
            player4.setVisibility(View.VISIBLE);
            player5.setVisibility(View.GONE);
        }
        if(total_players == 5) {
            player1.setVisibility(View.VISIBLE);
            player2.setVisibility(View.VISIBLE);
            player3.setVisibility(View.VISIBLE);
            player4.setVisibility(View.VISIBLE);
            player5.setVisibility(View.VISIBLE);
        }
    }
}
