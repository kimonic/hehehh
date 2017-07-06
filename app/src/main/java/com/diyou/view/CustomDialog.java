package com.diyou.view;

import com.diyou.v5yibao.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
    private int phoneNum;

    public CustomDialog(Activity context, String title, String subtitle,
            int functionId, int phoneNum)
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
            Intent intent = new Intent();
            switch (functionId)
            {
            // case Constants.PHONECALL:
            // intent.setAction(Intent.ACTION_CALL);
            // intent.setData(Uri.parse("tel:" + this.phoneNum));
            // context.startActivity(intent);
            // this.dismiss();
            // break;
            // case Constants.LOGOUT:
            // MyApplication.accountFragmentIsLoad = false;
            // SharedPreUtils.putString(Constants.SHARE_USERKEY,
            // AESencrypt.encrypt2PHP(AESencrypt.PASSWORD, ""),
            // context);
            // SharedPreUtils.putString(Constants.SHARE_USERID,
            // AESencrypt.encrypt2PHP(AESencrypt.PASSWORD, ""),
            // context);
            // UserConfig.getInstance().clear();
            // context.getSharedPreferences(MainActivity.LOCK,
            // context.MODE_PRIVATE).edit().clear().commit();
            // context.setResult(Constants.LOGOUT);
            // context.finish();
            // this.dismiss();
            // break;
            // case Constants.APPEXIT:
            // context.finish();
            // android.os.Process.killProcess(android.os.Process.myPid());
            // this.dismiss();
            // break;
            // case Constants.PAYPASSWORDEDITACTIVITY:
            // context.startActivity(new Intent(context,
            // PayPasswordEditActivity.class));
            // context.finish();
            // break;
            // case Constants.MOBILEPHONEBINDINGACTIVITY:
            // context.startActivity(new Intent(context,
            // MobilePhoneBindingActivity.class));
            // context.finish();
            // break;

            default:
                break;
            }
            break;
        case R.id.cancel_btn:
            this.dismiss();
            break;
        default:
            break;
        }
    }
}