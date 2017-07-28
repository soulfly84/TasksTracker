package com.metalspb.taskstracker.backgroundTasks.rest;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RestClient {

    private static final String BASE_URL = "http://p.serveronline.net";
    private TasksAPI tasksAPI;

    public RestClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();//для вывода логов
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        tasksAPI = retrofit.create(TasksAPI.class);
    }

    public TasksAPI getTasksAPI() {
        return tasksAPI;
    }
}
