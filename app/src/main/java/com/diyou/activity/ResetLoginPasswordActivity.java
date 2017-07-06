package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONObject;

import com.diyou.application.MyApplication;
import com.diyou.base.BaseActivity;
import com.diyou.config.UrlConstants;
import com.diyou.http.HttpUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.ProgressDialogBar;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ResetLoginPasswordActivity extends BaseActivity
        implements OnClickListener
{

    private EditText activity_update_newlogin_password_new_password;
    private EditText activity_update_newlogin_password_check_new_password;
    private ImageView new_password_imageview;
    private ImageView again_password_imageview;
    protected ProgressDialogBar progressDialogBar;
    private boolean showAgainPayment = true;
    private boolean showPayment = true;
    private int type;
    private String code;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        type = getIntent().getIntExtra("type", -1);
        code = getIntent().getStringExtra("code");
        username = getIntent().getStringExtra("username");
        setContentView(R.layout.activity_reset_login_password);
        initView();
    }

    private void initView()
    {
        findViewById(R.id.activity_update_login_password_title_layout)
                .findViewById(R.id.title_bar_back).setOnClickListener(this);
        ((TextView) findViewById(
                R.id.activity_update_login_password_title_layout)
                        .findViewById(R.id.title_name)).setText(
                                R.string.title_activity_update_newlogin_password);
        findViewById(R.id.updataemailcctivity_next_btn)
                .setOnClickListener(this);
        findViewById(R.id.show_new_password_RelativeLayout)
                .setOnClickListener(this);
        new_password_imageview = (ImageView) findViewById(
                R.id.show_new_password);
        findViewById(R.id.show_login_password_again_RelativeLayout)
                .setOnClickListener(this);
        again_password_imageview = (ImageView) findViewById(
                R.id.show_login_password_again);
        activity_update_newlogin_password_new_password = (EditText) findViewById(
                R.id.activity_update_newlogin_password_new_password);
        activity_update_newlogin_password_check_new_password = (EditText) findViewById(
                R.id.activity_update_newlogin_password_check_new_password);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.show_new_password_RelativeLayout:
            if (showPayment)
            {
                showPayment = false;
                activity_update_newlogin_password_new_password
                        .setInputType(InputType.TYPE_CLASS_TEXT);
                new_password_imageview
                        .setBackgroundResource(R.drawable.show_password_press);
            }
            else
            {
                showPayment = true;
                activity_update_newlogin_password_new_password
                        .setInputType(0x81);
                new_password_imageview
                        .setBackgroundResource(R.drawable.show_passwprd);
            }
            break;
        case R.id.show_login_password_again_RelativeLayout:
            if (showAgainPayment)
            {
                showAgainPayment = false;
                activity_update_newlogin_password_check_new_password
                        .setInputType(InputType.TYPE_CLASS_TEXT);
                again_password_imageview
                        .setBackgroundResource(R.drawable.show_password_press);

            }
            else
            {
                showAgainPayment = true;
                activity_update_newlogin_password_check_new_password
                        .setInputType(0x81);
                again_password_imageview
                        .setBackgroundResource(R.drawable.show_passwprd);
            }

            break;
        case R.id.title_bar_back:
            finish();
            break;
        case R.id.updataemailcctivity_next_btn:
            if (StringUtils.checkLoginPassword(activity_update_newlogin_password_new_password.getText()
                    .toString()))
            {
                if(StringUtils.isEmpty(StringUtils.getString(activity_update_newlogin_password_check_new_password))){
                    ToastUtil.show(R.string.activity_update_login_password_no_meg_password);
                }else if (!activity_update_newlogin_password_new_password.getText()
                        .toString()
                        .equals(activity_update_newlogin_password_check_new_password
                                .getText().toString()))
                {
                    ToastUtil.show(R.string.activity_update_login_password_error_password);
                }
                else
                {
                    requestData();
                }
            }
            break;
        }
    }

    private void requestData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("code", code);
        map.put("type", type + "");
        map.put("username", username);
        map.put("new_password", activity_update_newlogin_password_new_password
                .getText().toString());
        map.put("confirm_password",
                activity_update_newlogin_password_check_new_password.getText()
                        .toString());
        HttpUtil.post(UrlConstants.EIDT_RECOBER_PWD, map,
                new JsonHttpResponseHandler()
                {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        ToastUtil.show("设置失败");
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                            JSONObject response)
                    {
                        try
                        {
                            if (CreateCode.AuthentInfo(response))
                            {
                                JSONObject data = StringUtils
                                        .parseContent(response);

                                if (data.optString("result").equals("success"))
                                {
                                    ToastUtil.show("设置成功");
                                    MyApplication.getInstance()
                                            .getRetrievePasswordActivity()
                                            .finish();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                                else
                                {
                                    ToastUtil.show(
                                            data.optString("description"));
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish()
                    {
                        if (progressDialogBar.isShowing())
                        {
                            progressDialogBar.dismiss();
                        }
                        super.onFinish();
                    }

                    @Override
                    public void onStart()
                    {
                        if (progressDialogBar == null)
                        {
                            progressDialogBar = ProgressDialogBar.createDialog(
                                    ResetLoginPasswordActivity.this);
                        }
                        progressDialogBar.setProgressMsg("设置中...");
                        progressDialogBar.show();
                        super.onStart();
                    }
                });
    }

}
