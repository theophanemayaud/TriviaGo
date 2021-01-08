package com.epfl.triviago;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity {

    private int SETUP_ACTIVITY = 1;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously();
    }

    public void clickedCreateGameButtonXmlCallback(View view) {
        Intent intentSetUp = new Intent(WelcomeActivity.this, SetUpActivity.class);
        startActivityForResult(intentSetUp, SETUP_ACTIVITY);
    }

    public void clickedJoinGameButtonXmlCallback(View view) {
        Intent intentJoin = new Intent(WelcomeActivity.this, JoinActivity.class);
        startActivity(intentJoin);
    }

}