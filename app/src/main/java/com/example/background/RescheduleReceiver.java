package com.example.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class RescheduleReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        new ReScheduler(context)
                .scheduleHighFrequency()
                .scheduleLowFrequency(false);
    }
}
