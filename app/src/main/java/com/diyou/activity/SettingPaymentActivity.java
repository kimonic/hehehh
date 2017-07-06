package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.ProgressDialogBar;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingPaymentActivity extends BaseActivity
        implements OnClickListener
{
    private View title_bar;
    private RelativeLayout title_bar_back;
    private TextView title_name;
    private EditText payment_password;
    private EditText payment_password_again;
    private EditText login_password;
    private ImageView payment_password_imageview;
    private ImageView payment_password_again_imageview;
    private boolean showPayment = true;
    private boolean showAgainPayment = true;
    protected ProgressDialogBar progressDialogBar;
    private String mAction;

    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_payment_layout);
        intiView();
        mAction = getIntent().getStringExtra("action");
    }
    private boolean checkData(){
        String payPwd = payment_password.getText().toString().trim();
        if (!StringUtils.checkLoginPassword(payPwd))
        {
            return false;
        }else if (!payPwd
                .equals(payment_password_again.getText().toString().trim()))
        {
            ToastUtil.show(R.string.set_paypwd_twice_disagree);
            return false;
        }else if (StringUtils.isEmpty(login_password.getText().toString().trim()))
        {
            ToastUtil.show(R.string.remind_input_login_pwd);
            return false;
        }else if(payPwd
                .equals(login_password.getText().toString().trim())){
            ToastUtil.show(R.string.set_paypwd_paypwd_loginpwd_like);
            return false;
        }
        return true;
    }
    private void submitData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance()
                .getLoginToken(SettingPaymentActivity.this));
        map.put("password", login_password.getText().toString().trim());
        map.put("new_paypassword", payment_password.getText().toString().trim());
        map.put("confirm_paypassword",
                payment_password_again.getText().toString().trim());
        // RequestParams params = new RequestParams();
        // params.put("diyou", CreateCode.p2sDiyou(CreateCode.GetJson(map)));
        // params.put("xmdy", CreateCode.p2sXmdy(CreateCode.p2sDiyou(CreateCode
        // .GetJson(map))));
        HttpUtil.post(UrlConstants.EDITPAYPWD, map,
                new JsonHttpResponseHandler()
                {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        ToastUtil.show("密码设置失败");
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

                                    if (data.optString("result")
                                            .equals("success"))
                                    {

                                        ToastUtil.show("支付密码设置成功");
                                        if (WithdrawActivity.ACTION_WITHDRAW
                                                .equals(mAction))
                                        {
                                            startActivity(new Intent(
                                                    SettingPaymentActivity.this,
                                                    WithdrawActivity.class));
                                        }
                                        finish();
                                    }

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
                        hideProgressDialog();
                        super.onFinish();
                    }

                    @Override
                    public void onStart()
                    {
                        showProgressDialog();
                        super.onStart();
                    }

                });

    }

    private void intiView()
    {
        title_bar = findViewById(R.id.bid_title_bar);
        title_bar_back = (RelativeLayout) title_bar
                .findViewById(R.id.title_bar_back);
        title_name = (TextView) title_bar.findViewById(R.id.title_name);
        title_name.setText("设置支付密码");
        payment_password = (EditText) findViewById(R.id.payment_password);
        payment_password_again = (EditText) findViewById(
                R.id.payment_password_again);
        login_password = (EditText) findViewById(R.id.login_password);
        findViewById(R.id.show_payment_password_RelativeLayout)
                .setOnClickListener(this);
        findViewById(R.id.show_payment_password_again_RelativeLayout)
                .setOnClickListener(this);
        payment_password_imageview = (ImageView) findViewById(
                R.id.show_payment_password);
        payment_password_again_imageview = (ImageView) findViewById(
                R.id.show_payment_password_again);
        findViewById(R.id.confirm_button).setOnClickListener(this);
        title_bar_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.confirm_button:
            if(checkData()){
                submitData();
            }
            break;

        case R.id.title_bar_back:
            finish();
            break;
        case R.id.show_payment_password_RelativeLayout:
            if (showPayment)
            {
                showPayment = false;
                payment_password.setInputType(InputType.TYPE_CLASS_TEXT);
                payment_password_imageview
                        .setBackgroundResource(R.drawable.show_password_press);
            }
            else
            {
                showPayment = true;
                payment_password.setInputType(0x81);
                payment_password_imageview
                        .setBackgroundResource(R.drawable.show_passwprd);
            }
            break;
        case R.id.show_payment_password_again_RelativeLayout:
            if (showAgainPayment)
            {
                showAgainPayment = false;
                payment_password_again.setInputType(InputType.TYPE_CLASS_TEXT);
                payment_password_again_imageview
                        .setBackgroundResource(R.drawable.show_password_press);

            }
            else
            {
                showAgainPayment = true;
                payment_password_again.setInputType(0x81);
                payment_password_again_imageview
                        .setBackgroundResource(R.drawable.show_passwprd);
            }

            break;
        }

    }

}
