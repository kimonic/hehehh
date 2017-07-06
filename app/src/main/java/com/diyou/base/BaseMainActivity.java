package com.diyou.base;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.diyou.activity.BeforeLoginActivity;
import com.diyou.activity.LockActivity;
import com.diyou.application.MyApplication;
import com.diyou.config.Constants;
import com.diyou.config.UserConfig;
import com.diyou.util.HomeWatcher;
import com.diyou.util.HomeWatcher.OnHomePressedListener;
import com.diyou.util.SharedPreUtils;
import com.diyou.v5yibao.R;
import com.example.encryptionpackages.AESencrypt;
import com.example.encryptionpackages.CreateCode;

import java.util.List;

public class BaseMainActivity extends FragmentActivity
{
    private boolean isActive = true;
    private HomeWatcher mHomeWatcher;
    private boolean isBackground;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.diyou.v5.activity.BaseActivity");
        registerReceiver(this.broadcastReceiver, filter);
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener()
        {

            @Override
            public void onHomePressed()
            {
                if (!MyApplication.isCountDownTimerStart)
                {
                    MyApplication.getInstance().getTimer().start();
                    MyApplication.isOnClickLockKey = true;
                    MyApplication.isCountDownTimerStart = true;
                }
            }

            @Override
            public void onHomeLongPressed()
            {
            }

            @Override
            public void onLockPressed()
            {
                isBackground = isBackground(getApplicationContext());
                MyApplication.getInstance().getTimer().cancel();
                MyApplication.getInstance().setTime(0);
                MyApplication.isOnClickLockKey = true;
            }
        });
        mHomeWatcher.startWatch();
    }

    private void isStartLock()
    {
        isActive = false;
        if (!isActive)
        {
            if (getSharedPreferences(Constants.LOCK, MODE_PRIVATE)
                    .getString(Constants.LOCK_KEY, null) != null)
            {
                ActivityManager am = (ActivityManager) getSystemService(
                        ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                if (!cn.getClassName().contains("LockActivity"))
                {
                    Intent intent = new Intent(BaseMainActivity.this,
                            LockActivity.class);
                    startActivity(intent);
                    isActive = true;
                    MyApplication.isCountDownTimerStart = false;
                    MyApplication.isOnClickLockKey = false;
                }

            }
        }
    }

    @Override
    protected void onResume()
    {
        if (MyApplication.getInstance().getTime() == 0)
        {
            if (!MyApplication.lockActivityIstThere
                    && MyApplication.isOnClickLockKey)
            {
                if (!isBackground)
                {
                    MyApplication.lockActivityIstThere = false;
                    isStartLock();
                }

            }
        }
        else
        {
            if (MyApplication.getInstance().getTimer() != null)
            {
                MyApplication.getInstance().getTimer().cancel();
            }
            MyApplication.getInstance().getTimer().cancel();

        }
        MyApplication.isCountDownTimerStart = false;

        super.onResume();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    protected void onDestroy()
    {
        try
        {
            unregisterReceiver(broadcastReceiver);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            MyApplication.isLogin = false;
            SharedPreUtils.putString(Constants.SHARE_LOGINTOKEN,
                    AESencrypt.encrypt2PHP(CreateCode.getSEND_AES_KEY(), ""),
                    MyApplication.app);
            UserConfig.getInstance().setLoginToken(null);
            UserConfig.getInstance().clear();
            getSharedPreferences(Constants.LOCK, MODE_PRIVATE).edit()
                    .clear().commit();
            if (!(MyApplication.getInstance().getThirdFragment() == null))
            {
                MyApplication.getInstance().getThirdFragment().loginOut();
            }
            // if (!(MyApplication.getInstance().getFirstFragment() == null))
            // {
            // MyApplication.getInstance().getFirstFragment().setLoginShow();
            // }
            Intent intent1 = new Intent(BaseMainActivity.this,
                    BeforeLoginActivity.class);
            intent1.putExtra("otherLogin", true);
            startActivity(intent1);

        }
    };

    public void startActivity(Intent intent)
    {
        MyApplication.getInstance().setTime(100);
        super.startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    public void startActivityForResult(Intent intent, int requestCode)
    {
        MyApplication.getInstance().setTime(100);
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

//    public void finish1()
//    {
//        super.finish();
//        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right3);
//    }

    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    private boolean isBackground(Context context)
    {

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses)
        {
            if (appProcess.processName.equals(context.getPackageName()))
            {
                return appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND;
            }
        }
        return false;

    }
}
