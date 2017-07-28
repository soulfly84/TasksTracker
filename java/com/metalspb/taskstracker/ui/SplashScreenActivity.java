package com.metalspb.taskstracker.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.metalspb.taskstracker.R;
import com.metalspb.taskstracker.backgroundTasks.CheckStatusBackground;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;

@EActivity(R.layout.splash_activity)
public class SplashScreenActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SplashScreenActivity";
    @NonConfigurationInstance
    @Bean
    CheckStatusBackground taskBackground;


    @AfterViews
        //@Background(delay = 3000)
    void ready() {
        Log.d(LOG_TAG, "Получаю token= " + App.getAuthToken());
        if (App.getAuthToken().equals("")) {
            Intent mainIntent = new Intent(SplashScreenActivity.this, LoginActivity_.class);
            SplashScreenActivity.this.startActivity(mainIntent);
            SplashScreenActivity.this.finish();
        } /*else if (NetworkStatusChecker.isNetworkAvailable(this)) {
            taskBackground.getTasksFromServer();
            Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity_.class);
            SplashScreenActivity.this.startActivity(mainIntent);
            SplashScreenActivity.this.finish();
        }*/ else {
            //showNoSyncAccessError();
            Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity_.class);
            SplashScreenActivity.this.startActivity(mainIntent);
            SplashScreenActivity.this.finish();
        }


    }

    void showNoSyncAccessError() {
        Toast.makeText(getApplicationContext(),
                getString(R.string.internet_sync_error), Toast.LENGTH_LONG)
                .show();

    }

    /*@Override
    protected void onPause() {
        super.onPause();
        finish();
    }*/
}
