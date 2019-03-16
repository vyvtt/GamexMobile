package com.gamex.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
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
import com.gamex.activity.MainActivity;
import com.gamex.models.Profile;
import com.gamex.services.network.BaseCallBack;
import com.gamex.services.network.CheckInternetTask;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;
import com.gamex.utils.TextInputLayoutValidator;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;
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
public class EditProfileFragment extends BaseFragment {
    @Inject
    @Named("cache")
    DataService dataService;
    Call<Profile> callGetProfile;
    Call<ResponseBody> callUpdateProfile;
    @Inject
    SharedPreferences sharedPreferences;

    private String accessToken;
    private final String TAG = ChangePasswordFragment.class.getSimpleName();
    private Validator validator;
    private SweetAlertDialog sweetAlertDialog;

    private TextView txtToolBarTitle, txtNoInternet, txtLoading;
    private ProgressBar progressBar;

    private NestedScrollView layout;
    private Button btnApply;
    private Profile profile;
    private TextView txtFullname;

    @NotEmpty(message = Constant.ERR_REQUIRED, trim = true)
    private TextInputLayout tilFirstName;
    @NotEmpty(message = Constant.ERR_REQUIRED, trim = true)
    private TextInputLayout tilLastName;

    @Length(min = 6, message = "At lease 6 characters")
    private TextInputLayout tilUserName;
    private TextInputLayout tilEmail;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        ((GamexApplication) context.getApplicationContext()).getAppComponent().inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accessToken = "Bearer " + sharedPreferences.getString(Constant.PREF_ACCESS_TOKEN, "");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        mappingViewElement(view);
        getAccountInfo();
        initValidator();

