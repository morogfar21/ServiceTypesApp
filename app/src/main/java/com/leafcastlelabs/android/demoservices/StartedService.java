package com.leafcastlelabs.android.demoservices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//background service (started service)
public class StartedService extends Service {

    private static final String TAG = "StartedService";
    public static final String EKSTRA_KEY_SLEEPTIME = "EKSTRA_KEY_SLEEPTIME";
    public static final String SERVICE_TASK_RESULT_COMPLETE = "SERVICE_TASK_RESULT_COMPLETE";
    public static final String EXTRA_KEY_BROADCAST_RESULT = "EXTRA_KEY_BROADCAST_RESULT";

    ExecutorService execService;

    int tasks;                  //number of tasks run
    int sleepTime;       //sleep time in milliseconds
    boolean started = false;    //is started

    public StartedService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tasks = 0;
        Log.d(TAG, "onCreate: Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand: ");

        sleepTime = intent.getIntExtra(EKSTRA_KEY_SLEEPTIME, 3000);

        doBackgroundStuff();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        started = false;
        super.onDestroy();
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

        //DONT DO THIS (SLEEP ON MAIN THREAD)
        /*
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            Log.e(TAG, "doRecursiveWork: Thread error", e);
        }
        */

        execService.submit(new Runnable() {
            @Override
            public void run() {
                tasks++;
                Log.d(TAG, "run: Task started: " + tasks);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Log.e(TAG, "run: EROOR", e);
                }
                sendTaskResultAsBroadcast("Task# " + tasks + " completed");

                if(started) {
                    doRecursiveWork();
                }
            }
        });
    }

    private void sendTaskResultAsBroadcast(String result){
        Log.d(TAG, "broadcasting: " + result);
        Intent broadcast = new Intent();
        broadcast.setAction(SERVICE_TASK_RESULT_COMPLETE);
        broadcast.putExtra(EXTRA_KEY_BROADCAST_RESULT, result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
