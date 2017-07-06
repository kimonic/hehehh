package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.pulltorefresh.PullToRefreshBase;
import com.diyou.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.diyou.pulltorefresh.PullToRefreshScrollView;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.LoadingLayout;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TextView;

public class FundsDetailActivity extends BaseActivity
        implements OnClickListener, OnRefreshListener<ScrollView>
{

    private TextView mTvMoneySum;
    private TextView mTvCanBalance;
    private TextView mTvFreezeBalance;
    private TextView mTvInterestReceived;
    private TextView mTvCollectionCapital;
    private TextView mTvCollectionInterest;

    private boolean isFirst = true;
    private LoadingLayout mLoadingLayout;
    private PullToRefreshScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funds_detail);
        initActionBar();
        initView();
        requestData();
    }

    private void initView()
    {
        mTvMoneySum = (TextView) findViewById(R.id.tv_fund_money_sum);
        mTvCanBalance = (TextView) findViewById(R.id.tv_fund_can_balance);
        mTvFreezeBalance = (TextView) findViewById(R.id.tv_fund_freeze_balance);
        mTvInterestReceived = (TextView) findViewById(
                R.id.tv_fund_interest_received);
        mTvCollectionCapital = (TextView) findViewById(
                R.id.tv_fund_collection_capital);
        mTvCollectionInterest = (TextView) findViewById(
                R.id.tv_fund_collection_interest);
        findViewById(R.id.rl_fund_detail_withdraw).setOnClickListener(this);
        findViewById(R.id.rl_fund_detail_recharge).setOnClickListener(this);
        mScrollView = (PullToRefreshScrollView) findViewById(
                R.id.sv_fund_detail);
        mScrollView.setOnRefreshListener(this);
        mLoadingLayout = (LoadingLayout) findViewById(
                R.id.fund_detail_loadlayout);
        mLoadingLayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        requestData();
                    }
                });
        mLoadingLayout.setOnStartLoading(mScrollView);
    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.fund_detail_title);
        findViewById(R.id.title_back).setOnClickListener(this);
        findViewById(R.id.rl_title_bar_right).setVisibility(View.VISIBLE);
        findViewById(R.id.rl_title_bar_right).setOnClickListener(this);
    }

    private void requestData()
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        HttpUtil.post(UrlConstants.PERSONAL_FUND_DETAIL, map,
                new JsonHttpResponseHandler()
                {
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
                                    JSONObject jo = new JSONObject(
                                            json.optString("data"));
                                    mTvMoneySum.setText(StringUtils
                                            .getMoneyFormat(jo.optString(
                                                    "total_amount")));
                                    mTvCanBalance.setText(StringUtils
                                            .getMoneyFormat(jo.optString(
                                                    "balance_amount")));
                                    mTvFreezeBalance.setText(StringUtils
                                            .getMoneyFormat(jo.optString(
                                                    "freeze_amount")));
                                    mTvInterestReceived.setText(StringUtils
                                            .getMoneyFormat(jo.optString(
                                                    "interest_yes_total")));
                                    mTvCollectionCapital.setText(StringUtils
                                            .getMoneyFormat(jo.optString(
                                                    "principal_wait_total")));
                                    mTvCollectionInterest.setText(StringUtils
                                            .getMoneyFormat(jo.optString(
                                                    "interest_wait_total")));
                                    if (isFirst)
                                    {
                                        isFirst = false;
                                        mLoadingLayout.setOnStopLoading(
                                                FundsDetailActivity.this,
                                                mScrollView);
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
                    public void onFinish()
                    {
                        mScrollView.onRefreshComplete();
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        if (isFirst)
                        {
                            mLoadingLayout.setOnLoadingError(
                                    FundsDetailActivity.this, mScrollView);
                        }
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
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
        case R.id.rl_title_bar_right:
            startActivity(new Intent(FundsDetailActivity.this,
                    TradeRecordActivity.class));
            break;
        case R.id.rl_fund_detail_recharge:
            requestYibaoRegistered(new Runnable()
            {

                @Override
                public void run()
                {
                    startActivity(new Intent(FundsDetailActivity.this,
                            RechargeActivity.class));
                }
            });
            break;
        case R.id.rl_fund_detail_withdraw:
            requestYibaoRegistered(new Runnable()
            {

                @Override
                public void run()
                {
                    getBankCardInfo();
                }
            });
            break;
        default:
            break;
        }
    }

    private void getBankCardInfo()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        HttpUtil.post(UrlConstants.BANK_CARD_INFO, map,
                new JsonHttpResponseHandler()
                {

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

                                    JSONObject jo = new JSONObject(
                                            json.optString("data"));
                                    if ("0".equals(jo.optString("is_bind")))
                                    {
                                        ToastUtil.show("请先绑定银行卡");
                                        startActivity(new Intent(
                                                FundsDetailActivity.this,
                                                BankCardManagerActivity.class));
                                    }
                                    else
                                    {
                                        startActivity(new Intent(
                                                FundsDetailActivity.this,
                                                WithdrawActivity.class));
                                    }
                                }
                                else
                                {
                                    ToastUtil.show(
                                            json.getString("description"));
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
                    public void onFinish()
                    {
                        hideProgressDialog();
                        super.onFinish();
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

    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView)
    {
        requestData();
    }

}
