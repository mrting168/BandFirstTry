package com.CustomApplication;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * Created by mrting on 5/20/2016.
 */
public class CustomApplication extends Application {
    private static Context context;
    public void onCreate(){
        context=getApplicationContext();
    }

    public static Context getCustomAppContext(){
        return context;
    }
}
