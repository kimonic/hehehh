package com.diyou.activity;

import java.util.Random;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.CountDownButton;
import com.diyou.view.ProgressDialogBar;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UpdateEmailActivity extends BaseActivity implements OnClickListener
{

    private EditText mCode;
    private EditText mEmail;
    private ProgressDialogBar progressBar;
    private String email;
    private Random mRandom;
    private Button mCode_btn;
    private Button mNext_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        mRandom = new Random();
        setContentView(R.layout.activity_up_data_email);
        init();
    }

    private void init()
    {
        findViewById(R.id.emailactivationcomplete_title_layout)
                .findViewById(R.id.title_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.emailactivationcomplete_title_layout)
                .findViewById(R.id.title_name))
                        .setText(R.string.emailactivation_title);
        mCode = (EditText) findViewById(R.id.updataemailcctivity_code_et);
        mCode_btn = (Button) findViewById(R.id.updataemailcctivity_code_btn);
        mCode_btn.setOnClickListener(this);
        mEmail = (EditText) findViewById(R.id.updataemailcctivity_email_tv);
        mNext_btn = (Button) findViewById(R.id.updataemailcctivity_next_btn);
        mNext_btn.setEnabled(false);
        mNext_btn.setOnClickListener(this);
        mEmail.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count)
            {
                if (mEmail.getText().toString().contains("@"))
                {
                    mNext_btn.setEnabled(true);
                }
                else
                {
                    mNext_btn.setEnabled(false);
                }
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.title_back:
            finish();
            break;
        case R.id.updataemailcctivity_code_btn:
            requestCode();
            break;
        case R.id.updataemailcctivity_next_btn:
            requestUpDataEmail();
            break;
        default:
            break;
        }
    }

    private void requestUpDataEmail()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("module", "users");
        map.put("q", "send_update_email_new");
        map.put("method", "get");
        map.put("user_id",
                UserConfig.getInstance().getUserId(UpdateEmailActivity.this));
        map.put("email", mEmail.getText().toString().trim());
        map.put("code", mCode.getText().toString().trim());
        HttpUtil.get(CreateCode.getUrl(map), new JsonHttpResponseHandler()
        {
            @Override
            public void onFailure(int statusCode, Header[] headers,
                    String responseString, Throwable throwable)
            {
                ToastUtil.show(responseString);
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONArray errorResponse)
            {
                ToastUtil.show(errorResponse.toString());
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                ToastUtil.show(errorResponse.toString());
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    if (response.getString("result").equals("success"))
                    {
                        ToastUtil.showLongToast("激活邮件已经发送到你的新邮箱，请注意查收,");
                        finish();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFinish()
            {
                progressBar.dismiss();
                super.onFinish();
            }

            @Override
            public void onStart()
            {
                if (progressBar == null)
                {
                    progressBar = ProgressDialogBar
                            .createDialog(UpdateEmailActivity.this);
                }
                progressBar.setCanceledOnTouchOutside(true);
                progressBar.show();
                super.onStart();
            }

        });
    }

    private void requestCode()
    {
        String code = mRandom.nextInt(999999) + "";
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("module", "users");
        map.put("q", "CCCC");
        map.put("method", "get");
        map.put("user_id",
                UserConfig.getInstance().getUserId(UpdateEmailActivity.this));
        map.put("email", email);
        map.put("code", code);
        HttpUtil.get(CreateCode.getUrl(map), new JsonHttpResponseHandler()
        {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    String responseString, Throwable throwable)
            {
                ToastUtil.show(responseString);
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONArray errorResponse)
            {
                ToastUtil.show(errorResponse.toString());
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                ToastUtil.show(errorResponse.toString());
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    if (response.getString("result").equals("success"))
                    {
                        CountDownButton downButton = new CountDownButton();
                        downButton.init(UpdateEmailActivity.this, mCode_btn);
                        downButton.start();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFinish()
            {
                progressBar.dismiss();
                super.onFinish();
            }

            @Override
            public void onStart()
            {
                if (progressBar == null)
                {
                    progressBar = ProgressDialogBar
                            .createDialog(UpdateEmailActivity.this);
                }
                progressBar.setCanceledOnTouchOutside(true);
                progressBar.show();
                super.onStart();
            }
        });
    }
}
