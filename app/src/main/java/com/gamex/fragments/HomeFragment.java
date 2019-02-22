package com.gamex.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamex.MainActivity;
import com.gamex.R;
import com.gamex.adapters.ExhibitionListRecycleViewAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    RecyclerView rvOngoing, rvNear, rvYourEvent;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Home");

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Test data only
        ArrayList<String> exImg = new ArrayList<>();
        ArrayList<String> exName = new ArrayList<>();
        ArrayList<String> exDate = new ArrayList<>();
        exName.add("AUTOMECHANIKA HO CHI MINH CITY 2019");
        exName.add("TELEFILM 2019 / ICTCOMM 2019");
        exName.add("VIFA GOOD URBAN 2019 – VIFAG.U. 2019");
        exName.add("VIETBUILD HOME 2019");
        exName.add("VIETWATER 2019");
        exDate.add("February 28th to March 2nd");
        exDate.add("February 28th to March 2nd");
        exDate.add("February 28th to March 2nd");
        exDate.add("February 28th to March 2nd");
        exDate.add("February 28th to March 2nd");
        exDate.add("February 28th to March 2nd");

//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvOngoing = view.findViewById(R.id.fg_home_rv_ongoing);
        rvOngoing.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvNear = view.findViewById(R.id.fg_home_rv_near);
        rvNear.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvYourEvent = view.findViewById(R.id.fg_home_rv_your_event);
        rvYourEvent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        ExhibitionListRecycleViewAdapter adapter = new ExhibitionListRecycleViewAdapter(getContext(), exImg, exName, exDate);
        rvOngoing.setAdapter(adapter);
        rvNear.setAdapter(adapter);
        rvYourEvent.setAdapter(adapter);

        return view;
    }
}
