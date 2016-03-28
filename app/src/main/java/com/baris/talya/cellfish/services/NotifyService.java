package com.baris.talya.cellfish.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.baris.talya.cellfish.Constants;
import com.baris.talya.cellfish.MainActivity;
import com.baris.talya.cellfish.R;

import java.util.Calendar;

/**
 * Created by Elkana on 17/11/15.
 */
public class NotifyService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notifyUser();
        return super.onStartCommand(intent, flags, startId);
    }

    private void notifyUser() {
        SharedPreferences pref = getSharedPreferences(Constants.PREFERENCE_NAME, Activity.MODE_PRIVATE);
String summary = getResources().getString(R.string.title_summary_content);
        int prev = pref.getInt(Constants.PREV_COUNTER, -1);
        if (prev>=0) {
            summary = summary.replace("[NUM]", String.valueOf(prev));
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.fish)
                            .setContentTitle(getString(R.string.title_summary))
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setContentText(summary);


// Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(this, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
            // stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setAutoCancel(true);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
            mNotificationManager.notify(0, mBuilder.build());
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Constants.LAST_NOTIFICATION_DATE, (Calendar.getInstance().get(Calendar.DATE)));
        editor.commit();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
