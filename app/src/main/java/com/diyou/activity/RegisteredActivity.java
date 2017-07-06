package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.application.MyApplication;
import com.diyou.base.BaseActivity;
import com.diyou.config.Constants;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.CustomDialog;
import com.diyou.util.SharedPreUtils;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.CountDownButton;
import com.diyou.view.ProgressDialogBar;
import com.example.encryptionpackages.AESencrypt;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * @author 注册
 * 
 */
public class RegisteredActivity extends BaseActivity implements OnClickListener
{

    private EditText mVerificationCode;
    private EditText mPassword;
    private EditText referrer;
    private TextView phone1;
    private TextView phone2;
    private String mUserName;
    private Button mVerificationCode_btn;
    protected Dialog progressDialogBar;
    private View prompt;
    private Boolean isChecked = false;
    private ImageView password_visual;
    private String mVerifyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().setRegisteredActivity(this);
        setContentView(R.layout.activity_registered);
        Intent intent = getIntent();
        mUserName = intent.getStringExtra("mUesrname");
        initView();
    }

    private void initView()
    {
        phone1 = (TextView) findViewById(R.id.registeredactivity_telephone1);
        phone1.setText(mUserName.subSequence(0, 3) + "****"
                + mUserName.subSequence(7, 11));
        phone2 = (TextView) findViewById(R.id.registeredactivity_telephone2);
        phone2.setText(mUserName.subSequence(0, 3) + "****"
                + mUserName.subSequence(7, 11));
        mVerificationCode_btn = (Button) findViewById(
                R.id.updatamobilephone_code_btn);
        mVerificationCode_btn.setOnClickListener(this);
        findViewById(R.id.loginactivity_cls).setOnClickListener(this);
        referrer = (EditText) findViewById(R.id.beforeloginactivity_uesrname);
        mVerificationCode = (EditText) findViewById(
                R.id.registeredactivity_code);
        mPassword = (EditText) findViewById(R.id.registeredactivity_password);
        findViewById(R.id.registeredactivity_btn_next).setOnClickListener(this);
        findViewById(R.id.registeredactivity_back).setOnClickListener(this);
        prompt = findViewById(R.id.registeredactivity_prompt);
        password_visual = (ImageView) findViewById(R.id.password_visual);
        password_visual.setOnClickListener(this);

        //网站服务协议
        findViewById(R.id.tv_register_net_agreement).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.registeredactivity_back:
            finish();
            break;
        case R.id.loginactivity_cls:
            referrer.setText(null);
            break;
        case R.id.password_visual:
            if (!isChecked)
            {
                password_visual.setImageResource(
                        R.drawable.icon_password_visual_cancel);
                mPassword.setTransformationMethod(
                        PasswordTransformationMethod.getInstance());
            }
            else
            {
                password_visual
                        .setImageResource(R.drawable.icon_password_visual_ok);
                mPassword.setTransformationMethod(null);
            }
            mPassword.setSelection(mPassword.getText().toString().length());
            isChecked = !isChecked;
            break;
        case R.id.updatamobilephone_code_btn:
            volleyGetVerificationCode();
            break;
        case R.id.registeredactivity_btn_next:
            if(StringUtils.isEmpty(mVerifyCode)){
                ToastUtil.show(R.string.remind_send_auth_code_first);
            }else if (StringUtils.isEmpty(mVerificationCode.getText().toString()))
            {
                ToastUtil.show(R.string.remind_input_auth_code);
            }else if(!mVerifyCode.equals(StringUtils.getString(mVerificationCode))){
                ToastUtil.show(R.string.remind_input_correct_auth_code);
            }else if(StringUtils.checkLoginPassword(mPassword.getText().toString())){
                volleyRegistered();
            }
            break;
            case R.id.tv_register_net_agreement:
                Intent intent = new Intent(this, AdvertisingActivity.class);
                intent.putExtra("action", "websiteAgreement");
                intent.putExtra("url",UrlConstants.AGREEMENT_WEBSITE);
                startActivity(intent);
                break;
        default:
            break;
        }
    }

    private void volleyRegistered()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("phone", mUserName);
        map.put("phone_code", mVerificationCode.getText().toString());
        map.put("password", mPassword.getText().toString());
        map.put("type", "REG");
        map.put("referrer", referrer.getText().toString());
        HttpUtil.post(UrlConstants.REG, map, new JsonHttpResponseHandler()
        {
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
                    progressDialogBar = ProgressDialogBar
                            .createDialog(RegisteredActivity.this);
                }
                progressDialogBar.show();
                super.onStart();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                ToastUtil.show("网络请求失败,请稍后在试！");
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    if (CreateCode.AuthentInfo(response))
                    {
                        JSONObject json = StringUtils.parseContent(response);
                        if ("success".equals(json.optString("result")))
                        {
                            ToastUtil.show("注册成功");
                            // setResult(Constants.REGISTERSUCCESS);
                            volleyLogin();
                        }
                        else
                        {
                            ToastUtil.show(json.optString("description"));
                        }
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
    }

    private void volleyLogin()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("member_name", mUserName);
        map.put("password", mPassword.getText().toString());
        map.put("type", 2 + "");
        HttpUtil.post(UrlConstants.LOGIN, map, new JsonHttpResponseHandler()
        {
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
                    progressDialogBar = ProgressDialogBar
                            .createDialog(RegisteredActivity.this);
                }
                progressDialogBar.show();
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
                        JSONObject json = StringUtils.parseContent(response);
                        // JSON还没调试，有点小问题
                        if (json.optString("result").contains("success"))
                        {

                            JSONObject data = json.getJSONObject("data");
                            UserConfig.getInstance().setLoginToken(
                                    data.optString("login_token"));
                            MyApplication.isLogin = true;
                            // 设置home页面登录按钮小时处理
                            // MyApplication.app.getFirstFragment()
                            // .setLogindimss();
                            MyApplication.app.getThirdFragment().loginIn();
                            MyApplication.getInstance().getBeforeLoginActivity()
                                    .finish();
                            //  存Token
                            SharedPreUtils.putString(Constants.SHARE_LOGINTOKEN,
                                    AESencrypt.encrypt2PHP(
                                            CreateCode.getSEND_AES_KEY(),
                                            data.optString("login_token")
                                                    .toString()),
                                    MyApplication.app);

                            CustomDialog dialog = new CustomDialog(
                                    RegisteredActivity.this, "登录成功！",
                                    "马上设置手势密码?", Constants.LOGINSUCCESS, "");
                            dialog.show();
                        }
                        else
                        {
                            ToastUtil.show(json.optString("description"));
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
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                ToastUtil.show("网络请求失败,请稍后在试！");
            }

        });

    }

    private void volleyGetVerificationCode()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", "");
        map.put("phone", mUserName);
        map.put("type", "reg");
        map.put("is_check", "1");// 验证手机是否存在/或者已被注册
        final String random = StringUtils.getRandom().toString();
        mVerifyCode=random;
        map.put("phone_code", random);
        HttpUtil.post(UrlConstants.REG_SMS, map, new JsonHttpResponseHandler()
        {
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
                    progressDialogBar = ProgressDialogBar
                            .createDialog(RegisteredActivity.this);
                }
                progressDialogBar.show();
                super.onStart();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                ToastUtil.show(R.string.generic_error);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    if (CreateCode.AuthentInfo(response))
                    {
                        JSONObject json = StringUtils.parseContent(response);
                        if ("success".equals(json.optString("result")))
                        {
//                            mVerifyCode=random;
                            CountDownButton downButton = new CountDownButton();
                            downButton.init(RegisteredActivity.this,
                                    mVerificationCode_btn);
                            downButton.start();
                            ToastUtil.show("发送成功");
                            prompt.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            ToastUtil.show(json.optString("description"));
                        }
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
    }

    class MyTextWatcher implements TextWatcher
    {
        private RadioButton imageView;

        public MyTextWatcher(RadioButton imageView)
        {
            this.imageView = imageView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {
            imageView.setChecked(s.length() > 0 ? true : false);

        }

        @Override
        public void afterTextChanged(Editable s)
        {

        }

    }

}
