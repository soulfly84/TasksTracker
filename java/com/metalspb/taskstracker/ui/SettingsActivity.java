package com.metalspb.taskstracker.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.metalspb.taskstracker.R;
import com.metalspb.taskstracker.ui.fragments.SettingsFragment;

//@EActivity
public class SettingsActivity extends PreferenceActivity {

    private static final boolean DEFAULT_VALUE = true;
    private static final String LOG_TAG = SettingsFragment.class.getSimpleName();



    private SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.notifications_preferences);
        }
    }



}
