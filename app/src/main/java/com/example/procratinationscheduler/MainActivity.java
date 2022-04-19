package com.example.procratinationscheduler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.contentcapture.ContentCaptureSession;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private String LOG_FILE = "saveState";

    private TextView tvFocusTimer;                                                                  // Timer & button
    private static final long START_TIME_IN_MILLIS = 1200000;                                       // Later shall be defined by the user (20 mins now)
    private Button focusButton, resetButton;
    private CountDownTimer focusCountDownTimer;                                                     // NOTE: To avoid using a reset button, implement a switch
    private boolean focusTimerRunning;                                                              // and make use of the boolean to set the main Focus button
    private long focusTimeLeft = START_TIME_IN_MILLIS;                                              // text to Start, Pause, Reset/Stop. See Flutter Recorder project

    /* 1. Make a layout design xml for the new row
    2. Declare a Linear layout for the dynamic list
    3. Make a list object i.e  List<String> pointList = new ArrayList<>();
    4. In inCreate, add things in the list with pointList.add("Points"); & Set some onClickListener for adding the new rows
    5. Create the addView method as bellow and use where needed.
    6. Inflate, Hook the new elements, call addView on itself with the created vew class as parameter */

    /* Dynamic List
    1. New layout resource, like cardview design
    2. New  Java class somethingAdapter
    3. In its Java file: SomethingAdapter extends RecyclerView.Adapter<SomethingAdapter.SomethingView>
    4. ArrayList<Something> somethingsList = new ArrayList<>();, generate constructor for it
    5. Inside:  public class SomethingView extends RecyclerView.ViewHolder{  }
    6. Create constructor
    7. Alt + Enter, implement methods
    8. Inside the methods, see TaskAdapter
    9. Declare the     ArrayList<Something> somethingList = new ArrayList<>(); in the activity that the final list should appear (the one with RecyclerView layout list here)
   10. In onCreate get the intent Extras with the "list" we created in the Submit Button            (It's null at this point => error, fixed with Button temporarily)
   11. Set the RecyclerView (here named layoutList) adapter to create a new item from the somethingList



     */
    //RecyclerView layoutList;
    //ArrayList<Task> taskList = new ArrayList<>();                                                   // 9
    //Button buttonGetList;
    //TextView sum;
    //Double total = 0.0;

    //List<String> pointList = new ArrayList<>();
    //List<Double> pointlist = new ArrayList(4);                                         //try later

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Calendar calendar = Calendar.getInstance();                                                 //      DATE AT TOP
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        TextView textViewDate = findViewById(R.id.title_textView);
        textViewDate.setText("Productivity Manager 9000 X-Pro \nDate: " + currentDate);

        //Declarations

        //sum = findViewById(R.id.list_sum);
        //sum.setText("Sum: " + total);
        //buttonGetList = findViewById(R.id.get_list);

        tvFocusTimer = findViewById(R.id.focus_timer);                                              // Timer
        focusButton = findViewById(R.id.focus_button);
        resetButton = findViewById(R.id.reset_button);

        focusButton.setOnClickListener(new View.OnClickListener() {                                 // Start - Pause
            @Override
            public void onClick(View view) {
                if (focusTimerRunning) {
                    pauseTimer();
                } else { startTimer();}
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {                                 //      Reset
            @Override
            public void onClick(View view) {
                resetTimer();
            }
        });
        updateCountDownText();                                                                      // Run once first to show the initial timer instead of 00:00


        //RecyclerView
/*        layoutList = findViewById(R.id.recycler_tasks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutList.setLayoutManager(layoutManager);

        buttonGetList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskList = (ArrayList<Task>) getIntent().getExtras().getSerializable("list");          // 10
                layoutList.setAdapter(new TaskAdapter(taskList));                                           // 11
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);              // SwipeRefresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                taskList.clear();
                swipeRefreshLayout.setRefreshing(false);
                taskList = (ArrayList<Task>) getIntent().getExtras().getSerializable("list");
                taskList.addAll(taskList);
                layoutList.setAdapter(new TaskAdapter(taskList));

            }
        }); */

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);                         //     FAB -ulous
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this, DynamicList.class);
                startActivity(in);
            }
        });
    }


    private void startTimer() {                                                                     //      START TIMER
        focusCountDownTimer = new CountDownTimer(focusTimeLeft, 1000) {            // Set Initial time, interval (1 sec),
            @Override
            public void onTick(long l) {                                                            // l = millisUntilFinished, the duration of the whole timer
                focusTimeLeft = l;                                                                  // gets updated every interval
                updateCountDownText();                                                              // Method to update the timer text
                refreshNotifications("Time remaining: " + l /1000);                                 // Updatable notification
            }

            @Override
            public void onFinish() {                                                                //      FINISH
                focusTimerRunning = false;
                focusButton.setText("Start");
                focusButton.setVisibility(View.INVISIBLE);
                resetButton.setVisibility(View.VISIBLE);
            }
        }.start();                                                                                  // When the timer starts,
        focusTimerRunning = true;                                                                   // set boolean to true,
        focusButton.setText("Pause");                                                               // change the button text to pause,
        resetButton.setVisibility(View.INVISIBLE);                                                  // the Reset button is invisible

        Toast.makeText(MainActivity.this,"Focus Intensifies!!", Toast.LENGTH_LONG).show();
    }

    private void pauseTimer() {                                                                     //      PAUSE TIMER
        focusCountDownTimer.cancel();                                                               // Cancel countdown, time left is saved in l
        focusTimerRunning = false;                                                                  // Set timer running to false
        focusButton.setText("Start");                                                               // Set timer button text to Start
        resetButton.setVisibility(View.VISIBLE);                                                    // Reset button appears
    }

    private void resetTimer() {                                                                     //      RESET TIMER
        focusTimeLeft = START_TIME_IN_MILLIS;                                                       // Get the original duration
        updateCountDownText();
        resetButton.setVisibility(View.INVISIBLE);
        focusButton.setVisibility(View.VISIBLE);                                                    // cuz it gets invisible at finish()
    }
    private void updateCountDownText() {
        int minutes = (int) focusTimeLeft /1000 / 60;       // cast into int because we're using long for the calculation. /1000 to turn millis into secs, /60 to turn secs into minutes
        int seconds = (int) focusTimeLeft /1000 % 60;

        String focusTimeLeftFormatted = String.format(                                              //      FORMAT THE TIME
                Locale.getDefault(),"%02d:%02d", minutes, seconds);                         // Locale.getDefault() to avoid some bug
        tvFocusTimer.setText(focusTimeLeftFormatted);                                               // Set the timer text to the time
    }


    public void refreshNotifications(String message) {                                              //      LOCK SCREEN NOTIFICATION

        int minutes = (int) focusTimeLeft /1000 / 60;       // cast into int because we're using long for the calculation. /1000 to turn millis into secs, /60 to turn secs into minutes
        int seconds = (int) focusTimeLeft /1000 % 60;
        String focusTimeLeftFormatted = String.format(                                              //      FORMAT THE TIME
                Locale.getDefault(),"%02d:%02d", minutes, seconds);                         // Locale.getDefault() to avoid some bug

        int notifyID = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel  = new NotificationChannel(                                 // Create the notification channel
                    "1",
                    "channel 1",                                                             // Channel description
                    NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);              // Create the notification manager &
            manager.createNotificationChannel(channel);                                             // pass the channel


            Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());        // Tapping notification Intent, simulates tapping app button
            //Intent intent = new Intent(this, MainActivity.class);//want this specific activity
            PendingIntent pendingIntent = PendingIntent.getActivity(this,//or getApplicationContext    // Pending intents get wrapped around a normal Intent,
                    0,                                                                  // Can be used to later Update or Cancel this PendingIntent
                    intent,                                                                         // Out intent
                    PendingIntent.FLAG_UPDATE_CURRENT);                                             // Defines what happens when we recreate this PendingIntent with a new Intent
