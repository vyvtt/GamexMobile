package com.gamex.services.di;

import com.gamex.activity.ExhibitionDetailActivity;
import com.gamex.activity.FacebookLoginActivity;
import com.gamex.activity.LoginActivity;
import com.gamex.activity.MainActivity;
import com.gamex.activity.RegisterActivity;
import com.gamex.activity.SplashActivity;
import com.gamex.activity.SurveyActivity;
import com.gamex.activity.ViewAllExhibitionActivity;
import com.gamex.adapters.EndlessRecycleViewAdapter;
import com.gamex.adapters.HomeAdapter;
import com.gamex.adapters.ListCompanyAdapter;
import com.gamex.fragments.HomeFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface AppComponent {
    void inject(HomeFragment homeFragment);
    void inject(ExhibitionDetailActivity exhibitionDetailActivity);
    void inject(HomeAdapter homeAdapter);
    void inject(ListCompanyAdapter listCompanyAdapter);
    void inject(EndlessRecycleViewAdapter endlessRecycleViewAdapter);
    void inject(ViewAllExhibitionActivity viewAllExhibitionActivity);
    void inject(SurveyActivity surveyActivity);

    // splash
    void inject(SplashActivity splashActivity);
    // login
    void inject(LoginActivity loginActivity);
    // fb login
    void inject(FacebookLoginActivity facebookLoginActivity);
    // register
    void inject(RegisterActivity registerActivity);
    // main
    void inject(MainActivity mainActivity);
}
