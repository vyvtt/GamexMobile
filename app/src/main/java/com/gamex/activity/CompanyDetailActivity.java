package com.gamex.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.adapters.SurveyOverviewAdapter;
import com.gamex.models.Company;
import com.gamex.models.Survey;
import com.gamex.services.network.BaseCallBack;
import com.gamex.services.network.CheckInternetTask;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
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
    private SweetAlertDialog sweetAlertDialog;
    private String companyId;

    private Toolbar toolbar;

    // expand survey list
    private LinearLayout layoutSurvey;
    private RecyclerView rvListSurvey;
    private RelativeLayout btnExpand;
    private LinearLayout layoutSurveyExpand;
    private boolean isExpand = false;
    private TextView txtSurveyNoti;

    // loading
    private ProgressBar progressBar;
    private TextView txtNoInternet, txtLoading;
    private Button btnRefresh;
    private LinearLayout layoutMain;

    // info
    private Company company;
    private ImageView imgLogo;
    private TextView txtName, txtWebsite, txtAddress, txtPhone, txtEmail, txtDescription;

    // survey
    private List<Survey> surveys;
    private boolean isFromScan;
    private String scanResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ((GamexApplication) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_company_detail);

        accessToken = "Bearer " + sharedPreferences.getString(Constant.PREF_ACCESS_TOKEN, "");

        mappingViewElement();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            isFromScan = bundle.getBoolean(Constant.EXTRA_COMPANY_IS_SCAN_SURVEY);

            if (isFromScan) {
                // From Scan
                scanResult = bundle.getString(Constant.EXTRA_SCAN_QR_RESULT);
                companyId = scanResult.substring(scanResult.indexOf("companyId"));
                companyId = companyId.replace("companyId=", "");


            } else {
                // From Exhibition Details -> show normal
                companyId = bundle.getString(Constant.EXTRA_COMPANY_ID);
            }

            callAPICompanyDetails();
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "resume");
        // refresh data when user done activity
        if (isFromScan) {
            Log.i(TAG, "call api to refresh");
            callAPIListSurvey();
        }
    }

    private void checkInternet() {

        txtLoading.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        txtNoInternet.setVisibility(View.GONE);
        btnRefresh.setVisibility(View.GONE);
        layoutMain.setVisibility(View.GONE);

        new CheckInternetTask(internet -> {
            if (internet) {
                callAPICompanyDetails();
            } else {
                txtLoading.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                txtNoInternet.setVisibility(View.VISIBLE);
                btnRefresh.setVisibility(View.VISIBLE);
            }
        });
    }

    private void callAPIListSurvey() {

        HashMap<String, String> apiParams = new HashMap<>();
        String[] params = scanResult.split("&");
        for (String param : params) {
            String[] temp1 = param.split("=");
            apiParams.put(temp1[0], temp1[1]);
        }

        Call<List<Survey>> call = dataService.scanGetSurveys(accessToken, apiParams);
        call.enqueue(new BaseCallBack<List<Survey>>(this) {
            @Override
            public void onSuccess(Call<List<Survey>> call, Response<List<Survey>> response) {
                Log.i(TAG, response.message());

                if (response.isSuccessful()) {

                    Log.i(TAG, "Refresh survey list success");
                    surveys = response.body();
                    // show survey
                    layoutSurvey.setVisibility(View.VISIBLE);
                    txtSurveyNoti.setText(surveys.isEmpty() ? "This company has no survey" : "Total " + surveys.size() + " survey(s) in this exhibition");
                    expandListSurvey();
                    setSurveyListAdapter();

                } else {
                    Log.i(TAG, "Refresh survey list FAIL");
                }
            }

            @Override
            public void onFailure(Call<List<Survey>> call, Throwable t) {
                Log.i(TAG, "Refresh survey list FAIL");
            }
        });
    }

    private void callAPICompanyDetails() {

        call = dataService.getCompanyDetails(accessToken, companyId);
        call.enqueue(new BaseCallBack<Company>(this) {
            @Override
            public void onSuccess(Call<Company> call, Response<Company> response) {
                Log.i(TAG, response.message());

                if (response.isSuccessful()) {

                    layoutMain.setVisibility(View.VISIBLE);
                    company = response.body();
                    loadDataToView();

                } else {
                    handleRequestFail(response.message(), "Please try again later");
                }
                // hide progress loading view
                txtLoading.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Company> call, Throwable t) {
                handleRequestFail("Cannot connect to GamEx server", "Please try again later");
                txtLoading.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void handleRequestFail(String content, String confirm) {
        sweetAlertDialog = new SweetAlertDialog(CompanyDetailActivity.this, SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setTitleText("Oops ...")
                .setContentText(content)
                .setConfirmText(confirm)
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                });
        btnRefresh.setVisibility(View.VISIBLE);
    }

    private void expandListSurvey() {
        layoutSurveyExpand.setVisibility(isExpand ? View.VISIBLE : View.GONE);
        btnExpand.setRotation(isExpand ? 180f : 0f);
        btnExpand.setOnClickListener(v -> onClickButtonExpand(layoutSurveyExpand, btnExpand));
    }

    private void setSurveyListAdapter() {
        // test list survey
        rvListSurvey.setHasFixedSize(true);
        rvListSurvey.setLayoutManager(new LinearLayoutManager(getApplication()));
//        List<Survey> listSurvey = new ArrayList<>();
//        listSurvey.add(new Survey(1, "Survey Build successful!", 30, "requirements of national and international conventions, large scale public and trade exhibitions, corporate meetings and specialized events.", true));
//        listSurvey.add(new Survey(1, "Technology survey?", 100, "requirements of national and international conventions, large scale public and trade exhibitions, corporate meetings and specialized events.", false));
//        listSurvey.add(new Survey(1, "Survey about your life", 99, "requirements of national and international conventions, large scale public and trade exhibitions, corporate meetings and specialized events.", true));
//        listSurvey.add(new Survey(1, "I'm dead inside Survey", 22, "requirements of national and international conventions, large scale public and trade exhibitions, corporate meetings and specialized events.", false));

        rvListSurvey.setAdapter(new SurveyOverviewAdapter(surveys));
    }

    private void mappingViewElement() {
        toolbar = findViewById(R.id.company_detail_toolbar);

        // survey
        layoutSurvey = findViewById(R.id.company_survey_layout);
        btnExpand = findViewById(R.id.company_survey_btnExpand);
        layoutSurveyExpand = findViewById(R.id.company_surveys_expand);
        rvListSurvey = findViewById(R.id.company_survey_list);
        txtSurveyNoti = findViewById(R.id.company_survey_noti);

        // loading
        progressBar = findViewById(R.id.company_progress_bar);
        txtLoading = findViewById(R.id.company_txt_loading);
        txtNoInternet = findViewById(R.id.company_txt_no_internet);
        btnRefresh = findViewById(R.id.company_refresh);
        layoutMain = findViewById(R.id.company_layout_main);

        // info
        txtName = findViewById(R.id.company_name);
        txtWebsite = findViewById(R.id.company_website);
        txtAddress = findViewById(R.id.company_address);
        txtPhone = findViewById(R.id.company_phone);
        txtEmail = findViewById(R.id.company_email);
        txtDescription = findViewById(R.id.company_description);
        imgLogo = findViewById(R.id.company_img);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.mn_item_bookmark:
                // TODO bookmark
                item.setIcon(getDrawable(R.drawable.ic_star_fill));
        }
        return super.onOptionsItemSelected(item);
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
