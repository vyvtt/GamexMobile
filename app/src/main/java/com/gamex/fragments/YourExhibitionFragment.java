package com.gamex.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gamex.R;
import com.gamex.adapters.YourExhibitionTabAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourExhibitionFragment extends BaseFragment {

    public YourExhibitionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_your_exhibition, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        ViewPager viewPager = getView().findViewById(R.id.fg_yourEx_viewpager);
        YourExhibitionTabAdapter adapter = new YourExhibitionTabAdapter(getChildFragmentManager());

        viewPager.setAdapter(adapter);
        TabLayout tabLayout = getView().findViewById(R.id.fg_yourEx_tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    // Hide scan QR icon on the top right menu
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mn_item_qr).setVisible(false);
    }
}
