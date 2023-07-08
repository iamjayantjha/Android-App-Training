package com.androidapptraining;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /*if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){

        }*/
        Toast.makeText(context, "Command Received", Toast.LENGTH_SHORT).show();
        startMyService(context,intent);

    }

    private void startMyService(Context context, Intent intent) {
        Intent service = new Intent(context, MyService.class);
        service.putExtra("name",intent.getStringExtra("name"));
        Toast.makeText(context, "Starting Service", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(service);
        }else {
            context.startService(service);
        }
    }
}
