package com.gamex.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.services.network.BaseCallBack;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;
import com.gamex.utils.TextInputLayoutValidator;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends BaseFragment {

    @Inject
    @Named("cache")
    DataService dataService;
    Call<ResponseBody> call;
    @Inject
    SharedPreferences sharedPreferences;

    private String accessToken;
    private final String TAG = ChangePasswordFragment.class.getSimpleName();

    private TextView txtToolBarTitle, txtNoInternet, txtLoading, txtNote;
    private ProgressBar progressBar;
    private Button btnChangePass;
    private Validator validator;
    private SweetAlertDialog sweetAlertDialog;

    private TextInputLayout tilCurPass;
    @Password(message = Constant.ERR_LENGTH_MIN_6)
    private TextInputLayout tilNewPass;
    @ConfirmPassword
    private TextInputLayout tilRePass;

    public ChangePasswordFragment() { }

    @Override
    public void onAttach(Context context) {
        ((GamexApplication) context.getApplicationContext()).getAppComponent().inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accessToken = "Bearer " + sharedPreferences.getString(Constant.PREF_ACCESS_TOKEN, "");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        //bind
        mappingViewElement(view);
        initValidator();
        btnChangePass.setOnClickListener(v -> validator.validate());

        return view;
    }

    private void mappingViewElement(View view) {
        btnChangePass = view.findViewById(R.id.password_btnChangePassword);
        tilCurPass = view.findViewById(R.id.password_tilCurPass);
        tilNewPass = view.findViewById(R.id.password_tilNewPass);
        tilRePass = view.findViewById(R.id.password_tilRePass);
        txtNote = view.findViewById(R.id.password_note);

        txtToolBarTitle = mActivity.findViewById(R.id.main_toolbar_title);
        txtNoInternet = mActivity.findViewById(R.id.main_txt_no_internet);
        txtLoading = mActivity.findViewById(R.id.main_txt_loading);
        progressBar = mActivity.findViewById(R.id.main_progress_bar);

        txtToolBarTitle.setText("Change Password");

        txtNote.setText(Html.fromHtml("You can leave <b> Current Password </b> field empty if you Login with Facebook Account and haven't create a password yet."));
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

        sweetAlertDialog = new SweetAlertDialog(mActivity, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Connecting ...");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        call = dataService.changePassword(
                accessToken,
                tilCurPass.getEditText().getText().toString(),
                tilNewPass.getEditText().getText().toString(),
                tilRePass.getEditText().getText().toString()
        );

        call.enqueue(new BaseCallBack<ResponseBody>(mActivity) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, response.message());

                try {

                    if (response.isSuccessful()) {

                        if (sweetAlertDialog != null) {
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            sweetAlertDialog
                                    .setTitleText("Great")
                                    .setContentText("Change Password successfully!")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
                        }
                        tilCurPass.getEditText().setText("");
                        tilNewPass.getEditText().setText("");
                        tilRePass.getEditText().setText("");

                    } else {

                        String strErr = response.errorBody().string();
                        JSONObject jsonErr = new JSONObject(strErr);
                        String mesErr = "";

                        JSONObject jsonModelState = jsonErr.getJSONObject("modelState");
                        JSONArray errArray = jsonModelState.getJSONArray("errorMessage");
                        for (int i = 0; i < errArray.length(); i++) {
                            mesErr += errArray.getString(i) + "\n";
                        }

                        handleFailOnRequest(mesErr.isEmpty() ? response.message() : mesErr, "OK");
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e.fillInStackTrace());
                    handleFailOnRequest("Something went wrong", "Please try again later");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t.fillInStackTrace());
                handleFailOnRequest("Cannot connect to GamEx server", "Please try again later");
            }
        });
    }

    private void handleFailOnRequest(String content, String confirm) {
        if (sweetAlertDialog != null) {
            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
            sweetAlertDialog.setTitleText("Oops ...")
                    .setContentText(content)
                    .setConfirmText(confirm)
                    .setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                    });
        }
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
