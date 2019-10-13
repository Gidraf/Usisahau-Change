package com.glab.usisahauchange.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.glab.usisahauchange.R;
import com.glab.usisahauchange.utils.WakeLocker;

public class AlertReceiver extends BroadcastReceiver {
    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        WakeLocker.acquire(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, AlertUserService.class).putExtra("start","alerting"));
            context.startService(new Intent(context, AlertUserService.class).putExtra("start","alerting"));
            return;
        }
        context.startService(new Intent(context, AlertUserService.class).putExtra("start","alerting"));
    }
}
