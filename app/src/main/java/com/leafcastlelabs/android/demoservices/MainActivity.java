package com.leafcastlelabs.android.demoservices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static com.leafcastlelabs.android.demoservices.StartedService.EKSTRA_KEY_SLEEPTIME;
import static com.leafcastlelabs.android.demoservices.StartedService.EXTRA_KEY_BROADCAST_RESULT;
import static com.leafcastlelabs.android.demoservices.StartedService.SERVICE_TASK_RESULT_COMPLETE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //widgets
    private Button btnStartService, btnStopService,
            btnStartForeground,btnStopForeground,
            btnBind, btnUnbind, btnGetCount,
            btnExit;
    private TextView txtCount;

    private ServiceConnection serviceConnection;
    private BoundService boundService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();

        createServiceConnection();

    }

    //init UI widgets
    private void setupUI() {
        btnStartService = findViewById(R.id.btnServiceStart);
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService();
            }
        });

        btnStopService = findViewById(R.id.btnServiceStop);
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService();
            }
        });



        btnStartForeground = findViewById(R.id.btnStartForeground);
        btnStartForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startForegroundService();
            }
        });

        btnStopForeground = findViewById(R.id.btnStopForeground);
        btnStopForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopForegroundService();
            }
        });

        btnBind = findViewById(R.id.btnBind);
        btnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindService();
            }
        });

        btnUnbind = findViewById(R.id.btnUnbind);
        btnUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unbindService();
            }
        });

        btnGetCount = findViewById(R.id.btnGetCount);
        btnGetCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCount();
            }
        });

        txtCount = findViewById(R.id.txtLastCount);

        btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter();
        filter.addAction(SERVICE_TASK_RESULT_COMPLETE);

        LocalBroadcastManager.getInstance(this).registerReceiver(backgroundServiceResultReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(backgroundServiceResultReceiver);
    }

    //background service
    private void startService(){
        Intent backgroundServiceIntent = new Intent(this, StartedService.class);
        backgroundServiceIntent.putExtra(EKSTRA_KEY_SLEEPTIME, 5000);
        startService(backgroundServiceIntent);
    }

    private void stopService(){
        Intent backgroundServiceIntent = new Intent(this, StartedService.class);
        stopService(backgroundServiceIntent);
    }

    //foreground service
    private void startForegroundService() {
        Intent foregroundServiceIntent = new Intent(this, ForegroundService.class);
        startService(foregroundServiceIntent);
    }

    private void stopForegroundService() {
        Intent foregroundServiceIntent = new Intent(this, ForegroundService.class);
        stopService(foregroundServiceIntent);
    }


    private void handleResult(String res){
        Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
    }

    //broadcast receiver

    private BroadcastReceiver backgroundServiceResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent resultData) {

            String result = resultData.getStringExtra(EXTRA_KEY_BROADCAST_RESULT);
            if(result!=null){
                handleResult(result);
            }
        }
    };

    //bound service

    private void getCount() {

        if (boundService != null) {
            int count = boundService.getCount();
            txtCount.setText("Last count:" + count);
        }
    }

    private void createServiceConnection(){
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

                Log.d(TAG, "onServiceConnected: ");
                boundService = ((BoundService.BoundServiceBinder)iBinder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

                Log.d(TAG, "onServiceDisconnected: ");

            }
        };
    }

    private void bindService(){
        Intent boundIntent = new Intent(this, BoundService.class);
        bindService(boundIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void unbindService(){
        if(serviceConnection!=null) {
            unbindService(serviceConnection);
        }
    }
}