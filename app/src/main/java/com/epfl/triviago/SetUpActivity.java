package com.epfl.triviago;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // [START declare_database_ref]
    private DatabaseReference mDatabase;

    //Useful Variables


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Games").child("Name").setValue("TestGame");

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

        // here theophane
        Spinner spinner_category = (Spinner) findViewById(R.id.spinner_trivia_category);
        ArrayAdapter<String> adapter_category = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, TriviaQuestion.Categories);
        spinner_category.setAdapter(adapter_category);
        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("NICE", "position : " + position );
                Log.v("NICE", "Category is : " + TriviaQuestion.getCategoryTextFromInt(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.v("NICE", "Nothing selected" );
            }
        });

    }

    public void clickedEasyButtonXmlCallback(View view) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Games").child("Name").child("Difficulty").setValue("easy");

        View button1View = findViewById(R.id.button_easy);
        button1View.setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        View button2View = findViewById(R.id.button_medium);
        button2View.setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        View button3View = findViewById(R.id.button_hard);
        button3View.setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clickedMediumButtonXmlCallback(View view) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Games").child("Name").child("Difficulty").setValue("medium");

        View button1View = findViewById(R.id.button_easy);
        button1View.setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        View button2View = findViewById(R.id.button_medium);
        button2View.setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        View button3View = findViewById(R.id.button_hard);
        button3View.setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clickedHardButtonXmlCallback(View view) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Games").child("Name").child("Difficulty").setValue("hard");

        View button1View = findViewById(R.id.button_easy);
        button1View.setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        View button2View = findViewById(R.id.button_medium);
        button2View.setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        View button3View = findViewById(R.id.button_hard);
        button3View.setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
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
