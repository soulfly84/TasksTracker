package com.metalspb.taskstracker.backgroundTasks.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.metalspb.taskstracker.R;
import com.metalspb.taskstracker.backgroundTasks.CheckStatusBackground;

public class TrackerSyncAdapter extends AbstractThreadedSyncAdapter {


    private final static String LOG_TAG ="SyncAdapter";

    public TrackerSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        final CheckStatusBackground checkStatusBackground = CheckStatusBackground.getInstance();
        Log.d(LOG_TAG, "onPerformSync. Начало синхрониз----");
        Thread t = new Thread(new Runnable() {
            public void run() {
                checkStatusBackground.getTasksFromServer();
            }
        });
        t.start();

    }

    //запуск синхронизации сразу
    private static void syncImmediately(Context context) {
        Log.d(LOG_TAG, "syncImmediately()");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED,
                true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,
                true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    private static Account getSyncAccount(Context context) {
        Log.d(LOG_TAG, "Инициализирую синхр getSyncAccount");
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));

        if (null == accountManager.getPassword(newAccount)) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        Log.d(LOG_TAG, "onAccountCreated");
       final int SYNC_INTERVAL = 60 * 60 * 24;//Синхрониз 1 раз в день
        final int SYNC_FLEXTIME = SYNC_INTERVAL / 3; //Синхроних позже, если нет инета
        TrackerSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount,
                context.getString(R.string.content_authority), true);
        ContentResolver.addPeriodicSync(newAccount,
                context.getString(R.string.content_authority),
                Bundle.EMPTY,
                SYNC_INTERVAL);
        syncImmediately(context);
    }

    private static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }



}

