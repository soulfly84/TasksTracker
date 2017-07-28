package com.metalspb.taskstracker.backgroundTasks.rest.models;

import com.google.gson.annotations.SerializedName;

public class UserRegistrationModel {
    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;


    public boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }




}


