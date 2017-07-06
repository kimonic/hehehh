package com.diyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

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

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TreeMap;

/**未登陆时弹出的activity*/
public class BeforeLoginActivity extends BaseActivity implements OnClickListener
{
    /**输入手机号或用户名*/
    private EditText mUesrname;
    protected ProgressDialogBar progressDialogBar;
    /**下一步按钮*/
    private Button mSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().setBeforeLoginActivity(this);
        setContentView(R.layout.activity_beforelogin);
        initView();
    }

    private void initView()
    {
        findViewById(R.id.loginactivity_back).setOnClickListener(this);
        mSubmit = (Button) findViewById(R.id.beforeloginactivity_btn_next);
        mSubmit.setOnClickListener(this);
        findViewById(R.id.loginactivity_cls).setOnClickListener(this);
        mUesrname = (EditText) findViewById(R.id.beforeloginactivity_uesrname);
        //监听输入变化
        mUesrname.addTextChangedListener(new TextWatcher()
        {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                mSubmit.setEnabled(!StringUtils.isEmpty(s.toString().trim()));
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
                // TODO Auto-generated method stub
                
            }
        });
    }

    // TODO onClick监听
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.loginactivity_back://返回
            finish();
            break;
        case R.id.loginactivity_cls://清空输入
            mUesrname.setText(null);
            break;
        case R.id.beforeloginactivity_btn_next://下一步
            if (StringUtils.isEmpty2(mUesrname.getText().toString()))
            {
                ToastUtil.show("请输入手机号、邮箱或用户名");
            }
            else
            {
                volleyLogin();//登陆
            }
            break;
        default:
            break;
        }
    }
    /**用户登陆*/
    private void volleyLogin()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("username", mUesrname.getText().toString());
        HttpUtil.post(UrlConstants.CHECK, map, new JsonHttpResponseHandler()
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
                            .createDialog(BeforeLoginActivity.this);
                }
                progressDialogBar.setProgressMsg("登录中...");
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
                        if (json.optString("result").contains("success"))
                        {
                            JSONObject data = json.getJSONObject("data");
                            Intent intent = new Intent();
                            // TODO status 状态 0不存在 1存在；type 类型 1 邮箱 2手机 3
                            // 用户名；title 注册协议名称
                            if ("1".equals(data.optString("status")))
                            {

                                intent.putExtra("type", data.optInt("type"));
                                intent.putExtra("mUesrname",
                                        mUesrname.getText().toString());
                                intent.setClass(BeforeLoginActivity.this,
                                        LoginActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                // TODO 目前只支持手机账号登录注册
                                if (StringUtils.checkPhoneNum(
                                        mUesrname.getText().toString()))
                                {
                                    ToastUtil.show("账号不存在，请注册");
                                    intent.setClass(BeforeLoginActivity.this,
                                            RegisteredActivity.class);
                                    intent.putExtra("mUesrname",
                                            mUesrname.getText().toString());
                                    startActivity(intent);
                                }else
                                {
                                    ToastUtil.show("未注册用户请输入手机号码");
                                }
                            }
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
}
