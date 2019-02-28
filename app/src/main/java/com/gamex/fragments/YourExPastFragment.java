package com.gamex.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamex.R;
import com.gamex.adapters.YourExhibitionDataAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourExPastFragment extends BaseFragment {
    RecyclerView rvYourExCurrent;

    public YourExPastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_your_ex_current, container, false);

        // TODO: THIS IS TEST DATA
        ArrayList<String> exImg = new ArrayList<>();
        ArrayList<String> exName = new ArrayList<>();
        ArrayList<String> exDate = new ArrayList<>();
        ArrayList<String> exAddr = new ArrayList<>();
        exName.add("AUTOMECHANIKA HO CHI MINH CITY 2019");
        exName.add("TELEFILM 2019 / ICTCOMM 2019");
        exName.add("VIFA GOOD URBAN 2019 – VIFAG.U. 2019");
        exName.add("VIETBUILD HOME 2019");
        exName.add("VIETWATER 2019");
        exName.add("VIETWATER 2019");
        exName.add("VIETWATER 2019");
        exDate.add("February 28th to March 2nd");
        exDate.add("February 28th to March 2nd");
        exDate.add("February 28th to March 2nd");
        exDate.add("February 28th to March 2nd");
        exDate.add("February 28th to March 2nd");
        exDate.add("February 28th to March 2nd");
        exDate.add("February 28th to March 2nd");
        exAddr.add("This exhibition is expired");
        exAddr.add("799 Nguyen Van Linh, Tan Phu Ward, Dist 7, HCMC, Viet Nam");
        exAddr.add("799 Nguyen Van Linh, Tan Phu Ward, Dist 7, HCMC, Viet Nam");
        exAddr.add("799 Nguyen Van Linh, Tan Phu Ward, Dist 7, HCMC, Viet Nam");
        exAddr.add("799 Nguyen Van Linh, Tan Phu Ward, Dist 7, HCMC, Viet Nam");
        exAddr.add("799 Nguyen Van Linh, Tan Phu Ward, Dist 7, HCMC, Viet Nam");
        exAddr.add("799 Nguyen Van Linh, Tan Phu Ward, Dist 7, HCMC, Viet Nam");

//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvYourExCurrent = view.findViewById(R.id.fg_yourEx_rv_current);
        rvYourExCurrent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        YourExhibitionDataAdapter adapter = new YourExhibitionDataAdapter(getContext(), exImg, exName, exDate, exAddr);
        rvYourExCurrent.setAdapter(adapter);
        return view;
    }

}
