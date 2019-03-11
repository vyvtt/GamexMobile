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
    private ProgressBar progressBarLoadMore, progressBarFirstLoad;
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
        progressBarLoadMore = findViewById(R.id.view_all_load_more);

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

//        new Handler().postDelayed(() -> {
//            itShouldLoadMore = true;
//            int i = 0;
//            while (i < 10) {
//                Exhibition tmp = new Exhibition(
//                        "id " + i,
//                        "Exhibition " + i,
//                        null,
//                        "Address " + i,
//                        "Start date",
//                        "End date",
//                        "logo",
//                        null,
//                        null,
//                        null);
//                i++;
////                lastId = tmp.getExhibitionId();
//                exhibitionList.add(tmp);
//                adapter.notifyDataSetChanged();
//            }
//        }, 5000);

        // TODO call API

//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                progressDialog.dismiss();
//                // remember here we are in the main thread, that means,
//                //volley has finished processing request, and we have our response.
//                // What else are you waiting for? update itShouldLoadMore = true;
//                itShouldLoadMore = true;
//
//                if (response.length() <= 0) {
//                    // no data available
//                    Toast.makeText(MainActivity.this, "No data available", Toast.LENGTH_SHORT).show();
//
//                    return;
//                }
//
//                for (int i = 0; i < response.length(); i++) {
//                    try {
//                        JSONObject jsonObject = response.getJSONObject(i);
//
//                        // please note this last id how we have updated it
//                        // if there are 4 items for example, and we are ordering in descending order,
//                        // then last id will be 1. This is because outside a loop, we will get the last
//                        // value [Thanks to JAVA]
//
//                        lastId = jsonObject.getString("id");
//                        String title = jsonObject.getString("title");
//                        String description = jsonObject.getString("description");
//
//                        recyclerModels.add(new RecyclerModel(title, description));
//                        recyclerAdapter.notifyDataSetChanged();
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                // please note how we have updated our last id variable which is initially 0 (String)
//                // outside the loop, java will return the last value, so here it will
//                // certainly give us lastId that we need
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // also here, volley is not processing, unlock it should load more
//                itShouldLoadMore = true;
//                progressDialog.dismiss();
//                Toast.makeText(MainActivity.this, "network error!", Toast.LENGTH_SHORT).show();
//                new AlertDialog.Builder(MainActivity.this)
//                        .setMessage(error.toString())
//                        .show();
//            }
//        });
//
//        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void loadMore() {
        progressBarLoadMore.setVisibility(View.VISIBLE);

        if (!isNoMoreData) {
            callAPI();
        }
    }

    private void callAPI() {
        itShouldLoadMore = false;

        HashMap<String, Object> apiParam = new HashMap<>();
        apiParam.put("type", type);
        apiParam.put("take", 5);
        apiParam.put("skip", exhibitionList.size());

        call = dataService.getExhibitionsList(accessToken, apiParam);
        call.enqueue(new BaseCallBack<List<Exhibition>>(this) {
            @Override
            public void onSuccess(Call<List<Exhibition>> call, Response<List<Exhibition>> response) {
                if (response.code() == 200) {
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
                progressBarLoadMore.setVisibility(View.GONE);
                itShouldLoadMore = true;
            }

            @Override
            public void onFailure(Call<List<Exhibition>> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.i(TAG, "Cancel HTTP request on onFailure()");
                } else {
                    Toast.makeText(ViewAllExhibitionActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, t.getMessage());
                }
                progressBarFirstLoad.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
                progressBarLoadMore.setVisibility(View.GONE);
                itShouldLoadMore = true;
            }
        });
    }

//    private void loadMore() {
//        itShouldLoadMore = false;
//        progressBarLoadMore.setVisibility(View.VISIBLE);
//
//        new Handler().postDelayed(() -> {
//            itShouldLoadMore = true;
//            int i = exhibitionList.size();
//            int end = exhibitionList.size() + 10;
//            while (i < end) {
//                Exhibition tmp = new Exhibition(
//                        "id " + i,
//                        "Exhibition " + i,
//                        null,
//                        "Address " + i,
//                        "Start date",
//                        "End date",
//                        "logo",
//                        null,
//                        null,
//                        null);
//                i++;
//                lastId = tmp.getExhibitionId();
//                exhibitionList.add(tmp);
//                adapter.notifyDataSetChanged();
//            }
//            progressBarLoadMore.setVisibility(View.GONE);
//        }, 5000);
//
////        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
////            @Override
////            public void onResponse(JSONArray response) {
////                progressWheel.setVisibility(View.GONE);
////
////                // since volley has completed and it has our response, now let's update
////                // itShouldLoadMore
////
////                itShouldLoadMore = true;
////
////                if (response.length() <= 0) {
////                    // we need to check this, to make sure, our dataStructure JSonArray contains
////                    // something
////                    Toast.makeText(MainActivity.this, "no data available", Toast.LENGTH_SHORT).show();
////                    return; // return will end the program at this point
////                }
////
////                for (int i = 0; i < response.length(); i++) {
////                    try {
////                        JSONObject jsonObject = response.getJSONObject(i);
////
////                        // please note how we have updated the lastId variable
////                        // if there are 4 items for example, and we are ordering in descending order,
////                        // then last id will be 1. This is because outside a loop, we will get the last
////                        // value
////
////                        lastId = jsonObject.getString("id");
////                        String title = jsonObject.getString("title");
////                        String description = jsonObject.getString("description");
////
////                        recyclerModels.add(new RecyclerModel(title, description));
////                        recyclerAdapter.notifyDataSetChanged();
////
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
////                }
////
////            }
////        }, new Response.ErrorListener() {
////            @Override
////            public void onErrorResponse(VolleyError error) {
////                progressWheel.setVisibility(View.GONE);
////                // volley finished and returned network error, update and unlock  itShouldLoadMore
////                itShouldLoadMore = true;
////                Toast.makeText(MainActivity.this, "Failed to load more, network error", Toast.LENGTH_SHORT).show();
////
////            }
////        });
////
////        Volley.newRequestQueue(this).add(jsonArrayRequest);
//
//    }

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
