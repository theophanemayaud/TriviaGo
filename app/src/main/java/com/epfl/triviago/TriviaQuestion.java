package com.epfl.triviago;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class TriviaQuestion extends AppCompatActivity {

    private final String question = "{\"response_code\":0,\"results\":[{\"category\":\"General Knowledge\",\"type\":\"multiple\",\"difficulty\":\"easy\",\"question\":\"What is the Zodiac symbol for Gemini?\",\"correct_answer\":\"Twins\",\"incorrect_answers\":[\"Fish\",\"Scales\",\"Maiden\"]}]}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_trivia_question);
    }

    public void answerRadioButtonClicked(View view) {

    }
}