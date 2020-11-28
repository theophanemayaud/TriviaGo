package com.epfl.triviago;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

public class TriviaQuestionActivity extends AppCompatActivity {

    private final String question = "{\"response_code\":0,\"results\":[{\"category\":\"General Knowledge\",\"type\":\"multiple\",\"difficulty\":\"easy\",\"question\":\"What is the Zodiac symbol for Gemini?\",\"correct_answer\":\"Twins\",\"incorrect_answers\":[\"Fish\",\"Scales\",\"Maiden\"]}]}";

    private Integer mResponse_index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_question);


    }

    public void answerRadioButtonClicked(View view) {
        // is it clicked ?
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.answer_1:
                if (checked) {
                    mResponse_index = 0;
                }
                break;
            case R.id.answer_2:
                if (checked) {
                    mResponse_index = 1;
                }
                break;
            case R.id.answer_3:
                if (checked) {
                    mResponse_index = 2;
                }
                break;
            case R.id.answer_4:
                if (checked) {
                    mResponse_index = 3;
                }
                break;
        }
    }

    public void answerValidateOnClick(View view) {
        // display a toast if there is no radio selected
        if (mResponse_index == -1) {
            Toast.makeText(this, "Please select a proposition", Toast.LENGTH_SHORT).show();
        } else {
            // put here the logic to validate the question
        }
    }
}