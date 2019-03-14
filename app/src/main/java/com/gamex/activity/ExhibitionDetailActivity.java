package com.gamex.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.adapters.EventDetailTabAdapter;
import com.gamex.models.CompanyInExhibition;
import com.gamex.models.Exhibition;
import com.gamex.services.network.BaseCallBack;
import com.gamex.services.network.CheckInternetTask;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Response;

@SuppressWarnings("FieldCanBeLocal")
public class ExhibitionDetailActivity extends AppCompatActivity {
    @Inject
    @Named("cache")
    DataService dataService;
    @Inject
    Picasso picasso;
    @Inject
    SharedPreferences sharedPreferences;

    Call<Exhibition> call;
    private Exhibition exhibitionDetails;

    private final String TAG = ExhibitionDetailActivity.class.getSimpleName();
    private String accessToken;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    private TextView txtExName, txtNoInternet, txtLoading;
    private String exId, exName, exImg;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private ImageView imgEx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((GamexApplication) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_event_detail);

        accessToken = "Bearer " + sharedPreferences.getString(Constant.PREF_ACCESS_TOKEN, "");

        // bind
        mappingViewElement();
        setOnEvent(toolbar);
        getSaveDataFromIntent(); // ex name, img, id from intent -> set to view
        // TODO real data
        checkInternet();

        //TODO test data here
//        List<CompanyInExhibition> listCompany = new ArrayList<>();
//        listCompany.add(new CompanyInExhibition(123, "ADPEX", "---"));
//        listCompany.add(new CompanyInExhibition(123, "UBM VIETNAM", "---"));
//        listCompany.add(new CompanyInExhibition(123, "FSoft", "---"));
//        listCompany.add(new CompanyInExhibition(123, "Global Expo", "---"));
//        listCompany.add(new CompanyInExhibition(123, "CECT,BEXCO", "---"));
//        listCompany.add(new CompanyInExhibition(123, "Automobile Manufacturers’ Association (VAMA) ", "---"));
//        Exhibition exhibitionTest = new Exhibition(
//                "id-1",
//                "TELEFILM 2019 / ICTCOMM 2019",
//                "- Vietnam International Exhibition On Film And Television Technology.\n" +
//                        "- The 2nd Vietnam International Exhibition on Products, Services of Telecommuniction, Information Technology & Communication",
//                "Rm.G3, Ground Floor, Fosco Building, No.6 Phung Khac Khoan, Dakao Ward, District 1, HoChiMinh City, Vietnam",
//                " June 6th",
//                "June 8th ",
//                "--- logo",
//                listCompany
//                );
//        ViewPager viewPager = findViewById(R.id.event_detail_viewpager);
//        EventDetailTabAdapter adapter = new EventDetailTabAdapter(getSupportFragmentManager(), exhibitionTest);
//        viewPager.setAdapter(adapter);
//        TabLayout tabLayout = findViewById(R.id.event_detail_tablayout);
//        tabLayout.setupWithViewPager(viewPager);
    }

    private void checkInternet() {
        new CheckInternetTask(internet -> {
            if (internet) {
                Log.i(TAG, "Has Internet Connection");
                callAPI();
            } else {
                Log.i(TAG, "No Internet Connection");
                txtNoInternet.setVisibility(View.VISIBLE);
                txtLoading.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void callAPI() {
        call = dataService.getExhibitionDetails(accessToken, exId);
        call.enqueue(new BaseCallBack<Exhibition>(this) {
            @Override
            public void onSuccess(Call<Exhibition> call, Response<Exhibition> response) {
                Log.i(TAG, response.toString());
                if (response.isSuccessful()) {
                    exhibitionDetails = response.body();
                    progressBar.setVisibility(View.GONE);
                    txtLoading.setVisibility(View.GONE);
                    Log.i(TAG, response.toString());
                    addTabAdapter();
                } else {
                    Log.i(TAG, response.toString());
                    Toast.makeText(ExhibitionDetailActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Exhibition> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.i(TAG, "Cancel HTTP request on onFailure()");
                } else {
                    Toast.makeText(ExhibitionDetailActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, t.getMessage(), t.fillInStackTrace());
                }
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }
        });
    }

    private void addTabAdapter() {
        ViewPager viewPager = findViewById(R.id.event_detail_viewpager);
        EventDetailTabAdapter adapter = new EventDetailTabAdapter(getSupportFragmentManager(), exhibitionDetails);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.event_detail_tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setOnEvent(Toolbar toolbar) {
        // add back button
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        appBarLayout.addOnOffsetChangedListener((AppBarLayout appBarLayout, int verticalOffset) -> {
            invalidateOptionsMenu();
        });
    }

    private void getSaveDataFromIntent() {
        // TODO get exhibition id, name and logo url from Intent extra -> DONE
        exName = getIntent().getStringExtra("EXTRA_EX_NAME");
        exId = getIntent().getStringExtra("EXTRA_EX_ID");
        exImg = getIntent().getStringExtra("EXTRA_EX_IMG");

        txtExName = findViewById(R.id.txtExName);
        txtExName.setText(exName);
        picasso.load(exImg)
                .placeholder((R.color.bg_grey))
                .error(R.drawable.exhibition_cover)
                .into(imgEx);
    }

    private void mappingViewElement() {
        toolbar = findViewById(R.id.event_detail_toolbar);
        appBarLayout = findViewById(R.id.event_detail_appbar);
        collapsingToolbar = findViewById(R.id.event_detail_collapse_toolbar);
        collapsingToolbar.setContentScrim(getResources().getDrawable(R.drawable.color_gradient_light));
        txtNoInternet = findViewById(R.id.event_txt_no_internet);
        txtLoading = findViewById(R.id.event_txt_loading);
        progressBar = findViewById(R.id.event_progress_bar);
        imgEx = findViewById(R.id.event_detail_img);

        txtLoading.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
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
                processBookmark();
                item.setIcon(getDrawable(R.drawable.ic_star_fill));
        }
        return super.onOptionsItemSelected(item);
    }

    private void processBookmark() {
        // TODO bookmark
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
