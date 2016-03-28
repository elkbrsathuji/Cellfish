package com.baris.talya.cellfish.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Elkana on 16/11/15.
 */
public class ScreenOnReceiver extends BroadcastReceiver {

    private boolean screenOff;
    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "BroadcastReceiver", Toast.LENGTH_SHORT).show();


        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            Intent i = new Intent(context, ScreenOnService.class);
            i.putExtra("screen_state", false);
            context.startService(i);

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

            Intent i = new Intent(context, ScreenOnService.class);
            i.putExtra("screen_state", true);
            context.startService(i);

        }

        // Toast.makeText(context, "BroadcastReceiver :"+screenOff, Toast.LENGTH_SHORT).show();

        // Send Current screen ON/OFF value to service

    }
}
