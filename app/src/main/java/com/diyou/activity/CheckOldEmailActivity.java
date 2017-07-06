package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.TextWatcherUtil;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.CountDownButton;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CheckOldEmailActivity extends BaseActivity
        implements OnClickListener
{

    private String mEmail;
    private EditText mEtCode;
    private Button mBtnSend;
    private View mRlMsg;
    private Button mBtnNext;
    private String mVerifyCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_old_phone);
        mEmail = getIntent().getStringExtra("email");
        initActionBar();
        initView();
    }

    private void initView()
    {
        TextView tvFlag = (TextView) findViewById(R.id.tv_phone_code_flag);
        TextView tvType = (TextView) findViewById(R.id.tv_phone_code_msg_type);
        tvFlag.setText(R.string.email_change_old_email);
        tvType.setText(R.string.email_bind_remind_msg);
        mBtnSend = (Button) findViewById(R.id.btn_send_phone_code);
        mBtnSend.setOnClickListener(this);
        TextView tvHideEmail = (TextView) findViewById(
                R.id.tv_phone_code_msg_account);
        TextView tvHideDisplay = (TextView) findViewById(
                R.id.tv_phone_bind_display);
        tvHideEmail.setText(StringUtils.getHideEmail(mEmail));
        tvHideDisplay.setText(StringUtils.getHideEmail(mEmail));
        mBtnNext = (Button) findViewById(R.id.btn_phone_change_number_next);
        mBtnNext.setOnClickListener(this);
        mEtCode = (EditText) findViewById(R.id.et_phone_code);
        mEtCode.addTextChangedListener(TextWatcherUtil.getVerifyCode(mBtnNext));
        mRlMsg = findViewById(R.id.rl_phone_code_msg);

    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.email_change_check_old);
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.title_back:
            finish();
            break;
        case R.id.btn_send_phone_code:
            mRlMsg.setVisibility(View.GONE);
            sendVerifyCode();
            break;
        case R.id.btn_phone_change_number_next:
            requestVerifyOld();
            break;
        default:
            break;
        }
    }

    private void requestVerifyOld()
    {
        if (!StringUtils.isEmpty(mVerifyCode)
                && mVerifyCode.equals(StringUtils.getString(mEtCode)))
        {
            Intent intent = new Intent(this, CheckNewEmailActivity.class);
            intent.putExtra("verifyCode", mVerifyCode);
            startActivity(intent);
            finish();
        }
        else
        {
            ToastUtil.show(R.string.phone_change_code_error);
        }
    }

    private void sendVerifyCode()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("email", mEmail);
        map.put("type", "4");
        HttpUtil.post(UrlConstants.EMAIL_SEND_CODE, map,
                new JsonHttpResponseHandler()
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
                            Throwable throwable, JSONObject errorResponse)
                    {
                        ToastUtil.show(R.string.generic_error);
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
                                JSONObject json = StringUtils
                                        .parseContent(response);
                                if ("success".equals(json.getString("result")))
                                {
                                    JSONObject jo = json.getJSONObject("data");
                                    mVerifyCode = jo.optString("code");
                                    CountDownButton downButton = new CountDownButton();
                                    downButton.init(CheckOldEmailActivity.this,
                                            mBtnSend);
                                    downButton.start();
                                    mRlMsg.setVisibility(View.VISIBLE);
                                    ToastUtil.show("发送成功");
                                }
                                else
                                {
                                    ToastUtil.show(
                                            json.getString("description"));
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
