package com.diyou.pushservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends Service
{

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        AlarmManager manager = (AlarmManager) getSystemService(
                Context.ALARM_SERVICE);
        // 创建Intent
        Intent intent = new Intent(this, PushReceiver.class);
        intent.setAction("com.example.clock");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, 0);
        // 周期触发
        manager.setRepeating(AlarmManager.RTC, 0, 5 * 1000, pendingIntent);
        super.onCreate();
    }

    @Override
    @Deprecated
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        Intent i = new Intent("com.example.service_destory");
        sendBroadcast(i);
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }

}
