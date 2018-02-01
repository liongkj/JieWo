package com.jiewo.kj.jiewo;

import android.app.Application;

import com.mlykotom.valifi.ValiFi;

/**
 * Created by khaij on 01/02/2018.
 */

public class ApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ValiFi.install(this);
    }
}
