package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.example.encryptionpackages.CreateCode;
import com.lidroid.xutils.util.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.greenrobot.event.EventBus;

/**
 * 邮箱验证码
 *
 * @author Administrator
 */
public class EmailCodeActivity extends BaseActivity implements OnClickListener {

    private String mEmail;
    private Button mBtnCode;
    private View mMsgLayout;
    private EditText mEtCode;
    private int type;
    private String emailCode = "";
    private String mVerifyCode = "";
    private Button btn_phone_bind_number_submit;
    private String authAction;
    private TextView tvServiceTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone_code);
        mEmail = getIntent().getStringExtra("email");
        type = getIntent().getIntExtra("type", -1);
        authAction = getIntent().getStringExtra("authAction");
        initActionBar();
        initView();
    }

    private void initView() {
        TextView tvFlag = (TextView) findViewById(R.id.tv_phone_code_flag);
        TextView tvMsgType = (TextView) findViewById(
                R.id.tv_phone_code_msg_type);
        TextView tvMsgAccount = (TextView) findViewById(
                R.id.tv_phone_code_msg_account);
        TextView mTvDisplay = (TextView) findViewById(
                R.id.tv_phone_bind_display);
        if (type == 2) {
            tvFlag.setText("请输入邮件验证码");
        } else {
            tvFlag.setText("邮箱");
            if (!StringUtils.isEmpty(mEmail)) {
                mTvDisplay.setText(StringUtils.getHideEmail(mEmail));
                tvMsgAccount.setText(StringUtils.getHideEmail(mEmail));
            }
        }
        tvMsgType.setText("系统已向邮箱");
        mEtCode = (EditText) findViewById(R.id.et_phone_code);

        mMsgLayout = findViewById(R.id.rl_phone_code_msg);
        mBtnCode = (Button) findViewById(R.id.btn_send_phone_code);
        mBtnCode.setOnClickListener(this);
        btn_phone_bind_number_submit = (Button) findViewById(
                R.id.btn_phone_bind_number_submit);
        btn_phone_bind_number_submit.setOnClickListener(this);
        if (type == 2) {
            btn_phone_bind_number_submit.setText("下一步");
        }
//设置客服电话
        tvServiceTel = (TextView) findViewById(R.id.tv_phone_code_service_tel);
        tvServiceTel.setText(SharedPreUtils.getString(Constants.KEY_SERVICE_TEL, "", this));
        tvServiceTel.setOnClickListener(this);
    }

    private void initActionBar() {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        if (type == 2) {
            tvTitle.setText(R.string.forgot_password_email_title);
        } else {
            tvTitle.setText(R.string.email_bind_title);
        }
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.btn_send_phone_code:
                if (type == 2) {
                    sendRecoverEmail();
                } else {
                    mMsgLayout.setVisibility(View.GONE);
                    sendCode();
                }
                break;
            case R.id.btn_phone_bind_number_submit:
                if (type == 2) {
                    if (checkCode(mEtCode.getText().toString())) {
                        if (emailCode.equals(mEtCode.getText().toString())) {
                            // 跳转到密码修改
                            Intent intent = new Intent(EmailCodeActivity.this,
                                    ResetLoginPasswordActivity.class);
                            intent.putExtra("type", type);
                            intent.putExtra("code", emailCode);
                            intent.putExtra("username", mEmail);
                            startActivityForResult(intent, Constants.REQUEST_CODE_NEW_PASSWORD);
                        } else {
                            ToastUtil.show("请输入正确的验证码");
                        }
                    }
                } else {
                    submitData();
                }
                break;
            case R.id.tv_phone_code_service_tel:
                Utils.openDial(tvServiceTel.getText().toString(), this);
                break;
            default:
                break;
        }
    }

    private boolean checkCode(String code) {
        if (StringUtils.isEmpty(code)) {
            ToastUtil.show("请输入验证码");
            return false;
        } else if (code.length() < 6) {
            ToastUtil.show("验证码为6位数");
            return false;
        }
        return true;
    }

    private void submitData() {
        String code = StringUtils.getString(mEtCode);
        if (checkCode(code)) {
            TreeMap<String, String> map = new TreeMap<String, String>();
            map.put("login_token", UserConfig.getInstance().getLoginToken(this));
            map.put("email", mEmail);
            map.put("code", code);
            HttpUtil.post(UrlConstants.EMAIL_BIND, map,
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
                        public void onSuccess(int statusCode, Header[] headers,
                                              JSONObject response) {
                            try {
                                if (CreateCode.AuthentInfo(response)) {
                                    JSONObject json = StringUtils
                                            .parseContent(response);
                                    if (json.optString("result")
                                            .contains("success")) {
                                        EventBus.getDefault().post(
                                                new EventObject("refresh"));
                                        ToastUtil.show("绑定成功");
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

                        @Override
                        public void onFailure(int statusCode, Header[] headers,
                                              Throwable throwable, JSONObject errorResponse) {
                            ToastUtil.show(R.string.generic_error);
                            super.onFailure(statusCode, headers, throwable,
                                    errorResponse);
                        }
                    });
        }
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
                            if (StringUtils
                                    .isEmpty(info.optString("realname_status"))||info.optString("realname_status").equals("-1")) {
                                // 未认证实名或认证失败
                                ToastUtil.show("请进行实名认证");
                                intent.setClass(EmailCodeActivity.this,
                                        RealNameAuthActivity.class);
                                startActivity(intent);

                            }
//                            else if (info.optString("realname_status")
//                                    .equals("-2")) {
//                                // 待审核
//                                ToastUtil.show("实名认证待审核中");
//                                intent.setClass(EmailCodeActivity.this,
//                                        RealNameInfoActivity.class);
//                                startActivity(intent);
//                            }
                            else if(!info.optString("register").equals("1")) {
                                startActivity(new Intent(EmailCodeActivity.this,
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

    private void sendCode() {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("email", mEmail);
        map.put("type", "1");
        HttpUtil.post(UrlConstants.EMAIL_SEND_CODE, map,
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
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        try {
                            if (CreateCode.AuthentInfo(response)) {
                                JSONObject json = StringUtils
                                        .parseContent(response);
                                System.out.println();
                                if (json.getString("result")
                                        .contains("success")) {
                                    JSONObject jo = json.getJSONObject("data");
                                    mVerifyCode = jo.optString("code");
                                    CountDownButton countBtn = new CountDownButton();
                                    countBtn.init(EmailCodeActivity.this,
                                            mBtnCode);
                                    countBtn.start();
                                    ToastUtil.show("发送成功");
                                    mMsgLayout.setVisibility(View.VISIBLE);
                                } else {
                                    ToastUtil.show(
                                            json.getString("description"));
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
                        ToastUtil.show(R.string.generic_error);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });

    }

    private void sendRecoverEmail() {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("email", mEmail);
        HttpUtil.post(UrlConstants.SEND_RECOVER_EMAIL, map,
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
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        try {
                            if (CreateCode.AuthentInfo(response)) {
                                JSONObject json = StringUtils
                                        .parseContent(response);

                                if (json.getString("result")
                                        .contains("success")) {
                                    CountDownButton countBtn = new CountDownButton();
                                    countBtn.init(EmailCodeActivity.this,
                                            mBtnCode);
                                    countBtn.start();
                                    ToastUtil.show("发送成功");
                                    mMsgLayout.setVisibility(View.VISIBLE);
                                    // TODO 获取邮箱验证码进行判断
                                    JSONObject jsonObject = json
                                            .getJSONObject("data");
                                    emailCode = jsonObject.getString("code");
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

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        ToastUtil.show(R.string.generic_error);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
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
