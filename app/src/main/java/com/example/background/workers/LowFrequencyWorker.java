package com.example.background.workers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.background.R;
import com.example.background.tasks.HttpGetTask;
import com.example.background.tasks.WriterTask;

public class LowFrequencyWorker extends Worker {

    public static final String PARAM_GO_FOREGROUND = "com.example.background.workers.PARAM_FOREGROUND";

    private final boolean goForeground;

    public LowFrequencyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        goForeground = workerParams.getInputData().getBoolean(PARAM_GO_FOREGROUND, false);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("LowFrequencyWorker", "Started worker. goForeground=" + goForeground);
        if (goForeground) {
            String channelId = "ExampleForegroundService";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "foreground-service", NotificationManager.IMPORTANCE_LOW);
                channel.setDescription("Foreground service channel");
                NotificationManagerCompat.from(getApplicationContext()).createNotificationChannel(channel);
            }

            try {
                setForegroundAsync(new ForegroundInfo(
                        R.id.notification_foreground_worker,
                        new NotificationCompat.Builder(getApplicationContext(), channelId)
                                .setOnlyAlertOnce(true)
                                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                                .setContentTitle(getApplicationContext().getText(R.string.app_name))
                                .build(),
                        1 //ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )).get();
            } catch (Throwable t) {
                Log.e("LowFrequencyWorker", "Failed to start foreground worker", t);
                return Result.failure();
            }
        }

        try {
            new WriterTask().run();
        } catch (Throwable t) {
            Log.w("LowFrequencyWorker", "Failed to run WriterTask", t);
        }
        try {
            new HttpGetTask().run();
        } catch (Throwable t) {
            Log.w("LowFrequencyWorker", "Failed to run HttpGetTask", t);
        }

        return Result.success();
    }
}
