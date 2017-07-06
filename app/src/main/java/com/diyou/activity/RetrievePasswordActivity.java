package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONException;
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

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author 密码找回
 */
public class RetrievePasswordActivity extends BaseActivity
        implements OnClickListener {
    protected Dialog progressDialogBar;
    private TextView loginactivity_password_login;
    private TextView loginactivity_code_login;
    private TextView layout_waring;
    private EditText mUserPWD;
    // 类型 1手机找回 2邮箱找回
    private int type = 1;
    private String mAccountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().setRetrievePasswordActivity(this);
        mAccountName = getIntent().getStringExtra("accountName");
        setContentView(R.layout.activity_retrieve_password);
        initView();
    }

    private void initView() {
        loginactivity_code_login = (TextView) findViewById(
                R.id.loginactivity_code_login);
        loginactivity_code_login.setOnClickListener(this);
        loginactivity_password_login = (TextView) findViewById(
                R.id.loginactivity_password_login);
        loginactivity_password_login.setOnClickListener(this);
        findViewById(R.id.loginactivity_back).setOnClickListener(this);
        loginactivity_code_login
                .setTextColor(getResources().getColor(R.color.blue));
        mUserPWD = (EditText) findViewById(R.id.loginactivity_pwd);
        layout_waring = (TextView) findViewById(R.id.layout_waring);
        findViewById(R.id.loginactivity_btn_sumbit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginactivity_back:
                finish();
                break;
            case R.id.loginactivity_password_login:
                type = 2;
                mUserPWD.setHint("请输入邮箱号");
                mUserPWD.setText(null);
                layout_waring.setText("请输入注册时设置的邮箱");
                loginactivity_password_login
                        .setTextColor(getResources().getColor(R.color.blue));
                loginactivity_code_login
                        .setTextColor(getResources().getColor(R.color.grayish));
                break;

            case R.id.loginactivity_code_login:
                type = 1;
                mUserPWD.setHint("请输入手机号");
                mUserPWD.setText(null);
                layout_waring.setText("请输入注册时设置的手机号");
                loginactivity_password_login
                        .setTextColor(getResources().getColor(R.color.grayish));
                loginactivity_code_login
                        .setTextColor(getResources().getColor(R.color.blue));
                break;
            case R.id.loginactivity_btn_sumbit:
                if (StringUtils.isEmpty2(mUserPWD.getText().toString())) {
                    if (type == 1) {
                        ToastUtil.show("手机号不能为空");
                        break;
                    }
                    if (type == 2) {
                        ToastUtil.show("邮箱不能为空");
                        break;
                    }
                }
                if (!StringUtils.isEmpty2(mUserPWD.getText().toString())) {
                    if (type == 1) {
                        if (!StringUtils
                                .checkPhoneNum(mUserPWD.getText().toString())) {
                            ToastUtil.show("手机格式不正确");
                            break;
                        }
                    }
                    if (type == 2) {
                        if (!StringUtils.checkEmail(mUserPWD.getText().toString())) {
                            ToastUtil.show("邮箱格式不正确");
                            break;
                        }
                    }
                }
                volleyCheck();
                break;
            default:
                break;
        }
    }

    private void volleyCheck() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("username", mUserPWD.getText().toString());
        map.put("name", mAccountName);//密码找回需验证是否绑定了该账户
        HttpUtil.post(UrlConstants.CHECK, map, new JsonHttpResponseHandler() {
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

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                try {
                    if (CreateCode.AuthentInfo(response)) {
                        JSONObject json = StringUtils.parseContent(response);
                        if (json.optString("result").contains("success")) {
                            JSONObject data = json.getJSONObject("data");
                            if ("1".equals(data.optString("status"))) {
                                Intent intent = new Intent();
                                switch (type) {
                                    case 1:
                                        intent.putExtra("type", type);
                                        intent.putExtra("phone",
                                                mUserPWD.getText().toString());
                                        intent.setClass(RetrievePasswordActivity.this, PhoneCodeActivity.class);
                                        startActivity(intent);
                                        break;
                                    case 2:
                                        intent.putExtra("type", type);
                                        intent.putExtra("email",
                                                mUserPWD.getText().toString());
                                        intent.setClass(RetrievePasswordActivity.this, EmailCodeActivity.class);
                                        startActivity(intent);
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                switch (type) {
                                    case 1:
                                        ToastUtil.show(R.string.remind_phone_no_exist);
                                        break;
                                    case 2:
                                        ToastUtil.show(R.string.remind_email_no_exist);
                                        break;
                                    default:
                                        break;
                                }

                            }
                        } else {
                            ToastUtil.show(json.optString("description"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                ToastUtil.show("网络请求失败,请稍后在试！");
            }

        });
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        // TODO Auto-generated method stub
        switch (arg1) {
            case RESULT_OK:
                finish();
                break;

            default:
                break;
        }
        super.onActivityResult(arg0, arg1, arg2);
    }

}
