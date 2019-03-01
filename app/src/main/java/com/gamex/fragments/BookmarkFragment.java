package com.gamex.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gamex.R;
import com.gamex.activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkFragment extends BaseFragment {
    TextView txtToolBarTitle;

    public BookmarkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Change toolbar title
        txtToolBarTitle = getActivity().findViewById(R.id.toolbar_title);
        txtToolBarTitle.setText("Bookmark");
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
        return inflater.inflate(R.layout.fragment_bookmark, container, false);
    }

    // Hide scan QR icon on the top right menu
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mn_item_qr).setVisible(false);
    }
}
