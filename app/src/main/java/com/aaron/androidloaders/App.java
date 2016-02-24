package com.aaron.androidloaders;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Aaron on 2016/2/24.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
