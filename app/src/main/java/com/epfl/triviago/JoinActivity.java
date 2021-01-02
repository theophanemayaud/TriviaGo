package com.epfl.triviago;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        int num = 5;
        SeekBar seekbar = findViewById(R.id.seekBar2);
        seekbar.setClickable(false);
        seekbar.setFocusable(false);
        seekbar.setEnabled(false);
        seekbar.setMax(num);
        seekbar.setProgress(2);


    }
}