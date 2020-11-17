package com.leafcastlelabs.android.demoservices;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BoundService extends Service {

    private static final String TAG = "BoundService";

    public class BoundServiceBinder extends Binder {
        BoundService getService() { return BoundService.this; }
    }

    private final BoundServiceBinder binder = new BoundServiceBinder();
    int count;
    boolean running;

    public BoundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        count = 0;
        running = true;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while(running){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count++;
                    Log.d(TAG, "count is now: " + count);
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        running = false;
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    public int getCount(){
        return count;
    }
}
