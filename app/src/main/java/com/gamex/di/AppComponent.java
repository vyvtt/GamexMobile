package com.gamex.di;

import com.gamex.activity.ExhibitionDetailActivity;
import com.gamex.adapters.HomeAdapter;
import com.gamex.fragments.ExDetailFragment;
import com.gamex.fragments.ExDetailListCompanyFragment;
import com.gamex.fragments.HomeFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface AppComponent {
    void inject(HomeFragment homeFragment);
    void inject(ExDetailFragment exDetailFragment);
    void inject(ExDetailListCompanyFragment detailListCompanyFragment);
    void inject(ExhibitionDetailActivity exhibitionDetailActivity);
    void inject(HomeAdapter homeAdapter);
}
