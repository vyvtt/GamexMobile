package com.gamex.di;

import com.gamex.fragments.HomeFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface AppComponent {
    void inject(HomeFragment fragment);
}
