package com.metalspb.taskstracker.utils;


import android.content.Context;
import android.widget.Toast;

import com.metalspb.taskstracker.R;

public class GlobalNotifications {
    public static void showNoInternetAccessError(Context context) {
        Toast.makeText(context, context.getString(R.string.internet_error), Toast.LENGTH_LONG).show();
    }


}
