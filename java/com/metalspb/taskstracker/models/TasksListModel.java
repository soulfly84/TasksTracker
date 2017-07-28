package com.metalspb.taskstracker.models;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TasksListModel {
    @SerializedName("error")
    private boolean error;
    @SerializedName("tasks")
    private List<TaskModel> data = new ArrayList<>();

    public List<TaskModel> getData() {
        return data;
    }

    public boolean isError() {
        return error;
    }
}
