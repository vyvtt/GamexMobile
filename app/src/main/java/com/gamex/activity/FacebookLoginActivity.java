package com.gamex.activity;

import android.content.Context;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.network.DataService;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FacebookLoginActivity extends AppCompatActivity {
    private final String TAG = FacebookLoginActivity.class.getSimpleName() + "-----------------------";
//    private static String target_url;
//    private static String target_url_prefix="www.example.com";
    private Context mContext;
    private WebView webView;
//    private WebView mWebviewPop;
    private FrameLayout webViewContainer;
    private SweetAlertDialog progressDialog;

    private String url_accesstoken;
    private String fbEmail, fbFirstname, fbLastName;
    Call<ResponseBody> call;
    @Inject
    @Named("no-cache")
    DataService dataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((GamexApplication) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        webView = (WebView) findViewById(R.id.webview);
        webViewContainer = findViewById(R.id.webview_frame);

        webView.getSettings().setJavaScriptEnabled(true);

        String url = getIntent().getStringExtra("URL");
        webView.loadUrl(url);

        this.webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                Uri uri = Uri.parse(url);
                String host = uri.getHost();
//                Log.i("uri: ", uri.toString());
                Log.i("host: ", host);

                if (url.contains("gamexwebapi.azurewebsites.net/#access_token")) {

                    Log.i(TAG, "URL Match! Stop webview *********************");

                    progressDialog = new
                            SweetAlertDialog(FacebookLoginActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                            .setTitleText("Processing ...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    // Get Cookie
                    // :ARRAffinity=9e5f3094bd196c13904d9e836a48b048684e2b4551551a13bc41c3f7ade383f2; .AspNet.ExternalCookie=EaLFH6AMTi6bCOIClZ_4QGZSf_poSJopMVTeczWRqjgEUkxw7s4othvHN_WFNB3Jj0Z9OHRs2OzXTupsnZjM0XVwQS1yNO-ahAGPL2V8DRiH5WoW0qJ8FN6HEfyYpCUkMjsWEpAdlzEgydBl0DNwhtrbdfokpEV1kdVhNveKPQeV2artUOf6P66-oCJy_4T8GbhizMK7PWmNJ2jetoYFaSHAY7KqbE8k547e7V-OKEz8ILAn01HKN9CfzkljHSq10I19j-A5kHrSK9EJ5KNPeLP0fx6SMZZgeiowV7MjEeHY93kruXlbxZp1TTG_geC6LAS7XOgNrIjZ-Mb0Ura4FWSG2_2nUuvdKP-iPhDZ3SKT70G_K5ntgnJh6EUo3UnmON88exYmKqAvDoWFylsSgmJQy4vUuZQYaPLekUIJoFM9hgRSjCr3yiitEgZzMDXcScy2tG1u3YIgYYNdnhbAcPfjqgerqx7-HHtTD3KqLJeX9gEPndCto3zQw-RZ--wyINWBXh-yjKtdjsARElWERx-k8Cg0d9C7yt3OmqhAjnY
                    String cookies = CookieManager.getInstance().getCookie(url);
                    String cookieAspNetExternalCookie = "";
                    String[] tmpCookies = cookies.split(";");
                    for (String cookie : tmpCookies ){
                        if(cookie.contains(".AspNet.ExternalCookie")){
                            String[] temp1 = cookie.split("=");
                            cookieAspNetExternalCookie = temp1[1];
                            break;
                        }
                    }
                    Log.i(TAG, ".AspNet.ExternalCookie: " + cookieAspNetExternalCookie);
                    // Get access token
                    String tmp = uri.toString();
                    String accessToken = tmp.substring(
                            tmp.indexOf("=") + 1,
                            tmp.indexOf("&")
                    );
                    Log.i("access token *****: ", accessToken);

                    destroyWebView();

                    // get user id
                    call = dataService.getFacebookGraph("https://graph.facebook.com/me?fields=id&access_token=" + accessToken);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.i("Response from get id: ", response.message());
                            try {
                                String strRespone = response.body().string();
                                Log.i(TAG, "response get id: " + strRespone);
                                JSONObject userId = new JSONObject(strRespone);
                                String strUserId = userId.getString("id");

                                call = dataService.getFacebookGraph(
                                        "https://graph.facebook.com/\n" +
                                                strUserId +
                                        "    ?fields=email,first_name,last_name\n" +
                                        "    &access_token=" + accessToken);

                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        try {
                                            String strRespone = response.body().string();
                                            Log.i(TAG, "response get info: " + strRespone);
                                        } catch (Exception e) {
                                            Log.e(TAG, e.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Log.e(TAG, "Get FB info email ..... fail");
                                    }
                                });

                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e(TAG, "Get FB user ID fail");
                        }
                    });

//                    AccessToken token = new AccessToken(
//                            accessToken,
//                            AccessToken.getCurrentAccessToken().getApplicationId(),
//                            AccessToken.getCurrentAccessToken().getUserId(),
//                            AccessToken.getCurrentAccessToken().getPermissions(),
//                            null,
//                            AccessTokenSource.WEB_VIEW,
//                            AccessToken.getCurrentAccessToken().getExpires(),
//                            null,
//                            null);
//
//                    Log.i("token: ", token.toString());
//
//                    GraphRequest graphRequest = GraphRequest.newMeRequest(token, (object, response) -> {
//                        Log.i(TAG, object.toString());
//                        getFBData(object);
//                        createGamexAccount(accessToken);
//                    });
//
//                    Bundle parameters = new Bundle();
//                    parameters.putString("fields", "id,email,first_name,last_name");
//                    graphRequest.setParameters(parameters);
//                    graphRequest.executeAsync();
//                    Log.i(TAG, "Call graph request");

                    return false;
                }

                view.loadUrl(url);
                return true;
            }
        });

