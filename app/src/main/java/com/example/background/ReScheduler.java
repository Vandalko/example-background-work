package com.example.background;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.background.workers.HighFrequencyForegroundService;
import com.example.background.workers.LowFrequencyWorker;

import java.util.concurrent.TimeUnit;

public class ReScheduler {

    private final Context context;

    public ReScheduler(Context context) {
        this.context = context;
    }


    public ReScheduler scheduleHighFrequency() {
        ContextCompat.startForegroundService(context, new Intent(context, HighFrequencyForegroundService.class)
                .setAction(HighFrequencyForegroundService.ACTION_START_TASKS));

        return this;
    }

    public ReScheduler scheduleLowFrequency(boolean restart) {
        ExistingPeriodicWorkPolicy existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP;
        if (restart) {
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.REPLACE;
        }

        WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork(
                        "LowFrequencyWorker-foreground",
                        existingPeriodicWorkPolicy,
                        new PeriodicWorkRequest.Builder(
                                LowFrequencyWorker.class,
                                PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                                TimeUnit.MILLISECONDS,
                                PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
                                TimeUnit.MILLISECONDS
                        )
                                .addTag("LowFrequencyWorker")
                                .setInputData(new Data.Builder()
                                        .putBoolean(LowFrequencyWorker.PARAM_GO_FOREGROUND, true)
                                        .build())
                                .setInitialDelay(0, TimeUnit.MILLISECONDS)
                                .build()
                );

        WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork(
                        "LowFrequencyWorker-background",
                        existingPeriodicWorkPolicy,
                        new PeriodicWorkRequest.Builder(
                                LowFrequencyWorker.class,
                                PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                                TimeUnit.MILLISECONDS,
                                PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
                                TimeUnit.MILLISECONDS
                        )
                                .addTag("LowFrequencyWorker")
                                .setInputData(new Data.Builder()
                                        .putBoolean(LowFrequencyWorker.PARAM_GO_FOREGROUND, false)
                                        .build())
                                .setInitialDelay(0, TimeUnit.MILLISECONDS)
                                .build()
                );

        return this;
    }
}
