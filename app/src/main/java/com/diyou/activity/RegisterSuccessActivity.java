package com.diyou.activity;

import com.diyou.base.BaseActivity;
import com.diyou.v5yibao.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class RegisterSuccessActivity extends BaseActivity
        implements OnClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);
        initView();
    }

    private void initView()
    {
        findViewById(R.id.title_activity_register_skip)
                .setOnClickListener(this);
        findViewById(R.id.title_activity_register_submit)
                .setOnClickListener(this);
        findViewById(R.id.activity_register_success_no_activication)
                .setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.title_activity_register_skip:
        case R.id.activity_register_success_no_activication:
            finish();
            break;
        case R.id.title_activity_register_submit:
            Intent intent = new Intent(RegisterSuccessActivity.this,
                    AccountSecurityActivity.class);
            // intent.putExtra("isFromRegidter", true);
            startActivity(intent);
            finish();
            break;

        default:
            break;
        }
    }
}
