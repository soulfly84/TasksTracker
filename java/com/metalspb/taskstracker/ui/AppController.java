package com.metalspb.taskstracker.ui;


import android.app.Application;
import android.content.SharedPreferences;

import com.activeandroid.ActiveAndroid;
import com.metalspb.taskstracker.utils.Constants;


public class AppController extends Application {

    private static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
        sharedPreferences = getSharedPreferences(Constants.SHARE_PREF_FILE_NAME, MODE_PRIVATE);
    }

    public static void saveAuthToken(String token) {
        sharedPreferences
                .edit()
                .putString(Constants.TOKEN_KEY, token)
                .apply();
    }

    public static String getAuthToken() {
        return sharedPreferences
                .getString(Constants.TOKEN_KEY, "");
    }

    public static void clearPrefs() {
        sharedPreferences
                .edit()
                .clear()
                .apply();
    }



}


