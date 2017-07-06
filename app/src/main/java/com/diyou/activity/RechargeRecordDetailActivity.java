package com.diyou.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.diyou.base.BaseActivity;
import com.diyou.bean.RechargeRecordInfo;
import com.diyou.util.DateUtils;
import com.diyou.util.StringUtils;
import com.diyou.v5yibao.R;

public class RechargeRecordDetailActivity extends BaseActivity implements View.OnClickListener {

    private int colorGray;
    private int colorBlue;
    private int colorOrange;
    private int colorRed;
    TextView mTvRechargeOrderNumber;
    TextView mTvRechargeStates;
    TextView mTvRechargeType;
    TextView mTvRechargePaytype;
    TextView mTvRechargeAmount;
    TextView mTvRechargeTime;
    TextView mTvRechargeRemark;
    TextView mTvRechargeManagerRemark;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_recharge_record_detail);
        initView();
        initData();
    }

    private void initView() {
        TextView tvTitleName = (TextView) findViewById(R.id.title_name);
        tvTitleName.setText(R.string.recharge_detail);
        findViewById(R.id.loginactivity_back).setOnClickListener(this);


        mTvRechargeOrderNumber = (TextView) findViewById(R.id.tv_recharge_ordernumber);

        mTvRechargeStates = (TextView) findViewById(R.id.tv_recharge_states);

        mTvRechargeType = (TextView) findViewById(R.id.tv_recharge_type);

        mTvRechargePaytype = (TextView) findViewById(R.id.tv_recharge_paytype);
        mTvRechargeAmount = (TextView) findViewById(R.id.tv_recharge_amount);
        mTvRechargeTime = (TextView) findViewById(R.id.tv_recharge_time);
        mTvRechargeRemark = (TextView) findViewById(R.id.tv_recharge_remark);
        mTvRechargeManagerRemark = (TextView) findViewById(R.id.tv_recharge_manager_remark);
    }

    private void initData() {
        initColor();
        RechargeRecordInfo info = (RechargeRecordInfo) getIntent().getSerializableExtra("RechargeInfo");

        mTvRechargeOrderNumber.setText(info.getInd());
        mTvRechargeStates.setText(info.getStatus_name());
        mTvRechargeType.setText(info.getTypeName());
        mTvRechargePaytype.setText(info.getPayment_name());
        mTvRechargeAmount.setText(StringUtils.getDoubleFormat(info.getAmount()) + "元");
        mTvRechargeTime.setText(DateUtils.getDateTimeFormat(info.getAdd_time()));

        if (!StringUtils.isEmpty(info.getRemark())) {
            mTvRechargeRemark.setText(info.getRemark());
        }

        if (!StringUtils.isEmpty(info.getVerify_remark())) {
            mTvRechargeManagerRemark.setText(info.getVerify_remark());
        }

        // -3用户撤销 -2待审核 -1失败 1成功
        if ("-3".equals(info.getStatus())) {
            mTvRechargeStates.setTextColor(colorGray);
        } else if ("-2".equals(info.getStatus())) {
            mTvRechargeStates.setTextColor(colorOrange);
        } else if ("1".equals(info.getStatus())) {
            mTvRechargeStates.setTextColor(colorBlue);
        } else if ("-1".equals(info.getStatus())) {
            mTvRechargeStates.setTextColor(colorRed);
        }
    }

    private void initColor() {
        colorGray = getResources().getColor(R.color.huise);
        colorBlue = getResources().getColor(R.color.lanse);
        colorOrange = getResources().getColor(R.color.chengse);
        colorRed = getResources().getColor(R.color.red);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginactivity_back:
                finish();
                break;

        }
    }
}
