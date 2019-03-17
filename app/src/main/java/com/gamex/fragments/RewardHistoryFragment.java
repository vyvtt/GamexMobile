package com.gamex.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.adapters.RewardHistoryAdapter;
import com.gamex.models.RewardHistory;
import com.gamex.services.network.BaseCallBack;
import com.gamex.services.network.CheckInternetTask;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RewardHistoryFragment extends BaseFragment {
    @Inject
    @Named("cache")
    DataService dataService;
    @Inject
    SharedPreferences sharedPreferences;
    Call<List<RewardHistory>> call;
    private List<RewardHistory> data;

    private String accessToken;
    private final String TAG = RewardHistoryFragment.class.getSimpleName();
    private SweetAlertDialog sweetAlertDialog;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView txtLoading, txtNoInternet, txtNoHistory;

    public RewardHistoryFragment() {
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
        View view = inflater.inflate(R.layout.fragment_reward_history, container, false);
        mappingViewElement(view);
        accessToken = "Bearer " + sharedPreferences.getString(Constant.PREF_ACCESS_TOKEN, "");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checkInternet();
    }

    private void checkInternet() {
        new CheckInternetTask(internet -> {
            if (internet) {
                Log.i(TAG, "Has Internet Connection");
                txtNoInternet.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                txtLoading.setVisibility(View.VISIBLE);
                callApiGetRewardHistory();
            } else {
                Log.i(TAG, "No Internet Connection");
                txtNoInternet.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }
        });
    }

    private void callApiGetRewardHistory() {
        call = dataService.getRewardHistory(accessToken);
        call.enqueue(new BaseCallBack<List<RewardHistory>>(mActivity) {
            @Override
            public void onSuccess(Call<List<RewardHistory>> call, Response<List<RewardHistory>> response) {
                Log.i(TAG, response.toString());

                if (response.isSuccessful()) {
                    data = response.body();
                    setDataAdapter();
                } else {
                    Log.e(TAG, "Request not successful");
                    handleOnFail("Something went wrong", "Try again later");
                }

                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<RewardHistory>> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t.fillInStackTrace());
                handleOnFail("Can not connect to GamEx Server", "Try again later");
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }
        });
    }

    private void setDataAdapter() {

        if (data == null || data.isEmpty()) {
            txtNoHistory.setVisibility(View.VISIBLE);
        } else {
            txtNoHistory.setVisibility(View.GONE);

            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
            RewardHistoryAdapter adapter = new RewardHistoryAdapter(data);
            recyclerView.setAdapter(adapter);
        }
    }

    private void handleOnFail(String content, String confirm) {
        if (sweetAlertDialog == null) {
            sweetAlertDialog = new SweetAlertDialog(mActivity, SweetAlertDialog.ERROR_TYPE);
        } else {
            if (sweetAlertDialog.getAlerType() != SweetAlertDialog.ERROR_TYPE) {
                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        }

        sweetAlertDialog
                .setTitleText("Oops ...")
                .setContentText(content)
                .showCancelButton(false)
                .setConfirmText(confirm)
                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
    }

    private void mappingViewElement(View view) {
        recyclerView = view.findViewById(R.id.reward_history_recycle_view);
        progressBar = view.findViewById(R.id.reward_history_loading);
        txtLoading = view.findViewById(R.id.reward_history_txt_loading);
        txtNoInternet = view.findViewById(R.id.reward_history_txt_no_internet);
        txtNoHistory = view.findViewById(R.id.reward_history_no_history);
    }

}
