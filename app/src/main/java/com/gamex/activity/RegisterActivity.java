package com.gamex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;
import com.gamex.utils.TextInputLayoutValidator;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    @Inject
    @Named("no-cache")
    DataService dataService;
    Call<ResponseBody> call;

    private TextView txtToLogin;
    private Validator validator;
    private Button btnRegister;
    private SweetAlertDialog progressDialog;
    private String TAG = RegisterActivity.class.getSimpleName() + "---------------------";

    @NotEmpty(message = Constant.ERR_REQUIRED, trim = true)
    TextInputLayout tilFirstname;

    @NotEmpty(message = Constant.ERR_REQUIRED, trim = true)
    TextInputLayout tilLastname;

    @NotEmpty(message = Constant.ERR_REQUIRED, trim = true)
    TextInputLayout tilUsername;

    @Password(message = Constant.ERR_LENGTH_MIN_6)
    TextInputLayout tilPassword;

    @ConfirmPassword
    TextInputLayout tilRePassword;

    @Email
    TextInputLayout tilEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((GamexApplication) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mappingViewElement();
        initValidator();

        txtToLogin.setOnClickListener(v -> {
            txtToLogin.setTextColor(getResources().getColor(R.color.btn_pressed));
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnRegister.setOnClickListener(v -> validator.validate());
    }

    private void mappingViewElement() {
        btnRegister = findViewById(R.id.btnUpdateProfile);
        txtToLogin = findViewById(R.id.txtToLogin);
        tilFirstname = findViewById(R.id.tilFirstName);
        tilLastname = findViewById(R.id.tilLastName);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        tilRePassword = findViewById(R.id.tilRePassword);
        tilEmail = findViewById(R.id.tilEmail);
    }

    private void initValidator() {
        // Create validator
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
        // Reset error
        validator.registerAdapter(TextInputLayout.class, new TextInputLayoutValidator());
        validator.setViewValidatedAction(view -> {
            if (view instanceof TextInputLayout) {
                ((TextInputLayout) view).setError("");
                ((TextInputLayout) view).setErrorEnabled(false);
            }
        });
    }

    private void processOnValid() {
        // Show progress dialog
        progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog
                .setTitleText("Waiting for Server")
                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Create data Map to send API
        HashMap<String, String> tmpUserInfo = new HashMap<>();
        tmpUserInfo.put("Username", tilUsername.getEditText().getText().toString());
        tmpUserInfo.put("Email", tilEmail.getEditText().getText().toString());
        tmpUserInfo.put("Password", tilPassword.getEditText().getText().toString());
        tmpUserInfo.put("ConfirmPassword", tilRePassword.getEditText().getText().toString());
        tmpUserInfo.put("FirstName", tilFirstname.getEditText().getText().toString());
        tmpUserInfo.put("LastName", tilLastname.getEditText().getText().toString());

        call = dataService.register(tmpUserInfo);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, response.toString());

                if (response.isSuccessful()) {
                    progressDialog.setTitleText("Create Account Successfully!")
                            .setConfirmText("Great!")
                            .setConfirmClickListener(sweetAlertDialog -> {
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.putExtra("USERNAME_FROM_REGISTER", tilUsername.getEditText().getText().toString());
                                startActivity(intent);
                                finish();
                            })
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                } else {
                    String err = "";
                    if (response.code() == 400) {
                        try {
                            String errBody = response.errorBody().string();
                            Log.i(TAG, "err body: " + errBody);
                            JSONObject jsonError = new JSONObject(errBody);
                            JSONObject jsonModelState = jsonError.getJSONObject("modelState");
                            JSONArray errArray = jsonModelState.getJSONArray("registerError");
                            for (int i = 0; i < errArray.length(); i++) {
                                err += errArray.getString(i) + "\n";
                            }

                            Log.i(TAG, "err " + err);
                            Log.i(TAG, "modelState: " + jsonError.get("modelState").toString());
                            Log.i(TAG, "RegisterError: " + errArray.toString());

                        } catch (IOException | JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    progressDialog
                            .setTitleText("Oops...")
                            .setConfirmText("Try again")
                            .setContentText(err.isEmpty() ? response.message() : err)
                            .setConfirmClickListener(sweetAlertDialog -> progressDialog.dismissWithAnimation())
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, t.getMessage());
                progressDialog.setTitleText("Oops...")
                        .setContentText("Can not connect to Server")
                        .setConfirmText("Please try again later")
                        .setConfirmClickListener(sweetAlertDialog -> progressDialog.dismissWithAnimation())
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        });
    }


    private void processOnInValid(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getFailedRules().get(0).getMessage(this);
            if (view instanceof TextInputLayout) {
                ((TextInputLayout) view).setError(message);
                ((TextInputLayout) view).setErrorEnabled(true);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
