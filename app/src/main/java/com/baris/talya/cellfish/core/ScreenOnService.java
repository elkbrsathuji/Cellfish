package com.baris.talya.cellfish.core;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.baris.talya.cellfish.Constants;
import com.baris.talya.cellfish.MainActivity;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Elkana on 16/11/15.
 */
public class ScreenOnService extends Service {

    BroadcastReceiver mReceiver = null;


    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenOnReceiver();
        registerReceiver(mReceiver, filter);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences pref = getSharedPreferences(Constants.PREFERENCE_NAME, Activity.MODE_PRIVATE);
        if (intent != null && intent.hasExtra("screen_state")) {

            boolean screenOn = intent.getBooleanExtra("screen_state", false);
            if (screenOn) {
                Log.d("DEBUG", "service run, screen on");
                int today = Calendar.getInstance().get(Calendar.DATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putLong(Constants.DURATION_START_PERIOD+today, Calendar.getInstance().getTimeInMillis());
                Log.d("DEBUG","start time: "+Calendar.getInstance().getTimeInMillis());
                editor.commit();

                String counterKey = Constants.COUNTER + today;
                if (pref.contains(counterKey)) {// not first time today
                    int counter = pref.getInt(counterKey, 0);
                    counter++;
                    editor.putInt(counterKey, counter);
                    editor.commit();
                } else {
                    editor.putInt(counterKey, 1);
                    editor.commit();
                    deletePreviosCounters(pref);

                }
            } else {
                Calendar cal = Calendar.getInstance();
                int today=cal.get(Calendar.DATE);
                Log.d("DEBUG","end time: "+cal.getInstance().getTimeInMillis());

                long diff = cal.getTimeInMillis() - pref.getLong(Constants.DURATION_START_PERIOD+today, 0);
                Log.d("DEBUG","diff: "+diff/1000);
                long totalDuration = pref.getLong(Constants.DURATION_UNTIL_NOW+today, 0);

                totalDuration += diff;
                Log.d("DEBUG","total: "+totalDuration/1000);
                SharedPreferences.Editor editor = pref.edit();
                editor.putLong(Constants.DURATION_UNTIL_NOW+today, totalDuration);
                editor.commit();
            }
//else if (intent!=null &&intent.hasExtra(Constants.MIDNIGHT)){
//    Log.d("DEBUG","service run, midnight");
//    boolean midnight = intent.getBooleanExtra(Constants.MIDNIGHT, false);
//    if (midnight){
//        calculateAverage(pref);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putInt(Constants.LAST_RESET_DATE, (Calendar.getInstance().get(Calendar.DATE)));
//                editor.putInt(Constants.COUNTER, 0);
//        editor.commit();
//
//    }
        }
        return super.onStartCommand(intent, flags, startId);

    }

    private void deletePreviosCounters(SharedPreferences pref) {
        boolean continueDel = true;
        int i = -1;
        while (continueDel) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, i);
            int day = cal.get(Calendar.DATE);
            String key = Constants.COUNTER + day;
            if (pref.contains(key)) {
                if (i == -1) {//case of yesterday
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt(Constants.PREV_COUNTER, pref.getInt(key, 0));
                    editor.commit();
                }
                int openCounter = pref.getInt(key, 0);
                int total = openCounter + pref.getInt(Constants.TOTAL_AMOUNT_OPEN_SCREEN, 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.remove(key);
                editor.remove(Constants.DURATION_START_PERIOD+day);
                editor.putInt(Constants.TOTAL_AMOUNT_OPEN_SCREEN, total);
                editor.commit();
                i--;
            } else {
                continueDel = false;
            }
        }
    }

//    private void calculateAverage(SharedPreferences pref) {
//        int counter = pref.getInt(Constants.COUNTER, 0);
//        int curAvg=pref.getInt(Constants.AVERAGE,0);
//        int numDays=pref.getInt(Constants.NUM_OF_DAYS,0);
//        numDays++;
//        curAvg=(curAvg+counter)/numDays;
//
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putInt(Constants.AVERAGE, curAvg);
//        editor.putInt(Constants.NUM_OF_DAYS, numDays);
//        editor.putInt(Constants.PREV_COUNTER, counter);
//        editor.commit();
//
//    }

    @Override
    public void onDestroy() {

        Log.i("DEBUG", "Service  distroy");
        if (mReceiver != null)
            unregisterReceiver(mReceiver);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
