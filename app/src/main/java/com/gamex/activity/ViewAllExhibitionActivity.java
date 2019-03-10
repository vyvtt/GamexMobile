package com.gamex.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.gamex.R;
import com.gamex.adapters.EndlessRecycleViewAdapter;
import com.gamex.models.Exhibition;

import java.util.ArrayList;

public class ViewAllExhibitionActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private EndlessRecycleViewAdapter adapter;
    private ArrayList<Exhibition> exhibitionList;
    private static final int LOAD_LIMIT = 10;
    private String lastId = "0";
    private boolean itShouldLoadMore = true;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_exhibition);

        toolbar = findViewById(R.id.view_all_toolbar);
        recyclerView = findViewById(R.id.view_all_recycle_view);
        progressBar = findViewById(R.id.progress_bar_load_more);

        toolbar.setTitle("Up-coming Exhibitions");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        exhibitionList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new EndlessRecycleViewAdapter(this, exhibitionList);
        recyclerView.setAdapter(adapter);

        firstLoadData();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        if (itShouldLoadMore) {
                            loadMore();
                        }
                    }

                }
            }
        });
    }

    private void firstLoadData() {
        itShouldLoadMore = false;
        // test
        new Handler().postDelayed(() -> {
            itShouldLoadMore = true;
            int i = 0;
            while (i < 10) {
                Exhibition tmp = new Exhibition(
                        "id " + i,
                        "Exhibition " + i,
                        null,
                        "Address " + i,
                        "Start date",
                        "End date",
                        "logo",
                        null,
                        null,
                        null);
                i++;
//                lastId = tmp.getExhibitionId();
                exhibitionList.add(tmp);
                adapter.notifyDataSetChanged();
            }
        }, 5000);

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
        itShouldLoadMore = false;
        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            itShouldLoadMore = true;
            int i = exhibitionList.size();
            int end = exhibitionList.size() + 10;
            while (i < end) {
                Exhibition tmp = new Exhibition(
                        "id " + i,
                        "Exhibition " + i,
                        null,
                        "Address " + i,
                        "Start date",
                        "End date",
                        "logo",
                        null,
                        null,
                        null);
                i++;
                lastId = tmp.getExhibitionId();
                exhibitionList.add(tmp);
                adapter.notifyDataSetChanged();
            }
            progressBar.setVisibility(View.GONE);
        }, 5000);

//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                progressWheel.setVisibility(View.GONE);
//
//                // since volley has completed and it has our response, now let's update
//                // itShouldLoadMore
//
//                itShouldLoadMore = true;
//
//                if (response.length() <= 0) {
//                    // we need to check this, to make sure, our dataStructure JSonArray contains
//                    // something
//                    Toast.makeText(MainActivity.this, "no data available", Toast.LENGTH_SHORT).show();
//                    return; // return will end the program at this point
//                }
//
//                for (int i = 0; i < response.length(); i++) {
//                    try {
//                        JSONObject jsonObject = response.getJSONObject(i);
//
//                        // please note how we have updated the lastId variable
//                        // if there are 4 items for example, and we are ordering in descending order,
//                        // then last id will be 1. This is because outside a loop, we will get the last
//                        // value
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
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressWheel.setVisibility(View.GONE);
//                // volley finished and returned network error, update and unlock  itShouldLoadMore
//                itShouldLoadMore = true;
//                Toast.makeText(MainActivity.this, "Failed to load more, network error", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        Volley.newRequestQueue(this).add(jsonArrayRequest);

    }
}
