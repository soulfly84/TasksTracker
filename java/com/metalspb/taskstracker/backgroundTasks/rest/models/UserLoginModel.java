package com.metalspb.taskstracker.backgroundTasks.rest.models;


import com.google.gson.annotations.SerializedName;

public class UserLoginModel {

    @SerializedName("message")
    private String status;

    @SerializedName("error")
    private boolean error;

    @SerializedName("email")
    private String email;


    @SerializedName("apiKey")
    private String apiKey;

    public String getStatus() {
        return status;
    }

    public boolean getError () {
        return error;
    }

    public String getEmail ()
    {
        return email;
    }

    public String getApiKey ()
    {
        return apiKey;
    }


}

