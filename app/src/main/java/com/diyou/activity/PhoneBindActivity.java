package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.config.UrlConstants;
import com.diyou.http.HttpUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 手机绑定
 * 
 * @author Administrator
 * 
 */
public class PhoneBindActivity extends BaseActivity implements OnClickListener
{
    public static final int REQUEST_CODE_PHONE = 1022;
    private EditText phoneNumber;
    private String authAction;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        authAction = getIntent().getStringExtra("authAction");
        initActionBar();
        initView();

    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.bind_phone_title);
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    private void initView()
    {
        findViewById(R.id.btn_phone_bind_number_next).setOnClickListener(this);
        phoneNumber = (EditText) findViewById(R.id.et_phone_bind_num);
        phoneNumber.setFilters(new InputFilter[]
        { new InputFilter.LengthFilter(11) });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.btn_phone_bind_number_next:
            String phoneNum = StringUtils.getString(phoneNumber);
            if (StringUtils.isEmpty2(phoneNum))
            {
                ToastUtil.show("请输入手机号码");
            }
            else if (StringUtils.checkPhoneNum(phoneNum))
            {
                isThereMobilePhone(phoneNum);
            }
            else
            {
                ToastUtil.show("手机号码格式错误");
            }
            break;
        case R.id.title_back:
            finish();
            break;
        default:
            break;
        }

    }

    protected void isThereMobilePhone(final String phoneNum)
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("phone", phoneNum);
        HttpUtil.post(UrlConstants.CHECK_PHONE, map,
                new JsonHttpResponseHandler()
                {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        ToastUtil.show(errorResponse.optString("description"));
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
                                if (data.getString("result").equals("success"))
                                {
                                    JSONObject info = data
                                            .getJSONObject("data");
                                    if ("0".equals(info.optString("status")))
                                    {

                                        Intent intent = new Intent(
                                                PhoneBindActivity.this,
                                                PhoneCodeActivity.class);
                                        intent.putExtra("phone", phoneNum);
                                        intent.putExtra("authAction",
                                                authAction);
                                        startActivityForResult(intent,
                                                REQUEST_CODE_PHONE);
                                    }
                                    else if ("1"
                                            .equals(info.optString("status")))
                                    {
                                        ToastUtil.show("该手机已被绑定，请重新输入");
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

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2)
    {
        // TODO Auto-generated method stub
        if (arg0 == REQUEST_CODE_PHONE && arg1 == RESULT_OK)
        {
            finish();
        }
        super.onActivityResult(arg0, arg1, arg2);
    }
}
