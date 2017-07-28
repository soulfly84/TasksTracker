package com.metalspb.taskstracker.models;


import com.google.gson.annotations.SerializedName;


public class TaskModel {
    @SerializedName("message")
    private String message;

    @SerializedName("error")
    private boolean error;

    @SerializedName("taskId") private int id;
    @SerializedName("task") private String task;
    @SerializedName("adress") private String adress;
    @SerializedName("phoneNumber") private String phoneNumber;
    @SerializedName("urgent") private int urgent;
    @SerializedName("status") private int status;
    @SerializedName("finishDate") private String finishDate;
    @SerializedName("updatedDate") private String updatedDate;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public String getAdress() {
        return adress;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public int isUrgent() {
        return urgent;
    }
    public int getStatus() {
        return status;
    }
    public void setTask(String task) {
        this.task = task;
    }
    public String getFinishDate() {
        return finishDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return error;
    }

}
