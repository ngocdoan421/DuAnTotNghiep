package com.example.testt;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SessionManager.init(this);
    }
}
