package com.example.background.tasks;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

public class WriterTask implements Runnable, Callable<Void> {

    private static final int WRITE_AMOUNT = 1024 * 1024 * 1024;

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
        File devZero = new File("/dev/zero");
        File devNull = new File("/dev/null");
        int amount = 0;
        try (InputStream input = new FileInputStream(devZero); OutputStream output = new FileOutputStream(devNull)) {
            byte[] buff = new byte[1024];
            while (amount < WRITE_AMOUNT) {
                final int read = input.read(buff);
                if (read < 0) {
                    break;
                }
                amount += read;
                output.write(buff, 0, read);
            }
        }
        Log.d("WriterTask", "Finished writing " + amount + "bytes");

        return null;
    }
}
