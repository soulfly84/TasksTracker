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
import com.metalspb.taskstracker.backgroundTasks.rest.models.UserRegistrationModel;
import com.metalspb.taskstracker.utils.Constants;
import com.metalspb.taskstracker.utils.GlobalNotifications;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;


@EActivity(R.layout.register_activity)
public class RegistrationActivity extends AppCompatActivity {

    private final static String LOG_TAG = "RegistrationActivity";
    private final static int MIN_LENGTH = 2;

    @ViewById(R.id.registrationLayout)
    LinearLayout registrationLayout;
    @ViewById(R.id.nameTV)
    EditText nameTV;

    @ViewById(R.id.emailTV)
    EditText emailTV;

    @ViewById(R.id.passwordTV)
    EditText passwordTV;

    @ViewById(R.id.registerBtn)
    Button btnRegistration;

    @ViewById(R.id.toLoginScreenBtn)
    Button toLoginScreenBtn;


    @ViewById(R.id.progressTr)
    ProgressBar progressTr;

    @Click(R.id.registerBtn)
    void registerBtnClicked() {
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            String name = nameTV.getText().toString();
            String email = emailTV.getText().toString();
            String password = passwordTV.getText().toString();
            if (TextUtils.isEmpty(email)) {
                showLoginEmptyError();
            } else if (TextUtils.isEmpty(password)) {
                showRegistrationErrorPassword();
            } else {
                if (email.length() <= MIN_LENGTH && password.length() <= MIN_LENGTH) {
                    showMinCharacterError();
                } else if (!isValidEmail(email)) {
                    showValidEmailError();
                } else {
                    register(name, email, password);
                }
            }


        } else {
            GlobalNotifications.showNoInternetAccessError(this);
        }
    }

    @Background
    void register(String name, String email, String password) {
        Log.d(LOG_TAG, "register()----");
        showProgress();
        try {
            UserRegistrationModel registrationModel = RestService.getInstance().register(name, email, password);
            String errorMsg = registrationModel.getMessage();

            if (errorMsg.equals(Constants.SUCCESSFULLY_REGISTERED)) {
                navigateToMainScreen();
            } else if (errorMsg.equals(Constants.STATUS_LOGIN_BUSY_ALREADY)) {
                showLoginBusy();
            }
        } catch (IOException e) {
            showUnknownError();
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        hideProgress();
    }

    //Email validation
    public final boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Click(R.id.toLoginScreenBtn)
    void onRegistrationCancelClick() {
        navigateToLoginScreen();
    }

    @UiThread
    void navigateToLoginScreen() {
        startActivity(new Intent(this, LoginActivity_.class));
    }

    @UiThread
    void navigateToMainScreen() {
        startActivity(new Intent(this, TasksActivity_.class));
    }

    @UiThread
    void showLoginBusy() {
        Toast.makeText(getApplicationContext(),
                getString(R.string.registration_server_status_busy_login_error), Toast.LENGTH_LONG)
                .show();
    }

    @UiThread
    void showUnknownError() {
        Toast.makeText(getApplicationContext(),
                getString(R.string.registration_server_other_error), Toast.LENGTH_LONG)
                .show();
    }

    @UiThread
    void showLoginEmptyError() {
        Toast.makeText(getApplicationContext(),
                getString(R.string.registration_login_empty), Toast.LENGTH_LONG)
                .show();


    }

    @UiThread
    void showRegistrationErrorPassword() {
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

    @UiThread
    void showValidEmailError() {
        Toast.makeText(getApplicationContext(),
                getString(R.string.valid_email_error), Toast.LENGTH_LONG)
                .show();

    }


    @UiThread
    void showProgress() {
        progressTr.setVisibility(View.VISIBLE);
        btnRegistration.setVisibility(View.GONE);
    }

    @UiThread
    void hideProgress() {
        progressTr.setVisibility(View.GONE);
        btnRegistration.setVisibility(View.VISIBLE);
    }
}
