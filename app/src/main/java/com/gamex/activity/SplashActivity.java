package com.gamex.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.utils.Constant;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

public class SplashActivity extends AppCompatActivity {
    @Inject
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((GamexApplication) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("com.gamex", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String sign= Base64.encodeToString(md.digest(), Base64.DEFAULT);
//                Log.e("MY_KEY_HASH:", sign);
//                Toast.makeText(getApplicationContext(),sign,Toast.LENGTH_LONG).show();
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//        } catch (NoSuchAlgorithmException e) {
//        }
//        finish();

        Intent intent;
        boolean hasLoggedIn = sharedPreferences.getBoolean(Constant.PREF_HAS_LOGGED_IN, false);
        if (hasLoggedIn) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        new Handler().postDelayed(() -> {
                startActivity(intent);
                finish();
        },1000); // TODO 100ms is for open app faster -> change to 2000ms later
    }
}
