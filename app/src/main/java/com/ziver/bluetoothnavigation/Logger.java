package com.ziver.bluetoothnavigation;

import android.util.Log;

import androidx.annotation.NonNull;

public class Logger {

    public static void debug(@NonNull String debug) {
        Log.d("MY_LOG", debug);
    }

    public static void exception(@NonNull Throwable throwable) {
        Log.e("MY_LOG", String.valueOf(throwable.getMessage()));
    }
}
