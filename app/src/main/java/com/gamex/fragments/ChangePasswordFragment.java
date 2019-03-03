package com.gamex.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.R;
import com.gamex.utils.Constant;
import com.gamex.utils.TextInputLayoutValidator;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends BaseFragment {
    TextView txtToolBarTitle, txtNoInternet, txtLoading;
    ProgressBar progressBar;
    Button btnChangePass;
    Validator validator;

    @NotEmpty(message = Constant.ERR_REQUIRED)
    TextInputLayout tilCurPass;

    @Password(message = Constant.ERR_LENGTH_MIN_6)
    TextInputLayout tilNewPass;

    @ConfirmPassword
    TextInputLayout tilRePass;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        txtToolBarTitle = getActivity().findViewById(R.id.toolbar_title);
        txtNoInternet = getActivity().findViewById(R.id.main_txt_no_internet);
        txtLoading = getActivity().findViewById(R.id.main_txt_loading);
        progressBar = getActivity().findViewById(R.id.main_progress_bar);

        txtToolBarTitle.setText("Change Password");
        progressBar.setVisibility(View.VISIBLE);
        txtLoading.setVisibility(View.VISIBLE);
        // Hide scan QR
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        //bind
        btnChangePass = view.findViewById(R.id.btnChangePass);
        tilCurPass = view.findViewById(R.id.tilCurPass);
        tilNewPass = view.findViewById(R.id.tilNewPass);
        tilRePass = view.findViewById(R.id.tilRePass);

        initValidator();
        btnChangePass.setOnClickListener(v -> validator.validate());
        return view;
    }

    private void initValidator() {
        validator = new Validator(this);
        validator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                processOnValid();
            }
            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                processOnInValid(errors);
            }
        });
        validator.registerAdapter(TextInputLayout.class, new TextInputLayoutValidator());
        validator.setViewValidatedAction(view -> {
            if (view instanceof TextInputLayout) {
                ((TextInputLayout) view).setError("");
                ((TextInputLayout) view).setErrorEnabled(false);
            }
        });
    }

    private void processOnValid() {
        // TODO call API to check
        Toast.makeText(mActivity, "Valid!", Toast.LENGTH_LONG).show();
    }


    private void processOnInValid(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getFailedRules().get(0).getMessage(mActivity);
            if (view instanceof TextInputLayout) {
                ((TextInputLayout) view).setError(message);
                ((TextInputLayout) view).setErrorEnabled(true);
            } else {
                Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    // Hide scan QR icon on the top right menu
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mn_item_qr).setVisible(false);
    }

}
