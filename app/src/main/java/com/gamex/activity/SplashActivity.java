package com.gamex.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gamex.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        getWindow().setExitTransition(null);

        final Intent intent = new Intent(SplashActivity.this, LoginActivity.class);

//        Thread splashThread = new Thread()
//        {
//            @Override
//            public void run()
//            {
//                try {
//                    sleep(2000);
//                } catch (InterruptedException e)
//                {
//                    // do nothing
//                } finally
//                {
//                    ActivityOptions options = ActivityOptions
//                            .makeSceneTransitionAnimation(SplashActivity.this, findViewById(R.id.txtAppName), "app_logo");
//                    startActivity(intent, options.toBundle());
//                    finish();
//                }
//            }
//        };
//
//        splashThread.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                    ActivityOptions options = ActivityOptions
//                            .makeSceneTransitionAnimation(SplashActivity.this, findViewById(R.id.txtAppName), "app_logo");
                    startActivity(intent);
                    finish();
            }
        },2000);
    }
}
