package com.gamex.activity;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.adapters.SurveyOverviewAdapter;
import com.gamex.models.Company;
import com.gamex.models.Exhibition;
import com.gamex.models.Survey;
import com.gamex.models.SurveyTest;
import com.gamex.services.network.BaseCallBack;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Response;

public class CompanyDetailActivity extends AppCompatActivity {
    @Inject
    @Named("cache")
    DataService dataService;
    @Inject
    Picasso picasso;
    @Inject
    SharedPreferences sharedPreferences;
    Call<Company> call;
    private final String TAG = CompanyDetailActivity.class.getSimpleName();
    private String accessToken;
    private String companyId;

    private Toolbar toolbar;

    // expand survey list
    private RecyclerView rvListSurvey;
    private RelativeLayout btnExpand;
    private LinearLayout expandableLayout;
    private boolean isExpand = false;

    // loading
    private ProgressBar progressBar;
    private TextView txtNoInternet, txtLoading;

    // info
    private Company company;
    private ImageView imgLogo;
    private TextView txtName, txtWebsite, txtAddress, txtPhone, txtEmail, txtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((GamexApplication) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_company_detail);

        accessToken = sharedPreferences.getString("Bearer " + Constant.PREF_ACCESS_TOKEN, "");

        mappingViewElement();

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        expandListSurvey();
        setSurveyListAdapter();
    }

    private void callAPI() {
        // TODO company id
        call = dataService.getCompanyDetails(accessToken, companyId);
        call.enqueue(new BaseCallBack<Company>(this) {
            @Override
            public void onSuccess(Call<Company> call, Response<Company> response) {

            }

            @Override
            public void onFailure(Call<Company> call, Throwable t) {

            }
        });
    }

    private void expandListSurvey() {
        expandableLayout.setVisibility(isExpand ? View.VISIBLE : View.GONE);
        btnExpand.setRotation(isExpand ? 180f : 0f);
        btnExpand.setOnClickListener(v -> onClickButtonExpand(expandableLayout, btnExpand));
    }

    private void setSurveyListAdapter() {
        // test list survey
        rvListSurvey.setHasFixedSize(true);
        rvListSurvey.setLayoutManager(new LinearLayoutManager(getApplication()));

        List<Survey> listSurvey = new ArrayList<>();
        listSurvey.add(new Survey(1, "Survey Build successful!", 30, "requirements of national and international conventions, large scale public and trade exhibitions, corporate meetings and specialized events.", true));
        listSurvey.add(new Survey(1, "Technology survey?", 100, "requirements of national and international conventions, large scale public and trade exhibitions, corporate meetings and specialized events.", false));
        listSurvey.add(new Survey(1, "Survey about your life", 99, "requirements of national and international conventions, large scale public and trade exhibitions, corporate meetings and specialized events.", true));
        listSurvey.add(new Survey(1, "I'm dead inside Survey", 22, "requirements of national and international conventions, large scale public and trade exhibitions, corporate meetings and specialized events.", false));

        rvListSurvey.setAdapter(new SurveyOverviewAdapter(listSurvey));
    }

    private void mappingViewElement() {
        toolbar = findViewById(R.id.company_detail_toolbar);

        // survey
        btnExpand = findViewById(R.id.company_survey_btnExpand);
        expandableLayout = findViewById(R.id.company_survey_layout);
        rvListSurvey = findViewById(R.id.company_survey_list);

        // loading
        progressBar = findViewById(R.id.company_progress_bar);
        txtLoading = findViewById(R.id.company_txt_loading);
        txtNoInternet = findViewById(R.id.company_txt_no_internet);

        // info
        txtName = findViewById(R.id.company_name);
        txtWebsite = findViewById(R.id.company_website);
        txtAddress = findViewById(R.id.company_address);
        txtPhone = findViewById(R.id.company_phone);
        txtEmail = findViewById(R.id.company_email);
        txtDescription = findViewById(R.id.company_description);
    }

    private void loadDataToView() {
        txtName.setText(company.getName());
        txtWebsite.setText(company.getWebsite());
        txtAddress.setText(company.getAddress());
        txtPhone.setText(company.getPhone());
        txtEmail.setText(company.getEmail());
        txtDescription.setText(company.getDescription());

        picasso.load(company.getLogo())
                .placeholder((R.color.bg_grey))
                .error(R.color.bg_grey)
                .into(imgLogo);
    }

    private void onClickButtonExpand(final LinearLayout expandableLayout, final RelativeLayout btnToExpanlayout) {
        //Simply set View to Gone if not expanded
        //Rotation on button layout
        if (expandableLayout.getVisibility() == View.VISIBLE) {
            createRotateAnimator(btnToExpanlayout, 180f, 0f).start();
            expandableLayout.setVisibility(View.GONE);
            isExpand = false;
        } else {
            createRotateAnimator(btnToExpanlayout, 0f, 180f).start();
            expandableLayout.setVisibility(View.VISIBLE);
            isExpand = true;
        }
    }

    //Code to rotate button
    private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }
}
