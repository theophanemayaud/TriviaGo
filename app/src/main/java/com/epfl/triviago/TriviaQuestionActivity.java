package com.epfl.triviago;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.epfl.triviago.trivia.TriviaModel;
import com.epfl.triviago.trivia.TriviaResult;
import com.epfl.triviago.trivia.TriviaService;

import java.util.List;
import java.util.Random;

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
    public static final String INTENT_MAX_ATTEMPTS = "MAX_ATTEMPTS";
    public static final String INTENT_EFFECTIVE_ATTEMPTS = "EFFECTIVE_ATTEMPTS";

    // String Keys
    private static final String TRIVIA_STATE = "TRIVIA_QUESTION";
    private static final String RESPONSE_INDEX = "RESPONSE_INDEX";
    private static final String FINISHED_BOOL = "FINISHED_BOOL";
    private static final String ERROR_BOOL = "ERROR_BOOL";
    private static final String ATTEMPTS_COUNTER = "ATTEMPTS_COUNTER";

    //Useful variables
    private Integer mResponse_index = -1;
    private TriviaQuestion mTrivia = null;
    private boolean mfinished = false;
    private boolean mIsQCM_type = true;
    private Integer mAttempts_number = 1;
    private Integer intent_cat = 9;
    private Integer intent_cat_no_offset = 0;
    private String intent_diff = "easy";
    private Integer intent_max_attempts;

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
    private Button reloadQ;
    private ConstraintLayout container_cat;
    private ImageView image_cat;
    private ConstraintLayout container_attempt;
    private TextView attempt_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        // in case of an orientation change
        if (savedInstanceState != null) {
            merror = savedInstanceState.getBoolean(ERROR_BOOL);
            mIsQCM_type = savedInstanceState.getBoolean(INTENT_QCM_TYPE);

            if (mIsQCM_type) {
                setContentView(R.layout.activity_trivia_question);
            } else {
                setContentView(R.layout.activity_trivia_question_vf);
            }
            get_views();
            if (merror) {
                hide_all(false);
                return;
            }
            mTrivia = (TriviaQuestion) savedInstanceState.getSerializable(TRIVIA_STATE);
            mResponse_index = savedInstanceState.getInt(RESPONSE_INDEX);
            mfinished = savedInstanceState.getBoolean(FINISHED_BOOL);
            mAttempts_number = savedInstanceState.getInt(ATTEMPTS_COUNTER);
            intent_cat = savedInstanceState.getInt(INTENT_CATEGORY);
            intent_cat_no_offset = intent_cat - TriviaQuestion.TRIVIA_API_ARRAY_OFFSET;
            intent_diff = savedInstanceState.getString(INTENT_DIFFICULTY);
            intent_max_attempts = savedInstanceState.getInt(INTENT_MAX_ATTEMPTS);
            prepare_view();
            fillView();
            if (mfinished) {
                validate_answer();
            }
        } else {
            // get intent with difficulty, category and type
            Bundle b1 = getIntent().getExtras();
            mIsQCM_type = b1.getBoolean(INTENT_QCM_TYPE);
            intent_cat = b1.getInt(INTENT_CATEGORY);
            intent_cat_no_offset = intent_cat - TriviaQuestion.TRIVIA_API_ARRAY_OFFSET;
            intent_diff = b1.getString(INTENT_DIFFICULTY);
            intent_max_attempts = b1.getInt(INTENT_MAX_ATTEMPTS);

            // set appropriate layout
            if (mIsQCM_type) {
                setContentView(R.layout.activity_trivia_question);
            } else {
                setContentView(R.layout.activity_trivia_question_vf);
            }

            get_views();
            prepare_view();
            build_view(false);
        }
    }

    private void get_views() {
        layout_parent = findViewById(R.id.triviaParentLayout);
        container_attempt = findViewById(R.id.container_attempt);
        attempt_text = findViewById(R.id.attemps_counter);
        container_cat = findViewById(R.id.container_cat_diff);
        image_cat = findViewById(R.id.category_image);
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
        reloadQ = findViewById(R.id.reload_random);
    }

    private void prepare_view() {
        loader.setVisibility(View.VISIBLE);
        reloadQ.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        checkAnswer.setVisibility(View.VISIBLE);
        checkAnswer.setText("");
        question.setVisibility(View.VISIBLE);
        question.setText(R.string.loading_question);
        radioGroup.setVisibility(View.VISIBLE);
        answer_1.setEnabled(true);
        answer_2.setEnabled(true);
        answer_1.setChecked(false);
        answer_2.setChecked(false);
        answer_1.setVisibility(View.VISIBLE);
        answer_2.setVisibility(View.VISIBLE);
        answer_1.setText(R.string.loading_prop);
        answer_2.setText(R.string.loading_prop);
        if (mIsQCM_type) {
            answer_3.setEnabled(true);
            answer_4.setEnabled(true);
            answer_3.setChecked(false);
            answer_4.setChecked(false);
            answer_3.setVisibility(View.VISIBLE);
            answer_4.setVisibility(View.VISIBLE);
            answer_3.setText(R.string.loading_prop);
            answer_4.setText(R.string.loading_prop);
        }
        container_attempt.setVisibility(View.INVISIBLE);
        container_cat.setVisibility(View.VISIBLE);
        image_cat.setVisibility(View.VISIBLE);
        image_cat.setImageResource(R.mipmap.loading);
        category.setVisibility(View.VISIBLE);
        category.setText(R.string.loading_cat);
        difficulty.setVisibility(View.VISIBLE);
        difficulty.setText(R.string.loading_dif);
    }

    private void build_view(boolean randQ) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://opentdb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // let retrofit create the implementation
        TriviaService triviaService = retrofit.create(TriviaService.class);
        Call<TriviaModel> call;
        Random rand = new Random();
        if (randQ){
            intent_cat = rand.nextInt(TriviaQuestion.MAX_CATEGORIES)+TriviaQuestion.TRIVIA_API_ARRAY_OFFSET;
            intent_cat_no_offset = intent_cat - TriviaQuestion.TRIVIA_API_ARRAY_OFFSET;
            intent_diff = TriviaQuestion.DIFFICULTY.get(rand.nextInt(TriviaQuestion.MAX_DIFFICULTY));
        }
        String cat = intent_cat.toString();
        if (mIsQCM_type) {
            call = triviaService.getQCMQuestion(cat, intent_diff);
        } else {
            call = triviaService.getTFQuestion(cat, intent_diff);
        }

        call.enqueue(new Callback<TriviaModel>() {
            @Override
            public void onResponse(Call<TriviaModel> call, Response<TriviaModel> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Error code :" + response.code());
                }
                TriviaModel body = response.body();
                Integer response_api = body.getResponseCode();
                if (response_api == 1){
                    Log.e(TAG, "No question found relaunching a request");
                    hide_all(true);
                    return;
                }
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
                hide_all(false);
            }
        });
    }

    private void fillView() {
        String color_hex_code = TriviaQuestion.Bg_colors.get(intent_cat_no_offset);
        int bg_tint = Color.parseColor(color_hex_code);
        layout_parent.setBackground(getResources().getDrawable(R.drawable.tile_background));
        layout_parent.getBackground().setColorFilter(bg_tint, PorterDuff.Mode.ADD);
        radioGroup.setVisibility(View.VISIBLE);
        question.setTextColor(getResources().getColor(R.color.black));
        question.setText(Html.fromHtml(mTrivia.mQuestion));
        answer_1.setVisibility(View.VISIBLE);
        answer_2.setVisibility(View.VISIBLE);
        answer_1.setText(Html.fromHtml(mTrivia.mResponses.get(0)));
        answer_2.setText(Html.fromHtml(mTrivia.mResponses.get(1)));
        if (mIsQCM_type) {
            answer_3.setVisibility(View.VISIBLE);
            answer_4.setVisibility(View.VISIBLE);
            answer_3.setText(Html.fromHtml(mTrivia.mResponses.get(2)));
            answer_4.setText(Html.fromHtml(mTrivia.mResponses.get(3)));
        }
        container_attempt.setVisibility(View.VISIBLE);
        attempt_text.setText(getString(R.string.attempts, mAttempts_number, intent_max_attempts));
        container_cat.setVisibility(View.VISIBLE);
        image_cat.setVisibility(View.VISIBLE);
        image_cat.setImageResource(TriviaQuestion.Cat_icons.get(intent_cat_no_offset));
        category.setVisibility(View.VISIBLE);
        category.setText(Html.fromHtml(mTrivia.mCategory));
        difficulty.setVisibility(View.VISIBLE);
        difficulty.setText(getString(R.string.difficulty, Html.fromHtml(mTrivia.mDifficulty).toString()));
        loader.setVisibility(View.INVISIBLE);
        okButton.setVisibility(View.VISIBLE);
        checkAnswer.setVisibility(View.VISIBLE);
        checkAnswer.setText(R.string.select_answer);
        // TODO: remove this for production
        Toast.makeText(this, "ANSWER : " + mTrivia.mResponses.get(mTrivia.mCorrectIndex), Toast.LENGTH_LONG).show();
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

    private void hide_all(boolean randQ) {
        container_cat.setVisibility(View.INVISIBLE);
        container_attempt.setVisibility(View.INVISIBLE);
        image_cat.setVisibility(View.INVISIBLE);
        loader.setVisibility(View.INVISIBLE);
        radioGroup.setVisibility(View.INVISIBLE);

        if (randQ){
            question.setText(R.string.no_question_found);
            reloadQ.setVisibility(View.VISIBLE);
        } else {
            question.setText(R.string.no_internet_error);
            backButton.setVisibility(View.VISIBLE);
            mfinished = true;
        }
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
    }

    private void validate_answer() {
        if (mResponse_index == mTrivia.mCorrectIndex) {
            checkAnswer.setBackground(getResources().getDrawable(R.drawable.correct_answer_shape));
            checkAnswer.setText(R.string.correct);
            mResult = true;
            mfinished = true;
        }
        else {
            checkAnswer.setBackground(getResources().getDrawable(R.drawable.incorrect_answer_shape));
            checkAnswer.setText(getString(R.string.wrong) + " " + Html.fromHtml(mTrivia.mResponses.get(mTrivia.mCorrectIndex)));
            if (mAttempts_number >= intent_max_attempts){
                mResult = false;
                mfinished = true;
            }
        }
        checkAnswer.setTextColor(getResources().getColor(R.color.white));
        okButton.setVisibility(View.INVISIBLE);
        answer_1.setEnabled(false);
        answer_2.setEnabled(false);
        if (mIsQCM_type) {
            answer_3.setEnabled(false);
            answer_4.setEnabled(false);
        }
        backButton.setVisibility(View.VISIBLE);
        if (mfinished){
            if (mResult) {
                backButton.setText(R.string.next_way_message_trivia);
            } else {
                backButton.setText(R.string.failed_too_much);
            }
        } else {
            backButton.setText(R.string.try_again);
        }
    }

    public void backButtonOnClick(View view) {
        if (mfinished) {
            // send score back as an Intent
            Intent intent = new Intent(TriviaQuestionActivity.this, MainActivity.class);
            intent.putExtra(INTENT_RESULT, mResult);
            intent.putExtra(INTENT_EFFECTIVE_ATTEMPTS, mAttempts_number);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            mAttempts_number += 1;
            prepare_view();
            build_view(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!merror) {
            outState.putSerializable(TRIVIA_STATE, mTrivia);
            outState.putInt(RESPONSE_INDEX, mResponse_index);
            outState.putBoolean(FINISHED_BOOL, mfinished);
            outState.putInt(ATTEMPTS_COUNTER, mAttempts_number);
        }
        outState.putBoolean(ERROR_BOOL, merror);
        outState.putBoolean(INTENT_QCM_TYPE, mIsQCM_type);
        outState.putInt(INTENT_CATEGORY, intent_cat);
        outState.putString(INTENT_DIFFICULTY, intent_diff);
        outState.putInt(INTENT_MAX_ATTEMPTS, intent_max_attempts);
    }

    public void reloadRandomQuestion(View view) {
        prepare_view();
        build_view(true);
        reloadQ.setVisibility(View.GONE);
    }
}