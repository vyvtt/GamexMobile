package com.gamex.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.utils.Constant;

import javax.inject.Inject;

public class SplashActivity extends AppCompatActivity {
    @Inject
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((GamexApplication) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
        },2000); // TODO 100ms is for open app faster -> change to 2000ms later
    }
}
