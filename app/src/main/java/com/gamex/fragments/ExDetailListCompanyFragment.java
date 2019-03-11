package com.gamex.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.adapters.ListCompanyAdapter;
import com.gamex.models.CompanyInExhibition;

import java.io.Serializable;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExDetailListCompanyFragment extends Fragment {

    private final String TAG = ExDetailListCompanyFragment.class.getSimpleName();
    private RecyclerView rvListCompany;
    private FragmentActivity mContext;
    private List<CompanyInExhibition> listCompanyFromActivity, listCompanySaved;
    private TextView txtNoData;

    public ExDetailListCompanyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mContext = (FragmentActivity) context;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("LIST_COMPANY_SAVED", (Serializable) listCompanySaved);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ex_detail_list_company, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            listCompanyFromActivity = (List<CompanyInExhibition>) bundle.getSerializable("EXHIBITION_LIST_COMPANY");
            Log.i(TAG, "List company: " + listCompanyFromActivity.size());
        } else {
            Log.e(TAG, "No bundle in ExDetailListCompanyFragment from Activity");
            Toast.makeText(mContext, "Something when wrong. Try again later.", Toast.LENGTH_LONG).show();
        }

        mappingViewElement(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            // restore of view state that had previously been frozen by onSaveInstanceState(Bundle)
            this.listCompanySaved = (List<CompanyInExhibition>) savedInstanceState.getSerializable("LIST_COMPANY_SAVED");
            setRecycleViewAdapter(listCompanySaved);
        } else {
            if (listCompanySaved != null) {
                // Returning from back stack, data is fine, do nothing
            } else {
                // fragment create for 1st time -> get from exhibitionFromActivity
                setRecycleViewAdapter(listCompanyFromActivity);
                listCompanySaved = listCompanyFromActivity;
            }
        }
    }

    private void setRecycleViewAdapter(List<CompanyInExhibition> data) {
        if (data.isEmpty()) {
            txtNoData.setVisibility(View.VISIBLE);
        } else {
            txtNoData.setVisibility(View.GONE);
            rvListCompany.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            ListCompanyAdapter adapter = new ListCompanyAdapter(mContext, data);
            rvListCompany.setAdapter(adapter);
        }
    }

    private void mappingViewElement(View view) {
        rvListCompany = view.findViewById(R.id.fg_company_list_rv);
        txtNoData = view.findViewById(R.id.fg_list_company_txt_no_data);
    }
}
