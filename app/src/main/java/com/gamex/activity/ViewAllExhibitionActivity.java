package com.gamex.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.adapters.EndlessRecycleViewAdapter;
import com.gamex.models.Exhibition;
import com.gamex.services.network.BaseCallBack;
import com.gamex.services.network.CheckInternetTask;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Response;

public class ViewAllExhibitionActivity extends AppCompatActivity {
    @Inject
    @Named("cache")
    DataService dataService;
    Call<List<Exhibition>> call;
    @Inject
    SharedPreferences sharedPreferences;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private EndlessRecycleViewAdapter adapter;
    private List<Exhibition> exhibitionList;
    private boolean itShouldLoadMore = true;
    private boolean isNoMoreData = false;
    private ProgressBar progressBarFirstLoad;
    private TextView txtLoading, txtNoInternet;
    private String type, accessToken;
    private final String TAG = ViewAllExhibitionActivity.class.getSimpleName() + "----------";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((GamexApplication) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_exhibition);

        mappingViewElement();

        type = getIntent().getStringExtra("VIEW_ALL_TYPE");
        accessToken = "Bearer " + sharedPreferences.getString(Constant.PREF_ACCESS_TOKEN, "");

        if (type.equals(Constant.API_TYPE_ONGOING)) {
            toolbar.setTitle("Ongoing Exhibitions");
        } else if (type.equals(Constant.API_TYPE_UPCOMING)) {
            toolbar.setTitle("Upcoming Exhibitions");
        } else {
            toolbar.setTitle("Exhibitions Near You");
        }
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        exhibitionList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new EndlessRecycleViewAdapter(this, exhibitionList);
        recyclerView.setAdapter(adapter);

        // load 1st time
        new CheckInternetTask(internet -> {
            if (internet) {
                Log.i(TAG, "Has Internet Connection");
                txtNoInternet.setVisibility(View.GONE);
                firstLoadData();
            } else {
                Log.i(TAG, "No Internet Connection");
                txtNoInternet.setVisibility(View.VISIBLE);
                txtLoading.setVisibility(View.GONE);
                progressBarFirstLoad.setVisibility(View.GONE);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    Log.i(TAG, "Scroll down");
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        Log.i(TAG, "can not scroll anymore");
                        if (itShouldLoadMore) {
                            Log.i(TAG, "load more");
                            loadMore();
                        }
                    }
                }
            }
        });
    }

    private void checkInternetFirstLoad() {
        new CheckInternetTask(internet -> {
            if (internet) {
                Log.i(TAG, "Has Internet Connection");
                firstLoadData();
            } else {
                Log.i(TAG, "No Internet Connection");
                txtNoInternet.setVisibility(View.VISIBLE);
                txtLoading.setVisibility(View.GONE);
                progressBarFirstLoad.setVisibility(View.GONE);
            }
        });
    }

    private void mappingViewElement() {
        toolbar = findViewById(R.id.view_all_toolbar);
        recyclerView = findViewById(R.id.view_all_recycle_view);

        txtLoading = findViewById(R.id.view_all_txt_loading);
        txtNoInternet = findViewById(R.id.view_all_txt_no_internet);
        progressBarFirstLoad = findViewById(R.id.view_all_loading);
    }

    private void firstLoadData() {
        txtLoading.setVisibility(View.VISIBLE);
        progressBarFirstLoad.setVisibility(View.VISIBLE);

        if (!isNoMoreData) {
            callAPI();
        }
    }

    private void loadMore() {
        if (!isNoMoreData) {

            exhibitionList.add(null);
            adapter.notifyDataSetChanged();
            callAPI();

        } else {
            Toast.makeText(this, "No more data!", Toast.LENGTH_SHORT).show();
        }
    }

    private void callAPI() {
        itShouldLoadMore = false;

        HashMap<String, Object> apiParam = new HashMap<>();
        apiParam.put("type", type);
        apiParam.put("take", 5);

        if (exhibitionList.isEmpty()) {
            apiParam.put("skip", 0); // no data yet -> take from 0
        } else {
            apiParam.put("skip", exhibitionList.size() - 1); // minus NULL item above
        }

        call = dataService.getExhibitionsList(accessToken, apiParam);
        call.enqueue(new BaseCallBack<List<Exhibition>>(this) {
            @Override
            public void onSuccess(Call<List<Exhibition>> call, Response<List<Exhibition>> response) {

                if (!exhibitionList.isEmpty()) {
                    int position = exhibitionList.size() - 1;
                    adapter.removeAt(position);
                }

                if (response.isSuccessful()) {
                    List<Exhibition> tmp = response.body();
                    Log.i(TAG, response.toString());

                    if (tmp == null || tmp.isEmpty()) {
                        isNoMoreData = true;
                    } else {

                        for (Exhibition exhibition : tmp) {
                            exhibitionList.add(exhibition);
                            adapter.notifyDataSetChanged();
                        }

                        tmp = null;
                    }
                } else {
                    Log.i(TAG, response.toString());
                    Toast.makeText(ViewAllExhibitionActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
                progressBarFirstLoad.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
                itShouldLoadMore = true;
            }

            @Override
            public void onFailure(Call<List<Exhibition>> call, Throwable t) {

                if (!exhibitionList.isEmpty()) {
                    exhibitionList.remove(exhibitionList.size() - 1);
                    adapter.notifyDataSetChanged();
                }

                if (call.isCanceled()) {
                    Log.i(TAG, "Cancel HTTP request on onFailure()");
                } else {
                    Toast.makeText(ViewAllExhibitionActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, t.getMessage());
                }
                progressBarFirstLoad.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
                itShouldLoadMore = true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
