package com.gamex.services.di;

import com.gamex.activity.CompanyDetailActivity;
import com.gamex.activity.ExhibitionDetailActivity;
import com.gamex.activity.FacebookLoginActivity;
import com.gamex.activity.LoginActivity;
import com.gamex.activity.MainActivity;
import com.gamex.activity.RegisterActivity;
import com.gamex.activity.ScanQRActivity;
import com.gamex.activity.SplashActivity;
import com.gamex.activity.SurveyActivity;
import com.gamex.activity.ViewAllExhibitionActivity;
import com.gamex.adapters.EndlessRvExhibitionAdapter;
import com.gamex.adapters.HomeAdapter;
import com.gamex.adapters.ListCompanyAdapter;
import com.gamex.fragments.ExDetailFragment;
import com.gamex.fragments.HistoryFragment;
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
    void inject(EndlessRvExhibitionAdapter endlessRvExhibitionAdapter);
    void inject(ViewAllExhibitionActivity viewAllExhibitionActivity);
    void inject(SurveyActivity surveyActivity);
    void inject(HistoryFragment fragment);

    // splash
    void inject(SplashActivity activity);
    // login
    void inject(LoginActivity activity);
    // fb login
    void inject(FacebookLoginActivity activity);
    // register
    void inject(RegisterActivity activity);
    // main
    void inject(MainActivity activity);
    // scan qr
    void inject(ScanQRActivity activity);
    // event details
    void inject(ExDetailFragment exDetailFragment);
    // company details
    void inject(CompanyDetailActivity activity);
}
