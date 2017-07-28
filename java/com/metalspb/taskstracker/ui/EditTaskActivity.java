package com.metalspb.taskstracker.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.metalspb.taskstracker.R;
import com.metalspb.taskstracker.backgroundTasks.CheckStatusBackground;
import com.metalspb.taskstracker.backgroundTasks.rest.NetworkStatusChecker;
import com.metalspb.taskstracker.models.storege.TasksEntity;
import com.metalspb.taskstracker.utils.Constants;
import com.metalspb.taskstracker.utils.GlobalNotifications;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@EActivity(R.layout.task_view_activity)
public class EditTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "EditTaskActivity";

    @Extra("id")
    int taskId;
    @Extra("taskTitle")
    String taskTitle;
    @Extra("adress")
    String adress;
    @Extra("phoneNumber")
    String phoneNumber;
    @Extra("urgent")
    int urgent;
    @Extra("taskStatus")
    int taskStatus;
    @Extra("finishDate")
    String finishDate;

    int taskStatusChanged;
    @ViewById(R.id.descriptionET)
    TextView descriptionET;
    @ViewById(R.id.adressTV)
    TextView adressTV;
    @ViewById(R.id.datePickerBtn)
    TextView datePickerTv;
    @ViewById(R.id.urgentTv)
    TextView urgentTv;

    @ViewById(R.id.phoneNumberTV)
    TextView phoneNumberTV;
    @ViewById(R.id.statusSpinner)
    Spinner statusSpinner;

    @ViewById(R.id.toolbar_layout)
    Toolbar toolbar;

    @ViewById(R.id.progressTr)
    ProgressBar progressTr;

    MenuItem item_save;

    @Bean
    CheckStatusBackground checkStatusBackground;

    @AfterViews
    void load() {
        setActionBar();
        descriptionET.setText(taskTitle);
        adressTV.setText(adress);
        if (urgent == 1) {
            urgentTv.setVisibility(View.VISIBLE);
        } else
            urgentTv.setVisibility(View.INVISIBLE);

        adressTV.setText(adress);
        datePickerTv.setText(finishDate);
        phoneNumberTV.setText(phoneNumber);

        Log.d(TAG, "Получил Дату = " + datePickerTv.getText().toString());
        Log.d(TAG, "Получил id = " + taskId);
        Log.d(TAG, "Получил taskStatus = " + taskStatus);

        taskStatusChanged = taskStatus;
        statusSpinner.setSelection(taskStatusChanged);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case Constants.TASK_NEW:
                        taskStatusChanged = 0;
                        //statusSpinner.setSelection(taskStatus);
                        break;
                    case Constants.TASK_PENDING:
                        taskStatusChanged = 1;
                        break;
                    case Constants.TASK_DONE:
                        taskStatusChanged = 2;
                        break;
                }
                checkSaveBtnVisability();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        //toolbar.setTitle("Новая задача");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }



    @Click(R.id.adressTV)
    public void showMap(View v) {
        Log.d(TAG, "startMapIntent Получил adress = " + adress);

        Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4192?q=" + Uri.encode(adress));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else
            notifyInstallMapsApp();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_task_menu, menu);
        item_save = menu.findItem(R.id.item_save);

        checkSaveBtnVisability();

        return (super.onCreateOptionsMenu(menu));
    }

    private void checkSaveBtnVisability() {
        if (taskStatusChanged == taskStatus) {
            item_save.setVisible(false);
        } else
            item_save.setVisible(true);
    }

    @OptionsItem(R.id.home)
    void backBtn() {
        if (NetworkStatusChecker.isNetworkAvailable(this) && taskStatusChanged != taskStatus) {
            postUpdatedTaskToServer();
        } else {
            GlobalNotifications.showNoInternetAccessError(this);
        }
        onBackPressed();
        finish();
    }

    @OptionsItem(R.id.item_save)
    void saveBtn() {
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            postUpdatedTaskToServer();

        } else {
            GlobalNotifications.showNoInternetAccessError(this);
        }
    }
   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            return true;
        }
        switch (item.getItemId()) {
            case R.id.item_save:


                break;

        }
        return (super.onOptionsItemSelected(item));
    }
*/

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_fade_in, R.anim.exit_push_out);
    }

    @Background
    public void postUpdatedTaskToServer() {
        Log.d(TAG, "postUpdatedTaskToServer---");
        showProgressBar();
        checkStatusBackground.updateTaskAtServer(taskId, taskStatusChanged, getDateTime());
        //String statusMsg = checkStatusBackground.statusMsg();
        if (checkStatusBackground.statusMsg().equals(Constants.STATUS_TASK_UPDATED)) {
            TasksEntity.updateById(taskId, taskStatusChanged, getDateTime());
            Log.d(TAG, "Обновил в бд = " + taskId + " - " + taskStatusChanged);
            notifyUpdated();
        } else if (checkStatusBackground.statusMsg().equals(Constants.STATUS_TASK_FAILED_TO_UPDATE)) {
            Log.d(TAG, "Не Сохранил  задачу = " + taskId);
            notifyErorUpdated();
        }
        hideProgressBar();
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @UiThread
    void notifyUpdated() {
        Toast.makeText(getBaseContext(), R.string.toast_updated, Toast.LENGTH_LONG).show();
        backBtn();
        finish();
    }

    @UiThread
    void notifyErorUpdated() {
        Toast.makeText(this, R.string.toast_update_error, Toast.LENGTH_LONG).show();
    }

    @UiThread
    void notifyInstallMapsApp() {
        Toast.makeText(this, R.string.toast_install_maps, Toast.LENGTH_LONG).show();
    }


    @UiThread
    public void showProgressBar() {
        Log.d(TAG, "showProgressBar");
        progressTr.setVisibility(View.VISIBLE);

    }


    @UiThread
    public void hideProgressBar() {
        Log.d(TAG, "hideProgressBar");
        progressTr.setVisibility(View.GONE);
    }


}
