package com.metalspb.taskstracker.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.metalspb.taskstracker.backgroundTasks.CheckStatusBackground;
import com.metalspb.taskstracker.models.storege.TasksEntity;
import com.metalspb.taskstracker.utils.Constants;
import com.metalspb.taskstracker.utils.RxSubscryber;
import com.metalspb.taskstracker.views.TaskView;

import org.androidannotations.annotations.EBean;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@EBean
@InjectViewState
public class TasksPresenter extends MvpPresenter<TaskView> {

    CheckStatusBackground statusBackground;

    private static final String TAG = "TasksFragmentLog";
    private Disposable mDisposable;

    public TasksPresenter() {
    }


    public void getTasksFromServer() {
        statusBackground = CheckStatusBackground.getInstance();
        Observable<String> loadListObservable = RxSubscryber.loadListObservable();

        getViewState().showProgressBar();

        Log.d(TAG, "connectionServer----------");
        mDisposable = loadListObservable
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) {

                        getViewState().showProgressBar();
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<String, Boolean>() {
                    @Override
                    public Boolean apply(String query) {

                        statusBackground.getTasksFromServer();
                        //String statusMsg = statusBackground.statusMsg();

                        return statusBackground.getStatus() == Constants.UPDATE_FINISHED;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean connected) {
                        if (connected) {
                            loadTasks(0);
                            getViewState().resetSpinner();
                            getViewState().showSucessToast();
                        } else {
                            getViewState().showServerErrorToast();
                        }
                        getViewState().hideProgressBar();
                    }
                });
    }


    public void loadTasks(final int status) {
        Log.d(TAG, "loadTasks----------");
        Observable<String> loadListObservable;
        loadListObservable = RxSubscryber.loadListObservable();
        mDisposable = loadListObservable
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) {

                        getViewState().showProgressBar();
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<String, List<TasksEntity>>() {
                    @Override
                    public List<TasksEntity> apply(String query) {

                        return TasksEntity.selectAll(query, status);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<TasksEntity>>() {
                    @Override
                    public void accept(List<TasksEntity> result) {
                        Log.d(TAG, "loadTasks. result " + result);
                        getViewState().showAllTasksList(result);
                        getViewState().hideProgressBar();
                    }
                });
    }

    public void clearResurses() {
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }


}
