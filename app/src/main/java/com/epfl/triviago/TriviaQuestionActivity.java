package com.epfl.triviago;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.epfl.triviago.trivia.TriviaModel;
import com.epfl.triviago.trivia.TriviaResult;
import com.epfl.triviago.trivia.TriviaService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TriviaQuestionActivity extends AppCompatActivity {

    // Tag for Logcat
    private final String TAG = this.getClass().getSimpleName();

    // intent strings
    public static final String INTENT_RESULT = "RESULT";
    public static final String INTENT_QCM_TYPE = "TYPE";
    public static final String INTENT_CATEGORY = "CATEGORY";
    public static final String INTENT_DIFFICULTY = "DIFFICULTY";

    // String Keys
    private static final String TRIVIA_STATE = "TRIVIA_QUESTION";
    private static final String RESPONSE_INDEX = "RESPONSE_INDEX";
    private static final String FINISHED_BOOL = "FINISHED_BOOL";
    private static final String ERROR_BOOL = "ERROR_BOOL";


    private Integer mResponse_index = -1;
    private TriviaQuestion mTrivia = null;
    private boolean mfinished = false;
    private boolean mIsQCM_type = true;
    private Integer intent_cat = 9;
    private String intent_diff = "easy";

    // UI elements
    private View layout_parent;
    private TextView checkAnswer;
    private TextView question;
    private TextView category;
    private TextView difficulty;
    private RadioButton answer_1;
    private RadioButton answer_2;
    private RadioButton answer_3;
    private RadioButton answer_4;
    private ProgressBar loader;
    private Button backButton;
    private Button okButton;
    private RadioGroup radioGroup;
    private boolean mResult = false;
    private boolean merror = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get intent with difficulty, category and type
        Bundle b1 = getIntent().getExtras();
        mIsQCM_type = b1.getBoolean(INTENT_QCM_TYPE);
        intent_cat = b1.getInt(INTENT_CATEGORY);
        intent_diff = b1.getString(INTENT_DIFFICULTY);

        if (mIsQCM_type) {
            setContentView(R.layout.activity_trivia_question);
        } else {
            setContentView(R.layout.activity_trivia_question_vf);
        }

        if (savedInstanceState != null) {
//            Log.e(TAG, "Restart");
            merror = savedInstanceState.getBoolean(ERROR_BOOL);
            mIsQCM_type = savedInstanceState.getBoolean(INTENT_QCM_TYPE);
            if (mIsQCM_type) {
                setContentView(R.layout.activity_trivia_question);
            } else {
                setContentView(R.layout.activity_trivia_question_vf);
            }
            get_views();
            if (merror) {
//                Log.e(TAG, "Hiding all");
                hide_all();
                return;
            }
//            Log.e(TAG, "No errors");
            mTrivia = (TriviaQuestion) savedInstanceState.getSerializable(TRIVIA_STATE);
            mResponse_index = savedInstanceState.getInt(RESPONSE_INDEX);
            mfinished = savedInstanceState.getBoolean(FINISHED_BOOL);
            prepare_view();
            fillView();
            if (mfinished) {
                validate_answer();
            }
        } else {
            // get intent for settings
//        Log.v(TAG, "OK");

            get_views();
            prepare_view();
            build_view();
        }
    }

    private void get_views() {
        layout_parent = findViewById(R.id.triviaParentLayout);
        checkAnswer = findViewById(R.id.checkResponse);
        question = findViewById(R.id.textQuestion);
        answer_1 = findViewById(R.id.answer_1);
        answer_2 = findViewById(R.id.answer_2);
        if (mIsQCM_type) {
            answer_3 = findViewById(R.id.answer_3);
            answer_4 = findViewById(R.id.answer_4);
        }
        category = findViewById(R.id.textCategory);
        difficulty = findViewById(R.id.textDifficulty);
        loader = findViewById(R.id.loading_progressBar);
        backButton = findViewById(R.id.quitButton);
        okButton = findViewById(R.id.validate_answer);
        radioGroup = findViewById(R.id.answer_group);
    }

    private void prepare_view() {
        loader.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.GONE);
        checkAnswer.setBackground(layout_parent.getBackground());
        checkAnswer.setText("");

        question.setText(R.string.loading_question);
        answer_1.setText(R.string.loading_prop);
        answer_2.setText(R.string.loading_prop);
        if (mIsQCM_type) {
            answer_3.setText(R.string.loading_prop);
            answer_4.setText(R.string.loading_prop);
        }
        category.setText(R.string.loading_cat);
        difficulty.setText(R.string.loading_dif);
    }

    private void build_view() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://opentdb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // let retrofit create the implementation
        TriviaService triviaService = retrofit.create(TriviaService.class);
        Call<TriviaModel> call;
        if (mIsQCM_type) {
            call = triviaService.getQCMQuestion(intent_cat.toString(), intent_diff);
        } else {
            call = triviaService.getTFQuestion(intent_cat.toString(), intent_diff);
        }

        call.enqueue(new Callback<TriviaModel>() {
            @Override
            public void onResponse(Call<TriviaModel> call, Response<TriviaModel> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(TriviaQuestionActivity.this, "Error code : " + response.code(), Toast.LENGTH_SHORT).show();
                }
                TriviaModel body = response.body();
                TriviaResult result = body.getResults().get(0);
                String question = result.getQuestion();
                String difficulty = result.getDifficulty();
                String category = result.getCategory();
                List<String> incorrectResponses = result.getIncorrectAnswers();
                String correctResponse = result.getCorrectAnswer();
                String type = result.getType();
                mTrivia = new TriviaQuestion(question, difficulty, category, incorrectResponses, correctResponse, type);
                fillView();
            }

            @Override
            public void onFailure(Call<TriviaModel> call, Throwable t) {
                Toast.makeText(TriviaQuestionActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                // stop progress bar and tell the user it has to check the internet connection
                merror = true;
                hide_all();
            }
        });
    }

    private void fillView() {
        question.setText(Html.fromHtml(mTrivia.mQuestion));
        answer_1.setText(Html.fromHtml(mTrivia.mResponses.get(0)));
        answer_2.setText(Html.fromHtml(mTrivia.mResponses.get(1)));
        if (mIsQCM_type) {
            answer_3.setText(Html.fromHtml(mTrivia.mResponses.get(2)));
            answer_4.setText(Html.fromHtml(mTrivia.mResponses.get(3)));
        }
        category.setText(Html.fromHtml(mTrivia.mCategory));
        difficulty.setText(Html.fromHtml(mTrivia.mDifficulty));
        loader.setVisibility(View.INVISIBLE);
        checkAnswer.setBackground(layout_parent.getBackground());
        checkAnswer.setText(R.string.select_answer);
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
        checkAnswer.setText(R.string.validate_answer);
    }

    public void answerValidateOnClick(View view) {
        // display a toast if there is no radio selected
        if (mResponse_index == -1) {
            Toast.makeText(this, "Please select a proposition", Toast.LENGTH_SHORT).show();
        } else {
            validate_answer();
        }
    }

    private void hide_all() {
        loader.setVisibility(View.INVISIBLE);
        question.setText(R.string.no_internet_error);
        question.setTextColor(getResources().getColor(R.color.red));
        answer_1.setVisibility(View.INVISIBLE);
        answer_2.setVisibility(View.INVISIBLE);
        if (mIsQCM_type) {
            answer_3.setVisibility(View.INVISIBLE);
            answer_4.setVisibility(View.INVISIBLE);
        }
        category.setVisibility(View.INVISIBLE);
        difficulty.setVisibility(View.INVISIBLE);
        okButton.setVisibility(View.INVISIBLE);
        checkAnswer.setVisibility(View.INVISIBLE);
        backButton.setVisibility(View.VISIBLE);
        mfinished = true;
    }

    private void validate_answer() {
        if (mResponse_index == mTrivia.mCorrectIndex) {
            checkAnswer.setBackground(getResources().getDrawable(R.drawable.correct_answer_shape));
            checkAnswer.setText(R.string.correct);
            mResult = true;
        }
        else {
            checkAnswer.setBackground(getResources().getDrawable(R.drawable.incorrect_answer_shape));
            checkAnswer.setText(getString(R.string.wrong) + " " + Html.fromHtml(mTrivia.mResponses.get(mTrivia.mCorrectIndex)));
            mResult = false;
        }
        checkAnswer.setTextColor(getResources().getColor(R.color.white));
        mfinished = true;
        okButton.setVisibility(View.INVISIBLE);
        backButton.setVisibility(View.VISIBLE);
        answer_1.setEnabled(false);
        answer_2.setEnabled(false);
        if (mIsQCM_type) {
            answer_3.setEnabled(false);
            answer_4.setEnabled(false);
        }
    }

    public void backButtonOnClick(View view) {
        if (mfinished) {
            // send score back as an Intent
            Intent intent = new Intent(TriviaQuestionActivity.this, MainActivity.class);
            intent.putExtra(INTENT_RESULT, mResult);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "You did not validate your answer", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        Log.e(TAG, "begin save state");
        if (!merror) {
//            Log.e(TAG, "Save with no errors");
            outState.putSerializable(TRIVIA_STATE, mTrivia);
            outState.putInt(RESPONSE_INDEX, mResponse_index);
            outState.putBoolean(FINISHED_BOOL, mfinished);
        }
//        Log.e(TAG, "Save errors");
        outState.putBoolean(ERROR_BOOL, merror);
        outState.putBoolean(INTENT_QCM_TYPE, mIsQCM_type);
    }
}