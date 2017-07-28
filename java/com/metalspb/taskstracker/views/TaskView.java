package com.metalspb.taskstracker.views;

import com.arellomobile.mvp.MvpView;
import com.metalspb.taskstracker.models.storege.TasksEntity;

import java.util.List;


public interface TaskView extends MvpView {
    void showAllTasksList(List<TasksEntity> tasksList);
    void loadAllTasks();
    void resetSpinner();
    void showProgressBar();
    void hideProgressBar();
    void showSucessToast();
    void showServerErrorToast();
}