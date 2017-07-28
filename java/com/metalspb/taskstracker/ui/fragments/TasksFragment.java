package com.metalspb.taskstracker.ui.fragments;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.metalspb.taskstracker.R;
import com.metalspb.taskstracker.backgroundTasks.rest.NetworkStatusChecker;
import com.metalspb.taskstracker.models.storege.TasksEntity;
import com.metalspb.taskstracker.presenters.TasksPresenter;
import com.metalspb.taskstracker.ui.EditTaskActivity_;
import com.metalspb.taskstracker.ui.NewTaskActivity_;
import com.metalspb.taskstracker.ui.adapter.TasksAdapter;
import com.metalspb.taskstracker.utils.MvpFragment;
import com.metalspb.taskstracker.views.ClickListener;
import com.metalspb.taskstracker.views.TaskView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EFragment(R.layout.tasks_fragment)
@OptionsMenu(R.menu.menu_tasks_fragment)
 public class TasksFragment extends MvpFragment implements TaskView, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "TasksFragmentLog";

    private TasksAdapter tasksAdapter;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

    @ViewById(R.id.fragment_root_layout)
    CoordinatorLayout rootLayout;

    @ViewById(R.id.recyclerViewList)
    RecyclerView recyclerView;


    @ViewById(R.id.refresh_layout)
    SwipeRefreshLayout refresh_layout;


    @ViewById(R.id.emptyListLayout)
    LinearLayout emptyListLayout;

    @ViewById(R.id.syncBtn)
    Button syncBtn;

    @Bean
    @InjectPresenter
    TasksPresenter presenter;

    Spinner spinner;

    @InstanceState
    int spinnerSelection = 0;

    @ViewById(R.id.expenses_fab)
    FloatingActionButton fab;

    @AfterViews
    void loadLayoutManager() {
        Log.d(TAG, "AfterViews--- spinnerSelection " + spinnerSelection);
        loadAllTasks();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       /* recyclerView.setItemAnimator(new DefaultItemAnimator());


        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_from_right);
        recyclerView.setLayoutAnimation(animation);

        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();*/

        refresh_layout.setOnRefreshListener(this);
    }



    @Click(R.id.expenses_fab)
    void fabExpensesOnClickListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTaskActivity_.intent(getActivity()).start()
                        .withAnimation(R.anim.enter_pull_in, R.anim.exit_fade_out);


            }
        });
    }

    @Click(R.id.syncBtn)
    void syncBtnOnClickListener() {
        Log.d(TAG, "syncBtnOnClickListener---");
        connectToServer();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);


        MenuItem spinnerItem = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(spinnerItem);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.spinner_list_item_array, R.layout.spinner_drop_title);
        adapter.setDropDownViewResource(R.layout.spinner_drop_list);

        spinner.setAdapter(adapter);
        spinner.setSelection(spinnerSelection);
        spinner.setOnItemSelectedListener(itemSelectedListener);
        super.onCreateOptionsMenu(menu, inflater);
    }


    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selectedItem = parent.getItemAtPosition(position).toString();
            switch (selectedItem) {
                case "все":
                    Log.d(TAG, "onItemSelected Все");
                    spinnerSelection = 0;
                    loadAllTasks();
                    break;
                case "новые":
                    Log.d(TAG, "onItemSelected Новые");
                    spinnerSelection = 1;
                    loadAllTasks();
                    break;
                case "делаю":
                    Log.d(TAG, "onItemSelected Делаю");
                    spinnerSelection = 2;
                    loadAllTasks();
                    break;
                case "сделано":
                    Log.d(TAG, "onItemSelected Сделано");
                    spinnerSelection = 3;
                    loadAllTasks();
                    break;
            }
        } // to close the onItemSelected

        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_list:
            connectToServer();
            break;
        }

        return (super.onOptionsItemSelected(item));
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume---");
        loadAllTasks();
    }


    private void toggleSelection(int position) {
        tasksAdapter.toggleSelection(position);
        int count = tasksAdapter.getSelectedItemCount();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public void showAllTasksList(final List<TasksEntity> tasksList) {
        Log.d(TAG, "showAllTasksList--- tasksList = " + tasksList);
        if (tasksList.size() == 0) {
            Log.d(TAG, "tasksList.size() = " + tasksList.size());
            emptyListLayout.setVisibility(View.VISIBLE);
            refresh_layout.setVisibility(View.GONE);
            //recyclerView.setVisibility(View.GONE);
        } else {
            emptyListLayout.setVisibility(View.GONE);
            refresh_layout.setVisibility(View.VISIBLE);
            Log.d(TAG, "recyclerView видимый");
            tasksAdapter = new TasksAdapter(getActivity(), tasksList, new ClickListener() {
                @Override
                public void onItemClicked(int position) {

                    // TasksEntity tasksEntity = TasksEntity.selectById(position);

                    if (actionMode != null) {
                        toggleSelection(position);
                    } else {
                        TasksEntity tasksEntity = tasksList.get(position);
                        int id = (int) (long) tasksEntity.getId();
                        EditTaskActivity_.intent(getActivity())
                                .taskId(id)
                                .taskTitle(tasksEntity.getTask())
                                .adress(tasksEntity.getAdress())
                                .phoneNumber(tasksEntity.getPhoneNumber())
                                .urgent(tasksEntity.isUrgent())
                                .taskStatus(tasksEntity.getStatus())
                                .finishDate(tasksEntity.getFinishDate())
                                .start()
                                .withAnimation(R.anim.enter_pull_in, R.anim.exit_fade_out);
                    }
                }

                @Override
                public boolean onItemLongClicked(int position) {
                    if (actionMode == null) {
                        AppCompatActivity activity = (AppCompatActivity) getActivity();
                        actionMode = activity.startSupportActionMode(actionModeCallback);
                    }
                    toggleSelection(position);
                    return true;
                }
            });


            recyclerView.setAdapter(tasksAdapter);
            recyclerView.setVisibility(View.VISIBLE);
            refresh_layout.setRefreshing(false);
        }
    }




    void connectToServer() {
        Log.d(TAG, "startConnectionServer()");
        if (NetworkStatusChecker.isNetworkAvailable(getActivity())) {
            presenter.getTasksFromServer();
        } else {
            Snackbar.make(rootLayout,
                    getString(R.string.error_no_internet),
                    Snackbar.LENGTH_LONG).show();
            refresh_layout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        connectToServer();
    }

    @Override
    public void loadAllTasks() {
        Log.d(TAG, "loadAllTasks spinnerSelection " + spinnerSelection);
        presenter.loadTasks(spinnerSelection);
    }

    @Override
    public void resetSpinner() {
        if (spinner != null){
            spinner.setSelection(0);
        }
    }

    @Override
    public void showProgressBar() {
        refresh_layout.setRefreshing(true);
    }

    @Override
    public void hideProgressBar() {
        refresh_layout.setRefreshing(false);
    }

    @Override
    public void showSucessToast() {
        Snackbar.make(rootLayout,
                getString(R.string.refresh_finished),
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showServerErrorToast() {
        Snackbar.make(rootLayout,
                getString(R.string.unknown_server_error),
                Snackbar.LENGTH_LONG).show();
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_action_bar, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_remove:
                    tasksAdapter.removeItems(tasksAdapter.getSelectedItems());

                    mode.finish();
                    return true;
                default:
                    return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            tasksAdapter.clearSelection();
            actionMode = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.clearResurses();
    }
}