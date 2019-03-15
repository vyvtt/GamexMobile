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
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.adapters.EndlessRvExhibitionAdapter;
import com.gamex.adapters.EndlessRvHistoryAdapter;
import com.gamex.models.Exhibition;
import com.gamex.models.History;
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
public class HistoryFragment extends BaseFragment {
    TextView txtToolBarTitle;

    @Inject
    @Named("cache")
    DataService dataService;
    Call<List<History>> call;
    @Inject
    SharedPreferences sharedPreferences;

    RecyclerView recyclerView;
    EndlessRvHistoryAdapter adapter;

    List<History> dataHistory;
    private boolean itShouldLoadMore = true;
    private boolean isNoMoreData = false;

    private ProgressBar progressBarFirstLoad;
    private TextView txtLoading, txtNoInternet;
    private String accessToken;

    private final String TAG = HistoryFragment.class.getSimpleName();


    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        ((GamexApplication) context.getApplicationContext()).getAppComponent().inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        accessToken = "Bearer " + sharedPreferences.getString(Constant.PREF_ACCESS_TOKEN, "");
        dataHistory = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        mappingViewElement(view);

//        dataHistory.add(new History("1", "Gain 100 points by survey", "24/03/2019 21:00pm"));
//        dataHistory.add(new History("1", "Gain 100 points by survey", "24/03/2019 21:00pm"));
//        dataHistory.add(new History("1", "Gain 100 points by survey", "24/03/2019 21:00pm"));
//        dataHistory.add(new History("1", "Gain 100 points by survey", "24/03/2019 21:00pm"));
//        dataHistory.add(new History("1", "Gain 100 points by survey", "24/03/2019 21:00pm"));
//        dataHistory.add(new History("1", "Gain 100 points by survey", "24/03/2019 21:00pm"));
//        dataHistory.add(new History("1", "Gain 100 points by survey", "24/03/2019 21:00pm"));
//        dataHistory.add(new History("1", "Gain 100 points by survey", "24/03/2019 21:00pm"));
//        dataHistory.add(new History("1", "Gain 100 points by survey", "24/03/2019 21:00pm"));
//        dataHistory.add(new History("1", "Gain 100 points by survey", "24/03/2019 21:00pm"));
//        dataHistory.add(new History("1", "Gain 100 points by survey", "24/03/2019 21:00pm"));
//        dataHistory.add(new History("1", "Gain 100 points by survey", "24/03/2019 21:00pm"));
//        dataHistory.add(new History("1", "Gain 100 points by survey", "24/03/2019 21:00pm"));
//        dataHistory.add(new History("1", "Gain 100 points by survey", "24/03/2019 21:00pm"));
//        dataHistory.add(new History("1", "Gain 100 points by survey", "24/03/2019 21:00pm"));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        adapter = new EndlessRvHistoryAdapter(dataHistory);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // load 1st time
        new CheckInternetTask(internet -> {
            if (internet) {
                txtNoInternet.setVisibility(View.GONE);
                txtLoading.setVisibility(View.VISIBLE);
                progressBarFirstLoad.setVisibility(View.VISIBLE);

                if (!isNoMoreData) {
                    callAPI();
                }
            } else {
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
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        if (itShouldLoadMore) {
                            loadMore();
                        }
                    }
                }
            }
        });
    }

    private void loadMore() {
        if (!isNoMoreData) {

            dataHistory.add(null);
            adapter.notifyDataSetChanged();
            callAPI();

        } else {
            Toast.makeText(mActivity, "No more data!", Toast.LENGTH_SHORT).show();
        }
    }

    private void callAPI() {
        itShouldLoadMore = false;

        HashMap<String, Object> apiParam = new HashMap<>();
        apiParam.put("take", 10);

        if (dataHistory.isEmpty()) {
            apiParam.put("skip", 0); // no data yet -> take from 0
        } else {
            apiParam.put("skip", dataHistory.size() - 1); // minus NULL item above
        }

        call = dataService.getActivityHistory(accessToken, apiParam);
        call.enqueue(new BaseCallBack<List<History>>(mActivity) {
            @Override
            public void onSuccess(Call<List<History>> call, Response<List<History>> response) {

                if (!dataHistory.isEmpty()) {
                    int position = dataHistory.size() - 1;
                    adapter.removeAt(position);
                }

                if (response.isSuccessful()) {
                    List<History> tmp = response.body();
                    Log.i(TAG, response.toString());

                    if (tmp == null || tmp.isEmpty()) {
                        isNoMoreData = true;
                    } else {

                        for (History history : tmp) {
                            dataHistory.add(history);
                            adapter.notifyDataSetChanged();
                        }

                        tmp = null;
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
            public void onFailure(Call<List<History>> call, Throwable t) {

                if (!dataHistory.isEmpty()) {
                    dataHistory.remove(dataHistory.size() - 1);
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mn_item_qr).setVisible(false);
    }

    private void mappingViewElement(View view) {
        txtToolBarTitle = mActivity.findViewById(R.id.main_toolbar_title);
        txtToolBarTitle.setText("Activity History");

        recyclerView = view.findViewById(R.id.history_recycle_view);

        txtLoading = view.findViewById(R.id.history_txt_loading);
        txtNoInternet = view.findViewById(R.id.history_txt_no_internet);
        progressBarFirstLoad = view.findViewById(R.id.history_loading);

    }

}
