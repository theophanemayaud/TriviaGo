package com.laubasthe.triviago;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class FragmentPlayersStats extends Fragment {

    //Data from intent
    private String gameName;
    private String playerName;

    private boolean userJustFinished = true;

    private final ArrayList<String> playerSeenNames = new ArrayList<>();

    private DatabaseReference usersDb;
    private ValueEventListener usersListener;

    public static FragmentPlayersStats getInstance() {
        return new FragmentPlayersStats();
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
        SharedPreferences prefs = getActivity().getSharedPreferences(EndActivity.MyPREFERENCES, MODE_PRIVATE);
        gameName = prefs.getString(ChooseNextWaypoint.INTENT_GAME_NAME, null);
        playerName = prefs.getString(ChooseNextWaypoint.INTENT_PLAYER_NAME, null);

        createLayout(view);
        return view;
    }

    public void createLayout(View view) {
        // load game infos from DB
        RelativeLayout rLayout = view.findViewById(R.id.rlayout_player_stats);

        usersDb = FirebaseDatabase.getInstance().getReference().child(gameName).child("Users");
        usersListener = usersDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot usersSnapshot) {
                String current_player_name;

                for (DataSnapshot userSnap : usersSnapshot.getChildren()) {
                    current_player_name = userSnap.getKey();

                    if (userSnap.child("rate").exists()) {
                        float score = userSnap.child("rate").getValue(Float.class);

                        // check all that we already added
                        boolean userAlreadySeen = false;
                        for (int i = 0; i < playerSeenNames.size(); i++) {
                            if (playerSeenNames.get(i).equals(current_player_name)) {
                                userAlreadySeen = true;
                            }
                        }

                        if (userAlreadySeen == false) {
                            RelativeLayout.LayoutParams lprams = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            lprams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            if (playerSeenNames.size() > 0) {
                                lprams.addRule(RelativeLayout.BELOW, playerSeenNames.size()); //don't add 1 to size at it references the id below the current one !
                                lprams.setMargins(0, 10, 0, 0);

                            }

                            TextView userView = new TextView(getContext());
                            userView.setText(current_player_name + ":    " + String.format("%.2f", score * 100) + "% correct!");
                            userView.setTextAppearance(getActivity(), R.style.fontForEndGame);
                            if (current_player_name.equals(playerName)) {
                                userView.setTextColor(getResources().getColor(R.color.bg_blue));
                            }
                            userView.setLayoutParams(lprams);
                            userView.setId(playerSeenNames.size() + 1);

                            playerSeenNames.add(current_player_name);
                            rLayout.addView(userView);

                            if(!userJustFinished){
                                Toast.makeText(getActivity(), "Another player finished !", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }
                userJustFinished=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TxGO", "Error with database");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (usersListener != null) {
            usersDb.removeEventListener(usersListener);
        }
    }
}
