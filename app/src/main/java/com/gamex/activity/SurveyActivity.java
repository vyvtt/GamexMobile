package com.gamex.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.models.ProposedAnswer;
import com.gamex.models.Question;
import com.gamex.models.QuestionType;
import com.gamex.models.Survey;
import com.gamex.services.network.BaseCallBack;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SurveyActivity extends AppCompatActivity {
    public static final String QUESTION_NUMBER = "QUESTION_NUMBER";
    public static final String QUESTIONS = "QUESTIONS";
    public static final String SURVEY_TITLE = "SURVEY_TITLE";
    public static final String SURVEY_DESCRIPTION = "SURVEY_DESCRIPTION";
    public static final String SURVEY_POINT = "SURVEY_POINT";

    @Inject
    @Named("cache")
    DataService dataService;
    Call<Survey> call;
    @Inject
    SharedPreferences sharedPreferences;
    private String accessToken;

    private RelativeLayout layoutOverview;
    private RelativeLayout layoutMain;
    private LinearLayout optionsLayout;

    private TextView txtQuestion, txtTitle, txtDes, txtPoint, txtCountQuestion;
    private Button btnStart, btnNext, btnPrev;
    private ProgressBar progressBar;
    private Toast toast;

    private SweetAlertDialog sweetAlertDialog;
    private final String TAG = SurveyActivity.class.getSimpleName() + "---------";
    private String surveyTitle, surveyDescription, surveyPoint;
    private int surveyId;

    private Survey survey;
    private int curQuestionNumber;
    private int totalQuestions;
    private List<Question> listQuestions;
    private boolean isAnswered;

    private QuestionType optionsType;
    private View optionsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((GamexApplication) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        //init value
        if (listQuestions == null) {
            listQuestions = new ArrayList<>();
        }
        accessToken = "Bearer " + sharedPreferences.getString(Constant.PREF_ACCESS_TOKEN, "");

        getDataFromIntent();
        mappingViewElement();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveUserAnswer();

        outState.putInt(QUESTION_NUMBER, curQuestionNumber);
        outState.putSerializable(QUESTIONS, (Serializable) listQuestions);

        outState.putString(SURVEY_DESCRIPTION, surveyDescription);
        outState.putString(SURVEY_POINT, surveyPoint);
        outState.putString(SURVEY_TITLE, surveyTitle);
    }


    /**
     * restore state of the quiz on activity resumes after stop/pause
     * @param savedInstanceState provides access to the data prior to activity resume
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        curQuestionNumber = savedInstanceState.getInt(QUESTION_NUMBER);
        listQuestions = (ArrayList<Question>) savedInstanceState.getSerializable(QUESTIONS);

        surveyDescription = savedInstanceState.getString(SURVEY_DESCRIPTION);
        surveyPoint = savedInstanceState.getString(SURVEY_POINT);
        surveyTitle = savedInstanceState.getString(SURVEY_TITLE);

        if (listQuestions.isEmpty()) {

            layoutOverview.setVisibility(View.VISIBLE);
            layoutMain.setVisibility(View.GONE);
            curQuestionNumber = 0;

            txtTitle.setText(surveyTitle);
            txtDes.setText(surveyDescription);
            txtPoint.setText(Html.fromHtml("Total <b>" + surveyPoint + " points</b> gain when completed."));

        } else {
            layoutOverview.setVisibility(View.GONE);
            layoutMain.setVisibility(View.VISIBLE);

            displayQuestion();
        }
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        surveyId = intent.getIntExtra("SV_ID", -1);
        surveyDescription = intent.getStringExtra("SV_DES");
        surveyPoint = intent.getStringExtra("SV_POINT");
        surveyTitle = intent.getStringExtra("SV_TITLE");

        txtTitle.setText(surveyTitle);
        txtDes.setText(surveyDescription);
        txtPoint.setText(Html.fromHtml("Total <b>" + surveyPoint + " points</b> gain when completed."));
    }

    private void mappingViewElement() {
        // layout
        layoutOverview = findViewById(R.id.survey_overview_layout);
        layoutMain = findViewById(R.id.survey_main_layout);
        optionsLayout = findViewById(R.id.linearLayout_Options);
        // text for overview View
        txtTitle = findViewById(R.id.survey_title);
        txtDes = findViewById(R.id.survey_description);
        txtPoint = findViewById(R.id.survey_point);
        // text for survey
        txtQuestion = findViewById(R.id.question_text);
        txtCountQuestion = findViewById(R.id.questions_remaining);
        progressBar = findViewById(R.id.determinantProgressBar);
        //register the click events for the previous and next buttons
        btnStart = findViewById(R.id.survey_start);
        btnStart.setOnClickListener(startButtonClickListener);
        btnPrev = findViewById(R.id.prev_button);
        btnPrev.setOnClickListener(prevButtonClickListener);
        btnNext = findViewById(R.id.next_button);
        btnNext.setOnClickListener(nextButtonClickListener);
    }

    private View.OnClickListener startButtonClickListener = v -> {
        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Fetching data ...").setCancelable(false);
        sweetAlertDialog.show();

        // TODO call api to get question
        surveyId = 1;
        call = dataService.getSurveyQuestions(accessToken, surveyId);
        call.enqueue(new BaseCallBack<Survey>(this) {
            @Override
            public void onSuccess(Call<Survey> call, Response<Survey> response) {
                Log.i(TAG, response.message());
                if (response.isSuccessful()) {
                    // TODO get data
                    layoutOverview.setVisibility(View.GONE);
                    layoutMain.setVisibility(View.VISIBLE);

                    survey = response.body();

                    listQuestions = survey.getQuestions();
                    totalQuestions = listQuestions.size();
                    curQuestionNumber = 0;
                    progressBar.setMax(totalQuestions);

                    displayQuestion();

                    Log.i(TAG, survey.toString());

                    sweetAlertDialog.dismissWithAnimation();

                } else {
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setTitleText("Opps ...")
                            .setContentText("Something went wrong")
                            .setConfirmText("Try again later")
                            .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
                }
            }

            @Override
            public void onFailure(Call<Survey> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                alertOnFail("Can not connect to GamEx server");
            }
        });
    };

    private View.OnClickListener prevButtonClickListener = v -> {
        saveUserAnswer();
        if (curQuestionNumber > 0) {
            curQuestionNumber--;
            displayQuestion();
        } else {
            alertNoPrevQuestions();
        }
    };

    private View.OnClickListener nextButtonClickListener = v -> {
        saveUserAnswer();

        if (!isAnswered) {
            alertQuestionUnanswered();
            return;
        }

        curQuestionNumber++;
        if (curQuestionNumber < listQuestions.size()) {
            displayQuestion();
        } else {
            curQuestionNumber--;
            displayConfirmAlert("", "Do you want to submit the answers?", false);
        }
    };

    private void saveUserAnswer() {

        if (curQuestionNumber < listQuestions.size()) {

            Question currentQuestion = listQuestions.get(curQuestionNumber);
            ArrayList<Integer> answerSelectId = new ArrayList<>();
            String answerText;

            switch (optionsType) {
                case RADIOBUTTON:
                    //save the selected RadioButton IDs
                    int selectButtonId = ((RadioGroup) optionsView).getCheckedRadioButtonId();
                    RadioButton selectedRadioButton = findViewById(selectButtonId);

                    if (selectedRadioButton == null) { // not select yet
                        return;
                    } else {
                        answerSelectId.add(selectButtonId);
                        currentQuestion.setUserAnswerButtonId(answerSelectId);
                        isAnswered = true;
                    }
                    break;

                case CHECKBOX:
                    //save checkbox IDs that have been checked by the user
                    LinearLayout parentLayout = (LinearLayout) optionsView;
                    int numOfCheckBox = parentLayout.getChildCount();
                    for (int i = 0; i < numOfCheckBox; i++) {
                        CheckBox childCheckBox = (CheckBox) parentLayout.getChildAt(i);
                        if (childCheckBox.isChecked()) {
                            answerSelectId.add(i);
                            isAnswered = true;
                        }
                    }
                    currentQuestion.setUserAnswerButtonId(answerSelectId);
                    break;

                case EDITTEXT:
                    //save the EditText answer
                    EditText edtAnswer = (EditText) optionsView;
                    answerText = edtAnswer.getText().toString();
                    if (!TextUtils.isEmpty(answerText)) {
                        currentQuestion.setUserAnswerText(answerText);
                        isAnswered = true;
                    } else {
                        currentQuestion.setUserAnswerText(null);
                    }
                    break;
            }
        }
    }

    private void displayQuestion() {

        optionsLayout.removeAllViews();

        //display the current question number and total number of listQuestions
        String text = (curQuestionNumber + 1) + "/" + totalQuestions;
        txtCountQuestion.setText(text);

        //update the progress bar status
        progressBar.setProgress(curQuestionNumber + 1);

        if (isAnswered) { // refresh state from previous question
            isAnswered = false;
        }

        Question currentQuestion = listQuestions.get(curQuestionNumber);
        txtQuestion.setText(currentQuestion.getContent());

        //set the button for last question to be 'Submit', rather than 'Next'
        if (curQuestionNumber == listQuestions.size() - 1) {
            btnNext.setText("Submit");
        } else {
            btnNext.setText("Next");
        }

        displayOptions();
    }

    private void displayOptions() {

        //get the current question and it's options
        Question question = listQuestions.get(curQuestionNumber);
        List<ProposedAnswer> proposedAnswers = question.getProposedAnswers();
        QuestionType currentQuestionType = question.getQuestionType();

        switch (currentQuestionType) {

            case RADIOBUTTON:
                //For the case of radio buttons, create a RadioGroup and add each option as a RadioButton
                //to the group - set an ID for each RadioButton to be referred later
                RadioGroup radioGroup = new RadioGroup(this);
                for (int i = 0; i < proposedAnswers.size(); i++) {
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setText(proposedAnswers.get(i).getContent());
                    radioButton.setId(proposedAnswers.get(i).getProposedAnswerId());
                    radioButton.setPadding(0, 40, 0, 40);
                    radioGroup.addView(radioButton);
                }
                optionsLayout.addView(radioGroup);

                //restore saved answers
                if (question.getUserAnswerButtonId() != null && question.getUserAnswerButtonId().size() > 0) {
                    // radio button -> only one answer
                    int total = radioGroup.getChildCount();
                    int previousSelectId = question.getUserAnswerButtonId().get(0);

                    for (int index = 0; index < total; index++) {
                        int curId = radioGroup.getChildAt(index).getId();
                        if (curId == previousSelectId) {
                            RadioButton selectedRadioButton = (RadioButton) radioGroup.getChildAt(index);
                            selectedRadioButton.setChecked(true);
                            break;
                        }
                    }
                }

                optionsView = radioGroup;
                break;


            case CHECKBOX:
                //For the case of check boxes, create a new CheckBox for each option
                for (ProposedAnswer proposedAnswer : proposedAnswers) {
                    CheckBox checkbox = new CheckBox(this);
                    checkbox.setText(proposedAnswer.getContent());
                    checkbox.setId(proposedAnswer.getProposedAnswerId());
                    checkbox.setPadding(0, 40, 0, 40);
                    optionsLayout.addView(checkbox);
                }

                //restore saved answers
                if (question.getUserAnswerButtonId() != null && question.getUserAnswerButtonId().size() > 0) {
                    for (int index : question.getUserAnswerButtonId()) {
                        ((CheckBox) optionsLayout.getChildAt(index)).setChecked(true);
                    }
                }
                optionsView = optionsLayout;
                break;

            case EDITTEXT:
                //For the case of edit text, display an EditText for the user to enter the answer
                EditText editText = new EditText(this);

                //restore saved answers, set hint text if answer is empty/remains unanswered
                if (!TextUtils.isEmpty(question.getUserAnswerText())) {
                    editText.setText(question.getUserAnswerText());
                    editText.setSelection(question.getUserAnswerText().length());
                } else {
                    editText.setHint("Type your answer here");
                }

                optionsLayout.addView(editText);
                optionsView = editText;
                break;
        }
        optionsType = currentQuestionType;
    }

    private void alertQuestionUnanswered() {
        cancelToast();
        toast = Toast.makeText(this, "Did you answer?", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void alertNoPrevQuestions() {
        cancelToast();
        toast = Toast.makeText(this, "This is the first question", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void alertOnFail(String content) {
        if (sweetAlertDialog != null && sweetAlertDialog.isShowing()) {
            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
            sweetAlertDialog.setTitleText("Oops ...")
                    .setContentText(content)
                    .setConfirmText("Try again later")
                    .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
        }
    }

    private void displayConfirmAlert(String title, String message, final boolean isBackPressed) {
        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog
                .setTitleText(title)
                .setContentText(message)
                .setConfirmText("Yes")
                .setCancelText("Cancel")
                .setConfirmClickListener(sweetAlertDialog -> {
                    if (isBackPressed) {
                        finish();
                    } else {
                        // TODO submit
                        Log.i(TAG, "Submit: " + listQuestions.toString());
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        sweetAlertDialog.setTitleText("Gathering data ...");
                        submitAnswer();
                    }
                })
                .setCancelClickListener(sweetAlertDialog -> {
                    if (sweetAlertDialog != null) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
    }

    private void cancelToast() {
        if (toast != null)
            toast.cancel();
    }

    @Override
    public void onBackPressed() {
        displayConfirmAlert("Quit?", "You haven't submit this survey yet. If you quit, All of yours answers will be lost", true);
    }

    private String createJsonResponse() {
        try {
            JSONObject jsonSurvey = new JSONObject();
            JSONArray jsonArraySurveyAnswer = new JSONArray();

            for (Question question : listQuestions) {

                JSONObject tmp = new JSONObject();
                tmp.put("questionId", question.getQuestionId());

                if (question.getQuestionType() == QuestionType.EDITTEXT) {
                    tmp.put("other", question.getUserAnswerText());

                } else if (question.getQuestionType() == QuestionType.RADIOBUTTON) {
                    tmp.put("proposedAnswerId", question.getUserAnswerButtonId().get(0));

                } else {
                    JSONArray jsonArrayProposedAnswer = new JSONArray();
                    for (Integer proposedAnswerId : question.getUserAnswerButtonId()) {
                        jsonArrayProposedAnswer.put(proposedAnswerId);
                    }
                    tmp.put("proposedAnswerIds", jsonArrayProposedAnswer);
                }

                jsonArraySurveyAnswer.put(tmp);
            }

            jsonSurvey.put("surveyId", surveyId);
            jsonSurvey.put("surveyAnswers", jsonArraySurveyAnswer);

            Log.i(TAG, jsonSurvey.toString());
            return jsonSurvey.toString();

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
        return null;
    }

    private void submitAnswer() {
        String jsonAnswers = createJsonResponse();

        if (jsonAnswers == null) {
            alertOnFail("Something went wrong");
        }

        if (sweetAlertDialog != null) {
            sweetAlertDialog.setTitleText("Connecting to Server ...");
        }

        RequestBody answer = RequestBody.create
                (okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonAnswers);
        Call<ResponseBody> call = dataService.submitSurvey(accessToken, answer);

        call.enqueue(new BaseCallBack<ResponseBody>(this) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, response.message());

                try {
                    if (response.isSuccessful()) {

                        String strResponse = response.body().string();
                        JSONObject jsonResponse = new JSONObject(strResponse);
                        int point = jsonResponse.getInt("point");

                        if (sweetAlertDialog != null) {
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            sweetAlertDialog
                                    .setTitleText("Submit successfully")
                                    .setContentText("You gain " + point + " points!")
                                    .setConfirmText("Great")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                            // TODO back to list survey
                                        }
                                    });
                        }

                    } else {
                        alertOnFail(response.message());
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e.fillInStackTrace());
                    alertOnFail("Something went wrong");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                alertOnFail("Can not connect to GamEx server");
            }
        });
    }
}
