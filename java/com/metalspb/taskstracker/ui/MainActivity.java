package com.metalspb.taskstracker.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.metalspb.taskstracker.R;
import com.metalspb.taskstracker.backgroundTasks.CheckStatusBackground;
import com.metalspb.taskstracker.backgroundTasks.ServiceSample;
import com.metalspb.taskstracker.backgroundTasks.sync.TrackerSyncAdapter;
import com.metalspb.taskstracker.models.storege.TasksEntity;
import com.metalspb.taskstracker.ui.fragments.SettingsFragment_;
import com.metalspb.taskstracker.ui.fragments.TasksFragment_;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;


@EActivity
public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private static final String TAG = "MainLogs";
    private static FragmentManager fragmentManager;
    private TasksEntity tasksEntity;
    private ServiceSample mServiceSample;
    private boolean isBound = false;

    @ViewById(R.id.toolbar_layout)
    Toolbar toolbar;

    ActionBar actionBar;

    @InstanceState
    String toolbarTitle;

    @StringRes(R.string.menu_tasks) String tasksTitle;

    @StringRes(R.string.menu_settings) String settingsTitle;

    @Bean
    @NonConfigurationInstance
    CheckStatusBackground checkStatusBackground;

    @AfterViews
    void setupViews() {
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();


        //setDrawerLayout();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);

       // TrackerSyncAdapter.initializeSyncAdapter(this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            replaceFragment(new TasksFragment_());
        }


    }



    public void onBackStackChanged() {
        Fragment f = fragmentManager.findFragmentById(R.id.main_container);
        if (f != null) {
            setToolbarTitle(f.getClass().getName());
        }
    }


    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void userLogout() {
        App.clearPrefs();
        checkStatusBackground.deleteAllTasksInDB();
        startActivity(new Intent(this, LoginActivity_.class));
    }


    public static void replaceFragment(Fragment fragment) {
        String backStackName = fragment.getClass().getName();

        boolean isFragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);

        if (!isFragmentPopped && fragmentManager.findFragmentByTag(backStackName) == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container, fragment, backStackName);
            transaction.addToBackStack(backStackName);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.commit();
        }
    }

    private void setToolbarTitle(String backStackEntryName) {
        if (backStackEntryName.equals(TasksFragment_.class.getName())) {
            setTitle(tasksTitle);
            toolbarTitle = tasksTitle;
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
            //navigationView.setCheckedItem(R.id.menu_tasks);
        }else if (
                backStackEntryName.equals(SettingsFragment_.class.getName())) {
            setTitle(settingsTitle);
            toolbarTitle = settingsTitle;
           // navigationView.setCheckedItem(R.id.menu_settings);
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof ServiceSample.SampleBinder) {
                mServiceSample = ((ServiceSample.SampleBinder) service).getService();
            }
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceSample = null;
            isBound = false;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_notify:
                if (mServiceSample != null) {
                    mServiceSample.sendNotification();
                    return true;
                }
                break;

            case R.id.menu_settings:
                replaceFragment(new SettingsFragment_());
                break;

            case R.id.menu_quit:
                userLogout();
                break;



        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, ServiceSample.class);
        bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        if (isBound) {
            unbindService(mServiceConnection);
        }
        super.onStop();
    }

}