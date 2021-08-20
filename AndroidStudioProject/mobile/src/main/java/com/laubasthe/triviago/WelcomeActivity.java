package com.laubasthe.triviago;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {

    private final int SETUP_ACTIVITY = 1;
    LinearLayout rules_layout;
    LinearLayout credits_layout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Initial interface
        rules_layout = findViewById(R.id.rules_layout);
        credits_layout = findViewById(R.id.credits_layout);
        rules_layout.setVisibility(View.GONE);
        credits_layout.setVisibility(View.GONE);

        // initialize firebase app check to ensure access to db only from app
        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        // APPCHECK : when needing a debug token for firebase app check in emulator,
        // uncomment next two lines and comment two after them. Then copy token from run
        // logs into firebase project app check settings
//        firebaseAppCheck.installAppCheckProviderFactory(
//                DebugAppCheckProviderFactory.getInstance());
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("firebase sign in", "signInAnonymously:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("firebase sign in", "signInAnonymously:failure", task.getException());
                            Log.e("exception", String.valueOf(task.getException()));
                            Log.e("string", task.toString());
                            Toast.makeText(WelcomeActivity.this, "Error with database. Make sure you have internet, then contact us.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void clickedCreateGameButtonXmlCallback(View view) {
        Intent intentSetUp = new Intent(WelcomeActivity.this, SetUpActivity.class);
        startActivityForResult(intentSetUp, SETUP_ACTIVITY);
    }

    public void clickedJoinGameButtonXmlCallback(View view) {
        Intent intentJoin = new Intent(WelcomeActivity.this, JoinActivity.class);
        startActivity(intentJoin);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rules:
                rules_layout.setVisibility(View.VISIBLE);
                credits_layout.setVisibility(View.GONE);
                return true;
            case R.id.create:
                Intent intentSetUp = new Intent(WelcomeActivity.this, SetUpActivity.class);
                startActivityForResult(intentSetUp, SETUP_ACTIVITY);
                return true;
            case R.id.join:
                Intent intentJoin = new Intent(WelcomeActivity.this, JoinActivity.class);
                startActivity(intentJoin);
                return true;
            case R.id.credits:
                rules_layout.setVisibility(View.GONE);
                credits_layout.setVisibility(View.VISIBLE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}