package com.metalspb.taskstracker.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.metalspb.taskstracker.R;
import com.metalspb.taskstracker.backgroundTasks.CheckStatusBackground;
import com.metalspb.taskstracker.backgroundTasks.rest.NetworkStatusChecker;
import com.metalspb.taskstracker.models.storege.TasksEntity;
import com.metalspb.taskstracker.ui.fragments.DatePickerFragment;
import com.metalspb.taskstracker.ui.fragments.TimePickerFragment;
import com.metalspb.taskstracker.utils.Constants;
import com.metalspb.taskstracker.utils.GlobalNotifications;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@EActivity(R.layout.task_create_activity)
public class NewTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "TasksEntity";
    private int categoryId;

    @ViewById(R.id.descriptionET)
    EditText descriptionET;
    @ViewById(R.id.adressTV)
    TextView adressTV;
    @ViewById(R.id.telTV)
    TextView telTV;
    @ViewById(R.id.datePickerBtn)
    Button datePickerBtn;

    @ViewById(R.id.timePickerBtn)
    Button timePickerBtn;

    @ViewById(R.id.statusSpinner)
    Spinner statusSpinner;

    @ViewById(R.id.splashLayout)
    LinearLayout splashLayout;

    @ViewById(R.id.mainLayout)
    LinearLayout mainLayout;

    @ViewById(R.id.toolbar_layout)
    Toolbar toolbar;
    int TASK_STATUS = 0;

    @ViewById(R.id.urgentCh)
    CheckBox urgentCh;

    int urgent = 0;



    String date = null;
    @Bean
    CheckStatusBackground checkStatusBackground;

    MenuItem item_save;

    int taskStatus = 0;

    @AfterViews
    void load() {
        setActionBar();
        date = toDayDate();
        Log.d(TAG, "Дата = " + date);
        datePickerBtn.setText(date);
        String[] data = {"Новая", "Делаю", "Сделана"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
        statusSpinner.setSelection(taskStatus);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case Constants.TASK_NEW:
                        taskStatus = 0;
                        //statusSpinner.setSelection(taskStatus);
                        break;
                    case Constants.TASK_PENDING:
                        taskStatus = 1;
                        break;
                    case Constants.TASK_DONE:
                        taskStatus = 2;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.new_task_title);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @CheckedChange({R.id.urgentCh})
    void checkBoxChecked(boolean checked) {
        if (checked) {
            urgent = 1;
        } else
            urgent = 0;
    }


    @OptionsItem(R.id.home)
    void back() {
        onBackPressed();
        finish();
    }


    @Click(R.id.datePickerBtn)
    public void showDatePickerDialog(View v) {

        DatePickerFragment dateFragm = new DatePickerFragment();

       /* Bundle args = new Bundle();
        Calendar calender = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            calender = Calendar.getInstance();
            args.putInt("year", calender.get(Calendar.YEAR));
            args.putInt("month", calender.get(Calendar.MONTH));
            args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        }


        dateFragm.setArguments(args);*/
        dateFragm.setCallBack(ondate);
        dateFragm.show(getSupportFragmentManager(), "Date Picker");
    }


    @Click(R.id.timePickerBtn)
    public void showTimePickerDialog(View v) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setCallBack(ontime);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }


    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            String date = dayOfMonth + "." + monthOfYear + "." + year;
            datePickerBtn.setText(date);
        }
    };

    TimePickerDialog.OnTimeSetListener ontime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            timePickerBtn.setText(hourOfDay + ":" + minute);
        }
    };






    private String toDayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(new Date());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_task_menu, menu);
        item_save = menu.findItem(R.id.item_save);


        return (super.onCreateOptionsMenu(menu));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        switch (item.getItemId()) {
            case R.id.item_save:
                if (NetworkStatusChecker.isNetworkAvailable(this)) {
                    postUpdatedTaskToServer();

                } else {
                    GlobalNotifications.showNoInternetAccessError(this);
                }

                break;

        }
        return (super.onOptionsItemSelected(item));
    }


    private void postNewTaskToServer(String s, int task_status, String date) {
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_fade_in, R.anim.exit_push_out);
    }

  /*  public void showDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String toDayDate = sdf.format(new Date());
        datePickerBtn.setText(toDayDate, TextView.BufferType.EDITABLE);
    }*/

