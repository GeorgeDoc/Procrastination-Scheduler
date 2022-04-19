package com.example.procratinationscheduler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DynamicList extends AppCompatActivity implements View.OnClickListener{
    private String LOG_LIST = "savedState";


    LinearLayout layoutDynamicList;
    Button buttonAdd, buttonSubmit;
    TextView testing;
    EditText saveTest;

    ListView listViewList;                                                                          // BASIC FEATURE LIST - ListView
    ArrayAdapter<String> lvAdapter;
    String[] featuresList = {"Basic Layout", "Basic Timer", "Basic List", "Basic Sum", "ScrollView",
            "LockScreen timer", "App save state", "Timer for the buttons ", "Dynamic list", "Timer with animation", "Statistics", "Timer for a list?"};

    List<Double> pointlist = new ArrayList<>(4);
    ArrayList<Task> taskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamic_activity_list);

        layoutDynamicList = findViewById(R.id.layout_dynamic_list);
        buttonAdd = findViewById(R.id.button_add);
        buttonSubmit = findViewById(R.id.button_submit);

        listViewList = (ListView) findViewById(R.id.listView_list);                                 // LIST VIEW
        Parcelable state = listViewList.onSaveInstanceState();                                      // 21/ 8
        lvAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, featuresList);//
        listViewList.setAdapter(lvAdapter);
        listViewList.onRestoreInstanceState(state);                                                 //21/08


        pointlist.add(0.0);                                                                         // Doubles dropdown spinner
        pointlist.add(0.5);
        pointlist.add(1.0);
        pointlist.add(2.0);

        buttonAdd.setOnClickListener(this);
        buttonSubmit.setOnClickListener(this);

        testing =findViewById(R.id.tvTest);                                                         // TEST
        testing.setText("Testing save - restore");
        saveTest = findViewById(R.id.etSaveTest);
        saveTest.setText("Testing initiated", TextView.BufferType.EDITABLE);

    }

    @Override                                                                                       // MENU
    public boolean onCreateOptionsMenu(Menu menu) {                                                 // Left click, generate, override methods,
        //return super.onCreateOptionsMenu(menu);                                                   // onCreateOptionsMenu and onOptionsItemSelected
        getMenuInflater().inflate(R.menu.checklist_menu,menu);                                      // Use my custom menu
        return true;                                                                                //
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {                                  // SELECT ITEM FROM LIST
        int id = item.getItemId();
        if (id == R.id.item_done) {                                                                 // If menu item tick box is tapped,
            String itemSelected = "Features added: \n";
            for (int i=0; i<listViewList.getCount(); i++) {                                         // add all the checked list view items
                if (listViewList.isItemChecked(i)) {
                    itemSelected += listViewList.getItemAtPosition(i) + "\n";
                }
            }
            Toast.makeText(this, itemSelected, Toast.LENGTH_LONG).show();                   // TOAST
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {                                                                // Button clicks
        switch (view.getId()) {
            case R.id.button_add:                                                                   // Add
                addView();
                break;
            case R.id.button_submit:                                                                // Submit
                if (checkIfValidAndERad()) {                                                        // Check if it's ok to submit
                    Intent in = new Intent(DynamicList.this, MainActivity.class);    // Intent to transition
                    Bundle bundle = new Bundle();                                                   // Create Bundle
                    bundle.putSerializable("list", taskList);                                       // We made Tasks Serializable, so serializable objects in bundle, gonna call them list
                    in.putExtras(bundle);                                                           // Put then into the intent
                    startActivity(in);                                                              // Go
                }
                break;
        }
    }

    private boolean checkIfValidAndERad() {                                                         // Check method
        taskList.clear();                                                                           // Clear in order to count? & check later if it's still empty,
        boolean result = true;                                                                      // so we can show the appropriate error Toast message

        for(int i=0; i<layoutDynamicList.getChildCount(); i++) {                                    // for all children (Tasks)
            View taskView = layoutDynamicList.getChildAt(i);                                        // Create Task View in the current position

            EditText editTextName = (EditText) taskView.findViewById(R.id.edit_item_name);          // Declare Task parameters
            AppCompatSpinner spinnerPoints = (AppCompatSpinner) taskView.findViewById(R.id.spinner_points);

            Task task = new Task();                                                                 // Create Task

            if (!editTextName.getText().toString().equals("")) {                                    // If name EditText not empty
                task.setTaskName(editTextName.getText().toString());                                // set name to given string
            } else {
                result = false;
                break;
            }

            if(spinnerPoints.getSelectedItemPosition()!=0.0) {                                      // If selected spinner points value is not 0
                task.setTaskPoints(pointlist.get(spinnerPoints.getSelectedItemPosition()));         // Set Task points to selected position's value
            } else {
                result = false;
                break;
            }

            taskList.add(task);                                                                     // Add the newly created Task to the task list
        }
        if (taskList.size()==0) {                                                                   // If submitted with empty task list
            result = false;
            Toast.makeText(this, "Add Tasks first", Toast.LENGTH_LONG).show();
        } else if (!result) {                                                                       // If submitted with error in setting parameters
            Toast.makeText(this, "Enter all details correctly", Toast.LENGTH_LONG).show();
        }

        return result;
    }

    private void addView() {                                                                        // ADD VIEW

        //View someView = getLayoutInflater().inflate(R.layout.theXmlRowDesign,null,false);
        View taskView = getLayoutInflater().inflate(R.layout.row_add_item, null,false);

        //Hooks for the new elements
        EditText editText = (EditText) taskView.findViewById(R.id.edit_item_name);
        AppCompatSpinner spinnerPoints = (AppCompatSpinner) taskView.findViewById(R.id.spinner_points);
        ImageView imageClose = (ImageView) taskView.findViewById(R.id.image_remove);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, pointlist);
        spinnerPoints.setAdapter(arrayAdapter);

        //Sum
        spinnerPoints.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Double activity_points = (Double) spinnerPoints.getSelectedItem();
                //
                //Double daily_sum = (Double) spinnerPoints.getSelectedItem();
                //total += daily_sum;
                //String sumString = total.toString();
                //sum.setText("Your daily point sum is: " + sumString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {            }
        });

        imageClose.setOnClickListener(new View.OnClickListener() {                                  // REMOVE VIEW
            @Override
            public void onClick(View view) {
                removeView(taskView);
            }
        });

        layoutDynamicList.addView(taskView);
    }

    private void removeView(View view) {
        layoutDynamicList.removeView(view);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Log.i(LOG_LIST, "In saved state");
        testing = findViewById(R.id.tvTest);
        testing.setText("Saved");

        saveTest = findViewById(R.id.etSaveTest);
        outState.putString("SaveET", saveTest.getText().toString());

        ArrayList<Integer> itemChecked = new ArrayList<>();
        //int itemsChecked[];
        for (int i=0; i<listViewList.getCount(); i++) {                                         // add all the checked list view items
            if (listViewList.isItemChecked(i)) {
                itemChecked = (ArrayList<Integer>) listViewList.getItemAtPosition(i);
            }
            outState.putIntegerArrayList("itemsChecked", itemChecked);
        }
    }

    //RESTORE ACTIVITY STATE
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Log.i(LOG_LIST, "In saved state");
        testing = findViewById(R.id.tvTest);
        testing.setText(saveTest.getText().toString());

        //Retrieve the bundle contents
        String restoredString = savedInstanceState.getString("SaveET");
        saveTest.setText(restoredString);

        ArrayList<Integer> storedData = savedInstanceState.getIntegerArrayList("itemsChecked");
        listViewList = findViewById(R.id.listView_list);                                            // LIST VIEW
        for (int i=0; i<listViewList.getCount(); i++) {
            if(i==storedData.get(i))
            listViewList.setItemChecked(i, true);
        }
    }
}