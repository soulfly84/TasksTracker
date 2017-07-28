package com.metalspb.taskstracker.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.metalspb.taskstracker.R;
import com.metalspb.taskstracker.backgroundTasks.CheckStatusBackground;
import com.metalspb.taskstracker.models.storege.TasksEntity;
import com.metalspb.taskstracker.views.ClickListener;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TasksAdapter extends SelectableAdapter<TasksAdapter.ExpensesHolder> {

    CheckStatusBackground checkStatusBackground;
    private Context context;
    private static final String TAG = "TasksAdapter";
    private List<TasksEntity> tasksList;
    private ClickListener clickListener;

    public TasksAdapter(Context context, List<TasksEntity> tasksList, ClickListener clickListener) {
        this.context = context;
        this.tasksList = tasksList;
        this.clickListener = clickListener;
        Log.d(TAG, "создал TasksAdapter");
    }

    @Override
    public ExpensesHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new ExpensesHolder(itemView, clickListener);
    }

    @Override
    public void onBindViewHolder(ExpensesHolder holder, int position) {
        TasksEntity task = tasksList.get(position);
        holder.name.setText(task.getTask());
        int taskStatus = task.getStatus();
        Log.d(TAG, "onBindViewHolder. Получаю id= " + task.getId());
        Log.d(TAG, "onBindViewHolder. Получаю task= " + task.getTask());
        Log.d(TAG, "onBindViewHolder. Получаю taskStatus= " + taskStatus);



        if(taskStatus == 0){
            //New task
            holder.status_new.setVisibility(View.VISIBLE);
            holder.status_processing.setVisibility(View.GONE);
            holder.status_done.setVisibility(View.GONE);
            holder.rootLayout.setBackgroundColor(context.getResources().getColor(R.color.new_task_bg));

        }else if(taskStatus == 1){
            holder.status_new.setVisibility(View.GONE);
            holder.status_processing.setVisibility(View.VISIBLE);
            holder.status_done.setVisibility(View.GONE);

        }else if(taskStatus == 2){
            holder.status_new.setVisibility(View.GONE);
            holder.status_processing.setVisibility(View.GONE);
            holder.status_done.setVisibility(View.VISIBLE);
        }




        holder.date.setText(task.getFinishDate());
        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
       /* if (task.getCategory() != null) {
            holder.category.setText(task.getCategory().getTask());
        }*/



/*
        holder.status_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.spinner.setSelection(2);
                // устанавливаем обработчик нажатия

            }
        });

*/




    }






    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public void removeItems(List<Integer> positions) {
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                for (int i = 0; i <= positions.size(); i++) {
                    removeItem(positions.get(0));
                    positions.remove(0);
                }
            }
        }
    }

    private void removeItem(int position) {
        TasksEntity listItem = tasksList.get(position);
        if (tasksList.get(position) != null) {
            tasksList.get(position).delete();

            int id = (int) (long) listItem.getId();

            TasksEntity.deleteById(id);
            checkStatusBackground = new CheckStatusBackground();
            checkStatusBackground.deleteTaskAtServer(id);
            tasksList.remove(position);
        }
        notifyItemRemoved(position);
    }




    private String toDayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }


    class ExpensesHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ClickListener clickListener;
        private LinearLayout rootLayout;
        private TextView name, date, status_new;
        private View selectedOverlay;
        private ImageView status_processing, status_done;

        ExpensesHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            rootLayout = (LinearLayout) itemView.findViewById(R.id.rootLayout);
            name = (TextView) itemView.findViewById(R.id.task_title);
            date = (TextView) itemView.findViewById(R.id.task_date);
            status_new = (TextView) itemView.findViewById(R.id.status_new);
            status_processing = (ImageView) itemView.findViewById(R.id.status_processing);
            status_done = (ImageView) itemView.findViewById(R.id.status_done);

            // category = (TextView) itemView.findViewById(R.id.expanse_item_expense_category);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);



            // адаптер
         /*   ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                    R.array.spinner_list_item_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            Spinner spinner = (Spinner) itemView.findViewById(R.id.spinner);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(itemSelectedListener);
            spinner.setPrompt("Title");*/
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null)
                clickListener.onItemClicked(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return clickListener != null && clickListener.onItemLongClicked(getAdapterPosition());
        }
    }
}

