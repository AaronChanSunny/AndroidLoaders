package com.aaron.androidloaders.network;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Aaron on 2016/2/24.
 */
public class ApiProvider {

    private static final String BASE_URL = "https://api.douban.com";
    private static ApiProvider mInstance;

    private final Context mContext;

    public static ApiProvider getInstance(Context context) {
       if (mInstance == null) {
           mInstance = new ApiProvider(context);
       }

        return mInstance;
    }

    private ApiProvider(Context context) {
        mContext = context;
    }

    public ApiService getApiService() {
        Gson gson = provideGson();
        Cache cache = provideOkHttpCache((Application) mContext.getApplicationContext());
        OkHttpClient client = provideOkHttpClient(cache);

        Retrofit retrofit = provideRetrofit(gson, client);
        return retrofit.create(ApiService.class);
    }

    private Cache provideOkHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(application.getCacheDir(), cacheSize);
        return cache;
    }

    private Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    private OkHttpClient provideOkHttpClient(Cache cache) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .cache(cache)
                .build();

        return client;
    }

    private Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .build();
        return retrofit;
    }

}
