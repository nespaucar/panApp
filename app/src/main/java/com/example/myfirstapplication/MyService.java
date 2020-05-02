package com.example.myfirstapplication;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyService extends Service {
    private RequestQueue requestQueue;

    Handler handler = new Handler();
    private final int TIEMPO = 5000;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final SharedPreferences preferences = getSharedPreferences(MainActivity.STRING_PREFERENCES, MODE_PRIVATE);
        boolean p = preferences.getBoolean(MainActivity.PRIVATE_STATE_BUTTON_SESION, false);
        requestQueue = Volley.newRequestQueue(this);
        if(p) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    MainActivity.escucharNotificaciones(getApplicationContext(), requestQueue, preferences);
                    handler.postDelayed(this, TIEMPO);
                }

            }, TIEMPO);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
