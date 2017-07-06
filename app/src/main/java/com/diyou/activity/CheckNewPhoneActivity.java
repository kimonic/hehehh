package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.bean.EventObject;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.CountDownButton;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.greenrobot.event.EventBus;

public class CheckNewPhoneActivity extends BaseActivity
        implements OnClickListener
{

    private String mOldCode;
    private String mNewCode = "";
    private Button mBtnSend;
    private View mRlMsg;
    private EditText mEtNewPhone;
    private EditText mEtNewCode;
    private Button mBtnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_new_phone);
        initActionBar();
        mOldCode = getIntent().getStringExtra("verifyCode");
        initView();
    }

    private void initView()
    {
        mBtnSend = (Button) findViewById(R.id.btn_send_phone_code);
        mBtnSubmit = (Button) findViewById(R.id.btn_phone_change_number_next);
        mBtnSend.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
        mRlMsg = findViewById(R.id.rl_phone_code_msg);
        mEtNewPhone = (EditText) findViewById(R.id.et_phone_new);
        mEtNewPhone.setFilters(new InputFilter[]
        { new InputFilter.LengthFilter(11) });
        mEtNewCode = (EditText) findViewById(R.id.et_phone_code);
        mEtNewPhone.addTextChangedListener(mTextWatcher);
        mEtNewCode.addTextChangedListener(mTextWatcher);
        mTvHidePhone = (TextView) findViewById(R.id.tv_phone_code_msg_account);
    }

    TextWatcher mTextWatcher = new TextWatcher()
    {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s)
        {

            if (StringUtils.getString(mEtNewCode).length() == 6 && StringUtils
                    .checkPhoneNum(StringUtils.getString(mEtNewPhone)))
            {
                mBtnSubmit.setEnabled(true);
            }
            else
            {
                mBtnSubmit.setEnabled(false);
            }
        }
    };
    private TextView mTvHidePhone;

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.phone_change_check_new);
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
            String newPhone = StringUtils.getString(mEtNewPhone);
            if(StringUtils.isEmpty(newPhone)){
                ToastUtil.show(R.string.remind_input_phone_num);
                return;
            }else if(!StringUtils.checkPhoneNum(newPhone)){
                ToastUtil.show(R.string.remind_input_correct_phone);
                return;
            }else{
                mRlMsg.setVisibility(View.GONE);
                sendVerifyCode();
            }
            break;
        case R.id.btn_phone_change_number_next:
            if(StringUtils.isEmpty(mNewCode)){
                ToastUtil.show(R.string.remind_send_auth_code_first);
            }else if(!mNewCode.equals(StringUtils.getString(mEtNewCode))){
                ToastUtil.show(R.string.remind_input_correct_auth_code);
            }else{
                newPhoneSubmit();
            }
            break;
        default:
            break;
        }
    }

    private void newPhoneSubmit()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("new_phone", StringUtils.getString(mEtNewPhone));
        map.put("old_code", mOldCode);
        map.put("new_code", mNewCode);
        HttpUtil.post(UrlConstants.CHANGE_PHONE, map,
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
                                if ("success".equals(json.optString("result")))
                                {
                                    EventBus.getDefault()
                                            .post(new EventObject("refresh"));
                                    ToastUtil.show("手机更换成功");
                                    finish();
                                }
                                else
                                {
                                    ToastUtil.show(
                                            json.optString("description"));
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

    private void sendVerifyCode()
    {
         final String str=StringUtils.getRandom();
         mNewCode=str;
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("phone", StringUtils.getString(mEtNewPhone));
        map.put("type", "approve");
        map.put("phone_code", str);
        map.put("is_check", "1");// 验证手机是否存在/或者已被注册
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
                        if ("success".equals(json.getString("result")))
                        {
//                             mNewCode=str;
                            CountDownButton downButton = new CountDownButton();
                            downButton.init(CheckNewPhoneActivity.this,
                                    mBtnSend);
                            downButton.start();
                            mTvHidePhone.setText(StringUtils.getHidePhone(
                                    StringUtils.getString(mEtNewPhone)));
                            mRlMsg.setVisibility(View.VISIBLE);
                            ToastUtil.show("发送成功");
                        }
                        else
                        {
                            ToastUtil.show(json.getString("description"));
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
