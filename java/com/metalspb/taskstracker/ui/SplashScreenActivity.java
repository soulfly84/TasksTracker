package com.metalspb.taskstracker.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.metalspb.taskstracker.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.splash_activity)
public class SplashScreenActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SplashScreenActivity";

    @AfterViews
    void ready() {
        Log.d(LOG_TAG, "Получаю token= " + AppController.getAuthToken());
        if (AppController.getAuthToken().equals("")) {
            Intent mainIntent = new Intent(this, LoginActivity_.class);
            this.startActivity(mainIntent);
            this.finish();
        } else {
            Intent mainIntent = new Intent(this, TasksActivity_.class);
            this.startActivity(mainIntent);
            this.finish();
        }


    }



}
