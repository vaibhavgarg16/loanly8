package com.loan8.loan8.utils;

import android.app.Application;
import com.preference.PowerPreference;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PowerPreference.init(this);
    }
}
