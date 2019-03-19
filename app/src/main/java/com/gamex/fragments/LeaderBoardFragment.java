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
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.adapters.LeaderBoardAdapter;
import com.gamex.adapters.RewardExchangeAdapter;
import com.gamex.models.LeaderBoard;
import com.gamex.models.Rank;
import com.gamex.services.network.BaseCallBack;
import com.gamex.services.network.CheckInternetTask;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderBoardFragment extends BaseFragment {

    @Inject
    @Named("cache")
    DataService dataService;
    Call<Rank> call;
    @Inject
    SharedPreferences sharedPreferences;

    private RecyclerView recyclerView;
    private LeaderBoardAdapter adapter;
    private Rank rank;
    private List<LeaderBoard> leaderBoards;

    private String accessToken;
    private final String TAG = RewardExchangeFragment.class.getSimpleName();
    private ProgressBar progressBar;
    private TextView txtLoading, txtNoInternet, txtUserRank, txtUserPoint;
    private SweetAlertDialog sweetAlertDialog;


    public LeaderBoardFragment() {
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
        View view = inflater.inflate(R.layout.fragment_leader_board, container, false);

        mappingViewElement(view);
        setHasOptionsMenu(true);

        leaderBoards = new ArrayList<>();
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
                callApiLeaderBoard();
            } else {
                Log.i(TAG, "No Internet Connection");
                txtNoInternet.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }
        });
    }

    private void callApiLeaderBoard() {
        call = dataService.getLeaderBoard(accessToken);
        call.enqueue(new BaseCallBack<Rank>(mActivity) {
            @Override
            public void onSuccess(Call<Rank> call, Response<Rank> response) {
                Log.i(TAG, response.toString());

                if (response.isSuccessful()) {
                    rank = response.body();
                    leaderBoards = rank.getLeaderBoard();
                    setDataAdapter();
                    txtUserRank.setText(Html.fromHtml("<b>Your Rank</b>: " + rank.getCurrentUserRank()));
                    txtUserPoint.setText(Html.fromHtml("<b>Total Points</b>: " + rank.getCurrentUserPoint()));
                } else {
                    Log.e(TAG, "Request not successful");
                    handleOnFail("Something went wrong", "Try again later");
                }

                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Rank> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t.fillInStackTrace());
                handleOnFail("Can not connect to GamEx Server", "Try again later");
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }
        });
    }

    private void setDataAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        adapter = new LeaderBoardAdapter(mActivity, leaderBoards);
        recyclerView.setAdapter(adapter);
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
        txtUserRank = view.findViewById(R.id.fg_rank_user_rank);
        txtUserPoint = view.findViewById(R.id.fg_rank_user_point);

        txtLoading = mActivity.findViewById(R.id.main_txt_loading);
        txtNoInternet = mActivity.findViewById(R.id.main_txt_no_internet);
        progressBar = mActivity.findViewById(R.id.main_progress_bar);

        recyclerView = view.findViewById(R.id.fg_rank_recycle_view);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mn_item_qr).setVisible(false);
    }

}
