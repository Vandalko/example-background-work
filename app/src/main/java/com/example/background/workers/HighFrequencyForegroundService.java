package com.example.background.workers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.background.R;
import com.example.background.tasks.HttpGetTask;
import com.example.background.tasks.WriterTask;

import java.util.Timer;
import java.util.TimerTask;

public class HighFrequencyForegroundService extends Service {

    public static final String ACTION_START_TASKS = "com.example.background.ACTION_START_TASKS";

    private Timer timerTask = null;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "ExampleForegroundService";
            NotificationChannel channel = new NotificationChannel(channelId, "foreground-service", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Foreground service channel");
            NotificationManagerCompat.from(this).createNotificationChannel(channel);

            startForeground(R.id.notification_foreground_service,
                    new NotificationCompat.Builder(this, channelId)
                            .setOnlyAlertOnce(true)
                            .setSmallIcon(android.R.drawable.ic_dialog_alert)
                            .setContentTitle(getText(R.string.app_name))
                            .setOngoing(true)
                            .build());
        }

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        timerTask = new Timer();
        timerTask.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            new WakeLockWrapperRunnable(new WriterTask(), powerManager).run();
                        } catch (Throwable t) {
                            Log.w("HighFrequencyService", "Failed to run WriterTask", t);
                        }
                        try {
                            new WakeLockWrapperRunnable(new HttpGetTask(), powerManager).run();
                        } catch (Throwable t) {
                            Log.w("HighFrequencyService", "Failed to run HttpGetTask", t);
                        }
                    }
                },
                0,
                10_000
        );

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

    }

    private static class WakeLockWrapperRunnable implements Runnable {

        private final Runnable core;
        private final PowerManager powerManager;

        private WakeLockWrapperRunnable(Runnable core, PowerManager powerManager) {
            this.core = core;
            this.powerManager = powerManager;
        }


        @Override
        public void run() {
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.example.background:HighFrequencyForegroundService");
            try {
                wakeLock.acquire();
                core.run();
            } finally {
                wakeLock.release();
            }
        }
    }
}
