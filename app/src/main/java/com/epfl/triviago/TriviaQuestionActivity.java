package com.epfl.triviago;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

public class TriviaQuestionActivity extends AppCompatActivity {

    private final String question = "{\"response_code\":0,\"results\":[{\"category\":\"General Knowledge\",\"type\":\"multiple\",\"difficulty\":\"easy\",\"question\":\"What is the Zodiac symbol for Gemini?\",\"correct_answer\":\"Twins\",\"incorrect_answers\":[\"Fish\",\"Scales\",\"Maiden\"]}]}";

    private Integer mResponse_index = -1;
    private TriviaQuestion mTrivia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_question);

        // get intent for settings

        // launch service
//        Intent intent = new Intent(this, OTDBService.class);
//        intent.setAction(OTDBService.ACTION_GET_QUESTION);
//        intent.putExtra(OTDBService.DIFFICULTY_PARAM, "easy");
//        intent.putExtra(OTDBService.CATEGORY_PARAM, 9); // General knowledge
//        startService(intent);
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
            View layout = findViewById(R.id.triviaParentLayout);
            if (mResponse_index == 1) {
                layout.setBackgroundColor(getResources().getColor(R.color.correct_answer));
            }
            else {
                layout.setBackgroundColor(getResources().getColor(R.color.wrong_answer));
            }
        }
    }
}