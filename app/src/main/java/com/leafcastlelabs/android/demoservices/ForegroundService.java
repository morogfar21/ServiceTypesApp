package com.leafcastlelabs.android.demoservices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForegroundService extends Service {

    private static final String TAG = "ForegroundService";
    public static final String SERVICE_CHANNEL = "serviceChannel";
    public static final int NOTIFICATION_ID = 42;

    ExecutorService execService;
    boolean started = false;
    int count;

    public ForegroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        count = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(SERVICE_CHANNEL, "Foreground Service", NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, SERVICE_CHANNEL)
                .setContentTitle("This is your Foregraound service in the background")
                .setContentText("Just let it run, it's totally cool")
                .setSmallIcon(R.drawable.ic_service_24)
                .setTicker("Some more information about your service")
                .build();

        startForeground(NOTIFICATION_ID, notification);

        doBackgroundStuff();
        return START_STICKY;
    }

    private void doBackgroundStuff() {
        if(!started) {
            started = true;
            doRecursiveWork();
        }
    }

    private void doRecursiveWork(){
        if(execService == null) {
            execService = Executors.newSingleThreadExecutor();
        }

        execService.submit(new Runnable() {
            @Override
            public void run() {
                count++;
                Log.d(TAG, "Count: " + count);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "run: EROOR", e);
                }

                if(started) {
                    doRecursiveWork();
                }
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        started = false;
        super.onDestroy();
    }
}
