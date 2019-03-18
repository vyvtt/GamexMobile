package com.gamex.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.adapters.EndlessRvExhibitionAdapter;
import com.gamex.models.Exhibition;
import com.gamex.services.network.BaseCallBack;
import com.gamex.services.network.CheckInternetTask;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourExPastFragment extends BaseFragment {
    @Inject
    @Named("cache")
    DataService dataService;
    Call<List<Exhibition>> call;
    @Inject
    SharedPreferences sharedPreferences;

    private EndlessRvExhibitionAdapter adapter;
    private List<Exhibition> data;
    private boolean itShouldLoadMore = true;
    private boolean isNoMoreData = false;

    private ProgressBar progressBarFirstLoad;
    private TextView txtLoading, txtNoInternet, txtNoData;

    private String accessToken;
    private final String TAG = YourExCurrentFragment.class.getSimpleName();
    RecyclerView recyclerView;

    public YourExPastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        ((GamexApplication) context.getApplicationContext()).getAppComponent().inject(this);
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_your_ex_past, container, false);
        mappingViewElement(view);
        accessToken = "Bearer " + sharedPreferences.getString(Constant.PREF_ACCESS_TOKEN, "");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        data = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        adapter = new EndlessRvExhibitionAdapter(mActivity, data);
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

    private void firstLoadData() {
        txtLoading.setVisibility(View.VISIBLE);
        progressBarFirstLoad.setVisibility(View.VISIBLE);

        if (!isNoMoreData) {
            callAPI();
        }
    }

    private void loadMore() {
        if (!isNoMoreData) {

            data.add(null);
            adapter.notifyDataSetChanged();
            callAPI();

        } else {
            Toast.makeText(mActivity, "No more data!", Toast.LENGTH_SHORT).show();
        }
    }

    private void callAPI() {
        itShouldLoadMore = false;

        HashMap<String, Object> apiParam = new HashMap<>();
        apiParam.put("list", "checked-in");
        apiParam.put("type", "past");
        apiParam.put("take", 5);

        if (data.isEmpty()) {
            apiParam.put("skip", 0); // no data yet -> take from 0
        } else {
            apiParam.put("skip", data.size() - 1); // minus NULL item above
        }


        call = dataService.getExhibitionsList(accessToken, apiParam);
        call.enqueue(new BaseCallBack<List<Exhibition>>(mActivity) {
            @Override
            public void onSuccess(Call<List<Exhibition>> call, Response<List<Exhibition>> response) {

                if (!data.isEmpty()) {
                    int position = data.size() - 1;
                    adapter.removeAt(position);
                }

                if (response.isSuccessful()) {
                    List<Exhibition> tmp = response.body();
                    Log.i(TAG, response.toString());

                    if (tmp == null || tmp.isEmpty()) {
                        isNoMoreData = true;
                    } else {

                        for (Exhibition exhibition : tmp) {
                            data.add(exhibition);
                            adapter.notifyDataSetChanged();
                        }

                        tmp = null;
                    }

                    if (adapter.data.isEmpty()) {
                        txtNoData.setVisibility(View.VISIBLE);
                    } else {
                        txtNoData.setVisibility(View.GONE);
                    }

                } else {
                    Log.i(TAG, response.toString());
                    Toast.makeText(mActivity, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
                progressBarFirstLoad.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
                itShouldLoadMore = true;
            }

            @Override
            public void onFailure(Call<List<Exhibition>> call, Throwable t) {

                if (!data.isEmpty()) {
                    data.remove(data.size() - 1);
                    adapter.notifyDataSetChanged();
                }

                if (call.isCanceled()) {
                    Log.i(TAG, "Cancel HTTP request on onFailure()");
                } else {
                    Toast.makeText(mActivity, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, t.getMessage());
                }
                progressBarFirstLoad.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
                itShouldLoadMore = true;
            }
        });
    }


    private void mappingViewElement(View view) {
        recyclerView = view.findViewById(R.id.your_exhibition_past_recycle_view);
        txtLoading = view.findViewById(R.id.your_exhibition_past_txt_loading);
        txtNoInternet = view.findViewById(R.id.your_exhibition_past_txt_no_internet);
        progressBarFirstLoad = view.findViewById(R.id.your_exhibition_past_loading);

        txtNoData = view.findViewById(R.id.your_exhibition_past_no_data);
    }

}
