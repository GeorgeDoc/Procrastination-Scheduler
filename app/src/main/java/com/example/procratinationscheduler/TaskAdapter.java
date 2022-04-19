package com.example.procratinationscheduler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskView> {                       //1. Add extends RecyclerView.Adapter<SomethingAdapter.SomethingView>

    ArrayList<Task> tasksList = new ArrayList<>();                                                  //2. ArrayList<Something> somethingsList = new ArrayList<>();

    public TaskAdapter(ArrayList<Task> tasksList) {                                                 //3. generate constructor for it
        this.tasksList = tasksList;
    }

    @NonNull                                                                                        // 5. A;t + Enter: Implement methods
    @Override
    public TaskView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                                                                                    // View to inflate and then return it
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_task, parent,false);

        return new TaskView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskView holder, int position) {
        Task task = tasksList.get(position);                                                        // 6. get the position of the new row item
        holder.textTaskName.setText(task.getTaskName());                                            //    use the holder to set the parameters in the design to
        holder.textTaskPoints.setText(task.getTaskPoints().toString());                             //    what it can get from the row
    }

    @Override
    public int getItemCount() {                                                                     // Item count
        return tasksList.size();
    }

    public class TaskView extends RecyclerView.ViewHolder{                                          // 4. public class SomethingView extends RecyclerView.ViewHolder{  }

        TextView textTaskName, textTaskPoints;                                                      // Declare the things in the design
        public TaskView(@NonNull View itemView) {                                                   // Create constructor
            super(itemView);

            textTaskName = (TextView) itemView.findViewById(R.id.text_task_name);                   // Hook them
            textTaskPoints = (TextView) itemView.findViewById(R.id.text_task_points);
        }
    }

}
