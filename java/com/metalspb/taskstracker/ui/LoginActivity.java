package com.metalspb.taskstracker.ui;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.appcompat.BuildConfig;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.metalspb.taskstracker.R;
import com.metalspb.taskstracker.backgroundTasks.rest.NetworkStatusChecker;
import com.metalspb.taskstracker.backgroundTasks.rest.RestService;
import com.metalspb.taskstracker.backgroundTasks.rest.models.UserLoginModel;
import com.metalspb.taskstracker.utils.Constants;
import com.metalspb.taskstracker.utils.GlobalNotifications;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

@EActivity(R.layout.login_activity)
public class LoginActivity extends AppCompatActivity {

    private final static String LOG_TAG = "LoginActivity";
    private final static int MIN_LENGTH = 2;


    @ViewById(R.id.loginLayout)
    LinearLayout loginLayout;
    @ViewById(R.id.emailET)
    EditText emailET;
    @ViewById(R.id.passwordET)
    EditText passwordET;


    @ViewById(R.id.linkToRegisterScreenBtn)
    Button linkToRegisterScreenBtn;

    @ViewById(R.id.progressTr)
    ProgressBar progressTr;

    @ViewById(R.id.loginBtn)
    Button loginBtn;

    @Click(R.id.loginBtn)
    void loginUser() {

        if (NetworkStatusChecker.isNetworkAvailable(this)) {

            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();
            if (!TextUtils.isEmpty(email) & !TextUtils.isEmpty(password)) {
                if (email.length() >= MIN_LENGTH & password.length() >= MIN_LENGTH) {
                    userLogin(email, password);
                } else {
                    showMinCharacterError();

                }
            } else {
                if (TextUtils.isEmpty(email))
                    showEmptyEmailError();
                if (TextUtils.isEmpty(password))
                    showEmptyPasswordError();
            }
        } else {
            GlobalNotifications.showNoInternetAccessError(this);
        }
    }


    @Click(R.id.linkToRegisterScreenBtn)
    void newRegistrationUser() {
        navigateToRegistrationScreen();
    }

    @Background
    void userLogin(String email, String password) {
        showProgress();
        try {
            UserLoginModel userLoginModel = RestService.getInstance().login(email, password);
            String statusMsg = userLoginModel.getStatus();
            if (statusMsg.equals(Constants.SUCCESSFULLY_LOGINNED)) {
                App.saveAuthToken(userLoginModel.getApiKey());
                Log.d(LOG_TAG, "Срхраняю token= " + userLoginModel.getApiKey());
                navigateToMainScreen();
            } else {
                if (statusMsg.equals(Constants.STATUS_WRONG_CREDENTIALS))
                    showCredsWrong();
                if (statusMsg.equals(Constants.STATUS_ERROR))
                    showUnknownError();
            }
        } catch (IOException e) {
            showUnknownError();
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        hideProgress();
    }


    @UiThread
    void showProgress() {
        progressTr.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.GONE);
    }

    @UiThread
    void hideProgress() {
        progressTr.setVisibility(View.GONE);
        loginBtn.setVisibility(View.VISIBLE);
    }

    @UiThread
    void navigateToMainScreen() {
        startActivity(new Intent(this, MainActivity_.class));
    }

    @UiThread
    void navigateToRegistrationScreen() {
        startActivity(new Intent(this, RegistrationActivity_.class));
    }


    @UiThread
    void showCredsWrong() {
        Toast.makeText(getApplicationContext(),
                getString(R.string.server_status_wrong_creds), Toast.LENGTH_LONG)
                .show();
    }

    @UiThread
    void showUnknownError() {
        Toast.makeText(getApplicationContext(),
                getString(R.string.registration_server_other_error), Toast.LENGTH_LONG)
                .show();
    }


    @UiThread
    void showEmptyEmailError() {
        Toast.makeText(getApplicationContext(),
                getString(R.string.registration_login_empty), Toast.LENGTH_LONG)
                .show();
    }

    @UiThread
    void showEmptyPasswordError() {
        Toast.makeText(getApplicationContext(),
                getString(R.string.registration_password_empty), Toast.LENGTH_LONG)
                .show();
    }

    @UiThread
    void showMinCharacterError() {
        Toast.makeText(getApplicationContext(),
                getString(R.string.registration_text_error), Toast.LENGTH_LONG)
                .show();

    }

}