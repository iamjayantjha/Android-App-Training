package com.androidapptraining;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.core.app.NotificationCompat;

public class App extends Application {
    public static final String CHANNEL_ID = "Android App Training";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"Android App Training Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


    }
}
