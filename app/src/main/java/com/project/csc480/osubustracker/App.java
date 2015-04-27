package com.project.csc480.osubustracker;

import android.app.Application;
import android.content.Context;

/**
 * Created by Lucas on 4/25/2015.
 */
public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}