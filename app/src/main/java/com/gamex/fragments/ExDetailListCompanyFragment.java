package com.gamex.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.gamex.adapters.ListCompanyAdapter;
import com.gamex.models.Company;
import com.gamex.models.CompanyInExhibition;
import com.gamex.models.Exhibition;
import com.gamex.network.DataService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExDetailListCompanyFragment extends Fragment {
    @Inject
    @Named("cache")
    Retrofit retrofit;
    private Call<List<Company>> call;

    private final String TAG = ExDetailListCompanyFragment.class.getSimpleName();
    private RecyclerView rvListCompany;
    private String exhibitionId;
    private TextView txtNoInternet, txtLoading;
    private ProgressBar progressBar;
    private FragmentActivity mContext;
    private List<CompanyInExhibition> listCompanyFromActivity, listCompanySaved;

    public ExDetailListCompanyFragment() {
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
        View view = inflater.inflate(R.layout.fragment_ex_detail_list_company, container, false);
        mappingViewElement(view);

        Bundle bundle = getArguments();
        if (bundle != null) {
//            exhibitionId = bundle.getString("exhibitionId");
            listCompanyFromActivity = (List<CompanyInExhibition>) bundle.getSerializable("EXHIBITION_LIST_COMPANY");
            Log.i(TAG, "List company: " + listCompanyFromActivity.size());
        } else {
            Log.e(TAG, "No bundle in ExDetailListCompanyFragment from Activity");
            Toast.makeText(mContext, "Something when wrong. Try again later.", Toast.LENGTH_LONG).show();
        }

        // TODO test data only


        // TODO waiting api to check ------------------------------------------------------->
//        new CheckInternetTask(internet -> {
//           if (internet) {
//               // TODO call api
//               callAPI();
//           } else {
//               progressBar.setVisibility(View.GONE);
//               txtLoading.setVisibility(View.GONE);
//               txtNoInternet.setVisibility(View.VISIBLE);
//           }
//        });

        rvListCompany.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        ListCompanyAdapter adapter = new ListCompanyAdapter(getContext(), listCompanyFromActivity);
        rvListCompany.setAdapter(adapter);

        return view;
    }

    private void callAPI() {
        DataService service = retrofit.create(DataService.class);
        call = service.getListCompanyOfExhibition(exhibitionId);
        call.enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {
                if (response.code() == 200) {
                    // TODO set data adapter
                } else {
                    Log.i(TAG, response.toString());
                }
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Company>> call, Throwable t) {
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

    private void mappingViewElement(View view) {
        progressBar = mContext.findViewById(R.id.event_progress_bar);
        txtNoInternet = mContext.findViewById(R.id.event_txt_no_internet);
        txtLoading = mContext.findViewById(R.id.event_txt_loading);
        progressBar.setVisibility(View.VISIBLE);
        txtLoading.setVisibility(View.VISIBLE);
        rvListCompany = view.findViewById(R.id.fg_company_list_rv);
    }

}