/*    public void showSpinner(Spinner spinnerCategories) {
        List<TasksEntity> categoriesList = new ArrayList<TasksEntity>();
        categoriesList.addAll(TasksEntity.selectAll(""));
        CategoriesSpinnerAdapter categoriesSpinnerAdapter = new CategoriesSpinnerAdapter(this, categoriesList);
        categoriesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        categoriesSpinnerAdapter.notifyDataSetChanged();
        spinnerCategories.setAdapter(categoriesSpinnerAdapter);
        spinnerCategories.setOnItemSelectedListener(this);
    }*/

    private void saveTaskInDb(String desc, int status, String date) {
        Log.d(TAG, "saveTaskInDb--");
        TasksEntity expensesEntity = new TasksEntity();
        expensesEntity.setTask(desc);
        expensesEntity.setStatus(status);
        expensesEntity.setFinishDate(date);
      /*  TasksEntity category = (TasksEntity) spinnerCategories.getSelectedItem();
        expensesEntity.setCategory(category);*/
        expensesEntity.save();
    }


    @Background
    public void postUpdatedTaskToServer() {
        Log.d(TAG, "postUpdatedTaskToServer--- text: " + descriptionET.getText());
        showProgressBar();
        String finishDate = datePickerBtn.getText().toString() + ", " + timePickerBtn.getText().toString();


        Log.d(TAG, "postUpdatedTaskToServer--- adress: " + adressTV.getText().toString());
        Log.d(TAG, "postUpdatedTaskToServer--- urgent: " + urgent);
        Log.d(TAG, "postUpdatedTaskToServer--- taskStatus: " + taskStatus);
        Log.d(TAG, "postUpdatedTaskToServer--- finishDate: " + finishDate);


        checkStatusBackground.createTaskAtServer(descriptionET.getText().toString(), adressTV.getText().toString(), telTV.getText().toString(), urgent, taskStatus, datePickerBtn.getText().toString(), getDateTime());
        //String statusMsg = checkStatusBackground.statusMsg();
        if (checkStatusBackground.statusMsg().equals(Constants.STATUS_TASK_CREATED)) {
            TasksEntity newTasksItem = new TasksEntity(descriptionET.getText().toString(), adressTV.getText().toString(), telTV.getText().toString(), urgent, taskStatus, finishDate, getDateTime());
            newTasksItem.save();
            notifyCreated();
            Log.d(TAG, "Сохранил  задачу ");
        } else if (checkStatusBackground.statusMsg().equals(Constants.STATUS_TASK_FAILED_TO_CREATE)) {
            Log.d(TAG, "Не Сохранил  задачу ");
            notifyErorUpdated();
        }
        hideProgressBar();
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @UiThread
    void notifyCreated() {
        Toast.makeText(getBaseContext(), R.string.toast_created, Toast.LENGTH_LONG).show();
        back();
        finish();
    }

    @UiThread
    void notifyErorUpdated() {
        Toast.makeText(this, R.string.toast_update_error, Toast.LENGTH_LONG).show();
    }

    @UiThread
    void notifyInstallMapsApp() {
        Toast.makeText(this, R.string.toast_install_maps, Toast.LENGTH_LONG).show();
    }


    @UiThread
    public void showProgressBar() {
        Log.d(TAG, "showProgressBar");
        mainLayout.setVisibility(View.GONE);
        splashLayout.setVisibility(View.VISIBLE);

    }


    @UiThread
    public void hideProgressBar() {
        mainLayout.setVisibility(View.VISIBLE);
        splashLayout.setVisibility(View.GONE);
    }
}
