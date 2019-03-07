package com.gamex;

import android.app.Application;

import com.gamex.di.AppComponent;
import com.gamex.di.AppModule;
import com.gamex.di.DaggerAppComponent;
import com.gamex.di.NetworkModule;
import com.gamex.utils.Constant;

public class GamexApplication extends Application {
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        // Dagger%COMPONENT_NAME%
        mAppComponent = DaggerAppComponent.builder()
                // list of modules that are part of this component need to be created here too
                .appModule(new AppModule(this)) // This also corresponds to the name of your module: %component_name%Module
                .networkModule(new NetworkModule(Constant.BASE_URL))
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
