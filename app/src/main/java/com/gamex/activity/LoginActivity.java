package com.gamex.activity;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.network.DataService;
import com.gamex.utils.Constant;
import com.gamex.utils.TextInputLayoutValidator;
import com.google.zxing.common.StringUtils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener {
    @Inject
    @Named("no-cache")
    DataService dataService;
    Call<ResponseBody> call;

    TextView txtToRegister;
    Button btnLogin;
    Validator validator;
    private final String TAG = LoginActivity.class.getSimpleName() + "------------------------------";

    @NotEmpty(message = Constant.ERR_REQUIRED)
    private TextInputLayout tilUsername;

    @NotEmpty(message = Constant.ERR_REQUIRED)
    private TextInputLayout tilPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((GamexApplication) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mappingViewElement();
        initValidator();

        String userNameFromRegister = getIntent().getStringExtra("USERNAME_FROM_REGISTER");
        if (userNameFromRegister != null) {
            tilUsername.getEditText().setText(userNameFromRegister);
        }

        btnLogin.setOnClickListener(v -> validator.validate());

        txtToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void mappingViewElement() {
        //Bind view
        txtToRegister = findViewById(R.id.txtToRegister);
        btnLogin = findViewById(R.id.btnLogin);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
    }

    private void initValidator() {
        // Create validator
        validator = new Validator(this);
        validator.setValidationListener(this);
        // Reset error
        validator.registerAdapter(TextInputLayout.class, new TextInputLayoutValidator());
        validator.setViewValidatedAction(view -> {
            if (view instanceof TextInputLayout) {
                ((TextInputLayout) view).setError("");
                ((TextInputLayout) view).setErrorEnabled(false);
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
        // TODO call API to check
        Toast.makeText(this, "call api!", Toast.LENGTH_LONG).show();
        SweetAlertDialog progressDialog = new
                SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Waiting for Server");
        progressDialog.setCancelable(false);
        progressDialog.show();

        call = dataService.loginAccount(
                "password",
                tilUsername.getEditText().getText().toString(),
                tilPassword.getEditText().getText().toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, response.toString());
//                if (response.code() == 200) {
//                    Log.i(TAG, response.toString());
//                } else {
//                    Log.i(TAG, response.toString());
//                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, t.getMessage());
//                if (call.isCanceled()) {
//                    Log.i(TAG, "Cancel HTTP request on onFailure()");
//                } else {
//                    Toast.makeText(LoginActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, t.getMessage());
//                }
//                progressBar.setVisibility(View.GONE);
//                txtLoading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
//        errors.get(0).getView().requestFocus();
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof TextInputLayout) {
                ((TextInputLayout) view).setError(message);
                ((TextInputLayout) view).setErrorEnabled(true);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
