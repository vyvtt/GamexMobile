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
    TextView txtToolBarTitle;

    public YourExhibitionFragment() {
        // Required empty public constructor
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Change toolbar title
        txtToolBarTitle = getActivity().findViewById(R.id.main_toolbar_title);
        txtToolBarTitle.setText("Your Exhibitions");
        // Hide scan QR
        setHasOptionsMenu(true);
//        //set the back arrow in the toolbar
//        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((MainActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(false);
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

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) getView().findViewById(R.id.fg_yourEx_viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        // using getFragmentManager() will work too
        YourExhibitionTabAdapter adapter = new YourExhibitionTabAdapter(getChildFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.fg_yourEx_tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    // Hide scan QR icon on the top right menu
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mn_item_qr).setVisible(false);
    }
}
