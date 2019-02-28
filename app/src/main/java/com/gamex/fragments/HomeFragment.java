package com.gamex.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.R;
import com.gamex.adapters.HomeRVAdapter;
import com.gamex.models.Exhibition;
import com.gamex.network.GetDataService;
import com.gamex.network.APIClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {
    SwipeRefreshLayout refreshLayout;
    RecyclerView rvOngoing, rvNear, rvYourEvent;
    TextView txtToolBarTitle, txtNoInternet, txtLoading;
    ProgressBar progressBar;
    Call<List<Exhibition>> call;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        txtToolBarTitle = getActivity().findViewById(R.id.toolbar_title);
        progressBar = getActivity().findViewById(R.id.main_progress_bar);
        txtNoInternet = getActivity().findViewById(R.id.main_txt_no_internet);
        txtLoading = getActivity().findViewById(R.id.main_txt_loading);

        txtToolBarTitle.setText("Home");
        progressBar.setVisibility(View.VISIBLE);
        txtLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        refreshLayout = view.findViewById(R.id.swipeToRefresh);
        rvOngoing = view.findViewById(R.id.fg_home_rv_ongoing);
        rvNear = view.findViewById(R.id.fg_home_rv_near);
        rvYourEvent = view.findViewById(R.id.fg_home_rv_your_event);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO refresh
                progressBar.setVisibility(View.VISIBLE);
                txtLoading.setVisibility(View.VISIBLE);
                getExhibitionData();
                // Complete refresh
                refreshLayout.setRefreshing(false);
            }
        });

        getExhibitionData();
        return view;
    }

    private void getExhibitionData() {
        if (APIClient.isOnline()) {
            Toast.makeText(mActivity, "Has Internet Connection", Toast.LENGTH_SHORT).show();
            callAPI();
        } else {
            Toast.makeText(mActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
            txtNoInternet.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            txtLoading.setVisibility(View.GONE);
        }
    }

    private void callAPI() {
        /*Create handle for the RetrofitInstance interface*/
        GetDataService service = APIClient.getRetrofitInstance().create(GetDataService.class);
        call = service.getAllExhibition();
        call.enqueue(new Callback<List<Exhibition>>() {
            @Override
            public void onResponse(Call<List<Exhibition>> call, Response<List<Exhibition>> response) {
                setDataAdapter(response.body());
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Exhibition>> call, Throwable t) {
                if (call.isCanceled()) {
                    Toast.makeText(mActivity, "Call canceled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }
        });
    }

    private void setDataAdapter(List<Exhibition> listExhibition) {
        rvOngoing.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvNear.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvYourEvent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        HomeRVAdapter adapter = new HomeRVAdapter(getContext(), listExhibition);
        rvOngoing.setAdapter(adapter);
        rvNear.setAdapter(adapter);
        rvYourEvent.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Cancel retrofit call when change fragment
        if (call != null ) {
            call.cancel();
            // TODO: delete this, this Toast for test only
            if (call.isCanceled()) {
                Toast.makeText(mActivity, "Call canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
