package com.diyou.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.diyou.application.MyApplication;
import com.diyou.base.BaseActivity;
import com.diyou.config.Constants;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.CustomDialog;
import com.diyou.util.SharedPreUtils;
import com.diyou.util.StringUtils;
import com.diyou.util.TextWatcherUtil;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.CountDownButton;
import com.diyou.view.ProgressDialogBar;
import com.example.encryptionpackages.AESencrypt;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TreeMap;

/**
 * 登录页面
 * 
 */
public class LoginActivity extends BaseActivity implements OnClickListener
{

    private EditText mUserPWD;
    private String mUesrname;
    private TextView phone1;
    private TextView phone2;
    private TextView forgetpassword;
    private TextView loginactivity_password_login;
    private TextView loginactivity_code_login;
    private Button mVerificationCode_btn;
    protected ProgressDialogBar progressDialogBar;
    private View loginPrompt;
    /**
     * 登录类型 1 验证码登录； 2 密码登录；
     */
    private int type = 2;

    /**
     * 账号模式 1 邮箱； 2 手机； 3 用户名；
     */
    private int model = 0;
    private View accountfragment_bottom;
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MyApplication.getInstance().setLoginActivity(this);
        // 拿到用户名
        Intent intent = getIntent();
        mUesrname = intent.getStringExtra("mUesrname");
        model = intent.getIntExtra("type", -1);
        initView();
        listener();
    }

    private void initView()
    {
        accountfragment_bottom = findViewById(R.id.accountfragment_bottom);
        mBtnLogin = (Button) findViewById(R.id.loginactivity_btn_sumbit);
        mBtnLogin.setOnClickListener(this);
        phone1 = (TextView) findViewById(R.id.loginactivity_telephone1);
        phone2 = (TextView) findViewById(R.id.loginactivity_telephone2);

        mUserPWD = (EditText) findViewById(R.id.loginactivity_pwd);
        mUserPWD.addTextChangedListener(TextWatcherUtil.getEmpty(mBtnLogin));
        forgetpassword = (TextView) findViewById(
                R.id.loginactivity_forget_password);
        // 下划线
        forgetpassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        mVerificationCode_btn = (Button) findViewById(
                R.id.updatamobilephone_code_btn);
        loginPrompt = findViewById(R.id.loginactivity_prompt);
        loginactivity_code_login = (TextView) findViewById(
                R.id.loginactivity_code_login);
        loginactivity_password_login = (TextView) findViewById(
                R.id.loginactivity_password_login);
        loginactivity_password_login
                .setTextColor(getResources().getColor(R.color.blue));
        switch (model)
        {
        case 1:
            accountfragment_bottom.setVisibility(View.GONE);
            phone1.setText(StringUtils.getHideEmail(mUesrname));
            break;
        case 2:
            accountfragment_bottom.setVisibility(View.VISIBLE);
            // 设置手机号
            phone1.setText(StringUtils.getHidePhone(mUesrname));
            phone2.setText(StringUtils.getHidePhone(mUesrname));
            break;
        case 3:
            accountfragment_bottom.setVisibility(View.GONE);
            phone1.setText(StringUtils.getHideUser(mUesrname));
            break;

        default:
            break;
        }

    }

    private void listener()
    {
        forgetpassword.setOnClickListener(this);
        mVerificationCode_btn.setOnClickListener(this);
        loginactivity_code_login.setOnClickListener(this);
        loginactivity_password_login.setOnClickListener(this);
        findViewById(R.id.loginactivity_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.loginactivity_forget_password:
            //新增密码找回
            Intent intent = new Intent(LoginActivity.this,
                    RetrievePasswordActivity.class);
            intent.putExtra("accountName", mUesrname);
            startActivity(intent);
            break;
        case R.id.loginactivity_password_login:
            type = 2;
            mVerificationCode_btn.setVisibility(View.GONE);
            loginPrompt.setVisibility(View.GONE);
            forgetpassword.setVisibility(View.VISIBLE);
            mUserPWD.setHint(R.string.loginactivity_input_pwd);
            mUserPWD.setText("");
            mUserPWD.setTransformationMethod(
                    PasswordTransformationMethod.getInstance());
            // 限制长度 
            mUserPWD.setFilters(new InputFilter[]
            { new InputFilter.LengthFilter(Constants.PAY_PASSWORD_MAX_LENGTH) });
            loginactivity_password_login
                    .setTextColor(getResources().getColor(R.color.blue));
            loginactivity_code_login
                    .setTextColor(getResources().getColor(R.color.grayish));
            break;

        case R.id.loginactivity_code_login:
            type = 1;
            mVerificationCode_btn.setVisibility(View.VISIBLE);
            mUserPWD.setText("");
            forgetpassword.setVisibility(View.GONE);
            mUserPWD.setHint(R.string.loginactivity_input_code);
            // 限制长度 6
            mUserPWD.setFilters(new InputFilter[]
            { new InputFilter.LengthFilter(6) });
            mUserPWD.setTransformationMethod(null);
            loginactivity_password_login
                    .setTextColor(getResources().getColor(R.color.grayish));
            loginactivity_code_login
                    .setTextColor(getResources().getColor(R.color.blue));
            break;
        case R.id.updatamobilephone_code_btn:
            // TODO 验证码操作判断
            volleyGetVerificationCode();
            break;
        case R.id.loginactivity_back:
            finish();
            break;
        case R.id.loginactivity_btn_sumbit:
                switch (type)
                {
                case 1:
                    if (StringUtils.isEmpty2(mUserPWD.getText().toString()))
                    {
                        ToastUtil.show(R.string.loginactivity_toast_show1);
                    }else if(StringUtils.isEmpty(mVerifyCode)){
                        ToastUtil.show(R.string.remind_send_auth_code_first);
                    }else if(!mVerifyCode.equals(StringUtils.getString(mUserPWD))){
                        ToastUtil.show(R.string.remind_input_correct_auth_code);
                    }else{
                        volleyLogin();
                    }
                    break;
                case 2:
                    if (StringUtils.isEmpty2(mUserPWD.getText().toString()))
                    {
                        ToastUtil.show(R.string.loginactivity_toast_show2);
                    }else{
                        volleyLogin();
                    }
                    break;

                default:
                    break;
                }
                
            break;
        default:
            break;
        }
    }

    private void volleyLogin()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("member_name", mUesrname);
        map.put("password", mUserPWD.getText().toString().trim());
        map.put("type", type + "");//登录类型 1验证码登录 2密码登录
        map.put("phone_type", "1");//手机类型1=andriod 2=IOS
        HttpUtil.post(UrlConstants.LOGIN, map, new JsonHttpResponseHandler()
        {
            @Override
            public void onFinish()
            {
                hideProgressDialog();
                super.onFinish();
            }

            @Override
            public void onStart()
            {
                showProgressDialog(getString(R.string.remind_dialog_login));
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
                            setResult(Constants.LOGINSUCCESS);
                            MyApplication.isLogin = true;
                            if (!(MyApplication.app.getThirdFragment() == null))
                            {
                                MyApplication.app.getThirdFragment().loginIn();
                            }
                            // TODO 存Token
                            SharedPreUtils.putString(Constants.SHARE_LOGINTOKEN,
                                    AESencrypt.encrypt2PHP(
                                            CreateCode.getSEND_AES_KEY(),
                                            data.optString("login_token")
                                                    .toString()),
                                    MyApplication.app);
                            // TODO 设置手势密码对话框
                            CustomDialog dialog = new CustomDialog(
                                    LoginActivity.this, getString(R.string.dialog_title_set_gesture), 
                                    getString(R.string.dialog_subtitle_set_gesture),
                                    Constants.LOGINSUCCESS, "");
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
                ToastUtil.show(R.string.generic_error);
            }
        });
    }

    private String mVerifyCode="";
    private void volleyGetVerificationCode()
    {
        final String code=StringUtils.getRandom();
        mVerifyCode=code;
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("phone", mUesrname);
        map.put("type", "login");
        map.put("phone_code", code);
        HttpUtil.post(UrlConstants.REG_SMS, map, new JsonHttpResponseHandler()
        {
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

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    String responseString, Throwable throwable)
            {
                super.onFailure(statusCode, headers, responseString, throwable);
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
//                            mVerifyCode=code;
                            CountDownButton downButton = new CountDownButton();
                            downButton.init(LoginActivity.this,
                                    mVerificationCode_btn);
                            downButton.start();
                            ToastUtil
                                    .show(R.string.loginactivity_toast_success);
                            loginPrompt.setVisibility(View.VISIBLE);
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
}
