package com.gamex.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FacebookLoginActivity extends AppCompatActivity {
    private final String TAG = FacebookLoginActivity.class.getSimpleName() + "-----------------------";

    private WebView webView;
    private FrameLayout webViewContainer;
    private SweetAlertDialog progressDialog;
    private String cookieAspNetExternalCookie = "";
    private String accessToken;

    Call<ResponseBody> call;
    @Inject
    @Named("no-cache")
    DataService dataService;
    @Inject
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((GamexApplication) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        mappingViewElement();

        String url = getIntent().getStringExtra("URL");
        webView.loadUrl(url);

        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("gamexwebapi.azurewebsites.net/#access_token")) {
                    Log.i(TAG, "URL Match! Stop web view");

                    progressDialog = new
                            SweetAlertDialog(FacebookLoginActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                            .setTitleText("Processing ...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    getCookie(url);
                    getAccessToken(Uri.parse(url).toString());

                    clearCookies(FacebookLoginActivity.this);
                    destroyWebView();

                    // GEt user info
                    call = dataService.fbGetUserInfo("Bearer " + accessToken);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.i(TAG, "fbGetUserInfo Response success: " + response.toString());
                            try {
                                if (response.isSuccessful()) {
                                    String responseBody = response.body().string();
                                    Log.i(TAG, "body: " + responseBody);
                                    JSONObject json = new JSONObject(responseBody);
                                    boolean hasRegistered = json.getBoolean("hasRegistered");
                                    String fullname = json.getString("lastName") + " " + json.getString("firstName");

                                    if (hasRegistered) {
                                        Log.i(TAG, "hasRegistered: " + response.toString());
                                        savePrefAndStartMainActivity(fullname, accessToken);
                                    } else {
                                        createGamexAccount(accessToken, cookieAspNetExternalCookie, fullname);
                                    }
                                } else {
                                    // return other error code
                                    String err = "";
                                    if (response.code() == 400) {
                                        String errBody = response.errorBody().string();
                                        err = errBody;
                                    }
                                    dialogOnFail(err.isEmpty() ? response.message() : err, "Please try again later");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage(), e.fillInStackTrace());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.i(TAG, "Response fail because server");
                            dialogOnFail("Cannot connect to GamEx Server", "Please try again later");
                        }
                    });
                    return false;
                }
                view.loadUrl(url);
                return true;
            }
        });
    }

    private void getCookie(String url) {
        String cookies = CookieManager.getInstance().getCookie(url);
        cookieAspNetExternalCookie = "";
        String[] tmpCookies = cookies.split(";");
        for (String cookie : tmpCookies) {
            if (cookie.contains(".AspNet.ExternalCookie")) {
                String[] temp1 = cookie.split("=");
                cookieAspNetExternalCookie = temp1[1];
                break;
            }
        }
        cookieAspNetExternalCookie = ".AspNet.ExternalCookie="
                + cookieAspNetExternalCookie
                + ";Path=/;HttpOnly;Domain=gamexwebapi.azurewebsites.net";
    }

    public void clearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.i(TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.i(TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    private void getAccessToken(String tmp) {
        accessToken = tmp.substring(
                tmp.indexOf("=") + 1,
                tmp.indexOf("&")
        );
    }


    private void mappingViewElement() {
        webView = findViewById(R.id.fb_webview);
        webViewContainer = findViewById(R.id.webview_frame);
        webView.getSettings().setJavaScriptEnabled(true);
    }

    private void backToLoginOnRequestFail() {
        Log.i(TAG, "Request fail ----------> back to Login");
        Intent intent = new Intent(FacebookLoginActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void createGamexAccount(String accessToken, String cookieAspNetExternalCookie, String fullname) {
        progressDialog.setTitleText("Creating GamEx Account ...");

        call = dataService.fbRegisterExternal("Bearer " + accessToken, cookieAspNetExternalCookie);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "fbRegisterExternal Response success: " + response.toString());
                try {
                    if (response.isSuccessful()) {
                        savePrefAndStartMainActivity(fullname, accessToken);
                    } else {
                        String err = "";
                        if (response.code() == 400) {
                            String errBody = response.errorBody().string();
                            err = errBody;
                        }
                        dialogOnFail(err.isEmpty() ? response.message() : err, "Please try again later");
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e.fillInStackTrace());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "Response fail because server");
                dialogOnFail("Cannot connect to Gamex Server", "Please try again later");
            }
        });
    }

    private void dialogOnFail(String contentText, String confirmText) {
        if (progressDialog != null) {
            progressDialog
                    .setTitleText("Oops...")
                    .setConfirmText(confirmText)
                    .setContentText(contentText)
                    .setConfirmClickListener(sweetAlertDialog -> {
                        progressDialog.dismissWithAnimation();
                        backToLoginOnRequestFail();
                    })
                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
        }
    }

    private void savePrefAndStartMainActivity(String fullname, String accessToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.PREF_ACCESS_TOKEN, accessToken);
        editor.putString(Constant.PREF_FULLNAME, fullname);
        editor.putBoolean(Constant.PREF_HAS_LOGGED_IN, true);
        editor.apply();
        Intent intent = new Intent(FacebookLoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void destroyWebView() {
        // Make sure you remove the WebView from its parent view before doing anything.
        webViewContainer.removeAllViews();
        webView.clearHistory();
        // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
        // Probably not a great idea to pass true if you have other WebViews still alive.
        webView.clearCache(true);
        // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
        webView.loadUrl("about:blank");
        webView.onPause();
        webView.removeAllViews();
        webView.destroyDrawingCache();
        // NOTE: This pauses JavaScript execution for ALL WebViews,
        // do not use if you have other WebViews still alive.
        // If you create another WebView after calling this,
        // make sure to call mWebView.resumeTimers().
        webView.pauseTimers();
        // NOTE: This can occasionally cause a segfault below API 17 (4.2)
        webView.destroy();
        // Null out the reference so that you don't end up re-using it.
        webView = null;
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
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
