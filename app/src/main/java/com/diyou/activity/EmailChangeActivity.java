package com.diyou.activity;

import com.diyou.base.BaseActivity;
import com.diyou.util.StringUtils;
import com.diyou.v5yibao.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class EmailChangeActivity extends BaseActivity implements OnClickListener
{

    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_change);
        initActionBar();
        mEmail = getIntent().getStringExtra("email");
        initView();
    }

    private void initView()
    {
        TextView tvPhone = (TextView) findViewById(R.id.tv_phone_change_num);
        tvPhone.setText("已绑定邮箱" + StringUtils.getHideEmail(mEmail));
        Button btnChange = (Button) findViewById(R.id.btn_phone_change_submit);
        btnChange.setText(R.string.email_change_change);
        btnChange.setOnClickListener(this);
    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.email_change_title);
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
            Intent intent = new Intent(this, CheckOldEmailActivity.class);
            intent.putExtra("email", mEmail);
            startActivity(intent);
            finish();
            break;

        default:
            break;
        }
    }

}
