package com.gamex.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gamex.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkFragment extends BaseFragment {
    TextView txtToolBarTitle;

    public BookmarkFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        txtToolBarTitle = getActivity().findViewById(R.id.toolbar_title);
        txtToolBarTitle.setText("Bookmark");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark, container, false);
    }

}
