package com.metalspb.taskstracker.models.storege;

import android.util.Log;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

@Table(name = "model")
public class TasksEntity extends DBModel {
    private static final String TAG = "EditTaskActivity";

    @Column(unique = true, name = "taskId", onUniqueConflict = Column.ConflictAction.REPLACE)
    int taskId;

    @Column(name = "task")
    String task;

    @Column(name = "adress")
    String adress;

    @Column(name = "phoneNumber")
    String phoneNumber;

    @Column(name = "urgent")
    int urgent;

    @Column(name = "status")
    public int status;

    @Column(name = "finishDate")
    String finishDate;

    @Column(name = "updatedDate")
    public String updatedDate;



    public TasksEntity() {
        super();
    }

    public TasksEntity(int taskId, String task, String adress, String phoneNumber, int urgent, int status, String finishDate, String updatedDate) {
        this.taskId = taskId;
        this.task = task;
        this.adress = adress;
        this.phoneNumber = phoneNumber;
        this.urgent = urgent;
        this.status = status;
        this.finishDate = finishDate;
        this.updatedDate = updatedDate;
    }

    public TasksEntity(String task, String adress, String phoneNumber, int urgent, int status, String finishDate, String updatedDate) {
        this.task = task;
        this.adress = adress;
        this.phoneNumber = phoneNumber;
        this.urgent = urgent;
        this.status = status;
        this.finishDate = finishDate;
        this.updatedDate = updatedDate;
    }

    public int getmId() {
        return taskId;
    }

    public String getTask() {
        return task;
    }

    public int getStatus() {
        return status;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setTask(String task) {
        this.task = task;
    }
    public void setAdress(String adress) {
        this.adress = adress;
    }


    public void setStatus(int status) {
        this.status = status;
    }
    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }


    public void setId(int mId) {
        this.taskId = mId;
    }

    public String getAdress() {
        return adress;
    }

    public int isUrgent() {
        return urgent;
    }


    public static List<TasksEntity> selectAll(String query, int status) {
        if(status == 0){
            Log.d(TAG, "selectAll. no status = " + status);
            return new Select()
                    .from(TasksEntity.class)
                    //.where("task LIKE?", +'%' + query + '%')
                     .orderBy("task DESC")
                    .execute();
        }else{
            status = status - 1;
            Log.d(TAG, "selectAll. with status = " + status);
            return new Select()
                    .from(TasksEntity.class)
                    .where("status = " + status)
                    .orderBy("task DESC")
                    .execute();
        }

    }


    public static TasksEntity selectById(int query) {
        return new Select()
                .from(TasksEntity.class)
                .where("taskId LIKE?", query)
                .executeSingle();
    }

    public static void updateFromServerById(int taskId, String task, String adress, String phoneNumber, int urgent, int status, String finishDate, String updatedDate) {
        Log.d(TAG, "Обновляю updateFromServerById. id = " + taskId);
        new Update(TasksEntity.class)
                .set("task = ?," +
                     "adress = ?," +
                     "phoneNumber = ?," +
                     "urgent = ?," +
                     "status = ?," +
                     "finishDate = ?," +
                     "updatedDate = ?",
                     task, adress, phoneNumber, urgent, status, finishDate, updatedDate
                )
                .where("taskId = " + taskId)
                .execute();


    }

   public static void updateById(int taskId, int status, String updatedDate) {
        Log.d(TAG, "Обновляю updateFromServerById. id = " + taskId + " status = " + status);
        new Update(TasksEntity.class)
                .set("status = " + status + ", " + "updatedDate = \"" + updatedDate + "\"")
                .where("taskId = " + taskId)
                .execute();
        Log.d(TAG, "Обновил. status = " + status);
    }

    public static TasksEntity deleteById(int query) {
        return new Delete()
                .from(TasksEntity.class)
                .where("taskId = " + query)
                .executeSingle();
    }

    public static void deleteAllTasks() {
        TasksEntity.truncate(TasksEntity.class);
        //new Delete().from(TasksEntity.class).executeSingle();

    }

   /* public List<ExpensesEntity> expenses() {
        return getMany(ExpensesEntity.class, "category");
    }

    public static List<TasksEntity> selectAll(String query) {
        return new Select()
                .from(TasksEntity.class)
                .where("task LIKE?", new String[]{'%' + query + '%'})
                .execute();
    }

    public static TasksEntity selectById(long query) {
        return new Select()
                .from(TasksEntity.class)
                .where("id LIKE?", query)
                .executeSingle();
    }*/
}
