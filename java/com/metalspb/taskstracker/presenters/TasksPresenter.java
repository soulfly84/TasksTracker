package com.metalspb.taskstracker.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.metalspb.taskstracker.backgroundTasks.CheckStatusBackground;
import com.metalspb.taskstracker.storege.TasksEntity;
import com.metalspb.taskstracker.utils.Constants;
import com.metalspb.taskstracker.utils.RxSubscryber;
import com.metalspb.taskstracker.views.TaskView;

import org.androidannotations.annotations.EBean;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@EBean
@InjectViewState
public class TasksPresenter extends MvpPresenter<TaskView> {

    private CheckStatusBackground statusBackground;

    private static final String TAG = "TasksFragmentLog";
    private Disposable mDisposable;

    public TasksPresenter() {}

    public void getTasksFromServer() {
        statusBackground = CheckStatusBackground.getInstance();
        Observable<String> loadListObservable = RxSubscryber.loadListObservable();

        getViewState().showProgressBar();
        Log.d(TAG, "connectionServer----------");
        mDisposable = loadListObservable
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> getViewState().showProgressBar())
                .observeOn(Schedulers.io())
                .map(query -> {
                    statusBackground.getTasksFromServer();
                    return statusBackground.getStatus() == Constants.UPDATE_FINISHED;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connected -> {
                    if (connected) {
                        loadTasks(0);
                        getViewState().resetSpinner();
                        getViewState().showSucessToast();
                    } else {
                        getViewState().showServerErrorToast();
                    }
                    getViewState().hideProgressBar();
                });
    }


    public void loadTasks(final int status) {
        Log.d(TAG, "loadTasks----------");
        Observable<String> loadListObservable;
        loadListObservable = RxSubscryber.loadListObservable();
        mDisposable = loadListObservable
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> getViewState().showProgressBar())
                .observeOn(Schedulers.io())
                .map(query -> TasksEntity.selectAll(query, status))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    Log.d(TAG, "loadTasks. result " + result);
                    getViewState().showAllTasksList(result);
                    getViewState().hideProgressBar();
                });
    }

    public void clearResurses() {
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }


}
