package com.diyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.diyou.base.BaseActivity;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.TreeMap;

public class MoneyManagementActivity extends BaseActivity
        implements OnClickListener
{

    private TextView moneymanagementactivity_account_accumulate_earnings;// 累计收益
    private TextView moneymanagementactivity_uncollected_revenue;// 待收收益
    private TextView moneymanagementactivity_freeze_funds;// 冻结资金
    private TextView moneymanagementactivity_collected_the_principal;// 待收本金
    private TextView moneymanagementactivity_account_balance;// 账户余额
    private TextView mUserName_tv;
    private View mLoadlayout;
    private String mUserName;
    private DecimalFormat df;

    /**
     * 资金管理
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        df = new DecimalFormat("######0.00");
        mUserName = getIntent().getStringExtra("username");
        setContentView(R.layout.activity_money_management);
        initView();
        initData();
    }

    private void initData()
    {
        requestData();
    }

    private void requestData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("module", "account");
        map.put("q", "mobile_get_account_result");
        map.put("method", "get");
        map.put("user_id", UserConfig.getInstance().getUserId(this));
        HttpUtil.get(CreateCode.getUrl(map), new JsonHttpResponseHandler()
        {

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
                    moneymanagementactivity_account_accumulate_earnings
                            .setText(df.format(
                                    response.getDouble("recover_yes_profit")));// 累计收益
                    moneymanagementactivity_uncollected_revenue.setText(
                            response.getString("tender_wait_interest"));// 待收收益
                    moneymanagementactivity_freeze_funds
                            .setText(df.format(response.getDouble("_frost")));// 冻结资金
                    moneymanagementactivity_collected_the_principal.setText(df
                            .format(response.getDouble("tender_wait_capital")));// 待收本金
                    moneymanagementactivity_account_balance
                            .setText(df.format(response.getDouble("_balance")));
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
                mLoadlayout.setVisibility(View.GONE);
                super.onFinish();
            }

            @Override
            public void onStart()
            {
                super.onStart();
            }
        });
    }

    private void initView()
    {

        findViewById(R.id.moneymanagementactivity_titlelayout)
                .findViewById(R.id.title_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.moneymanagementactivity_titlelayout)
                .findViewById(R.id.title_name))
                        .setText(R.string.moneymanagementactivity_title);

        findViewById(R.id.moneymanagementactivity_top_up)
                .setOnClickListener(this);
        findViewById(R.id.moneymanagementactivity_withdrawal)
                .setOnClickListener(this);

        mLoadlayout = findViewById(
                R.id.moneymanagementactivity_loadlayout);
        moneymanagementactivity_account_accumulate_earnings = (TextView) findViewById(
                R.id.moneymanagementactivity_account_accumulate_earnings);// 累计收益
        moneymanagementactivity_uncollected_revenue = (TextView) findViewById(
                R.id.moneymanagementactivity_uncollected_revenue);// 待收收益
        moneymanagementactivity_freeze_funds = (TextView) findViewById(
                R.id.moneymanagementactivity_freeze_funds);// 冻结资金
        moneymanagementactivity_collected_the_principal = (TextView) findViewById(
                R.id.moneymanagementactivity_collected_the_principal);// 待收本金
        moneymanagementactivity_account_balance = (TextView) findViewById(
                R.id.moneymanagementactivity_account_balance);// 账户余额
        mUserName_tv = (TextView) findViewById(
                R.id.moneymanagementactivity_userName);
        mUserName_tv.setText(mUserName);
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2)
    {
        requestData();
        super.onActivityResult(arg0, arg1, arg2);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.title_back:
            finish();
            break;
        // case R.id.moneymanagementactivity_withdrawal:
        // startActivityForResult(new Intent(MoneyManagementActivity.this,
        // CashActivity.class), Constants.CASH_CODE);
        // break;
        // case R.id.moneymanagementactivity_top_up:
        // Intent intent = new Intent(MoneyManagementActivity.this,
        // TopUpActivity.class);
        // intent.putExtra("money", moneymanagementactivity_account_balance
        // .getText().toString());
        // startActivityForResult(intent, Constants.TOP_UP_CODE);
        //
        // break;

        default:
            break;
        }
    }

}
