package com.metalspb.taskstracker.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.arellomobile.mvp.MvpDelegate;

@SuppressWarnings("unused")
public class MvpAppCompatActivity extends AppCompatActivity {
    private MvpDelegate<? extends MvpAppCompatActivity> mMvpDelegate;
    public static final String LOG_TAG = "MainActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "MvpAppCompatActivity. onCreateView----");
        getMvpDelegate().onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "MvpAppCompatActivity. onStart----");

        getMvpDelegate().onAttach();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "MvpAppCompatActivity. onResume----");
        getMvpDelegate().onAttach();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "MvpAppCompatActivity. onSaveInstanceState----");

        getMvpDelegate().onSaveInstanceState(outState);
        getMvpDelegate().onDetach();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "MvpAppCompatActivity. onStop----");

        getMvpDelegate().onDetach();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "MvpAppCompatActivity. onDestroy----");

        if (isFinishing()) {
            getMvpDelegate().onDestroy();
        }
    }

    /**
     * @return The {@link MvpDelegate} being used by this Activity.
     */
    public MvpDelegate getMvpDelegate() {
        if (mMvpDelegate == null) {
            mMvpDelegate = new MvpDelegate<>(this);
        }
        return mMvpDelegate;
    }
}