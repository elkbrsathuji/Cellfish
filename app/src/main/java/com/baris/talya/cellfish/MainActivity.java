package com.baris.talya.cellfish;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.widget.ActionMenuPresenter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.baris.talya.cellfish.core.ScreenOnService;
import com.baris.talya.cellfish.services.NotifyService;
import com.baris.talya.cellfish.tools.ImageHelper;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private AlarmManager midnightAlarmMgr;
    private PendingIntent midnightAlarmIntent;

    private AlarmManager notifyAlarmMgr;
    private PendingIntent notifyAlarmIntent;
    SharedPreferences pref;
    TextView tv_counter;
    TextView tv_avg;
    private TextView textTimer;

    private long durationUntilNow = 0L;
    private long startTime = 0L;
    private Handler myHandler = new Handler();
    long timeInMillies = 0L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        pref = getSharedPreferences(Constants.PREFERENCE_NAME, Activity.MODE_PRIVATE);


        tv_counter = (TextView) findViewById(R.id.screeon_on_counter);
        tv_avg = (TextView) findViewById(R.id.average);
        textTimer = (TextView) findViewById(R.id.textTimer);
//        restartAtMidnight();
//notifyAtMorning();
        Intent i0 = new Intent();
        i0.setAction(".core.ScreenOnService");
        i0.setPackage("com.baris.talya.cellfish");

        if (!pref.contains(Constants.DATE_INIT_APP)) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(Constants.DATE_INIT_APP, Calendar.getInstance().get(Calendar.DATE));
            editor.commit();
            i0.putExtra("screen_state", true);
        }
        startService(i0);




    }

//    private void restartAtMidnight() {
//        Calendar midnightCalendar = Calendar.getInstance();
//        int today = midnightCalendar.get(Calendar.DATE);
//        if (pref.getInt(Constants.LAST_RESET_DATE, today-1) < today) {
//
//
//            midnightCalendar.set(Calendar.HOUR_OF_DAY, 23);
//            midnightCalendar.set(Calendar.MINUTE, 45);
//            AlarmManager midnightAlarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            Intent i = new Intent(this, ScreenOnService.class);
//            i.putExtra(Constants.MIDNIGHT, true);
//            PendingIntent midnightAlarmIntent = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//            midnightAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, midnightCalendar.getTimeInMillis(),
//                    AlarmManager.INTERVAL_DAY, midnightAlarmIntent)
//        }
//    }

    private void notifyAtMorning() {
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DATE);
        if (pref.getInt(Constants.LAST_NOTIFICATION_DATE, today - 1) < today) {

            calendar.set(Calendar.HOUR_OF_DAY, Constants.NOTIFICATION_TIME_H);
            calendar.set(Calendar.MINUTE, Constants.NOTIFICATION_TIME_M);

            AlarmManager notifyAlarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            final Intent i = new Intent(this, NotifyService.class);
            PendingIntent notifyAlarmIntent = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            notifyAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, notifyAlarmIntent);

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        int today = Calendar.getInstance().get(Calendar.DATE);
            durationUntilNow = pref.getLong(Constants.DURATION_UNTIL_NOW+today, 0);
            Log.d("DEBUG","duration till now: "+durationUntilNow/1000);
        startTime=pref.getLong(Constants.DURATION_START_PERIOD+today,Calendar.getInstance().getTimeInMillis());
        myHandler.postDelayed(updateTimerMethod, 0);
         if (pref.contains(Constants.COUNTER+today)){
        updateScreen();
        }else{
            Handler h= new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateScreen();
                }
            },500);
        }
    }


    private void updateScreen() {
        int today = Calendar.getInstance().get(Calendar.DATE);
        int counter = pref.getInt(Constants.COUNTER + today, 0);
        tv_counter.setText(String.valueOf(counter));

        int total = pref.getInt(Constants.TOTAL_AMOUNT_OPEN_SCREEN, 0);

        long diff = calculateDiffDays();
        if (diff == 0) {
            tv_avg.setText(String.valueOf(0));
        } else {
            long avg = pref.getInt(Constants.TOTAL_AMOUNT_OPEN_SCREEN, 0) / calculateDiffDays();

            tv_avg.setText(String.valueOf(avg));
        }
    }


    private Runnable updateTimerMethod = new Runnable() {

        public void run() {
            long timeInMillies = durationUntilNow+Calendar.getInstance().getTimeInMillis()-startTime;

            int seconds = (int) (timeInMillies / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            textTimer.setText("" + minutes + ":"
                    + String.format("%02d", seconds));
            myHandler.postDelayed(this, 0);
        }

    };


    private long calculateDiffDays() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, pref.getInt(Constants.DATE_INIT_APP, 0));
        long millisFirst = cal.getTimeInMillis();
        long todayInMillis = Calendar.getInstance().getTimeInMillis();
        long diff = todayInMillis - millisFirst;
        return diff / (24 * 60 * 60 * 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_share) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_TEXT,
                    "I'm a phone addict, how CellFish are you?\n" +
                            "http://bit.ly/1SQtg5J");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Cellfish");
            File imageFile = ImageHelper.takeScreenshot(this);
            if (imageFile != null) {
                Uri screenshotUri = Uri.fromFile(imageFile);
                intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            }
            startActivity(Intent.createChooser(intent, "Share via"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
