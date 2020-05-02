package com.example.myfirstapplication;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class NotificacionesSegundoPlano extends Service {

    private Context context = this;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences preferences = context.getSharedPreferences(MainActivity.STRING_PREFERENCES, context.MODE_PRIVATE);
        boolean p = preferences.getBoolean(MainActivity.PRIVATE_STATE_BUTTON_SESION, false);
        if(p) {
            //MainActivity.escucharNotificaciones((Activity) context, null);
            Toast.makeText(context, "NOTIFICACIONCILLA PUES", Toast.LENGTH_SHORT).show();
        }
        try {
            Thread.sleep(5000);
            //startService(new Intent(context, NotificacionesSegundoPlano.class));
        } catch (InterruptedException e) {
            // Restore interrupt status.
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
