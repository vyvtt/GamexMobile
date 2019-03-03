package com.gamex.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.R;
import com.gamex.activity.MainActivity;
import com.gamex.network.APIClient;
import com.gamex.network.CheckInternetTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends BaseFragment {
    TextView txtToolBarTitle, txtNoInternet, txtLoading;
    ProgressBar progressBar;
    TextInputEditText edtFirstName;
    TextInputEditText edtLastName;
    TextInputEditText edtUserName;
    TextInputEditText edtPhone;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        txtToolBarTitle = getActivity().findViewById(R.id.toolbar_title);
        txtNoInternet = getActivity().findViewById(R.id.main_txt_no_internet);
        txtLoading = getActivity().findViewById(R.id.main_txt_loading);
        progressBar = getActivity().findViewById(R.id.main_progress_bar);

        txtToolBarTitle.setText("Edit Profile");
        progressBar.setVisibility(View.VISIBLE);
        txtLoading.setVisibility(View.VISIBLE);
        // Hide scan QR
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getAccountInfo();
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }


    private void getAccountInfo() {
        new CheckInternetTask(internet -> {
           if (internet) {
               callAPI();
           } else {
               txtNoInternet.setVisibility(View.VISIBLE);
               progressBar.setVisibility(View.GONE);
               txtLoading.setVisibility(View.GONE);
           }
        });
    }

    private void callAPI() {

    }

    // Hide scan QR icon on the top right menu
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mn_item_qr).setVisible(false);
    }

}
