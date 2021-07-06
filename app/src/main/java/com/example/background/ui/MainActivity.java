package com.example.background.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.background.R;
import com.example.background.ReScheduler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.restartButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReScheduler(v.getContext())
                        .scheduleHighFrequency()
                        .scheduleLowFrequency(true);
            }
        });

        findViewById(R.id.autostartManagerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoStartHelper.getInstance().getAutoStartPermission(v.getContext());
            }
        });

        findViewById(R.id.powerSaverButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        startActivity(new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getPackageName())));
                    } catch (Throwable t) {
                        Log.e("MainActivity", "Unable to launch battery optimization options", t);
                    }
                }
            }
        });
    }
}