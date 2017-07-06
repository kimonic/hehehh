package com.diyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.diyou.base.BaseActivity;
import com.diyou.bean.EventObject;
import com.diyou.config.Constants;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.SharedPreUtils;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.util.Utils;
import com.diyou.v5yibao.R;
import com.diyou.view.CountDownButton;
import com.diyou.view.ProgressDialogBar;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TreeMap;

import de.greenrobot.event.EventBus;

/**
 * 手机验证码
 *
 * @author Administrator
 */
public class PhoneCodeActivity extends BaseActivity implements OnClickListener {
    private String phone;
    private EditText codeEditText;
    private Button mVerificationCode_btn;
    protected ProgressDialogBar progressDialogBar;
    private View mRlMsg;
    private int type;
    private Button submit;
    private String randomNummber;
    private String authAction;
    private TextView tvServiceTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone_code);
        Intent intent = getIntent();
        type = intent.getIntExtra("type", -1);
        phone = intent.getStringExtra("phone");
        authAction = intent.getStringExtra("authAction");
        initActionBar();
        initView();

    }

    private void initActionBar() {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        if (type == 1) {
            tvTitle.setText(R.string.forgot_password_phone_title);
        } else {
            tvTitle.setText(R.string.bind_phone_title);
        }
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    private void initView() {
        TextView tvPhoneFlag = (TextView) findViewById(R.id.tv_phone_code_flag);
        TextView tvPhoneNum = (TextView) findViewById(R.id.tv_phone_bind_display);
        TextView tvPhoneHide = (TextView) findViewById(
                R.id.tv_phone_code_msg_account);
        if (type == 1) {
            tvPhoneFlag.setText("请输入手机短信验证码");
        } else {
            tvPhoneNum.setText(StringUtils.getHidePhone(phone));
        }

        tvPhoneHide.setText(StringUtils.getHidePhone(phone));
        mVerificationCode_btn = (Button) findViewById(R.id.btn_send_phone_code);
        mVerificationCode_btn.setOnClickListener(this);
        codeEditText = (EditText) findViewById(R.id.et_phone_code);
        submit = (Button) findViewById(R.id.btn_phone_bind_number_submit);
        submit.setOnClickListener(this);
        mRlMsg = findViewById(R.id.rl_phone_code_msg);
        if (type == 1) {
            submit.setText("下一步");
        }
        //设置客服电话
        tvServiceTel = (TextView) findViewById(R.id.tv_phone_code_service_tel);
        tvServiceTel.setText(SharedPreUtils.getString(Constants.KEY_SERVICE_TEL, "", this));
        tvServiceTel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.btn_send_phone_code:
                mRlMsg.setVisibility(View.GONE);
                volleyGetVerificationCode();
                break;
            case R.id.btn_phone_bind_number_submit:
                String code = StringUtils.getString(codeEditText);
                if (checkCode(code)) {
                    if (type == 1) {
                        findBackPassword(code);
                    } else {
                        approvePhone(code);
                    }
                }
                break;
            case R.id.tv_phone_code_service_tel:
                Utils.openDial(tvServiceTel.getText().toString(), this);
                break;
            default:
                break;
        }
    }

    private void findBackPassword(String code) {
        if (!StringUtils.isEmpty2(randomNummber)) {

            if (randomNummber.equals(codeEditText.getText().toString())) {
                //跳转到密码修改
                Intent intent = new Intent(PhoneCodeActivity.this,
                        ResetLoginPasswordActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("code", code);
                intent.putExtra("username", phone);
                startActivityForResult(intent, Constants.REQUEST_CODE_NEW_PASSWORD);
            } else {
                ToastUtil.show("验证码不正确");
            }
        } else {
            ToastUtil.show("请先发送验证码");
        }
    }

    private boolean checkCode(String code) {
        if (StringUtils.isEmpty2(code)) {
            ToastUtil.show("请输入验证码");
            return false;
        } else if (code.length() < 6) {
            ToastUtil.show("验证码为6位数");
            return false;
        }
        return true;
    }

    private void approvePhone(String code) {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("phone", phone);
        map.put("type", "approve");
        map.put("phone_code", code);
        HttpUtil.post(UrlConstants.APPROVE_PHONE, map,
                new JsonHttpResponseHandler() {
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
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        ToastUtil.show(R.string.generic_error);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        try {
                            if (CreateCode.AuthentInfo(response)) {
                                JSONObject json = StringUtils
                                        .parseContent(response);
                                if ("success".equals(json.optString("result"))) {
                                    ToastUtil.show("手机绑定成功");
                                    EventBus.getDefault()
                                            .post(new EventObject("refresh"));
                                    // if (StringUtils.isEmpty2(payPwd))
                                    // {
                                    // startActivity(new Intent(
                                    // PhoneCodeActivity.this,
                                    // SettingPaymentActivity.class));
                                    // }
                                    if (Constants.ACTION_PROCESS_YBREG.equals(authAction)) {
                                        getApprove();
                                    } else {
                                        setResult(RESULT_OK);
                                        finish();
                                    }

                                } else {
                                    ToastUtil.show(
                                            json.optString("description"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }
                });
    }

    private void getApprove() {

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
                            if (StringUtils.isEmpty(info.optString("email"))) {
                                // 未认证邮箱
                                ToastUtil.show("请验证邮箱");
                                intent.setClass(PhoneCodeActivity.this,
                                        EmailBindActivity.class);
                                startActivity(intent);

                            } else if (StringUtils
                                    .isEmpty(info.optString("realname_status"))
                                    || info.optString("realname_status")
                                    .equals("-1")) {
                                // 未认证实名或认证失败
                                ToastUtil.show("请进行实名认证");
                                intent.setClass(PhoneCodeActivity.this,
                                        RealNameAuthActivity.class);
                                startActivity(intent);
                            }
                            /*else if (info.optString("realname_status")
                                    .equals("-2"))
                            {
                                // 待审核
                                ToastUtil.show("实名认证待审核中");
                                intent.setClass(PhoneCodeActivity.this,
                                        RealNameInfoActivity.class);
                                startActivity(intent);
                            }*/

                            else if (!info.optString("register").equals("1")) {
                                startActivity(new Intent(PhoneCodeActivity.this,
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
                setResult(RESULT_OK);
                finish();
                super.onFinish();
            }

            @Override
            public void onStart() {
                showProgressDialog();
                super.onStart();
            }

        });

    }

    private void volleyGetVerificationCode() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("phone", phone);
        if (type == 1) {
            map.put("type", "pwd");
        } else {
            map.put("is_check", "1");// 验证手机是否存在/或者已被注册
            map.put("type", "approve");
        }
        randomNummber = StringUtils.getRandom();
        map.put("phone_code", randomNummber);
        HttpUtil.post(UrlConstants.REG_SMS, map, new JsonHttpResponseHandler() {
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
                        JSONObject json = StringUtils.parseContent(response);
                        if ("success".equals(json.getString("result"))) {
                            CountDownButton downButton = new CountDownButton();
                            downButton.init(PhoneCodeActivity.this,
                                    mVerificationCode_btn);
                            downButton.start();
                            mRlMsg.setVisibility(View.VISIBLE);
                            ToastUtil.show("发送成功");
                        } else {
                            ToastUtil.show(json.getString("description"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        // TODO Auto-generated method stub
        super.onActivityResult(arg0, arg1, arg2);
        if (arg0 == Constants.REQUEST_CODE_NEW_PASSWORD && arg1 == RESULT_OK) {
            finish();
        }
    }
}
