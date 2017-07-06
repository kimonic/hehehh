package com.diyou.activity;

import com.diyou.bean.Reimbursement;
import com.diyou.util.BorrowCalculateManagerImpl;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;

import android.app.Activity;
import android.os.Bundle;

public class TestActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        String s = BorrowCalculateManagerImpl.Bill(1000.00, 0, 3.00,
                Reimbursement.Dengebenxi);
        ToastUtil.show(s);
    }

}
