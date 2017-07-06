package com.diyou.activity;

import com.diyou.base.BaseActivity;
import com.diyou.util.StringUtils;
import com.diyou.v5yibao.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class PhoneChangeActivity extends BaseActivity implements OnClickListener
{

    private String mPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_change);
        initActionBar();
        mPhoneNum = getIntent().getStringExtra("phoneNum");
        initView();
    }

    private void initView()
    {
        TextView tvPhone = (TextView) findViewById(R.id.tv_phone_change_num);
        tvPhone.setText("已绑定手机" + StringUtils.getHidePhone(mPhoneNum));
        findViewById(R.id.btn_phone_change_submit).setOnClickListener(this);
    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.phone_change_title);
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
        case R.id.btn_phone_change_submit:
            Intent intent = new Intent(this, CheckOldPhoneActivity.class);
            intent.putExtra("phoneNum", mPhoneNum);
            startActivity(intent);
            finish();
            break;

        default:
            break;
        }
    }

}
