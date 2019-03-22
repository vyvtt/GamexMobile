package com.gamex.activity;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
    Call<ResponseBody> callBookmark;
    Call<ResponseBody> callRemoveBookmark;

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
    private Button btnTryAgain;
    private LinearLayout layoutMain;

    // info
    private Company company;
    private ImageView imgLogo;
    private TextView txtName, txtWebsite, txtAddress, txtPhone, txtEmail, txtDescription;
    private boolean isFreeToRequest = true;

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

//            callAPICompanyDetails();
            checkInternet();
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnTryAgain.setOnClickListener(v -> {
            checkInternet();
            if (isFromScan) {
                Log.i(TAG, "call api to refresh survey");
                callAPIListSurvey();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "resume");

        // refresh data when user done activity
        if (isFromScan) {
            Log.i(TAG, "call api to refresh survey");
            callAPIListSurvey();
        }
    }

    private void checkInternet() {

        txtLoading.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        txtNoInternet.setVisibility(View.GONE);
        btnTryAgain.setVisibility(View.GONE);
        layoutMain.setVisibility(View.GONE);

        new CheckInternetTask(internet -> {
            if (internet) {
                callAPICompanyDetails();
            } else {
                txtLoading.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                txtNoInternet.setVisibility(View.VISIBLE);
                btnTryAgain.setVisibility(View.VISIBLE);
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
                    invalidateOptionsMenu();

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
                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
        btnTryAgain.setVisibility(View.VISIBLE);
    }

    private void expandListSurvey() {
        layoutSurveyExpand.setVisibility(isExpand ? View.VISIBLE : View.GONE);
        btnExpand.setRotation(isExpand ? 180f : 0f);
        btnExpand.setOnClickListener(v -> onClickButtonExpand(layoutSurveyExpand, btnExpand));
    }

    private void setSurveyListAdapter() {
        rvListSurvey.setHasFixedSize(true);
        rvListSurvey.setLayoutManager(new LinearLayoutManager(getApplication()));
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
        btnTryAgain = findViewById(R.id.company_refresh);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.mn_item_bookmark);
        if (company != null) {
            if (company.getBookmarked()) {
                item.setIcon(R.drawable.ic_star_fill);
            } else {
                item.setIcon(R.drawable.ic_star);
            }
            return super.onPrepareOptionsMenu(menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exhibition_detail_bookmark, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.mn_item_bookmark:
                progressBookmark();
        }
        return super.onOptionsItemSelected(item);
    }

    public void progressBookmark() {
        if (isFreeToRequest) {
            if (company.getBookmarked()) {
                // remove bm
                Log.i(TAG, "remove");
                callApiRemoveBookmark();
            } else {
                // add bm
                Log.i(TAG, "add");
                callApiBookmark();
            }
        } else {
            Log.i(TAG, "Currently process anther call api request");
        }
    }

    private void callApiBookmark() {
        progressBar.setVisibility(View.VISIBLE);
        txtLoading.setText(Constant.TXT_SAVING_BOOKMARK);
        txtLoading.setVisibility(View.VISIBLE);

        isFreeToRequest = false;
        callBookmark = dataService.bookmarkCompany(accessToken, company.getCompanyId());
        callBookmark.enqueue(new BaseCallBack<ResponseBody>(this) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, response.message());

                if (response.isSuccessful()) {
                    dialogOnBookmark("All Done", "Bookmark Successful", "OK", SweetAlertDialog.SUCCESS_TYPE);
                    company.setBookmarked(true);
                    invalidateOptionsMenu();
                } else {
                    dialogOnBookmark("Bookmark Failed", response.message(), "Please try again later", SweetAlertDialog.ERROR_TYPE);
                }

                progressBar.setVisibility(View.GONE);
                txtLoading.setText(Constant.TXT_LOADING);
                txtLoading.setVisibility(View.GONE);
                isFreeToRequest = true;
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogOnBookmark("Bookmark Failed", "Can not connect to GamEx Server", "Please try again later", SweetAlertDialog.ERROR_TYPE);
                progressBar.setVisibility(View.GONE);
                txtLoading.setText(Constant.TXT_LOADING);
                txtLoading.setVisibility(View.GONE);
                isFreeToRequest = true;
            }
        });
    }

    private void callApiRemoveBookmark() {
        progressBar.setVisibility(View.VISIBLE);
        txtLoading.setText(Constant.TXT_REMOVE_BOOKMARK);
        txtLoading.setVisibility(View.VISIBLE);

        isFreeToRequest = false;

        JSONObject json = new JSONObject();
        try {
            json.put("id", company.getCompanyId());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }

        RequestBody request = RequestBody.create
                (okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());

        callRemoveBookmark = dataService.removeBookmarkCompany(accessToken, request);
        callRemoveBookmark.enqueue(new BaseCallBack<ResponseBody>(this) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, response.message());

                if (response.isSuccessful()) {
                    dialogOnBookmark("All Done", "Bookmark removed!", "OK", SweetAlertDialog.SUCCESS_TYPE);
                    company.setBookmarked(false);
                    invalidateOptionsMenu();
                } else {
                    dialogOnBookmark("Remove Bookmark Failed", response.message(), "Please try again later", SweetAlertDialog.ERROR_TYPE);
                }

                progressBar.setVisibility(View.GONE);
                txtLoading.setText(Constant.TXT_LOADING);
                txtLoading.setVisibility(View.GONE);
                isFreeToRequest = true;
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogOnBookmark("Remove Bookmark Failed", "Can not connect to GamEx Server", "Please try again later", SweetAlertDialog.ERROR_TYPE);
                progressBar.setVisibility(View.GONE);
                txtLoading.setText(Constant.TXT_LOADING);
                txtLoading.setVisibility(View.GONE);
                isFreeToRequest = true;
            }
        });
    }

    private void dialogOnBookmark(String title, String content, String confirm, int type) {
        SweetAlertDialog dialog = new SweetAlertDialog(this, type);
        dialog.setTitleText(title)
                .setContentText(content)
                .setConfirmText(confirm)
                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
        dialog.setCancelable(false);
        dialog.show();
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