//        target_url = getIntent().getStringExtra("URL");
//        target_url_prefix = target_url.replaceFirst("https://", "");
//
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptCookie(true);
//        webView = (WebView) findViewById(R.id.webview);
//        //mWebviewPop = (WebView) findViewById(R.id.webviewPop);
//        webViewContainer = (FrameLayout) findViewById(R.id.webview_frame);
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setAppCacheEnabled(true);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSettings.setSupportMultipleWindows(true);
//        webView.setWebViewClient(new UriWebViewClient());
//        webView.setWebChromeClient(new UriChromeClient());
//        webView.loadUrl(target_url);

        mContext=this.getApplicationContext();
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
        Log.i(TAG, "DESTROY WEB VIEW /////////////////////");
    }

    private void getFBData(JSONObject object) {
        try {
//            progressDialog.dismissWithAnimation();
            fbEmail = object.getString("email");
            fbFirstname = object.getString("first_name");
            fbLastName = object.getString("last_name");
        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    private void createGamexAccount(String accessTokenFromFB) {
        progressDialog.setTitleText("Creating GamEx Account ...");

        HashMap<String, String> user = new HashMap<>();
        user.put("Email", fbEmail);
        user.put("FirstName", fbFirstname);
        user.put("LastName", fbLastName);

        call = dataService.loginWithFB(
                "Bearer " + accessTokenFromFB,
                user
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, response.toString());
                if (response.isSuccessful()) {
                    Log.i(TAG, "Create account OK");
                    progressDialog.setTitleText("Create account OK").changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                } else {
                    Log.i(TAG, "Call GamEx API fail: " + response.errorBody());
                    progressDialog.setTitleText("Create account FAIL").changeAlertType(SweetAlertDialog.ERROR_TYPE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

//    private class UriWebViewClient extends WebViewClient {
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            String host = Uri.parse(url).getHost();
//            //Log.d("shouldOverrideUrlLoading", url);
//            if (host.equals(target_url_prefix))
//            {
//                // This is my web site, so do not override; let my WebView load
//                // the page
//                if(mWebviewPop!=null)
//                {
//                    mWebviewPop.setVisibility(View.GONE);
//                    webViewContainer.removeView(mWebviewPop);
//                    mWebviewPop=null;
//                }
//                return false;
//            }
//
//            if(host.equals("m.facebook.com"))
//            {
//                return false;
//            }
//            // Otherwise, the link is not for a page on my site, so launch
//            // another Activity that handles URLs
////            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
////            startActivity(intent);
////            return true;
//            return true;
//        }
//
//        @Override
//        public void onReceivedSslError(WebView view, SslErrorHandler handler,
//                                       SslError error) {
//            Log.d("onReceivedSslError", "onReceivedSslError");
//            //super.onReceivedSslError(view, handler, error);
//        }
//    }
//
//    class UriChromeClient extends WebChromeClient {
//
//        @Override
//        public boolean onCreateWindow(WebView view, boolean isDialog,
//                                      boolean isUserGesture, Message resultMsg) {
//            mWebviewPop = new WebView(mContext);
//            mWebviewPop.setVerticalScrollBarEnabled(false);
//            mWebviewPop.setHorizontalScrollBarEnabled(false);
//            mWebviewPop.setWebViewClient(new UriWebViewClient());
//            mWebviewPop.getSettings().setJavaScriptEnabled(true);
//            mWebviewPop.getSettings().setSavePassword(false);
//            mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT));
//            webViewContainer.addView(mWebviewPop);
//            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
//            transport.setWebView(mWebviewPop);
//            resultMsg.sendToTarget();
//
//            return true;
//        }
//
//        @Override
//        public void onCloseWindow(WebView window) {
//            Log.d("onCloseWindow", "called");
//        }
//
//    }
}
