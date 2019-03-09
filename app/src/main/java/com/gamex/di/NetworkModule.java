package com.gamex.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gamex.network.DataService;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {
    private String baseURL;

    public NetworkModule(String baseURL) {
        this.baseURL = baseURL;
    }

    @Provides
    @Singleton
        // Application reference must come from AppModule.class
    SharedPreferences providesSharedPreferences(Application application) {
        return application.getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    Cache provideOkHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(application.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.cache(cache);
        client.addInterceptor(chain -> {
            Response response = chain.proceed(chain.request());
            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(15, TimeUnit.MINUTES) // 15 minutes cache
                    .build();
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", cacheControl.toString())
                    .build();
        });
        return client.build();
    }

    @Provides
    @Named ("cache")
    @Singleton
    Retrofit provideCacheRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseURL)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Named ("no-cache")
    @Singleton
    Retrofit provideNoCacheRetrofit(Gson gson) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseURL)
                .build();
    }

    @Provides
    @Named ("cache")
    @Singleton
    DataService provideCacheDataService(@Named("cache") Retrofit retrofit) {
        return retrofit.create(DataService.class);
    }

    @Provides
    @Named ("no-cache")
    @Singleton
    DataService provideNoCacheDataService(@Named("no-cache") Retrofit retrofit) {
        return retrofit.create(DataService.class);
    }

    @Provides @Singleton
    Picasso providePicasso(Application app) {
        return new Picasso.Builder(app)
                .loggingEnabled(true)
                .downloader(new OkHttp3Downloader(app))
                .build();
    }
}
