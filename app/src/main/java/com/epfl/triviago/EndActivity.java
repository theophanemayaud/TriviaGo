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
import android.content.SharedPreferences;
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

    //For pagerLayout
    TabLayout tabLayout;
    ViewPager viewPager;

    //Data from intent
    String gameName;
    String playerName;
    List<Float> waypointsRatesList = new ArrayList<>();

    //Structural elements
    LinearLayout linearLayout_players;
    LinearLayout linearLayout_individual;

    //Useful variables
    long total_players;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        // Get intent extras with game name and player name
        Bundle b1 = getIntent().getExtras();
        // TODO: CHECK why i can't get the values
        //gameName = b1.getString("name");
        //playerName = b1.getString("player");
        //waypointsRatesList = (List<Float>) b1.getSerializable("list");

        gameName = "AAAAA";
        playerName = "FRANK";
        waypointsRatesList.add(0, (float)0.98);
        waypointsRatesList.add(1, (float) 0.76);



        //Sendind data to the fragments
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("name", gameName);
        editor.putString("player", playerName);
        editor.putInt("max_players", waypointsRatesList.size());
        for (int i = 0; i<waypointsRatesList.size(); i++) {
            editor.putFloat("list", waypointsRatesList.get(i));
        }
        editor.commit();



        //Getting the Views
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.pager);
        //linearLayout_players = (LinearLayout) findViewById(R.id.linearLayout_players);
        //linearLayout_individual = (LinearLayout) findViewById(R.id.linearLayout_individual);

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

    public void clickedExitButtonXmlCallback(View view) {
        //TextView text = findViewById(R.id.textView6);
        //String blabla = text.getText().toString();

        Toast.makeText(EndActivity.this, "blabla:", Toast.LENGTH_SHORT).show();

    }

}

