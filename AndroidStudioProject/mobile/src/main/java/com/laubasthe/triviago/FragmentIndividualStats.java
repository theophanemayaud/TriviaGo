package com.laubasthe.triviago;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FragmentIndividualStats extends Fragment {

    //Data from intent
    private String gameName;
    private String playerName;
    private int total_waypoints;
    private final List<Float> waypointsRatesList = new ArrayList<>();

    public static FragmentIndividualStats getInstance() {
        FragmentIndividualStats fragmentIndividualStats = new FragmentIndividualStats();
        return fragmentIndividualStats;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individual_stats, container, false);

        //Retrieve data from EndActivity
        SharedPreferences prefs = getActivity().getSharedPreferences(EndActivity.MyPREFERENCES, MODE_PRIVATE);
        gameName = prefs.getString(ChooseNextWaypoint.INTENT_GAME_NAME, null);
        playerName = prefs.getString(ChooseNextWaypoint.INTENT_PLAYER_NAME, null);
        total_waypoints = prefs.getInt(EndActivity.TOT_WAYPS_COUNT, 0);
        for (int i = 0; i < total_waypoints; i++) {
            waypointsRatesList.add(i, prefs.getFloat(EndActivity.WAYPS_LIST_ID + i, 0));
        }

        //Creates a dynamic layout based on the number waypoints of the game
        createDynamicLayout(view);

        return view;
    }

    @SuppressLint("ResourceType")
    public void createDynamicLayout(View view) {
        // First TextView
        RelativeLayout rLayout = view.findViewById(R.id.rlayout);
        RelativeLayout.LayoutParams lprams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextView txtView = new TextView(getContext());
        txtView.setText("Waypoint " + 0 + ":    " + String.format("%.2f", waypointsRatesList.get(0) * 100) + "%   correct!");
        txtView.setTextAppearance(getActivity(), R.style.fontForEndGame);
        lprams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        txtView.setLayoutParams(lprams);
        txtView.setId(1);
        rLayout.addView(txtView);

        //Dynamically add the TextViews for each remaining waypoint
        for (int i = 1; i < total_waypoints; i++) {
            RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(getContext());
            textView.setText("Waypoint " + i + ":    " + String.format("%.2f", waypointsRatesList.get(i) * 100) + "%   correct!");
            textView.setTextAppearance(getActivity(), R.style.fontForEndGame);
            newParams.addRule(RelativeLayout.BELOW, i);
            newParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            newParams.setMargins(0, 10, 0, 0);
            textView.setLayoutParams(newParams);
            textView.setId(i + 1);
            rLayout.addView(textView);
        }
    }
}
