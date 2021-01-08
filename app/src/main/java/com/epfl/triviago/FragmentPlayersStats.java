package com.epfl.triviago;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public static final String MyPREFERENCES = "MyPrefs" ;

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
        SharedPreferences prefs = getActivity().getSharedPreferences(MyPREFERENCES , MODE_PRIVATE);
        gameName = prefs.getString("name", null);
        playerName = prefs.getString("player", null);

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
        DatabaseReference gameDb;
        gameDb = FirebaseDatabase.getInstance().getReference().child(gameName).child("Users");
        gameDb.addListenerForSingleValueEvent(new ValueEventListener() {

            int index =0;
            String player_name;
            float score;

            @Override
            public void onDataChange(DataSnapshot gameSnapshot) {

                total_players = gameSnapshot.getChildrenCount();

                for(DataSnapshot ds: gameSnapshot.getChildren()) {
                    player_name = ds.getKey();
                    score = gameSnapshot.child(player_name).child("rate").getValue(Float.class);

                    if (index==0){
                        player1.setText(player_name+":    "+score*100+"%   correct!");
                    }
                    if (index==1){
                        player2.setText(player_name+":    "+score*100+"%   correct!");
                    }
                    if (index==2){
                        player3.setText(player_name+":    "+score*100+"%   correct!");
                    }
                    if (index==3){
                        player4.setText(player_name+":    "+score*100+"%   correct!");
                    }
                    if (index==4){
                        player5.setText(player_name+":    "+score*100+"%   correct!!");
                    }

                    index+=1;
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
