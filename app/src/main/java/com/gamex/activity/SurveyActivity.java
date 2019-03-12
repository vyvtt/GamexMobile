package com.gamex.activity;

import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.models.Exhibition;
import com.gamex.services.network.BaseCallBack;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SurveyActivity extends AppCompatActivity {
    @Inject
    @Named("cache")
    DataService dataService;
    Call<ResponseBody> call;
    @Inject
    SharedPreferences sharedPreferences;
    private String accessToken;


    private RelativeLayout layoutOverview;
    private ConstraintLayout layoutMain;
    private LinearLayout optionsLayout;

    private TextView txtQuestion, txtTitle, txtDes, txtPoint, txtCountQuestion;
    private Button btnStart, btnNext, btnPrev;
    private ProgressBar progressBar;

    private SweetAlertDialog sweetAlertDialog;
    private final String TAG = SurveyActivity.class.getSimpleName() + "---------";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((GamexApplication) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        mappingViewElement();
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
        call = dataService.getSurveyDetails(accessToken);
        call.enqueue(new BaseCallBack<ResponseBody>(this) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, response.message());
                if (response.isSuccessful()) {
                    // TODO get data
                    layoutOverview.setVisibility(View.GONE);
                    layoutMain.setVisibility(View.VISIBLE);
                } else {
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setTitleText("Opps ...")
                            .setContentText("Something went wrong")
                            .setConfirmText("Try again later")
                            .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitleText("Opps ...")
                        .setContentText("Can not connect to GamEx server")
                        .setConfirmText("Try again later")
                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
            }
        });
    };

    private View.OnClickListener prevButtonClickListener = v -> {

    };

    private View.OnClickListener nextButtonClickListener = v -> {

    };

    private void getOverviewData() {
        txtTitle.setText(getIntent().getStringExtra(""));
        txtDes.setText(getIntent().getStringExtra(""));
        txtPoint.setText(getIntent().getStringExtra(""));

        accessToken = "Bearer " + sharedPreferences.getString(Constant.PREF_ACCESS_TOKEN,"");
    }
}
