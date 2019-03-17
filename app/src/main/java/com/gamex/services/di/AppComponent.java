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
import com.gamex.fragments.BookmarkExhibitionFragment;
import com.gamex.fragments.ChangePasswordFragment;
import com.gamex.fragments.EditProfileFragment;
import com.gamex.fragments.ExDetailFragment;
import com.gamex.fragments.HistoryFragment;
import com.gamex.fragments.HomeFragment;
import com.gamex.fragments.RewardExchangeFragment;
import com.gamex.fragments.RewardHistoryFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface AppComponent {

    void inject(HomeFragment fragment);
    void inject(ChangePasswordFragment fragment);
    void inject(HistoryFragment fragment);
    void inject(ExDetailFragment fragment);
    void inject(EditProfileFragment fragment);
    void inject(RewardExchangeFragment fragment);
    void inject(RewardHistoryFragment fragment);
    void inject(BookmarkExhibitionFragment fragment);

    void inject(HomeAdapter adapter);
    void inject(ListCompanyAdapter adapter);
    void inject(EndlessRvExhibitionAdapter adapter);

    void inject(ExhibitionDetailActivity activity);
    void inject(ViewAllExhibitionActivity activity);
    void inject(SurveyActivity activity);
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
    // company details
    void inject(CompanyDetailActivity activity);
}
