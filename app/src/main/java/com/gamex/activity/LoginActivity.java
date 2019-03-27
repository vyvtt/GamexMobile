package com.gamex.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    @Inject
    SharedPreferences sharedPreferences;

    private TextView txtToRegister;
    private Button btnLogin;
    private Validator validator;
    private final String TAG = LoginActivity.class.getSimpleName() + "------------------------------";
    private SweetAlertDialog progressDialog;
    private Button btnLoginWithFB;

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

        btnLoginWithFB.setOnClickListener(v -> {

            progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            progressDialog.setTitleText("Connecting");
            progressDialog.setCancelable(false);
            progressDialog.show();

            call = dataService.fbGetExternalProvider();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            Log.i(TAG, "body: " + responseBody);
                            JSONArray json = new JSONArray(responseBody);
                            String url = json.getJSONObject(0).getString("url");
                            Log.i(TAG, "url from step 1: " + url);

                            Log.i(TAG, "Call new activity");
                            Intent intent = new Intent(LoginActivity.this, FacebookLoginActivity.class);
                            intent.putExtra("URL", Constant.BASE_URL + url);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "Response code error: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e.fillInStackTrace());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "Request fail " + t.getMessage());
                }
            });
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//        super.onActivityResult(requestCode, resultCode, data);
//    }

//    private void facebookLogin() {
//        callbackManager = CallbackManager.Factory.create();
//        btnFacebookLogin.setReadPermissions(Arrays.asList("public_profile", "email"));
//        btnFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.i(TAG, "Call FB return success");
//                progressDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE)
//                        .setTitleText("Processing data ...");
//                progressDialog.setCancelable(false);
//                progressDialog.show();
//                String accessTokenFromFB = loginResult.getAccessToken().getToken();
//
//                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
//                    Log.i(TAG, object.toString());
//                    getFBData(object);
//                    createGamexAccount(accessTokenFromFB);
//                });
//
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "id,email,first_name,last_name");
//                graphRequest.setParameters(parameters);
//                graphRequest.executeAsync();
//                Log.i(TAG, "Call graph request");
//            }
//
//            @Override
//            public void onCancel() {
//                // App code
//                Log.i(TAG, "Cancelllllllllllll FB");
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
//                Log.i(TAG, exception.toString());
//            }
//        });
//    }

//    private void getFBData(JSONObject object) {
//        try {
////            progressDialog.dismissWithAnimation();
//            fbEmail = object.getString("email");
//            fbFirstname = object.getString("first_name");
//            fbLastName = object.getString("last_name");
//        } catch (JSONException e) {
//            Log.e(TAG, e.getLocalizedMessage());
//        }
//    }

//    private void createGamexAccount(String accessTokenFromFB) {
//        progressDialog.setTitleText("Creating GamEx Account ...");
//
//        HashMap<String, String> user = new HashMap<>();
//        user.put("Email", fbEmail);
//        user.put("FirstName", fbFirstname);
//        user.put("LastName", fbLastName);
//
//        call = dataService.loginWithFB(
//                "Bearer " + accessTokenFromFB,
//                user
//        );
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Log.i(TAG, response.toString());
//                if (response.isSuccessful()) {
//                    Log.i(TAG, "Create account OK");
//                    progressDialog.setTitleText("Create account OK").changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
//                } else {
//                    Log.i(TAG, "Call GamEx API fail: " + response.errorBody());
//                    progressDialog.setTitleText("Create account FAIL").changeAlertType(SweetAlertDialog.ERROR_TYPE);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });
//    }

    private void mappingViewElement() {
        //Bind view
        txtToRegister = findViewById(R.id.txtToRegister);
        btnLogin = findViewById(R.id.btnLogin);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        btnLoginWithFB = findViewById(R.id.main_btn_facebook_gamex);
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

        progressDialog = new
                SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Waiting for Server");
        progressDialog.setCancelable(false);
        progressDialog.show();

        call = dataService.login(
                "password",
                tilUsername.getEditText().getText().toString(),
                tilPassword.getEditText().getText().toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, response.toString());

                if (response.isSuccessful()) {
                    // return code 200 - 300
                    try {
                        String responseBody = response.body().string();
                        Log.i(TAG, "body: " + responseBody);
                        JSONObject json = new JSONObject(responseBody);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constant.PREF_ACCESS_TOKEN, json.getString("access_token"));
                        editor.putString(Constant.PREF_FULLNAME, json.getString("fullName"));
                        editor.putBoolean(Constant.PREF_HAS_LOGGED_IN, true);
                        editor.commit();
                    } catch (IOException | JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    progressDialog.dismissWithAnimation();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // return other error code
                    String err = "";
                    if (response.code() == 400) {
                        try {
                            String errBody = response.errorBody().string();
                            Log.i(TAG, "err body: " + errBody);
                            JSONObject jsonError = new JSONObject(errBody);
                            err = jsonError.get("error_description").toString();
                        } catch (IOException | JSONException | NullPointerException e) {
                            Log.e(TAG, e.getMessage());
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
                // Network exception - unexpected exception occurred creating the request or processing the response.
                Log.i(TAG, t.getMessage());
                progressDialog.setTitleText("Oops...")
                        .setContentText("Can not connect to Server")
                        .setConfirmText("Please try again later")
                        .setConfirmClickListener(sweetAlertDialog -> progressDialog.dismissWithAnimation())
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
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

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
