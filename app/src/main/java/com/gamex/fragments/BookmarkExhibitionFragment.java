package com.gamex.fragments;


import android.content.Context;
import android.content.Intent;
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

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.activity.ExhibitionDetailActivity;
import com.gamex.activity.ScanQRActivity;
import com.gamex.adapters.BookmarkExhibitionAdapter;
import com.gamex.models.Bookmark;
import com.gamex.services.network.BaseCallBack;
import com.gamex.services.network.CheckInternetTask;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkExhibitionFragment extends BaseFragment implements BookmarkExhibitionAdapter.EventListener {
    @Inject
    @Named("cache")
    DataService dataService;
    @Inject
    SharedPreferences sharedPreferences;
    Call<List<Bookmark>> callListBookmark;
    Call<ResponseBody> callRemoveBookmark;

    private String accessToken;
    private final String TAG = BookmarkExhibitionFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private List<Bookmark> data;
    private BookmarkExhibitionAdapter adapter;

    private ProgressBar progressBar;
    private TextView txtLoading, txtNoInternet, txtNoData;
    private SweetAlertDialog sweetAlertDialog;

    public BookmarkExhibitionFragment() {
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
        View view = inflater.inflate(R.layout.fragment_bookmark_exhibition, container, false);
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
                callApiGetListBookmark();
            } else {
                Log.i(TAG, "No Internet Connection");
                txtNoInternet.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }
        });
    }

    private void callApiGetListBookmark() {
        callListBookmark = dataService.getListBookmark(accessToken, "exhibition");
        callListBookmark.enqueue(new BaseCallBack<List<Bookmark>>(mActivity) {
            @Override
            public void onSuccess(Call<List<Bookmark>> call, Response<List<Bookmark>> response) {
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
            public void onFailure(Call<List<Bookmark>> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t.fillInStackTrace());
                handleOnFail("Can not connect to GamEx Server", "Try again later");
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }
        });
    }

    private void callApiRemove(String id, int position) {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }

        RequestBody request = RequestBody.create
                (okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());

        callRemoveBookmark = dataService.removeBookmarkExhibition(accessToken, request);
        callRemoveBookmark.enqueue(new BaseCallBack<ResponseBody>(mActivity) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, response.message());

                if (response.isSuccessful()) {
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    sweetAlertDialog.setTitleText("All Done")
                            .setContentText("Bookmark removed!")
                            .setConfirmText("OK")
                            .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
                    adapter.remove(position);

                    if (adapter.data.isEmpty()) {
                        txtNoData.setVisibility(View.VISIBLE);
                    } else {
                        txtNoData.setVisibility(View.GONE);
                    }

                } else {
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setTitleText("Remove Bookmark Failed")
                            .setContentText(response.message())
                            .setConfirmText("Please try again later")
                            .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
                }
                progressBar.setVisibility(View.GONE);
                txtLoading.setText(Constant.TXT_LOADING);
                txtLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitleText("Remove Bookmark Failed")
                        .setContentText("Can not connect to GamEx Server")
                        .setConfirmText("Please try again later")
                        .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);

                progressBar.setVisibility(View.GONE);
                txtLoading.setText(Constant.TXT_LOADING);
                txtLoading.setVisibility(View.GONE);
            }
        });
    }

    private void setDataAdapter() {
        if (data == null || data.isEmpty()) {
            txtNoData.setVisibility(View.VISIBLE);
        } else {
            txtNoData.setVisibility(View.GONE);

            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
            adapter = new BookmarkExhibitionAdapter(data, this);
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
        recyclerView = view.findViewById(R.id.bookmark_ex_recycle_view);
        progressBar = view.findViewById(R.id.bookmark_ex_loading);
        txtLoading = view.findViewById(R.id.bookmark_ex_txt_loading);
        txtNoInternet = view.findViewById(R.id.bookmark_ex_txt_no_internet);
        txtNoData = view.findViewById(R.id.bookmark_ex_no_data);
    }

    @Override
    public void onClickToRemove(String id, String name, int position) {
        sweetAlertDialog = new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog
                .setTitleText("Remove Bookmark?")
                .setContentText("Remove Bookmark for " + name + "?")
                .setConfirmText("Remove")
                .setCancelText("Cancel")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                    sweetAlertDialog.setTitleText("Removing ...");
                    sweetAlertDialog.showCancelButton(false);
                    callApiRemove(id, position);
                })
                .setCancelClickListener(SweetAlertDialog::dismissWithAnimation);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
    }

    @Override
    public void onClickToDetail(String id) {
        Intent intent = new Intent(mActivity, ExhibitionDetailActivity.class);
        intent.putExtra(Constant.EXTRA_SCAN_QR_EX_ID, id);
        startActivity(intent);
    }
}
