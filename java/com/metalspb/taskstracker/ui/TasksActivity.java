package com.metalspb.taskstracker.ui;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.metalspb.taskstracker.R;
import com.metalspb.taskstracker.backgroundTasks.CheckStatusBackground;
import com.metalspb.taskstracker.backgroundTasks.rest.NetworkStatusChecker;
import com.metalspb.taskstracker.backgroundTasks.sync.TrackerSyncAdapter;
import com.metalspb.taskstracker.storege.TasksEntity;
import com.metalspb.taskstracker.presenters.TasksPresenter;
import com.metalspb.taskstracker.ui.adapter.TasksAdapter;
import com.metalspb.taskstracker.views.ClickListener;
import com.metalspb.taskstracker.views.TaskView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_tasks)

 public class TasksActivity extends MvpAppCompatActivity implements TaskView, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "TasksFragmentLog";

    private TasksAdapter tasksAdapter;

    @Bean
    @InjectPresenter
    TasksPresenter presenter;



    @ViewById(R.id.toolbar_layout)
    Toolbar toolbar;

    ActionBar actionBar;

    @ViewById(R.id.drawer_layout)
    CoordinatorLayout rootLayout;

    @ViewById(R.id.recyclerViewList)
    RecyclerView recyclerView;


    @ViewById(R.id.refresh_layout)
    SwipeRefreshLayout refresh_layout;


    @ViewById(R.id.emptyListLayout)
    LinearLayout emptyListLayout;

    @ViewById(R.id.syncBtn)
    Button syncBtn;


    Spinner spinner;

    @InstanceState
    int spinnerSelection = 0;

    @ViewById(R.id.expenses_fab)
    FloatingActionButton fab;


    @AfterViews
    void setupViews() {
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        TrackerSyncAdapter.initializeSyncAdapter(this);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_nav);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_list_item_array, R.layout.spinner_drop_title);
        adapter.setDropDownViewResource(R.layout.spinner_drop_list);

        spinner.setAdapter(adapter);
        spinner.setSelection(spinnerSelection);
        spinner.setOnItemSelectedListener(itemSelectedListener);

    }

    @AfterViews
    void loadLayoutManager() {
        Log.d(TAG, "AfterViews--- spinnerSelection " + spinnerSelection);
        loadAllTasks();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refresh_layout.setOnRefreshListener(this);
    }



    @Click(R.id.expenses_fab)
    void fabExpensesOnClickListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(TasksActivity.this, NewTaskActivity_.class);
                TasksActivity.this.startActivity(mainIntent);
            }
        });
    }

    @Click(R.id.syncBtn)
    void syncBtnOnClickListener() {
        Log.d(TAG, "syncBtnOnClickListener---");
        connectToServer();
    }

  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        //MenuItem spinnerItem = menu.findItem(R.id.spinner);

        return super.onCreateOptionsMenu(menu);
    }

    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selectedItem = parent.getItemAtPosition(position).toString();
            switch (selectedItem) {
                case "Все задачи":
                    spinnerSelection = 0;
                    loadAllTasks();
                    break;
                case "Новые задачи":
                    spinnerSelection = 1;
                    loadAllTasks();
                    break;
                case "Делаю":
                    spinnerSelection = 2;
                    loadAllTasks();
                    break;
                case "Сделано":
                    spinnerSelection = 3;
                    loadAllTasks();
                    break;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_list:
            connectToServer();
            break;

            case R.id.menu_quit:
                userLogout();
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




    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void showAllTasksList(final List<TasksEntity> tasksList) {
        Log.d(TAG, "showAllTasksList--- tasksList = " + tasksList);
        if (tasksList.size() == 0) {
            Log.d(TAG, "tasksList.size() = " + tasksList.size());
            emptyListLayout.setVisibility(View.VISIBLE);
            refresh_layout.setVisibility(View.GONE);
        } else {
            emptyListLayout.setVisibility(View.GONE);
            refresh_layout.setVisibility(View.VISIBLE);
            tasksAdapter = new TasksAdapter(this, tasksList, new ClickListener() {
                @Override
                public void onItemClicked(int position) {
                    TasksEntity tasksEntity = tasksList.get(position);
                    int id = (int) (long) tasksEntity.getId();


                    EditTaskActivity_.intent(TasksActivity.this)
                            .taskId(id)
                            .taskTitle(tasksEntity.getTask())
                            .adress(tasksEntity.getAdress())
                            .phoneNumber(tasksEntity.getPhoneNumber())
                            .urgent(tasksEntity.isUrgent())
                            .taskStatus(tasksEntity.getStatus())
                            .finishDate(tasksEntity.getFinishDate())
                            .start();
                            //.withAnimation(R.anim.enter_pull_in, R.anim.exit_fade_out);
                }
            });


            recyclerView.setAdapter(tasksAdapter);
            recyclerView.setVisibility(View.VISIBLE);
            refresh_layout.setRefreshing(false);
        }
    }




    void connectToServer() {
        Log.d(TAG, "startConnectionServer()");
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
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



    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.clearResurses();
    }

    @Bean
    @NonConfigurationInstance
    CheckStatusBackground checkStatusBackground;
    private void userLogout() {
        AppController.clearPrefs();
        checkStatusBackground.deleteAllTasksInDB();
        startActivity(new Intent(this, LoginActivity_.class));
    }
}