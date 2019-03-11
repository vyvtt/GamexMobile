package com.gamex.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.network.DataService;
import com.gamex.utils.Constant;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

        webView = findViewById(R.id.webview);
        webViewContainer = findViewById(R.id.webview_frame);
        webView.getSettings().setJavaScriptEnabled(true);

        String url = getIntent().getStringExtra("URL");
        webView.loadUrl(url);

        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                String host = uri.getHost();
                Log.i("host: ", host);

                if (url.contains("gamexwebapi.azurewebsites.net/#access_token")) {
                    Log.i(TAG, "URL Match! Stop web view");

                    progressDialog = new
                            SweetAlertDialog(FacebookLoginActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                            .setTitleText("Processing ...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    // Get Cookie
                    // ARRAffinity=9e5f3094bd196c13904d9e836a48b048684e2b4551551a13bc41c3f7ade383f2;
                    // .AspNet.ExternalCookie=EaLFH6AMTi6bCOIClZ_4QGZSf_poSJopMVTeczWRqjgEUkxw7s4othvHN_WFNB3Jj0Z9OHRs2OzXTupsnZjM0XVwQS1yNO-ahAGPL2V8DRiH5WoW0qJ8FN6HEfyYpCUkMjsWEpAdlzEgydBl0DNwhtrbdfokpEV1kdVhNveKPQeV2artUOf6P66-oCJy_4T8GbhizMK7PWmNJ2jetoYFaSHAY7KqbE8k547e7V-OKEz8ILAn01HKN9CfzkljHSq10I19j-A5kHrSK9EJ5KNPeLP0fx6SMZZgeiowV7MjEeHY93kruXlbxZp1TTG_geC6LAS7XOgNrIjZ-Mb0Ura4FWSG2_2nUuvdKP-iPhDZ3SKT70G_K5ntgnJh6EUo3UnmON88exYmKqAvDoWFylsSgmJQy4vUuZQYaPLekUIJoFM9hgRSjCr3yiitEgZzMDXcScy2tG1u3YIgYYNdnhbAcPfjqgerqx7-HHtTD3KqLJeX9gEPndCto3zQw-RZ--wyINWBXh-yjKtdjsARElWERx-k8Cg0d9C7yt3OmqhAjnY
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

                    // Get access token
                    String tmp = uri.toString();
                    accessToken = tmp.substring(
                            tmp.indexOf("=") + 1,
                            tmp.indexOf("&")
                    );

                    Log.i(TAG, ".AspNet.ExternalCookie: " + cookieAspNetExternalCookie);
                    Log.i("Access Token *****: ", accessToken);

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

    private void savePrefAndStartMainActivity(String fullname, String accessToken) throws JSONException {
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
}
