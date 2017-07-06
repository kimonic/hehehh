package com.diyou.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diyou.base.BaseActivity;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class BeforeRegisteredActivity extends BaseActivity
        implements OnClickListener
{

    private EditText mUserName;
    protected Dialog progressDialogBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_registered);
        initView();
    }

    private void initView()
    {
        mUserName = (EditText) findViewById(R.id.registeredactivity_uesrname);
        findViewById(R.id.registeredactivity_cls).setOnClickListener(this);
        findViewById(R.id.registeredactivity_btn_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.registeredactivity_back:
            finish();
            break;
        case R.id.registeredactivity_cls:
            mUserName.setText(null);
            break;

        case R.id.registeredactivity_btn_next:
            if (StringUtils.isEmpty(mUserName.getText().toString())
                    && checkPhoneNum(mUserName.getText().toString()))
            {
                ToastUtil.show("请输入正确的手机号！");
            }
            else
            {
                Intent intent = new Intent(BeforeRegisteredActivity.this,
                        RegisteredActivity.class);
                intent.putExtra("mUserName", mUserName.getText().toString());
                startActivityForResult(intent, 10);
            }
            break;

        default:
            break;
        }
    }

    /**
     * 手机号验证
     * 
     * @param str
     * @return 验证通过返回true
     */
    private boolean checkPhoneNum(String string)
    {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(string);
        b = m.matches();
        return b;
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(arg0, arg1, arg2);
        if (arg1 == 10)
        {
            BeforeRegisteredActivity.this.finish();
        }
    }
}
