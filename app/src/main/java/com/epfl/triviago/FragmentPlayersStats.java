package com.epfl.triviago;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.List;

public class FragmentPlayersStats extends Fragment {

    public static final String MyPREFERENCES = "MyPrefs" ;

    //Data from intent
    String gameName;
    String playerName;
    int total_players;
    List<Float> waypointsRatesList = new ArrayList<>();
    List<String> playersList = new ArrayList<>();

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

        SharedPreferences prefs = getActivity().getSharedPreferences(MyPREFERENCES , MODE_PRIVATE);
        gameName = prefs.getString("name", null);
        playerName = prefs.getString("player", null);
        total_players = prefs.getInt("max_players", 0);
        for (int i = 0; i<total_players; i++) {
            waypointsRatesList.add(prefs.getFloat("list", 0));
        }

        //Getting the views
        player1 = view.findViewById(R.id.player1);
        player2 = view.findViewById(R.id.player2);
        player3 = view.findViewById(R.id.player3);
        player4 = view.findViewById(R.id.player4);
        player5 = view.findViewById(R.id.player5);

        //Creates a dynamic view based on the number of players
        createDynamicLayout (view);

        return  view;
    }

    public void createDynamicLayout(View view) {
        // load game infos from DB
        DatabaseReference gameDb;
        gameDb = FirebaseDatabase.getInstance().getReference().child(gameName).child("Users");
        gameDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot gameSnapshot) {
                for(DataSnapshot ds: gameSnapshot.getChildren()) {
                    playersList.add(ds.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TxGO", "Error writing to database");
            }
        });

        //for (int i=0; i<playersList.size(); i++) {
        //    LinearLayout llMain = view.findViewById(R.id.rlMain);
        //    TextView textView = new TextView(getContext());
        //    textView.setText("Game: "+gameName+"    Name: "+playersList.get(i)+ "TRYYYYY:   "+waypointsRatesList.get(i));
        //    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        //            LinearLayout.LayoutParams.MATCH_PARENT,
        //            LinearLayout.LayoutParams.MATCH_PARENT);
        //    textView.setLayoutParams(params);
        //    llMain.addView(textView);
        //}

        switch ((int) total_players) {
            case 1:
                player1.setVisibility(View.VISIBLE);
                player2.setVisibility(View.GONE);
                player3.setVisibility(View.GONE);
                player4.setVisibility(View.GONE);
                player5.setVisibility(View.GONE);
            case 2:
                player1.setVisibility(View.VISIBLE);
                player2.setVisibility(View.VISIBLE);
                player3.setVisibility(View.GONE);
                player4.setVisibility(View.GONE);
                player5.setVisibility(View.GONE);
            case 3:
                player1.setVisibility(View.VISIBLE);
                player2.setVisibility(View.VISIBLE);
                player3.setVisibility(View.VISIBLE);
                player4.setVisibility(View.GONE);
                player5.setVisibility(View.GONE);
            case 4:
                player1.setVisibility(View.VISIBLE);
                player2.setVisibility(View.VISIBLE);
                player3.setVisibility(View.VISIBLE);
                player4.setVisibility(View.VISIBLE);
                player5.setVisibility(View.GONE);
            case 5:
                player1.setVisibility(View.VISIBLE);
                player2.setVisibility(View.VISIBLE);
                player3.setVisibility(View.VISIBLE);
                player4.setVisibility(View.VISIBLE);
                player5.setVisibility(View.VISIBLE);
        }

    }

}
