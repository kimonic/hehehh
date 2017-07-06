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
import android.view.LayoutInflater;

import com.diyou.activity.EmailBindActivity;
import com.diyou.activity.LockActivity;
import com.diyou.activity.PhoneBindActivity;
import com.diyou.activity.RealNameAuthActivity;
import com.diyou.activity.RechargeActivity;
import com.diyou.activity.YiBaoRegisterActivity;
import com.diyou.application.MyApplication;
import com.diyou.config.Constants;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.HomeWatcher;
import com.diyou.util.HomeWatcher.OnHomePressedListener;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.ConfirmCancelDialog;
import com.diyou.view.ConfirmCancelDialog.onClickListener;
import com.diyou.view.ProgressDialogBar;
import com.diyou.view.SwipeBackLayout;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.TreeMap;

public class BaseActivity extends FragmentActivity
{
    protected SwipeBackLayout layout;
    private boolean isBackground;

    @Override
    protected void onCreate(Bundle arg0)
    {

        super.onCreate(arg0);
        layout = (SwipeBackLayout) LayoutInflater.from(this)
                .inflate(R.layout.swipebackbase, null);
        layout.attachToActivity(this);
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
        if (MyApplication.isActive)
        {
            MyApplication.isActive = false;
            if (getSharedPreferences(Constants.LOCK, MODE_PRIVATE)
                    .getString(Constants.LOCK_KEY, null) != null)
            {
                ActivityManager am = (ActivityManager) getSystemService(
                        ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                if (!cn.getClassName().contains("LockActivity"))
                {
                    Intent intent = new Intent(BaseActivity.this,
                            LockActivity.class);
                    startActivity(intent);
                    MyApplication.isActive = true;
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
                    || MyApplication.isOnClickLockKey)
            {
                if (isBackground == false)
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
        }
        super.onDestroy();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            unregisterReceiver(this);
            finish();
        }
    };
    private ProgressDialogBar progressDialogBar;
    private HomeWatcher mHomeWatcher;

    public void startActivity(Intent intent)
    {
        MyApplication.getInstance().setTime(100);
        super.startActivity(intent);
        //启动activity时的退出进入动画
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    public void startActivityForResult(Intent intent, int requestCode)
    {
        MyApplication.getInstance().setTime(100);
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right3);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right3);
        // overridePendingTransition(0, R.anim.base_slide_right_out);
    }


    public void showProgressDialog()
    {
        showProgressDialog("加载中...");
    }

    public void showProgressDialog(String msg){
        if (progressDialogBar == null)
        {
            progressDialogBar = ProgressDialogBar.createDialog(this);
        }
        progressDialogBar.setProgressMsg(msg);
        progressDialogBar.show();
    }

    public void hideProgressDialog()
    {
        if (progressDialogBar != null && progressDialogBar.isShowing())
        {
            progressDialogBar.dismiss();
        }

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

    public void requestYibaoRegistered(final Runnable runnable)
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        HttpUtil.post(UrlConstants.IS_TRUST_REGISTER, map,
                new JsonHttpResponseHandler()
                {

                    @Override
                    public void onStart()
                    {
                        showProgressDialog();
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                            JSONObject response)
                    {
                        try
                        {
                            if (CreateCode.AuthentInfo(response))
                            {
                                JSONObject json = StringUtils
                                        .parseContent(response);
                                if (json.optString("result")
                                        .contains("success"))
                                {

                                    JSONObject jo = new JSONObject(
                                            json.optString("data"));
                                    if (1 == jo.optInt("register"))
                                    {
                                        getApprove1(runnable);
//                                        runnable.run();
                                    }
                                    else
                                    {
                                        showYiBaoRegisterDialog();
                                    }
                                }
                                else
                                {
                                    ToastUtil.show(
                                            json.getString("description"));
                                }
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish()
                    {
                        hideProgressDialog();
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        ToastUtil.show(R.string.generic_error);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });

    }

    public void showYiBaoRegisterDialog()
    {
        ConfirmCancelDialog dialog = new ConfirmCancelDialog(this, "托管注册",
                "请先开通资金托管", "马上开通", "暂不开通");
        dialog.setOnClickListener(new onClickListener() {

            @Override
            public void confirmClick() {
                getApprove();
            }

            @Override
            public void cancelClick() {

            }
        });
        dialog.show();
    }

    private void getApprove()
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        HttpUtil.post(UrlConstants.APPROVE, map, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                ToastUtil.show(R.string.generic_error);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                try {
                    if (CreateCode.AuthentInfo(response)) {
                        JSONObject data = StringUtils.parseContent(response);
                        if (data.optString("result").equals("success")) {
                            JSONObject info = data.getJSONObject("data");
                            Intent intent = new Intent();
                            intent.putExtra("authAction",
                                    Constants.ACTION_PROCESS_YBREG);
                            if (StringUtils.isEmpty(info.optString("phone"))) {
                                // 未绑定手机
                                ToastUtil.show("请绑定手机");
                                intent.setClass(BaseActivity.this,
                                        PhoneBindActivity.class);
                                startActivity(intent);
                            }
                            else if (StringUtils
                                    .isEmpty(info.optString("email"))) {
                                // 未认证邮箱
                                ToastUtil.show("请验证邮箱");
                                intent.setClass(BaseActivity.this,
                                        EmailBindActivity.class);
                                startActivity(intent);
                            }
                            else if (StringUtils
                                    .isEmpty(info.optString("realname_status"))
                                    || info.optString("realname_status")
                                    .equals("-1")) {
                                // 未认证实名或认证失败
                                ToastUtil.show("请进行实名认证");
                                intent.setClass(BaseActivity.this,
                                        RealNameAuthActivity.class);
                                startActivity(intent);
                            }
//                            else if (info.optString("realname_status")
//                                    .equals("-2"))
//                            {
//                                // 待审核
//                                ToastUtil.show("实名认证待审核中");
//                                intent.setClass(BaseActivity.this,
//                                        RealNameInfoActivity.class);
//                                startActivity(intent);
//                            }
                            else {
                                startActivity(new Intent(BaseActivity.this,
                                        YiBaoRegisterActivity.class));
                            }

                        } else {
                            ToastUtil.show(data.optString("description"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFinish() {
                hideProgressDialog();
                super.onFinish();
            }

            @Override
            public void onStart() {
                showProgressDialog();
                super.onStart();
            }

        });

    }

    /**
     * 先对邮箱和手机进行判断
     */
    private void getApprove1(final Runnable runnable) {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token",
                UserConfig.getInstance().getLoginToken(this));
        HttpUtil.post(UrlConstants.APPROVE, map, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                ToastUtil.show(R.string.generic_error);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                try {
                    if (CreateCode.AuthentInfo(response)) {
                        JSONObject data = StringUtils.parseContent(response);
                        if (data.optString("result").equals("success")) {
                            JSONObject info = data.getJSONObject("data");
                            Intent intent = new Intent();
                            if (StringUtils.isEmpty(info.optString("phone"))) {
                                // 未绑定手机
                                ToastUtil.show("请绑定手机");
                                intent.putExtra("authAction",
                                        Constants.ACTION_PROCESS_YBREG);
                                intent.setClass(BaseActivity.this,
                                        PhoneBindActivity.class);
                                startActivity(intent);
                            }
                            else if (StringUtils
                                    .isEmpty(info.optString("email"))) {
                                // 未认证邮箱
                                ToastUtil.show("请验证邮箱");
                                intent.putExtra("authAction",
                                        Constants.ACTION_PROCESS_YBREG);
                                intent.setClass(BaseActivity.this,
                                        EmailBindActivity.class);
                                startActivity(intent);
                            }
                            else{
                                runnable.run();
                            }

                        } else {
                            ToastUtil.show(data.optString("description"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFinish() {
                hideProgressDialog();
                super.onFinish();
            }

            @Override
            public void onStart() {
                showProgressDialog();
                super.onStart();
            }

        });

    }

}
