package com.metalspb.taskstracker.backgroundTasks.rest;

import com.metalspb.taskstracker.backgroundTasks.rest.models.UserLoginModel;
import com.metalspb.taskstracker.backgroundTasks.rest.models.UserLogoutModel;
import com.metalspb.taskstracker.backgroundTasks.rest.models.UserRegistrationModel;
import com.metalspb.taskstracker.models.TaskModel;
import com.metalspb.taskstracker.models.TasksListModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;


public interface TasksAPI {

    @GET("/android_login_api/index.php/register")
    Call<UserRegistrationModel> registerUser(@Query("name") String name,
                                             @Query("email") String email,
                                             @Query("password") String password);

    @GET("/android_login_api/index.php/login")
    Call<UserLoginModel> login(@Query("email") String email,
                               @Query("password") String password);

    @GET("/android_login_api/index.php/logout")
    Call<UserLogoutModel> logout();

    //Add one task
    @GET("/android_login_api/index.php/newtask")
    Call<TaskModel> addTask(@Header("Authorization") String token,
                            @Query("task") String task,
                            @Query("adress") String adress,
                            @Query("phoneNumber") String tel,
                            @Query("urgent") int urgent,
                            @Query("status") int status,
                            @Query("finishDate") String finishDate,
                            @Query("updatedDate") String updatedDate);


    @GET("/android_login_api/index.php/tasks")
    @Headers({ "Content-Type: application/json; charset=windows-1251"})
    Call<TasksListModel> getTasks(@Header("Authorization") String token);


    @GET("/android_login_api/index.php/updatetask")
    Call<TaskModel> updateTask(@Header("Authorization") String token,
                               @Query("taskId") int task_id,
                               @Query("status") int status,
                               @Query("updatedDate") String updatedDate);

    @GET("/android_login_api/index.php/deletetask")
    Call<TaskModel> deleteTask(@Header("Authorization") String token,
                               @Query("taskId") int task_id);

}
