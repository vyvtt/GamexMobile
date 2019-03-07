package com.gamex.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.models.Exhibition;
import com.gamex.network.DataService;

import java.io.Serializable;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExDetailFragment extends Fragment {
    @Inject
    @Named("cache")
    Retrofit retrofit;
    private Call<Exhibition> call;

    private final String TAG = ExDetailFragment.class.getSimpleName();
    private String exhibitionId;
    private TextView txtNoInternet, txtLoading, txtStartDateTop, txtExName, txtStartDate, txtEndDate, txtLocation, txtDescription;
    private ProgressBar progressBar;
    private FragmentActivity mContext;
    private Exhibition exhibitionFromActivity, exhibitionSaved;

    public ExDetailFragment() {
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("EXHIBITION_SAVED", (Serializable) exhibitionSaved);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
//            exhibitionId = bundle.getString("exhibitionId");
            exhibitionFromActivity = (Exhibition) bundle.getSerializable("EXHIBITION_DETAILS");
        } else {
            Log.e(TAG, "No bundle from Activity");
            Toast.makeText(mContext, "Something when wrong. Try again later.", Toast.LENGTH_LONG).show();
        }
        View view = inflater.inflate(R.layout.fragment_ex_detail, container, false);
        mappingViewElement(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            // restore of view state that had previously been frozen by onSaveInstanceState(Bundle)
            this.exhibitionSaved = (Exhibition) savedInstanceState.getSerializable("EXHIBITION_SAVED");
            updateDataToView(exhibitionSaved);
        } else {
            if (exhibitionSaved != null) {
                // Returning from backstack, data is fine, do nothing
            } else {
                // fragment create for 1st time -> get from exhibitionFromActivity
                // TODO delete test data from this line
//                Exhibition details = new Exhibition(
//                        "123",
//                        "VIFA-EXPO 2019",
//                        "Vietnam International Exhibition On Film And Television Technology.- The 2nd Vietnam International Exhibition on Products, Services of Telecommuniction, Information Technology & Communication",
//                        "85 Ly Chinh Thang Street, Ward 7, District 3, Ho Chi Minh City, Vietnam",
//                        "March 6th",
//                        "March 9th",
//                        "logo", "abc", "abc", new ArrayList<>()
//                );
                updateDataToView(exhibitionFromActivity);
            }
        }
    }

//    private void callAPI() {
//        DataService service = retrofit.create(DataService.class);
//        call = service.getExhibitionDetails(exhibitionId);
//        call.enqueue(new Callback<Exhibition>() {
//            @Override
//            public void onResponse(Call<Exhibition> call, Response<Exhibition> response) {
//                Exhibition tmp = response.body();
//                if (tmp != null) {
//                    updateDataToView(tmp);
//                    exhibitionSaved = tmp;
//                    Log.i(TAG, response.toString());
//                } else {
//                    Log.i(TAG, response.toString());
//                }
//                progressBar.setVisibility(View.GONE);
//                txtLoading.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onFailure(Call<Exhibition> call, Throwable t) {
//                if (call.isCanceled()) {
//                    Log.i(TAG, "Cancel HTTP request on onFailure()");
//                } else {
//                    Toast.makeText(mContext, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, t.getMessage());
//                }
//                progressBar.setVisibility(View.GONE);
//                txtLoading.setVisibility(View.GONE);
//            }
//        });
//    }


    private void mappingViewElement(View view) {
//        progressBar = mContext.findViewById(R.id.event_progress_bar);
//        txtNoInternet = mContext.findViewById(R.id.event_txt_no_internet);
//        txtLoading = mContext.findViewById(R.id.event_txt_loading);
//        progressBar.setVisibility(View.VISIBLE);
//        txtLoading.setVisibility(View.VISIBLE);
        txtStartDateTop = view.findViewById(R.id.fg_ex_detail_start_date_top);
        txtExName = view.findViewById(R.id.fg_ex_detail_ex_name);
        txtStartDate = view.findViewById(R.id.fg_ex_detail_start_date);
        txtEndDate = view.findViewById(R.id.fg_ex_detail_end_date);
        txtLocation = view.findViewById(R.id.fg_ex_detail_location);
        txtDescription = view.findViewById(R.id.fg_ex_detail_description);
    }

    public void updateDataToView(Exhibition details) {
        txtStartDateTop.setText(details.getStartDate());
        txtExName.setText(details.getName());
        txtStartDate.setText(details.getStartDate());
        txtEndDate.setText(details.getEndDate());
        txtLocation.setText(details.getAddress());
        txtDescription.setText(details.getDescription());
        exhibitionSaved = new Exhibition();
    }

}
