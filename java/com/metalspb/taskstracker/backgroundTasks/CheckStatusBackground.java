package com.metalspb.taskstracker.backgroundTasks;


import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.metalspb.taskstracker.backgroundTasks.rest.RestService;
import com.metalspb.taskstracker.models.TaskModel;
import com.metalspb.taskstracker.models.TasksListModel;
import com.metalspb.taskstracker.storege.TasksEntity;
import com.metalspb.taskstracker.ui.AppController;
import com.metalspb.taskstracker.utils.Constants;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@EBean
public class CheckStatusBackground {
    private final static String TAG = "TasksEntity";
    private RestService restService = RestService.getInstance();

    private HashMap<Integer, String> existedIdList = null;
    public int getStatus() {
        return status;
    }
    private int status = 0;
    private final int STATUS_IDLE = 1;
    private final int STATUS_UPDATE = 2;
    private final int STATUS_CREATE = 3;


    private static CheckStatusBackground instance;

    public static CheckStatusBackground getInstance() {
        if (instance == null) {
            instance = new CheckStatusBackground();
        }
        return instance;
    }




    private String token = AppController.getAuthToken();
    private TaskModel mTaskModel = null;

    public void getTasksFromServer() {
        status = Constants.UPDATE_STARTED;

        String token = AppController.getAuthToken();
        Log.d(TAG, "CheckStatusBackground. getTasksFromServer()--- token " + token);
        try {
            TasksListModel tasksListModel = restService.getAllTasks(token);
            checkExisted();
            List<TaskModel> givenTasksList = tasksListModel.getData();
            Log.d(TAG, "TasksPresenter. Список givenTasksList = " + givenTasksList);

            for (TaskModel taskModel : givenTasksList) {
                mTaskModel = taskModel;
                int serverId = taskModel.getId();
                String finishDate = taskModel.getFinishDate();
                String updatedDate = taskModel.getUpdatedDate();
                String serverTask = taskModel.getTask();
                String adress = taskModel.getAdress();
                String phoneNumber = taskModel.getPhoneNumber();
                int serverStatus = taskModel.getStatus();

                if (getStatus(serverId, updatedDate) == STATUS_CREATE) {

                    TasksEntity newTasksItem = new TasksEntity(serverId, serverTask, adress, phoneNumber, taskModel.isUrgent(), serverStatus, finishDate, updatedDate);
                    newTasksItem.save();

                    Log.d(TAG, "ДоБавляЮ новуЮ задачу updatedDate " + updatedDate);
                } else if (getStatus(serverId, updatedDate) == STATUS_UPDATE) {
                    TasksEntity.updateFromServerById(serverId, serverTask, adress, phoneNumber, taskModel.isUrgent(), serverStatus, finishDate, updatedDate);
                    Log.d(TAG, "Обновляю текущую задачу id " + serverId);

                } else if (getStatus(serverId, updatedDate) == STATUS_IDLE) {
                    Log.d(TAG, "Все обновлено!");
                }

            }
            status = Constants.UPDATE_FINISHED;
            existedIdList = null;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Background
    public void deleteAllTasksInDB() {
        TasksEntity.deleteAllTasks();

    }


    //@Background
    public void updateTaskAtServer(int taskId, int taskStatus, String updatedDate) {
        try {
            mTaskModel = restService.updateTask(AppController.getAuthToken(), taskId, taskStatus, updatedDate);
            Log.d(TAG, "updateTaskAtServer--. mTaskModel = " + mTaskModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createTaskAtServer(String task, String adress, String phoneNumber, int urgent, int status, String finishDate, String updatedDate) {
        try {
            mTaskModel = restService.postNewTask(AppController.getAuthToken(), task, adress, phoneNumber, urgent, status, finishDate, updatedDate);
            Log.d(TAG, "updateTaskAtServer--. mTaskModel = " + mTaskModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String statusMsg() {
        if (mTaskModel != null) {
            return mTaskModel.getMessage();
        } else
            return Constants.STATUS_TASK_FAILED_TO_UPDATE;

    }

    public boolean isError() {
        return mTaskModel.isError();
    }



    @Background
    public void deleteTaskAtServer(int task_id) {

        Log.d(TAG, "deleteTaskAtServer--. Удаляю task = " + task_id);
        try {
            restService.deleteTask(token, task_id);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void checkExisted() {
        Log.d(TAG, "checkExisted-- ");
        existedIdList = new HashMap<Integer, String>();
        ActiveAndroid.beginTransaction();

        for (TasksEntity allTasks : TasksEntity.selectAll("", 0)) {
            int existedId = allTasks.getmId();
            String existedDate = allTasks.getUpdatedDate();
            String existedTask = allTasks.getTask();
            existedIdList.put(existedId, existedDate);
            Log.d(TAG, "Добавил в existedIdList: " + existedId + " - " + existedDate + " - " + existedTask);
        }
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();

    }


    private int getStatus(int serverId, String serverDate) {
        Log.d(TAG, "getStatus---- serverId " + serverId);

        if (existedIdList != null && existedIdList.size() != 0 && serverId != 0 && serverDate != null) {
            if (!existedIdList.containsKey(serverId)) {
                Log.d(TAG, serverId + "- Создаю новую STATUS_CREATE. создаю " + serverId);
                status = STATUS_CREATE;
            } else if (existedIdList.containsKey(serverId) && existedIdList.containsValue(serverDate)) {
                Log.d(TAG, serverId + "- Id и дата не обновились. STATUS_IDLE ");
                status = STATUS_IDLE;
            } else if (existedIdList.containsKey(serverId) && !existedIdList.containsValue(serverDate)) {
                Log.d(TAG, serverId + "- Дата обновилась. STATUS_UPDATE");
                status = STATUS_UPDATE;
            }
        } else {
            Log.d(TAG, "Таблица пуста. Создаю новую STATUS_CREATE. создаю");
            status = STATUS_CREATE;
        }
        return status;
    }


}
