package com.example.procratinationscheduler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import java.io.Serializable;

public class Task implements Serializable {                                                        // implements Serializable

    public String taskName;
    public Double taskPoints;

    public Task() {    }                                                                           // Empty constructor needed when we make a constructor with arguments
    public Task(String taskName, Double taskPoints) {                                              // Constructor
        this.taskName = taskName;
        this.taskPoints = taskPoints;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Double getTaskPoints() {
        return taskPoints;
    }

    public void setTaskPoints(Double taskPoints) {
        this.taskPoints = taskPoints;
    }
}
