package com.glab.usisahauchange.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;

import com.glab.usisahauchange.R;
import com.glab.usisahauchange.utils.WakeLocker;


public class AlertUserService extends Service {
   final IBinder alertBind =new AlertBinder();
    Handler timeHandler;
    boolean isOnProgress = false;
    RemoteViews remoteViews;
    Intent closeIntent;
    PendingIntent closePendingIntent;
    Vibrator vibrator;
    NotificationManager manager;

    public void setOnProgress(boolean onProgress) {
        isOnProgress = onProgress;
    }

    Runnable alertRunnable = new Runnable() {
        public void run() {

        }
    };

    public AlertUserService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return alertBind;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        remoteViews = new RemoteViews(getPackageName(),R.layout.alert_view);
        closeIntent = new Intent(this, AlertUserService.class);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        closeIntent.setAction("Wazi");
        closePendingIntent = PendingIntent.getService(this, 0 , closeIntent,0);
        remoteViews.setOnClickPendingIntent(R.id.close_notification, closePendingIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String intentString = intent.getStringExtra("start");
        String intentAction = intent.getAction();
        if (intentString != null && intentString.equals("alerting")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startMyOwnForeground();
            }
            else {
                buildNotification();
            }
        }
        else  if (intentAction!= null && intentAction.equals("Wazi")){
            stopNotifocation();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void stopNotifocation() {
        WakeLocker.release();
        vibrator.cancel();
        onDestroy();
        stopForeground(true);
        manager.cancelAll();
    }

    public boolean isOnProgress() {
        return isOnProgress;
    }

    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.glab.usisahauchange";
        String channelName = "Usisahau change service";
        NotificationChannel chan = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            long[] pattern = {0, 1000, 1000};
            vibrator.vibrate(pattern,0);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            assert manager != null;
            manager.createNotificationChannel(chan);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setShowWhen(true)
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setSmallIcon(R.drawable.ic_warningsign)
                    .setContentText("Make sure umeitisha change yako")
                    .setContentTitle("Usisahau Change");
            builder.setCustomContentView(remoteViews);
            builder.setAutoCancel(true);
            builder.setPriority(Notification.PRIORITY_MAX);
            Notification notification = builder.build();
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            startForeground(1, notification);
        }

    }

    private void buildNotification() {
        int notificationAction = android.R.drawable.ic_media_pause;
        PendingIntent play_pauseAction = null;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            // Create a new Notification
        long[] pattern = {0, 1000, 1000};
        vibrator.vibrate(pattern,0);
            builder = new NotificationCompat.Builder(this)
                    // Hide the timestamp
                    .setShowWhen(false)
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setContentText("Itisha change yako")
                    .setContentTitle("Usisahau Change");
            builder.setCustomContentView(remoteViews);
            builder.setPriority(Notification.PRIORITY_MAX);
            builder.setAutoCancel(true);
        Notification not = builder.build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else {
            startForeground(1, not);
        }
    }

    public class AlertBinder extends Binder {

        public AlertUserService getService() {
            return AlertUserService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WakeLocker.release();
        manager.cancelAll();
    }
}
