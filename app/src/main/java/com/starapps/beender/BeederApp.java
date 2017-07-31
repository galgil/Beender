package com.starapps.beender;

import android.app.Application;

import com.starapps.beender.utils.AppUtil;

public class BeederApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtil.showKeyHash(this);
    }
}
