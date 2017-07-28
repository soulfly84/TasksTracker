package com.metalspb.taskstracker.ui.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    TimePickerDialog.OnTimeSetListener ontimeSet;
    int hour, minute;

    public void setCallBack(TimePickerDialog.OnTimeSetListener ontime) {
        ontimeSet = ontime;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        Calendar calender = Calendar.getInstance();
        hour = calender.get(Calendar.HOUR_OF_DAY);
        minute = calender.get(Calendar.MINUTE);


        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), ontimeSet, hour, minute,
                DateFormat.is24HourFormat(getActivity()));


    }

   /* public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
    }*/


}