package com.gamex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.gamex.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        getWindow().setExitTransition(null);

        final Intent intent = new Intent(SplashActivity.this, LoginActivity.class);

        new Handler().postDelayed(() -> {
//                    ActivityOptions options = ActivityOptions
//                            .makeSceneTransitionAnimation(SplashActivity.this, findViewById(R.id.txtAppName), "app_logo");
                startActivity(intent);
                finish();
        },2000); // TODO 100ms is for open app faster -> change to 2000ms later
    }
}
