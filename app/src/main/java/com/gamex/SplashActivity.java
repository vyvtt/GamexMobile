package com.gamex;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Intent intent = new Intent(SplashActivity.this, LoginActivity.class);

        Thread splashThread = new Thread()
        {
            @Override
            public void run()
            {
                try {
                    sleep(2000);
                } catch (InterruptedException e)
                {
                    // do nothing
                } finally
                {
                    startActivity(intent);
                    overridePendingTransition(R.animator.fade_in, R.animator.fade_out);
                    finish();
                }
            }
        };

        splashThread.start();
    }
}
