package com.gamex.fragments;


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
import android.widget.Toast;

import com.gamex.R;
import com.gamex.adapters.ListCompanyAdapter;
import com.gamex.adapters.YourExhibitionDataAdapter;
import com.gamex.models.CompanyOverview;
import com.gamex.utils.Constant;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExDetailListCompanyFragment extends BaseFragment {
    RecyclerView rvListCompany;
    String exhibitionId;
    TextView txtNoInternet, txtLoading;
    ProgressBar progressBar;

    public ExDetailListCompanyFragment() {
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
        View view = inflater.inflate(R.layout.fragment_ex_detail_list_company, container, false);
        rvListCompany = view.findViewById(R.id.fg_company_list_rv);

        Bundle bundle = getArguments();
        if (bundle != null) {
            exhibitionId = bundle.getString("exhibitionId");
        } else {
            Log.e(Constant.TAG_FG_EX_DETAIL, "No bundle in ExDetailListCompanyFragment from Activity");
            Toast.makeText(mActivity, "Something when wrong. Try again later.", Toast.LENGTH_LONG).show();
        }

        // TODO test data only
        ArrayList<CompanyOverview> listCompany = new ArrayList<CompanyOverview>();
        listCompany.add(new CompanyOverview(123, "ADPEX", "---"));
        listCompany.add(new CompanyOverview(123, "UBM VIETNAM", "---"));
        listCompany.add(new CompanyOverview(123, "FSoft", "---"));
        listCompany.add(new CompanyOverview(123, "Global Expo", "---"));
        listCompany.add(new CompanyOverview(123, "CECT,BEXCO", "---"));
        listCompany.add(new CompanyOverview(123, "Automobile Manufacturers’ Association (VAMA) ", "---"));
        listCompany.add(new CompanyOverview(123, "Automobile Manufacturers’ Association (VAMA) ", "---"));
        listCompany.add(new CompanyOverview(123, "Automobile Manufacturers’ Association (VAMA) ", "---"));
        listCompany.add(new CompanyOverview(123, "Automobile Manufacturers’ Association (VAMA) ", "---"));
        listCompany.add(new CompanyOverview(123, "Automobile Manufacturers’ Association (VAMA) ", "---"));

        rvListCompany.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        ListCompanyAdapter adapter = new ListCompanyAdapter(getContext(), listCompany);
        rvListCompany.setAdapter(adapter);

        return view;
    }

}
