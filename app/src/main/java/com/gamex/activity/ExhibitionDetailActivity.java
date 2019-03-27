package com.gamex.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.adapters.EventDetailTabAdapter;
import com.gamex.models.Exhibition;
import com.gamex.services.network.BaseCallBack;
import com.gamex.services.network.CheckInternetTask;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
    Call<ResponseBody> callBookmark;
    Call<ResponseBody> callRemoveBookmark;
    private boolean isFreeToRequest = true;

    private Exhibition exhibitionDetails;

    private final String TAG = ExhibitionDetailActivity.class.getSimpleName();
    private String accessToken;

    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    private Button btnTryAgain;

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

        mappingViewElement();
        setOnEvent(toolbar);
        getSaveDataFromIntent(); // ex name, img, id from intent -> set to view

        checkInternet();
    }

    private void checkInternet() {
        new CheckInternetTask(internet -> {
            if (internet) {
                Log.i(TAG, "Has Internet Connection");

                txtNoInternet.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.GONE);
                txtLoading.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                callAPI();

            } else {
                Log.i(TAG, "No Internet Connection");

                btnTryAgain.setVisibility(View.VISIBLE);
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
                    addTabAdapter();
                    invalidateOptionsMenu();
//                    changeMenuBookmarkIcon();

                } else {
                    Toast.makeText(ExhibitionDetailActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    btnTryAgain.setVisibility(View.VISIBLE);
                }

                stopLoadingAnimation();
            }

            @Override
            public void onFailure(Call<Exhibition> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.i(TAG, "Cancel HTTP request on onFailure()");
                } else {
                    Toast.makeText(ExhibitionDetailActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, t.getMessage(), t.fillInStackTrace());
                    btnTryAgain.setVisibility(View.VISIBLE);
                }
                stopLoadingAnimation();
            }
        });
    }

    private void stopLoadingAnimation() {
        progressBar.setVisibility(View.GONE);
        txtLoading.setVisibility(View.GONE);
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

        appBarLayout.addOnOffsetChangedListener((AppBarLayout appBarLayout, int verticalOffset) -> invalidateOptionsMenu());
        btnTryAgain.setOnClickListener(v -> checkInternet());
    }

    private void getSaveDataFromIntent() {

        String strFromScanQR = getIntent().getStringExtra(Constant.EXTRA_SCAN_QR_EX_ID);

        if (strFromScanQR != null) {
            exId = strFromScanQR;
        } else {

            exName = getIntent().getStringExtra(Constant.EXTRA_EX_NAME);
            exId = getIntent().getStringExtra(Constant.EXTRA_EX_ID);
            exImg = getIntent().getStringExtra(Constant.EXTRA_EX_IMG);

            txtExName = findViewById(R.id.txtExName);
            txtExName.setText(exName);
            picasso.load(exImg)
                    .placeholder((R.color.bg_grey))
                    .error(R.drawable.exhibition_cover)
                    .into(imgEx);
        }
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

        btnTryAgain = findViewById(R.id.event_detail_refresh);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.mn_item_bookmark);
        if (exhibitionDetails != null) {
            if (exhibitionDetails.getBookmarked()) {
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
                onBackPressed();
//                finish();
                return true;
            case R.id.mn_item_bookmark:
                progressBookmark(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callApiBookmark() {

        Log.i(TAG + "----", exId);
        Log.i(TAG + "----", exhibitionDetails.getExhibitionId());

        progressBar.setVisibility(View.VISIBLE);
        txtLoading.setText(Constant.TXT_SAVING_BOOKMARK);
        txtLoading.setVisibility(View.VISIBLE);

        isFreeToRequest = false;
        callBookmark = dataService.bookmarkExhibition(accessToken, exhibitionDetails.getExhibitionId());
        callBookmark.enqueue(new BaseCallBack<ResponseBody>(this) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, response.message());

                if (response.isSuccessful()) {
                    dialogOnBookmark("All Done", "Bookmark Successful", "OK", SweetAlertDialog.SUCCESS_TYPE);
                    exhibitionDetails.setBookmarked(true);
                    invalidateOptionsMenu();
//                    menuItem.setIcon(R.drawable.ic_star_fill);
                } else {
                    try {
                        dialogOnBookmark("Bookmark Failed", response.errorBody().string(), "Please try again later", SweetAlertDialog.ERROR_TYPE);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e.fillInStackTrace());
                    }
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
            json.put("id", exhibitionDetails.getExhibitionId());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }

        RequestBody request = RequestBody.create
                (okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());

        callRemoveBookmark = dataService.removeBookmarkExhibition(accessToken, request);
        callRemoveBookmark.enqueue(new BaseCallBack<ResponseBody>(this) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, response.message());

                if (response.isSuccessful()) {
                    dialogOnBookmark("All Done", "Bookmark removed!", "OK", SweetAlertDialog.SUCCESS_TYPE);
                    exhibitionDetails.setBookmarked(false);
//                    menuItem.setIcon(R.drawable.ic_star);
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

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            super.onBackPressed();
            finish();
        }

//        super.onBackPressed();
//        finish();
    }

    public void progressBookmark(MenuItem item) {
        if (isFreeToRequest) {
            if (exhibitionDetails.getBookmarked()) {
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

    public void clickToOpenMap(View view) {
        String param = exhibitionDetails.getAddress();

        if (exhibitionDetails.getLng() != null && !exhibitionDetails.getLng().isEmpty()
                && exhibitionDetails.getLat() != null && !exhibitionDetails.getLat().isEmpty()) {
            param = exhibitionDetails.getLat()  +  "," + exhibitionDetails.getLng();
        }

        Uri sampleLink = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + param);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, sampleLink);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
