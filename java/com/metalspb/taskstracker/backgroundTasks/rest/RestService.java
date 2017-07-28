package com.metalspb.taskstracker.backgroundTasks.rest;


import android.support.annotation.NonNull;
import android.util.Log;

import com.metalspb.taskstracker.backgroundTasks.rest.models.UserLoginModel;
import com.metalspb.taskstracker.backgroundTasks.rest.models.UserLogoutModel;
import com.metalspb.taskstracker.backgroundTasks.rest.models.UserRegistrationModel;
import com.metalspb.taskstracker.models.TaskModel;
import com.metalspb.taskstracker.models.TasksListModel;

import java.io.IOException;


public final class RestService {

    private final static String LOG_TAG = "TasksEntity";

    private RestClient restClient;
    private static RestService instance;

    public static RestService getInstance() {
        if(instance == null){
            instance = new RestService();
        }
        return instance;
    }



    public RestService() {
        restClient = new RestClient();
    }

    public UserRegistrationModel register(@NonNull String name, @NonNull String email, @NonNull String password) throws IOException {
        Log.d(LOG_TAG, "UserRegistrationModel, передаю: " + name + " " + email + " "+ password);
        return restClient.getTasksAPI()
                .registerUser(name, email, password)
                .execute()
                .body();
    }

    public UserLoginModel login(@NonNull String login,
                                @NonNull String password) throws IOException {
        return restClient
                .getTasksAPI()
                .login(login, password)
                .execute()
                .body();
    }

    public UserLogoutModel logoutUser() throws IOException {
        return restClient
                .getTasksAPI()
                .logout()
                .execute()
                .body();
    }
    public TasksListModel getAllTasks(@NonNull String token) throws IOException {
        return restClient
                .getTasksAPI()
                .getTasks(token)
                .execute()
                .body();
    }



    public TaskModel updateTask(@NonNull String token, int task_id, int status, String updatedDate) throws IOException {
        return restClient.getTasksAPI()
                .updateTask(token, task_id, status, updatedDate)
                .execute()
                .body();
    }

    public TaskModel deleteTask(@NonNull String token, int task_id) throws IOException {
        return restClient
                .getTasksAPI()
                .deleteTask(token, task_id)
                .execute()
                .body();

    }



    public TaskModel postNewTask(@NonNull String token, String task, String adress, String tel, int urgent, int status, String finishDate, String updatedDate) throws IOException {
        return restClient
                .getTasksAPI()
                .addTask(token, task, adress, tel, urgent, status, finishDate, updatedDate)
                .execute()
                .body();

    }

}
