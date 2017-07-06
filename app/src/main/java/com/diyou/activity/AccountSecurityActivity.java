package com.diyou.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.diyou.base.BaseActivity;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.huifu.HuifuAuthenticationActivity;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.ProgressDialogBar;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TreeMap;

public class AccountSecurityActivity extends BaseActivity
        implements OnClickListener
{

    protected Dialog progressBar;

    private TextView activity_account_security_no_authentication_tv;
    private TextView activity_account_security_email_tv;
    private TextView activity_account_security_phone_number_tv;
    private TextView activity_account_security_phone_tv;
    private TextView activity_account_security_login_password_tv;
    private Button activity_account_security_huifu;

    protected String reg_trust;
    protected int email_status;
    protected int phone_status;
    protected int realname_status;

    protected String phone;

    private ImageView authentication_img;

    private ImageView email_img;

    private ImageView phone_img;

    private ImageView password_img;

    private TextView mAccount_security_level;

    private TextView activity_account_security_tip;

    /**
     * 账户安全
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);
        initView();
        initData();
    }

    private void initData()
    {
        getLevel();
        requestData();

    }

    private void requestData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("module", "dyp2p");
        map.put("q", "get_users");
        map.put("method", "get");
        map.put("user_id", UserConfig.getInstance()
                .getUserId(AccountSecurityActivity.this));
        HttpUtil.get(CreateCode.getUrl(map), new JsonHttpResponseHandler()
        {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    String responseString, Throwable throwable)
            {
                ToastUtil.show(responseString);
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONArray errorResponse)
            {
                ToastUtil.show(errorResponse.toString());
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                ToastUtil.show(errorResponse.toString());
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    email_status = response.getJSONObject("approve_result")
                            .getInt("email_status");
                    phone_status = response.getJSONObject("approve_result")
                            .getInt("phone_status");
                    realname_status = response.getJSONObject("approve_result")
                            .getInt("realname_status");
                    reg_trust = response.getJSONObject("approve_result")
                            .getJSONArray("reg_trust").toString();

                    activity_account_security_no_authentication_tv = (TextView) findViewById(
                            R.id.activity_account_security_no_authentication_tv);
                    activity_account_security_email_tv = (TextView) findViewById(
                            R.id.activity_account_security_email_tv);
                    activity_account_security_phone_number_tv = (TextView) findViewById(
                            R.id.activity_account_security_phone_number_tv);
                    activity_account_security_phone_tv = (TextView) findViewById(
                            R.id.activity_account_security_phone_tv);
                    // activity_account_security_login_password_tv

                    if (realname_status == 1)
                    {
                        activity_account_security_no_authentication_tv.setText(
                                R.string.activity_account_security_check);
                        authentication_img.setImageResource(R.drawable.dagou);
                    }
                    else
                    {
                        activity_account_security_no_authentication_tv.setText(
                                R.string.activity_account_security_no_authentication);
                        authentication_img
                                .setImageResource(R.drawable.gantanhao);

                    }

                    if (phone_status == 1)
                    {
                        activity_account_security_phone_tv.setText(
                                R.string.activity_account_security_has_binded);
                        phone = response.getJSONObject("approve_result")
                                .getString("phone");
                        activity_account_security_phone_number_tv
                                .setText(
                                        "手机(" + phone.substring(0, 3) + "****"
                                                + phone.substring(
                                                        phone.length() - 3,
                                                        phone.length())
                                                + ")");
                        phone_img.setImageResource(R.drawable.dagou);

                    }
                    else
                    {
                        activity_account_security_phone_tv.setText(
                                R.string.activity_account_security_no_authentication);
                        phone_img.setImageResource(R.drawable.gantanhao);

                    }
                    if (email_status == 1)
                    {
                        activity_account_security_email_tv.setText(
                                R.string.activity_account_security_has_binded);
                        email_img.setImageResource(R.drawable.dagou);

                    }
                    else
                    {
                        activity_account_security_email_tv.setText(
                                R.string.activity_account_security_no_authentication);
                        email_img.setImageResource(R.drawable.gantanhao);

                    }
                }
                catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFinish()
            {
                if (progressBar.isShowing())
                {
                    progressBar.dismiss();
                }
                super.onFinish();
            }

            @Override
            public void onStart()
            {
                if (progressBar == null)
                {
                    progressBar = ProgressDialogBar
                            .createDialog(AccountSecurityActivity.this);
                }
                if (!progressBar.isShowing())
                {
                    progressBar.show();
                }
                super.onStart();
            }
        });
    }

    private void initView()
    {

        mAccount_security_level = (TextView) findViewById(
                R.id.activity_account_security_level);
        activity_account_security_tip = (TextView) findViewById(
                R.id.activity_account_security_tip);
        findViewById(R.id.activity_account_security_title_layout)
                .findViewById(R.id.title_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.activity_account_security_title_layout)
                .findViewById(R.id.title_name))
                        .setText(R.string.title_activity_account_security);

        activity_account_security_no_authentication_tv = (TextView) findViewById(
                R.id.activity_account_security_no_authentication_tv);
        activity_account_security_email_tv = (TextView) findViewById(
                R.id.activity_account_security_email_tv);
        activity_account_security_phone_number_tv = (TextView) findViewById(
                R.id.activity_account_security_phone_number_tv);
        activity_account_security_phone_tv = (TextView) findViewById(
                R.id.activity_account_security_phone_tv);
        activity_account_security_login_password_tv = (TextView) findViewById(
                R.id.activity_account_security_login_password_tv);
        activity_account_security_login_password_tv.setText("已认证");
        activity_account_security_huifu = (Button) findViewById(
                R.id.activity_account_security_huifu);
        activity_account_security_huifu.setOnClickListener(this);

        findViewById(R.id.activity_account_security_no_authentication_layout)
                .setOnClickListener(this);
        findViewById(R.id.activity_account_security_email_layout)
                .setOnClickListener(this);
        findViewById(R.id.activity_account_security_phone_layout)
                .setOnClickListener(this);
        findViewById(R.id.activity_account_security_login_password_layout)
                .setOnClickListener(this);

        authentication_img = (ImageView) findViewById(
                R.id.activity_account_security_no_authentication_img);
        email_img = (ImageView) findViewById(
                R.id.activity_account_security_email_img);
        phone_img = (ImageView) findViewById(
                R.id.activity_account_security_phone_img);
        password_img = (ImageView) findViewById(
                R.id.activity_account_security_login_password_img);
        password_img.setImageResource(R.drawable.dagou);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.title_back:
            finish();
            break;
        // case R.id.activity_account_security_no_authentication_layout:
        // break;
        // case R.id.activity_account_security_email_layout:
        // if (email_status == 1)
        // {
        // startActivity(new Intent(AccountSecurityActivity.this,
        // UpdateEmailActivity.class));
        // }
        // else
        // {
        // startActivity(new Intent(AccountSecurityActivity.this,
        // EmailActivationActivity.class));
        // }
        // break;
        // case R.id.activity_account_security_phone_layout:
        // Intent intent = new Intent(AccountSecurityActivity.this,
        // UpdatePhoneNumberActivity.class);
        // intent.putExtra("phone", phone);
        // startActivity(intent);
        // break;
        // case R.id.activity_account_security_login_password_layout:
        // startActivity(new Intent(AccountSecurityActivity.this,
        // UpdateLoginPasswordActivity.class));
        //
        // break;
        case R.id.activity_account_security_huifu:
            if (reg_trust.trim().contains("success"))
            {
                ToastUtil.show("汇付注册已审核通过");
            }
            else
            {
                getPayUrl();
            }
            break;
        default:
            break;
        }
    }

    private void getPayUrl()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("module", "trust");
        map.put("q", "reg");
        map.put("method", "post");
        map.put("user_id", UserConfig.getInstance()
                .getUserId(AccountSecurityActivity.this));
        HttpUtil.get(CreateCode.getUrl(map), new JsonHttpResponseHandler()
        {
            @Override
            public void onStart()
            {

                super.onStart();
            }

            @Override
            public void onFinish()
            {

                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    String url = CreateCode.decrypt2HF(
                            response.getJSONObject("data").getString("url"));
                    Intent intent = new Intent(AccountSecurityActivity.this,
                            HuifuAuthenticationActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                ToastUtil.show("网络请求失败,请稍后在试！");

            }

        });
    }
    private void getLevel()
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("module", "dyp2p");
        map.put("q", "get_users");
        map.put("method", "get");
        map.put("user_id", UserConfig.getInstance().getUserId(this));
        HttpUtil.get(CreateCode.getUrl(map), new JsonHttpResponseHandler()
        {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    String responseString, Throwable throwable)
            {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONArray errorResponse)
            {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                // TODO Auto-generated method stub
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    JSONObject user_result = response
                            .getJSONObject("user_result");

                    JSONObject approve_result = response
                            .getJSONObject("approve_result");
                    switch (approve_result.getInt("safe_rank"))
                    {
                    case 0:
                        mAccount_security_level.setText("低");// 安全等级
                        break;
                    case 1:
                        mAccount_security_level.setText("中");// 安全等级
                        break;
                    case 2:
                        mAccount_security_level.setText("高");// 安全等级
                        activity_account_security_tip.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                    }

                }
                catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                finally
                {
                }
            }

            @Override
            public void onFinish()
            {
                super.onFinish();
            }

            @Override
            public void onStart()
            {
                super.onStart();
            }

        });

    }
}