///Need to reset notification timer
            NotificationCompat.Builder notification = new NotificationCompat.Builder(               // create the Notification
                    this, "1")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Focusing intensifies!!")
                    //.setUsesChronometer(true)
                    //.setOngoing(true)
                    //.setShowWhen(true)
                    //.setWhen(focusTimeLeft)
                    //.setTicker("ticker")
                    //.setChronometerCountDown(true)
                    .setContentText("placeholder text")
                    .setOnlyAlertOnce(true)
                    .setContentIntent(pendingIntent)                                                // tapping intent
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            int numMessages = 0;                                                                    // Start a loop that processes data & notifies the user
            notification.setContentText("Time remaining: " + focusTimeLeftFormatted)
                    .setNumber(++numMessages);

            NotificationManagerCompat notifyAdmin = NotificationManagerCompat.from(this);           // Because ID remains unchanged, the existing notification
            notifyAdmin.notify(notifyID, notification.build());                                     // is updated
        } else
            {                                                                                       /// update required
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification.Builder(getApplicationContext())
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setContentTitle("Focusing intensifies!!")
                    .setContentText("text, else")
                    .setWhen(focusTimeLeft)
                    .setContentText(tvFocusTimer.getText().toString()).build();
            manager.notify(1, notification);
        }
    }

    //SAVE ACTIVITY STATE
    /*@Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(LOG_FILE, "In saved state");

        //TRY: create another bundle for the contents
        //outState.putSerializable("savedList", taskList);
    }

    //RESTORE ACTIVITY STATE
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.i(LOG_FILE, "In saved state");

        //Retrieve the bundle contents
        Serializable storedData = savedInstanceState.getSerializable("savedList");
        //taskList = (ArrayList<Task>) storedData;
        //layoutList.setAdapter(new TaskAdapter(taskList));

    }*/
}






