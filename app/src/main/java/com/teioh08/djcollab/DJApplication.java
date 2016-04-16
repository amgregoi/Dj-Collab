package com.teioh08.djcollab;

import android.app.Application;

import com.bumptech.glide.Glide;

public class DJApplication extends Application {

    private static DJApplication aInstance;

    public DJApplication() {
        aInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }

    public static synchronized DJApplication getInstance() {
        return aInstance;
    }

}
