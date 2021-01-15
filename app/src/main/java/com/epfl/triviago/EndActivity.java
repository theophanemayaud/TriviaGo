package com.epfl.triviago;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.ListFragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EndActivity extends AppCompatActivity {

    // TODO make private what doesn't need to be public !!!!
    //For pagerLayout
    TabLayout tabLayout;
    ViewPager viewPager;

    //Data from intent
    String gameName;
    String playerName;
    List<Float> waypointsRatesList = new ArrayList<>();

    //Timer variable
    private int elapsedSeconds;

    //Structural elements
    ImageView stars;
    TextView score_message;
    TextView time_message;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String TOT_WAYPS_COUNT = "tot_waypoints" ;
    public static final String WAYPS_LIST_ID = "list" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        // Get intent extras with game name and player name
        Bundle b1 = getIntent().getExtras();

        gameName = b1.getString(ChooseNextWaypoint.INTENT_GAME_NAME);
        playerName = b1.getString(ChooseNextWaypoint.INTENT_PLAYER_NAME);
        waypointsRatesList = (List<Float>) b1.getSerializable(ChooseNextWaypoint.INTENT_PLAYER_STATS_LIST);
        long startTime = b1.getLong(JoinActivity.START_TIME_MS);

        long endTime = System.currentTimeMillis();
        long tDeltaMs = endTime - startTime;
        elapsedSeconds = (int) tDeltaMs / 1000;

        //Sendind data to the fragments
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(ChooseNextWaypoint.INTENT_GAME_NAME, gameName);
        editor.putString(ChooseNextWaypoint.INTENT_PLAYER_NAME, playerName);
        editor.putInt(TOT_WAYPS_COUNT, waypointsRatesList.size());
        for (int i = 0; i<waypointsRatesList.size(); i++) {
            editor.putFloat(WAYPS_LIST_ID+i, waypointsRatesList.get(i));
        }
        editor.commit();

        //Getting the Views
        stars = findViewById(R.id.num_stars);
        score_message = findViewById(R.id.score_message);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.pager);
        time_message = findViewById(R.id.time_message);

        //Edit layout according to individual score
        editLayout ();

        getTabs();
    }

    public void getTabs() {
        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                viewPagerAdapter.addFragment(FragmentIndividualStats.getInstance(), "Individual Stats");
                viewPagerAdapter.addFragment(FragmentPlayersStats.getInstance(), "Players Stats");
                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }
        });
    }

    public void editLayout () {
        //Set time score
        time_message.setText("Time taken: "+ elapsedSeconds/60 +"min " + elapsedSeconds%60 +"sec");

        //Get player score from firebase
        DatabaseReference gameDb;
        gameDb = FirebaseDatabase.getInstance().getReference().child(gameName).child("Users");
        gameDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot gameSnapshot) {
               if(gameSnapshot.child(playerName).exists()) {
                   float score = gameSnapshot.child(playerName).child("rate").getValue(Float.class);
                   if (score == (float) 1) {
                       score_message.setText("Perfect!");
                       stars.setImageDrawable(getResources().getDrawable(R.drawable._stars));
                   }
                   else if (score >= (float) 0.7) {
                       score_message.setText("Good Job!");
                       stars.setImageDrawable(getResources().getDrawable(R.drawable._2stars));
                   }
                   else if (score >= (float) 0.4) {
                       score_message.setText("Great!");
                       stars.setImageDrawable(getResources().getDrawable(R.drawable._1star));
                   }
                   else {
                       score_message.setText("Keep Learning !");
                       stars.setImageDrawable(getResources().getDrawable(R.drawable._0stars));
                   }
               }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TxGO", "Error writing to database");
            }
        });
    }

    public void clickedExitButtonXmlCallback(View view) {
        Toast.makeText(EndActivity.this, "Thanks for playing !", Toast.LENGTH_SHORT).show();

        Intent finishIntent = new Intent(this, WelcomeActivity.class);
        finishIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(finishIntent);
    }
}

