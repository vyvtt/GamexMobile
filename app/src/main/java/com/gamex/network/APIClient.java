package com.gamex.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class APIClient {
    private static Retrofit retrofit;
    private static OkHttpClient okHttpClient;

    public static Retrofit getRetrofitInstance() {
//        File httpCacheDirectory = new File(GamexApplication.getAppContext().getCacheDir(), "http-cache");
//        long cacheSize = (5 * 1024 * 1024);
//        Cache cache = new Cache(httpCacheDirectory, cacheSize);
//
//        if (okHttpClient == null) {
//            okHttpClient = new OkHttpClient.Builder()
//                    .addNetworkInterceptor(new CacheInterceptor())
//                    .cache(cache)
//                    .build();
//        }
//        if (retrofit == null) {
//            retrofit = new retrofit2.Retrofit.Builder()
//                    .baseUrl(Constant.BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .client(okHttpClient)
//                    .build();
//        }
//        return retrofit;
        return null;
    }

//    public static boolean isOnline() {
//        // Ping server to check internet connection
//        Runtime runtime = Runtime.getRuntime();
//        try {
//            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
//            int     exitValue = ipProcess.waitFor();
//            return (exitValue == 0);
//        }
//        catch (IOException e)          { e.printStackTrace(); }
//        catch (InterruptedException e) { e.printStackTrace(); }
//
//        return false;
//    }
}
