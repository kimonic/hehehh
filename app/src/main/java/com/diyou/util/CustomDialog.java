package com.diyou.util;

import com.diyou.activity.LockSetupActivity;
import com.diyou.application.MyApplication;
import com.diyou.config.Constants;
import com.diyou.config.UserConfig;
import com.diyou.v5yibao.R;
import com.example.encryptionpackages.AESencrypt;
import com.example.encryptionpackages.CreateCode;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * <p>
 * Title: CustomDialog
 * </p>
 * <p>
 * Description:自定义Dialog（参数传入Dialog样式文件，Dialog布局文件）
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * @author archie
 * @version 1.0
 */
public class CustomDialog extends Dialog
        implements android.view.View.OnClickListener
{
    Activity context;
    private Button confirmBtn;
    private Button cancelBtn;
    private String title;
    private String subtitle;
    private int functionId;
    private String phoneNum;

    public CustomDialog(Activity context, String title, String subtitle,
            int functionId, String phoneNum)
    {
        super(context, R.style.mystyle);
        this.context = context;
        this.title = title;
        this.subtitle = subtitle;
        this.functionId = functionId;
        this.phoneNum = phoneNum;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.custom_dialog);
        this.setCanceledOnTouchOutside(false);
        TextView title = (TextView) findViewById(R.id.customdialog_title);
        title.setText(this.title);
        TextView subtitle = (TextView) findViewById(R.id.customdialog_subtitle);
        subtitle.setText(this.subtitle);
        // 根据id在布局中找到控件对象
        confirmBtn = (Button) findViewById(R.id.confirm_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);

        // 设置按钮的文本颜色
        confirmBtn.setTextColor(0xff1E90FF);
        cancelBtn.setTextColor(0xff1E90FF);

        // 为按钮绑定点击事件监听器
        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.confirm_btn:
            switch (functionId)
            {
            case Constants.FORGOTPASSWORD:
                SharedPreUtils.putString(
                        Constants.SHARE_LOGINTOKEN, AESencrypt
                                .encrypt2PHP(CreateCode.getSEND_AES_KEY(), ""),
                        MyApplication.app);
                UserConfig.getInstance().setLoginToken(null);
                UserConfig.getInstance().clear();
                context.getSharedPreferences(Constants.LOCK,
                        Context.MODE_PRIVATE).edit().clear().commit();
                if (!(MyApplication.getInstance().getThirdFragment() == null))
                {
                    MyApplication.getInstance().getThirdFragment().loginOut();
                }
                context.sendBroadcast(
                        new Intent("com.diyou.v5.activity.BaseActivity"));
                context.finish();
                this.dismiss();
                break;
            case Constants.LOGINSUCCESS:
                context.startActivity(
                        new Intent(context, LockSetupActivity.class));
                MyApplication.getInstance().getBeforeLoginActivity().finish();
                context.finish();
                this.dismiss();
                break;
            case Constants.REGISTERSUCCESS:

                context.startActivity(
                        new Intent(context, LockSetupActivity.class));
                this.dismiss();
                break;
            case Constants.PHONECALL:
                Intent intent = new Intent();
                // intent.setAction(Intent.ACTION_CALL);
                intent.setAction(Intent.ACTION_DIAL);// 跳到拨号界面
                intent.setData(Uri.parse("tel:" + this.phoneNum));
                context.startActivity(intent);
                this.dismiss();
                break;
            case Constants.APPEXIT:
                context.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                this.dismiss();
                break;
            case Constants.LOGOUT:
                MyApplication.isLogin = false;
                SharedPreUtils.putString(
                        Constants.SHARE_LOGINTOKEN, AESencrypt
                                .encrypt2PHP(CreateCode.getSEND_AES_KEY(), ""),
                        MyApplication.app);
                UserConfig.getInstance().setLoginToken(null);
                UserConfig.getInstance().clear();
                context.getSharedPreferences(Constants.LOCK,
                        Context.MODE_PRIVATE).edit().clear().commit();
                if (!(MyApplication.getInstance().getThirdFragment() == null))
                {
                    MyApplication.getInstance().getThirdFragment().loginOut();
                }
                context.finish();
                this.dismiss();
                break;
            default:

                break;
            }
            break;
        case R.id.cancel_btn:

            ActivityManager am = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            @SuppressWarnings("deprecation")
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            if (cn.getClassName().contains("LoginActivity")
                    || cn.getClassName().contains("RegisteredActivity"))
            {
                context.finish();
                MyApplication.getInstance().getBeforeLoginActivity().finish();
                // context.setResult(Constants.LOGINREQUESTCODE);
            }
            else
            {
                this.dismiss();

            }
            break;
        default:
            break;
        }
    }
}