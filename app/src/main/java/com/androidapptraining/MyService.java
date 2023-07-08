package com.androidapptraining;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyService extends Service {
    MediaPlayer myPlayer;
    String name;

    public MyService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
        myPlayer = MediaPlayer.create(this,R.raw.default_tone);
        myPlayer.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //myPlayer.start();
        name = intent.getStringExtra("name");
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        Notification notification = new NotificationCompat.Builder(this,App.CHANNEL_ID)
                .setContentTitle("Android App Training")
                .setContentText("Service Running")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        try{
            startForeground(1,notification);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myPlayer.stop();
        Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
    }
}
