package com.example.background.tasks;

import android.util.Log;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;

public class HttpGetTask implements Runnable, Callable<Void> {

    @Override
    public void run() {
        try {
            call();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Void call() throws Exception {
        int amount = 0;
        try (InputStream input = new URL("https://jsonplaceholder.typicode.com/todos/1").openStream()) {
            byte[] buff = new byte[1024];
            int read = 0;
            while ((read = input.read(buff)) >= 0) {
                amount += read;
            }
        }
        Log.d("HttpGetTask", "Finished reading " + amount + "bytes");

        return null;
    }
}