        btnApply.setOnClickListener(v -> {
            if (checkIsTextChange()) {
                validator.validate();
            } else {
                Toast.makeText(mActivity, "You didn't change anything", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }


    private void getAccountInfo() {
        new CheckInternetTask(internet -> {
           if (internet) {
               txtNoInternet.setVisibility(View.GONE);
               progressBar.setVisibility(View.VISIBLE);
               txtLoading.setVisibility(View.VISIBLE);
               callApiGetUserProfile();
           } else {
               txtNoInternet.setVisibility(View.VISIBLE);
               progressBar.setVisibility(View.GONE);
               txtLoading.setVisibility(View.GONE);
           }
        });
    }

    private void callApiGetUserProfile() {
        callGetProfile = dataService.getProfile(accessToken);
        callGetProfile.enqueue(new BaseCallBack<Profile>(mActivity) {
            @Override
            public void onSuccess(Call<Profile> call, Response<Profile> response) {
                Log.i(TAG, response.message());

                if (response.isSuccessful()) {
                    profile = response.body();
                    loadDataToView();
                } else {
                    Log.i(TAG, "Not success: " + response.message());
                    handleOnFail("Something went wrong!", "Try again later");
                }

                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.i(TAG, "Call cancel", t.fillInStackTrace());
                } else {
                    Log.i(TAG, t.getMessage(), t.fillInStackTrace());
                    handleOnFail("Something went wrong!", "Try again later");
                }

                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }
        });

    }

    private void loadDataToView() {
        layout.setVisibility(View.VISIBLE);
        tilFirstName.getEditText().setText(profile.getFirstName());
        tilEmail.getEditText().setText(profile.getEmail());
        tilUserName.getEditText().setText(profile.getUsername());
        tilLastName.getEditText().setText(profile.getLastName());
    }

    private void handleOnFail(String content, String confirm) {
        if (sweetAlertDialog == null) {
            sweetAlertDialog = new SweetAlertDialog(mActivity, SweetAlertDialog.ERROR_TYPE);
        } else {
            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
        }

        sweetAlertDialog
                .setTitleText("Oops ...")
                .setContentText(content)
                .showCancelButton(false)
                .setConfirmText(confirm)
                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
    }

    private boolean checkIsTextChange() {
        if (!tilFirstName.getEditText().getText().toString().equals(profile.getFirstName())) {
            return true;
        }
        if (!tilLastName.getEditText().getText().toString().equals(profile.getLastName())) {
            return true;
        }
        if (!tilUserName.getEditText().getText().toString().equals(profile.getUsername())) {
            return true;
        }
        return false;
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

        String firstName = tilFirstName.getEditText().getText().toString().trim();
        String lastName = tilLastName.getEditText().getText().toString().trim();
        String email = tilEmail.getEditText().getText().toString().trim();
        String fullName = firstName + " " + lastName;

        callUpdateProfile = dataService.updateProfile(
                accessToken,
                firstName,
                lastName,
                email
        );
        callUpdateProfile.enqueue(new BaseCallBack<ResponseBody>(mActivity) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, response.message());

                try {
                    if (response.isSuccessful()) {

                        if (sweetAlertDialog != null) {
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            sweetAlertDialog
                                    .setTitleText("Great")
                                    .setContentText("Update successfully!")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
                        }

                        profile.setFirstName(firstName);
                        profile.setLastName(lastName);
                        profile.setEmail(email);
                        txtFullname.setText(fullName);

                        ((MainActivity) mActivity).updateProfileInDrawer(fullName);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constant.PREF_FULLNAME, fullName);
                        editor.apply();

                    } else {

                        String errMes = response.errorBody().string();
                        JSONObject jsonErr = new JSONObject(errMes);

                        try {
                            JSONObject jsonModelState = jsonErr.getJSONObject("modelState");
                            String errFromJson = "";

                            if (jsonModelState.has("model.Username")) {
                                errFromJson += jsonModelState.getJSONArray("model.Username").getString(0) + "\n";
                            }
                            if (jsonModelState.has("model.FirstName")) {
                                errFromJson += jsonModelState.getJSONArray("model.Username").getString(0) + "\n";
                            }
                            if (jsonModelState.has("model.LastName")) {
                                errFromJson += jsonModelState.getJSONArray("model.Username").getString(0) + "\n";
                            }
                            if (jsonModelState.has("errorMessage")) {
                                errFromJson += jsonModelState.getJSONArray("errorMessage").getString(0) + "\n";
                            }
                            Log.i(TAG, "Not success: " + errFromJson);
                            handleOnFail(errFromJson, "Try again later");

                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
                            handleOnFail(errMes, "Try again later");
                        }

                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e.fillInStackTrace());
                    handleOnFail("Something went wrong", "Try again later");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.i(TAG, "Request is cancel");
                } else {
                    Log.e(TAG, t.getMessage(), t.fillInStackTrace());
                    handleOnFail("Something went wrong!", "Try again later");
                }
            }
        });
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

    private void mappingViewElement(View view) {
        txtToolBarTitle = mActivity.findViewById(R.id.main_toolbar_title);
        txtNoInternet = mActivity.findViewById(R.id.main_txt_no_internet);
        txtLoading = mActivity.findViewById(R.id.main_txt_loading);
        progressBar = mActivity.findViewById(R.id.main_progress_bar);

        txtToolBarTitle.setText("Edit Profile");

        layout = view.findViewById(R.id.fg_edit_layout);
        btnApply = view.findViewById(R.id.btnUpdateProfile);
        txtFullname = view.findViewById(R.id.fg_edit_txtFullName);

        tilFirstName = view.findViewById(R.id.fg_edit_tilFirstName);
        tilLastName = view.findViewById(R.id.fg_edit_tilLastName);
        tilUserName = view.findViewById(R.id.fg_edit_tilUsername);
        tilEmail = view.findViewById(R.id.fg_edit_tilEmail);

    }

    // Hide scan QR icon on the top right menu
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mn_item_qr).setVisible(false);
    }


    @Override
    public void onStop() {
        super.onStop();

        if (sweetAlertDialog != null) {
            sweetAlertDialog.dismiss();
        }
        if (callGetProfile != null) {
            callGetProfile.cancel();
        }
        if (callUpdateProfile != null) {
            callUpdateProfile.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sweetAlertDialog != null) {
            sweetAlertDialog.dismiss();
        }
        if (callGetProfile != null) {
            callGetProfile.cancel();
        }
        if (callUpdateProfile != null) {
            callUpdateProfile.cancel();
        }
    }
}
