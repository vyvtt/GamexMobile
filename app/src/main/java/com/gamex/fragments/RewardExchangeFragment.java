package com.gamex.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.adapters.RewardExchangeAdapter;
import com.gamex.adapters.RewardTabAdapter;
import com.gamex.models.Reward;
import com.gamex.services.network.BaseCallBack;
import com.gamex.services.network.CheckInternetTask;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RewardExchangeFragment extends BaseFragment implements RewardExchangeAdapter.EventListener {
    @Inject
    @Named("cache")
    DataService dataService;
    @Inject
    SharedPreferences sharedPreferences;
    Call<List<Reward>> callListReward;
    Call<ResponseBody> callPoint;
    Call<ResponseBody> callExchange;

    private String accessToken;
    private final String TAG = RewardExchangeFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private List<Reward> data;
    private RewardExchangeAdapter adapter;
    private int currentPoint;

    private ProgressBar progressBar;
    private TextView txtLoading, txtNoInternet, txtPoint, txtNoReward;
    private SweetAlertDialog sweetAlertDialog;

    public RewardExchangeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_reward_exchange, container, false);
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
                callApiGetListRewards();
                callApiGetPoint();
            } else {
                Log.i(TAG, "No Internet Connection");
                txtNoInternet.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }
        });
    }

    private void callApiGetListRewards() {
        callListReward = dataService.getListRewards(accessToken);
        callListReward.enqueue(new BaseCallBack<List<Reward>>(mActivity) {
            @Override
            public void onSuccess(Call<List<Reward>> call, Response<List<Reward>> response) {
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
            public void onFailure(Call<List<Reward>> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t.fillInStackTrace());
                handleOnFail("Something went wrong", "Try again later");
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }
        });
    }

    private void callApiGetPoint() {
        callPoint = dataService.getRewardPoint(accessToken);
        callPoint.enqueue(new BaseCallBack<ResponseBody>(mActivity) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, response.toString());

                try {
                    if (response.isSuccessful()) {
                        JSONObject json = new JSONObject(response.body().string());
                        int point = json.getInt("point");
                        txtPoint.setText(point + " points");
                        currentPoint = point;
                    } else {
                        Log.e(TAG, "Request not successful");
                        handleOnFail(response.errorBody().string(), "Try again later");
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e.fillInStackTrace());
                }

                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t.fillInStackTrace());
                handleOnFail("Something went wrong", "Try again later");
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }
        });
    }

    private void callApiExchange(int id) {

        callExchange = dataService.exchangeReward(accessToken, id);
        callExchange.enqueue(new BaseCallBack<ResponseBody>(mActivity) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, response.message());

                try {

                    if (response.isSuccessful()) {
                        callApiGetPoint();
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        sweetAlertDialog
                                .setTitleText("Great")
                                .setContentText("Exchange reward successful. \n" +
                                        "Now you can view your reward\n in \"Reward History\" tab")
                                .showCancelButton(false)
                                .setConfirmText("OK")
                                .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation());
                    } else {
                        JSONObject jsonErr = new JSONObject(response.errorBody().string());
                        handleOnFail(jsonErr.getString("message"), "OK");
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e.fillInStackTrace());
                    handleOnFail("Something went wrong", "Try again later");
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t.fillInStackTrace());
                handleOnFail("Something went wrong. Can not exchange reward right now.", "Try again later");
            }
        });

    }

    private void setDataAdapter() {

        if (data == null || data.isEmpty()) {
            txtNoReward.setVisibility(View.VISIBLE);
        } else {
            txtNoReward.setVisibility(View.GONE);

            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
            adapter = new RewardExchangeAdapter(data, this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onClickToExchange(int rewardId, String des, int point) {

        sweetAlertDialog = new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog
                .setTitleText("Exchange Reward")
                .setContentText("Exchange \"" + des + "\" with " + point + " points? (You have " + currentPoint + " points)")
                .setConfirmText("Confirm")
                .setCancelText("Cancel")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                    sweetAlertDialog.setTitleText("Connecting");
                    sweetAlertDialog.showCancelButton(false);
                    callApiExchange(rewardId);
                })
                .setCancelClickListener(SweetAlertDialog::dismissWithAnimation);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

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
        progressBar = view.findViewById(R.id.reward_exchange_loading);
        txtLoading = view.findViewById(R.id.reward_exchange_txt_loading);
        txtNoInternet = view.findViewById(R.id.reward_exchange_txt_no_internet);

        recyclerView = view.findViewById(R.id.reward_exchange_recycle_view);
        txtPoint = view.findViewById(R.id.reward_exchange_point);
        txtNoReward = view.findViewById(R.id.reward_exchange_no_reward);
    }
}
