package com.gamex.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.R;
import com.gamex.models.ExhibitionDetails;
import com.gamex.network.APIClient;
import com.gamex.network.CheckInternetTask;
import com.gamex.network.GetDataService;
import com.gamex.utils.Constant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExDetailFragment extends BaseFragment {
    private String exhibitionId;
    private TextView txtNoInternet, txtLoading, txtStartDateTop, txtExName, txtStartDate, txtEndDate, txtLocation, txtDescription;
    private ProgressBar progressBar;
    private Call<ExhibitionDetails> call;

    public ExDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBar = getActivity().findViewById(R.id.event_progress_bar);
        txtNoInternet = getActivity().findViewById(R.id.event_txt_no_internet);
        txtLoading = getActivity().findViewById(R.id.event_txt_loading);
        progressBar.setVisibility(View.VISIBLE);
        txtLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            exhibitionId = bundle.getString("exhibitionId");
        } else {
            Log.e(Constant.TAG_FG_EX_DETAIL, "No bundle in ExDetailFragment from Activity");
            Toast.makeText(mActivity, "Something when wrong. Try again later.", Toast.LENGTH_LONG).show();
        }

        View view = inflater.inflate(R.layout.fragment_ex_detail, container, false);

        // TODO delete test data from this line
        txtStartDateTop = view.findViewById(R.id.fg_ex_detail_start_date_top);
        txtExName = view.findViewById(R.id.fg_ex_detail_ex_name);
        txtStartDate = view.findViewById(R.id.fg_ex_detail_start_date);
        txtEndDate = view.findViewById(R.id.fg_ex_detail_end_date);
        txtLocation = view.findViewById(R.id.fg_ex_detail_location);
        txtDescription = view.findViewById(R.id.fg_ex_detail_description);
        ExhibitionDetails details = new ExhibitionDetails(
                "123",
                "VIFA-EXPO 2019",
                "Vietnam International Exhibition On Film And Television Technology.- The 2nd Vietnam International Exhibition on Products, Services of Telecommuniction, Information Technology & Communication",
                "85 Ly Chinh Thang Street, Ward 7, District 3, Ho Chi Minh City, Vietnam",
                "March 6th",
                "March 9th",
                "logo", "abc", "abc"
        );
        setResponseData(details);

        // TODO waiting api to check ------------------------------------------------------->
//        new CheckInternetTask(internet -> {
//           if (internet) {
//               //bind
//               txtStartDateTop = view.findViewById(R.id.fg_ex_detail_start_date_top);
//               txtExName = view.findViewById(R.id.fg_ex_detail_ex_name);
//               txtStartDate = view.findViewById(R.id.fg_ex_detail_start_date);
//               txtEndDate = view.findViewById(R.id.fg_ex_detail_end_date);
//               txtLocation = view.findViewById(R.id.fg_ex_detail_location);
//               txtDescription = view.findViewById(R.id.fg_ex_detail_description);
//               // TODO call api
//               callAPI();
//           } else {
//               progressBar.setVisibility(View.GONE);
//               txtLoading.setVisibility(View.GONE);
//               txtNoInternet.setVisibility(View.VISIBLE);
//           }
//        });

        return view;
    }

    private void callAPI() {
        GetDataService service = APIClient.getRetrofitInstance().create(GetDataService.class);
        call = service.getEXhibitionDetails(exhibitionId);
        call.enqueue(new Callback<ExhibitionDetails>() {
            @Override
            public void onResponse(Call<ExhibitionDetails> call, Response<ExhibitionDetails> response) {
                if (response.code() == 200) {
                    setResponseData(response.body());
                } else {
                    Log.i(Constant.TAG_FG_EX_DETAIL, response.toString());
                }
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ExhibitionDetails> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.i(Constant.TAG_FG_EX_DETAIL, "Cancel HTTP request on onFailure()");
                } else {
                    Toast.makeText(mActivity, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG_FG_EX_DETAIL, t.getMessage());
                }
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }
        });
    }

    private void setResponseData(ExhibitionDetails details) {
        txtStartDateTop.setText(details.getStartDate());
        txtExName.setText(details.getName());
        txtStartDate.setText(details.getStartDate());
        txtEndDate.setText(details.getEndDate());
        txtLocation.setText(details.getAddress());
        txtDescription.setText(details.getDescription());
    }

}
