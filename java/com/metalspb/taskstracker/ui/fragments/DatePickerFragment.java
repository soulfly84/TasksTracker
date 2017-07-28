package com.metalspb.taskstracker.ui.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class DatePickerFragment extends DialogFragment {
    private static final String TAG = "EditTaskActivity";

    DatePickerDialog.OnDateSetListener ondateSet;
    private int year, month, day;

    public DatePickerFragment() {}

    public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
        ondateSet = ondate;
    }

    @SuppressLint("NewApi")
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");

        Log.d(TAG, "Получил year = " + year);
        Log.d(TAG, "Получил month = " + month);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), ondateSet, year, month, day);
    }


}