package com.epfl.triviago;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private int SECOND_ACTIVITY = 1;
    private int players;
    private int maxPlayers;
    private String gameName;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void clickedCreateGameButtonXmlCallback(View view) {
        Intent intentSetUp = new Intent(WelcomeActivity.this, SetUpActivity.class);
        startActivityForResult(intentSetUp, SECOND_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                gameName = data.getStringExtra("name");
                maxPlayers = Integer.parseInt(data.getStringExtra("numPlayers"));
                players = 0;
                mDatabase.child("Games").child(gameName).child("WaitingRoom").child("Players").setValue(players);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(WelcomeActivity.this, "Sorry, a problem " +
                        "was encountered...", Toast.LENGTH_SHORT).show();
            }
        }
    }//onActivityResult


    public void clickedJoinGameButtonXmlCallback(View view) {
        Intent intentJoin = new Intent(WelcomeActivity.this, JoinActivity.class);
        startActivity(intentJoin);


        // Update the number of players in waiting room


    }

}