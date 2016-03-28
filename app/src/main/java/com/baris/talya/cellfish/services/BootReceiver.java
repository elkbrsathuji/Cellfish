package com.baris.talya.cellfish.services;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.baris.talya.cellfish.Constants;
import com.baris.talya.cellfish.MainActivity;
import com.baris.talya.cellfish.core.ScreenOnService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Elkana on 19/11/15.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
       //    SharedPreferences pref= context.getSharedPreferences(Constants.PREFERENCE_NAME, Activity.MODE_PRIVATE);

        //    notifyAtMorning(context, pref);
      //  restartAtMidnight(context,pref);
            Intent i0 = new Intent();
            i0.setAction(".core.ScreenOnService");
            i0.setPackage("com.baris.talya.cellfish");
            context.startService(i0);
        }
    }


//    private void restartAtMidnight(Context context, SharedPreferences pref) {
//        Calendar midnightCalendar = Calendar.getInstance();
//        int today = midnightCalendar.get(Calendar.DATE);
//       int resetDate=pref.getInt(Constants.LAST_RESET_DATE,today-1);
//        Calendar lastResetCal=Calendar.getInstance();
//        lastResetCal.set(Calendar.DATE,resetDate);
//        int diffDays=midnightCalendar.get(Calendar.DAY_OF_MONTH) - lastResetCal.get(Calendar.DAY_OF_MONTH);
//        Log.d("DEBUG", "diff days: " + diffDays);
//while(diffDays>1) {
//    Intent i = new Intent(context, ScreenOnService.class);
//    i.putExtra(Constants.MIDNIGHT, true);
//    context.startService(i);
//    diffDays--;
//}
//    //    if (, today-1) < today) {
//if (diffDays>0){
//            midnightCalendar.set(Calendar.HOUR_OF_DAY, 23);
//            midnightCalendar.set(Calendar.MINUTE, 45);
//            AlarmManager midnightAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            Intent i = new Intent(context, ScreenOnService.class);
//            i.putExtra(Constants.MIDNIGHT, true);
//            PendingIntent midnightAlarmIntent = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//            midnightAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, midnightCalendar.getTimeInMillis(),
//                    AlarmManager.INTERVAL_DAY, midnightAlarmIntent);
//
//
////}
//}
  //  }

    private void notifyAtMorning(Context context, SharedPreferences pref){
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DATE);
        if (pref.getInt(Constants.LAST_NOTIFICATION_DATE, today-1) < today) {

            calendar.set(Calendar.HOUR_OF_DAY, Constants.NOTIFICATION_TIME_H);
            calendar.set(Calendar.MINUTE, Constants.NOTIFICATION_TIME_M);

            AlarmManager notifyAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            final Intent i = new Intent(context, NotifyService.class);
            PendingIntent notifyAlarmIntent = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            notifyAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, notifyAlarmIntent);

        }
    }


}
