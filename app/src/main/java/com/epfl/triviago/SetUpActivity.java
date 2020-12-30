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
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // [START declare_database_ref]
    private DatabaseReference mDatabase;

    //Useful Variables
    boolean questionType = false; //FALSE = Multiple choice & TRUE = true/false


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        // [START initialize_database_ref]
        defaultGameSetUp();


        //DatabaseReference myRef = database.getReference("message");
        //myRef.setValue("Hello, World!");


       // Spinner spinner_player = (Spinner) findViewById(R.id.spinner_players_num);
       // ArrayAdapter<CharSequence> adapter_player = ArrayAdapter.createFromResource(this,
       //         R.array.spinner_players, android.R.layout.simple_spinner_item);
       // adapter_player.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       // spinner_player.setAdapter(adapter_player);



        // here theophane
        //Spinner spinner_category = (Spinner) findViewById(R.id.spinner_trivia_category);
        //ArrayAdapter<String> adapter_category = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_spinner_dropdown_item, TriviaQuestion.Categories);
        //spinner_category.setAdapter(adapter_category);
        //spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //    @Override
        //    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //        Log.v("NICE", "position : " + position );
        //        Log.v("NICE", "Category is : " + TriviaQuestion.getCategoryTextFromInt(position));
        //    }

        //    @Override
        //    public void onNothingSelected(AdapterView<?> parent) {
        //        Log.v("NICE", "Nothing selected" );
        //    }
        //});

    }

    public void clickedDoneButtonXmlCallback(View view){
        finish();
    }

    public void defaultGameSetUp(){
        TextView nameGame = findViewById(R.id.nameGame);
        String name = nameGame.getText().toString();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Games").child("Name").setValue(name);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Games").child("Name").child("NumPlayers").setValue("2");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Games").child("Name").child("QuestionType").setValue("QCM");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Games").child("Name").child("Difficulty").setValue("easy");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Games").child("Name").child("MaxAttempts").setValue("3");
    }

    public void clickedQuestionTypeButtonXmlCallback(View view){
        if(questionType = false){
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Games").child("Name").child("QuestionType").setValue("true/false");
            questionType = true;

        }
        else {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Games").child("Name").child("QuestionType").setValue("QCM");
            questionType = false;
        }
    }

    public void clicked1pButtonXmlCallback (View view){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Games").child("Name").child("NumPlayers").setValue("1");

        findViewById(R.id.button_1p).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_2p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_3p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_4p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_5p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked2pButtonXmlCallback (View view){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Games").child("Name").child("NumPlayers").setValue("2");

        findViewById(R.id.button_1p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_2p).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_3p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_4p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_5p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked3pButtonXmlCallback (View view){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Games").child("Name").child("NumPlayers").setValue("3");

        findViewById(R.id.button_1p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_2p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_3p).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_4p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_5p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked4pButtonXmlCallback (View view){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Games").child("Name").child("NumPlayers").setValue("4");

        findViewById(R.id.button_1p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_2p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_3p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_4p).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        findViewById(R.id.button_5p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked5pButtonXmlCallback (View view){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Games").child("Name").child("NumPlayers").setValue("5");

        findViewById(R.id.button_1p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_2p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_3p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_4p).setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        findViewById(R.id.button_5p).setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
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

    public void clicked1AtpButtonXmlCallback(View view) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Games").child("Name").child("MaxAttempts").setValue("1");

        View button1View = findViewById(R.id.button_1atp);
        button1View.setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        View button2View = findViewById(R.id.button_3atp);
        button2View.setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        View button3View = findViewById(R.id.button_5atp);
        button3View.setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked3AtpButtonXmlCallback(View view) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Games").child("Name").child("MaxAttempts").setValue("3");

        View button1View = findViewById(R.id.button_1atp);
        button1View.setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        View button2View = findViewById(R.id.button_3atp);
        button2View.setBackgroundResource(R.drawable.buttonshape_difficulty_selected);
        View button3View = findViewById(R.id.button_5atp);
        button3View.setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
    }

    public void clicked5AtpButtonXmlCallback(View view) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Games").child("Name").child("MaxAttempts").setValue("5");

        View button1View = findViewById(R.id.button_1atp);
        button1View.setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        View button2View = findViewById(R.id.button_3atp);
        button2View.setBackgroundResource(R.drawable.buttonshape_difficulty_unselected);
        View button3View = findViewById(R.id.button_5atp);
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
