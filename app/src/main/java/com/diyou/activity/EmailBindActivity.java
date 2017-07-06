package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONException;
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
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 邮箱认证
 *
 * @author Administrator
 *
 */
public class EmailBindActivity extends BaseActivity implements OnClickListener
{

    public static final int REQUEST_CODE_BIND = 1021;
    private EditText mEtAccount;
    private String authAction;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        initActionBar();
        authAction = getIntent().getStringExtra("authAction");
        initView();
    }

    private void initView()
    {
        mEtAccount = (EditText) findViewById(R.id.et_phone_bind_num);
        mEtAccount.setHint(R.string.email_bind_hint);
        mEtAccount.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        findViewById(R.id.btn_phone_bind_number_next).setOnClickListener(this);
    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.email_bind_title);
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
        case R.id.btn_phone_bind_number_next:
            emailBindNext();
            break;

        default:
            break;
        }
    }

    private void emailBindNext()
    {
        String email = StringUtils.getString(mEtAccount);
        if (StringUtils.isEmpty(email))
        {
            ToastUtil.show(R.string.email_bind_hint);
        }
        else if (StringUtils.checkEmail(email))
        {
            checkData(email);
        }
        else
        {
            ToastUtil.show(R.string.email_bind_input_right);
        }
    }

    private void checkData(final String email)
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("email", email);
        HttpUtil.post(UrlConstants.CHECK_EMAIL, map,
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
                    public void onSuccess(int statusCode, Header[] headers,
                            JSONObject response)
                    {
                        try
                        {
                            if (CreateCode.AuthentInfo(response))
                            {
                                JSONObject json = StringUtils
                                        .parseContent(response);
                                if (json.optString("result")
                                        .contains("success"))
                                {
                                    JSONObject jo = json.getJSONObject("data");
                                    if ("1".equals(jo.optString("status")))
                                    {
                                        // String type =
                                        // getType(jo.optString("type"));
                                        // if(StringUtils.isEmpty(type)){
                                        // ToastUtil.show("无法检测该账户类型");
                                        // }else{
                                        ToastUtil.show("邮箱已被验证,请重新输入");
                                        // }
                                    }
                                    else if ("0".equals(jo.optString("status")))
                                    {
                                        Intent i = new Intent(
                                                EmailBindActivity.this,
                                                EmailCodeActivity.class);
                                        i.putExtra("email", email);
                                        i.putExtra("authAction", authAction);
                                        startActivityForResult(i,
                                                REQUEST_CODE_BIND);
                                    }
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

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        ToastUtil.show(R.string.generic_error);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });

    }

    // 类型 1 邮箱 2手机 3 用户名
    private String getType(String type)
    {
        if ("1".equals(type))
        {
            return "邮箱";
        }
        else if ("2".equals(type))
        {
            return "手机";
        }
        else if ("3".equals(type))
        {
            return "用户名";
        }
        return "";
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2)
    {
        super.onActivityResult(arg0, arg1, arg2);
        if (arg0 == REQUEST_CODE_BIND && arg1 == RESULT_OK)
        {
            finish();
        }
    }
}
