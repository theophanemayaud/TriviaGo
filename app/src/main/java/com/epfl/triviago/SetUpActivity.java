package com.epfl.triviago;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // [START declare_database_ref]
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.setValue("Hello Firebase!");
        mDatabase.child("users").child("firstchild").setValue("game1");

        //DatabaseReference myRef = database.getReference("message");
        //myRef.setValue("Hello, World!");


        Spinner spinner_player = (Spinner) findViewById(R.id.spinner_players_num);
        ArrayAdapter<CharSequence> adapter_player = ArrayAdapter.createFromResource(this,
                R.array.spinner_players, android.R.layout.simple_spinner_item);
        adapter_player.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_player.setAdapter(adapter_player);

        Spinner spinner_question = (Spinner) findViewById(R.id.spinner_questions_num);
        ArrayAdapter<CharSequence> adapter_question = ArrayAdapter.createFromResource(this,
                R.array.spinner_questions, android.R.layout.simple_spinner_item);
        adapter_question.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_question.setAdapter(adapter_question);

        Spinner spinner_category = (Spinner) findViewById(R.id.spinner_trivia_category);
        ArrayAdapter<CharSequence> adapter_category = ArrayAdapter.createFromResource(this,
                R.array.spinner_category, android.R.layout.simple_spinner_item);
        adapter_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_category.setAdapter(adapter_category);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
