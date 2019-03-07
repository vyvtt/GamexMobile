package com.gamex.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.gamex.adapters.HomeAdapter;
import com.gamex.models.Exhibition;
import com.gamex.network.CheckInternetTask;
import com.gamex.network.DataService;
import com.gamex.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    @Inject @Named("cache") DataService dataService;
    Call<List<Exhibition>> call;

    private final String TAG = HomeFragment.class.getSimpleName();
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView rvOngoing, rvNear, rvYourEvent;
    private TextView txtToolBarTitle, txtNoInternet, txtLoading;
    private ProgressBar progressBar;
    private FragmentActivity mContext;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        ((GamexApplication) context.getApplicationContext()).getAppComponent().inject(this);
        super.onAttach(context);
        if (context instanceof Activity) {
            mContext = (FragmentActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        mappingViewElement(view);
        setResponseToEvent();
        checkInternet();
        // TODO test
//        List<Exhibition> test = new ArrayList<>();
//        test.add(new Exhibition("id-1", "name 1", "start", "end", "logo"));
//        test.add(new Exhibition("id-2", "name 2", "start", "end", "logo"));
//        test.add(new Exhibition("id-3", "name 3", "start", "end", "logo"));
//        setDataAdapter(test);

        return view;
    }

    private void setResponseToEvent() {
        refreshLayout.setOnRefreshListener(() -> {
            // TODO refresh
            checkInternet();
            // Complete refresh
            refreshLayout.setRefreshing(false);
        });
    }

    private void mappingViewElement(View view) {
        txtToolBarTitle = mContext.findViewById(R.id.main_toolbar_title);
        progressBar = mContext.findViewById(R.id.main_progress_bar);
        txtNoInternet = mContext.findViewById(R.id.main_txt_no_internet);
        txtLoading = mContext.findViewById(R.id.main_txt_loading);
        refreshLayout = view.findViewById(R.id.swipeToRefresh);
        rvOngoing = view.findViewById(R.id.fg_home_rv_ongoing);
        rvNear = view.findViewById(R.id.fg_home_rv_near);
        rvYourEvent = view.findViewById(R.id.fg_home_rv_your_event);
        //set title
        txtToolBarTitle.setText("Home");
    }

    private void checkInternet() {
        new CheckInternetTask(internet -> {
            if (internet) {
                Log.i(TAG, "Has Internet Connection");
                progressBar.setVisibility(View.VISIBLE);
                txtLoading.setVisibility(View.VISIBLE);
                callAPI();
            } else {
                Log.i(TAG, "No Internet Connection");
                txtNoInternet.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }
        });
    }

    private void callAPI() {
        call = dataService.getAllExhibition();
        call.enqueue(new Callback<List<Exhibition>>() {
            @Override
            public void onResponse(Call<List<Exhibition>> call, Response<List<Exhibition>> response) {
                if (response.code() == 200) {
                    setDataAdapter(response.body());
                    progressBar.setVisibility(View.GONE);
                    txtLoading.setVisibility(View.GONE);
                    Log.i(TAG, response.toString());
                } else {
                    Log.i(TAG, response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Exhibition>> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.i(TAG, "Cancel HTTP request on onFailure()");
                } else {
                    Toast.makeText(mContext, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, t.getMessage());
                }
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }
        });
    }

    private void setDataAdapter(List<Exhibition> listExhibition) {
        Log.i(TAG, listExhibition.toString());
        rvOngoing.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvNear.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvYourEvent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        // TODO set data adapter here
        HomeAdapter adapter = new HomeAdapter(getContext(), listExhibition);
        rvOngoing.setAdapter(adapter);
        rvNear.setAdapter(adapter);
        rvYourEvent.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Cancel retrofit call when change fragment
        if (call != null) {
            call.cancel();
            Log.i(Constant.TAG_HOME, "Cancel retrofit request on Fragment stop");
        }
    }
}
