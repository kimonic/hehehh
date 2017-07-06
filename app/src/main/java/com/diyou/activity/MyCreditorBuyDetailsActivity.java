package com.diyou.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.diyou.adapter.RefundDetailAdapter;
import com.diyou.base.BaseActivity;
import com.diyou.bean.RefundDetailItem;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.pulltorefresh.PullToRefreshBase;
import com.diyou.pulltorefresh.PullToRefreshBase.Mode;
import com.diyou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.diyou.pulltorefresh.PullToRefreshListView;
import com.diyou.util.DateUtils;
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
import android.widget.ListView;
import android.widget.TextView;

/**
 * 我的项目-》我的债权-》购买记录—》债权购买详情
 *
 * @author Administrator
 */
public class MyCreditorBuyDetailsActivity extends BaseActivity
        implements OnClickListener, OnRefreshListener2<ListView> {
    private String transfer_id;
    private boolean isFirst = true;
    private LoadingLayout creditor_loadlayout;
    private String buy_repay_status;
    private PullToRefreshListView mListView;
    private View mContentView;
    private int mPage = 1;
    private TextView mTvCreditorName;
    private TextView mTvCreditorStatus;
    private TextView mTvCreditorValue;
    private TextView mTvCreditorTime;
    private TextView mTvCreditorPeriod;
    private TextView mTvCreditorAmount;
    private TextView mTvCreditorIncome;
    private RefundDetailAdapter mAdapter;
    private List<RefundDetailItem> mList = new ArrayList<RefundDetailItem>();
    private String mTransferId;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_creditor_buy_details);
        initActionBar();
        transfer_id = getIntent().getStringExtra("transfer_id");
        buy_repay_status = getIntent().getStringExtra("buy_repay_status");
        intiView();
        requestPageData();
    }

    private void requestPageData() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance()
                .getLoginToken(this));
        map.put("transfer_id", transfer_id);
        map.put("page", "" + mPage);
//        map.put("epage", "10");
        HttpUtil.post(UrlConstants.CREDITER_BUY_DETAILS, map,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        if (isFirst) {
                            creditor_loadlayout.setOnLoadingError(
                                    MyCreditorBuyDetailsActivity.this,
                                    mContentView);
                        }
                        // layout_ScrollView.onRefreshComplete();
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        try {
                            if (CreateCode.AuthentInfo(response)) {
                                JSONObject data = StringUtils
                                        .parseContent(response);
                                if (data.get("result").equals("success")) {
                                    JSONObject jo = data.optJSONObject("data");
                                    mTvCreditorName.setText(jo.optString("loan_name"));
                                    mTvCreditorValue.setText(jo.optString("amount_money") + getString(R.string.common_display_yuan_default));
                                    if(!StringUtils.isEmpty(jo.optString("expire_time")))
                                    mTvCreditorTime.setText(DateUtils.getDateTimeFormat(jo.optString("expire_time")));
                                    mTvCreditorPeriod.setText(jo.optString("period") + getString(R.string.common_peroid) + "/" +
                                            getString(R.string.common_peroid_sum) + jo.optString("total_period") + getString(R.string.common_peroid));
                                    mTvCreditorAmount.setText(jo.optString("amount"));
                                    mTvCreditorIncome.setText(jo.optString("income") + getString(R.string.common_display_yuan_default));
                                    mTransferId =jo.optString("transfer_id");
                                    JSONObject recoverJo = jo.optJSONObject("recover");
                                    JSONArray ja = recoverJo.optJSONArray("items");
                                    if (mPage > recoverJo.optInt("total_pages")) {
                                        ToastUtil.show(R.string.common_no_more_record);
                                    } else {
                                        for (int i = 0; i < ja.length(); i++) {
                                            JSONObject pJo = ja
                                                    .optJSONObject(i);
                                            mList.add(new RefundDetailItem(pJo.optInt("period_no"), pJo.optInt("period"),
                                                    pJo.optString("amount"), pJo.optString("principal_yes"), pJo.optString("interest_yes"),
                                                    pJo.optString("recover_time"), pJo.optString("status_name"), pJo.optInt("status")));
                                        }
                                        mPage++;
                                        mAdapter.refreshData();
                                    }

                                } else {
                                    ToastUtil.show(
                                            data.optString("description"));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (isFirst) {
                            creditor_loadlayout.setOnStopLoading(
                                    MyCreditorBuyDetailsActivity.this,
                                    mContentView);
                            isFirst = false;
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish() {
                        mListView.onRefreshComplete();
                        super.onFinish();
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                });


    }

    private void updateData() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance()
                .getLoginToken(this));
        map.put("transfer_id", transfer_id);
        map.put("page", "1");
//        map.put("epage", "10");
        HttpUtil.post(UrlConstants.CREDITER_BUY_DETAILS, map,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        ToastUtil.show(R.string.generic_error);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        try {
                            if (CreateCode.AuthentInfo(response)) {
                                JSONObject data = StringUtils
                                        .parseContent(response);
                                if (data.get("result").equals("success")) {
                                    JSONObject jo = data.optJSONObject("data");
                                    mTvCreditorName.setText(jo.optString("loan_name"));
                                    mTvCreditorValue.setText(jo.optString("amount_money") + getString(R.string.common_display_yuan_default));
                                    if(!StringUtils.isEmpty(jo.optString("expire_time")))
                                    mTvCreditorTime.setText(DateUtils.getDateTimeFormat(jo.optString("expire_time")));
                                    mTvCreditorPeriod.setText(jo.optString("period") + getString(R.string.common_peroid) + "/" +
                                            getString(R.string.common_peroid_sum) + jo.optString("total_period") + getString(R.string.common_peroid));
                                    mTvCreditorAmount.setText(jo.optString("amount"));
                                    mTvCreditorIncome.setText(jo.optString("income") + getString(R.string.common_display_yuan_default));

                                    JSONObject recoverJo = jo.optJSONObject("recover");
                                    JSONArray ja = recoverJo.optJSONArray("items");
                                    mList.clear();
                                    if (1 > recoverJo.optInt("total_pages")) {
                                        ToastUtil.show(R.string.common_no_more_record);
                                    } else {
                                        for (int i = 0; i < ja.length(); i++) {
                                            JSONObject pJo = ja
                                                    .optJSONObject(i);
                                            mList.add(new RefundDetailItem(pJo.optInt("period_no"), pJo.optInt("period"),
                                                    pJo.optString("amount"), pJo.optString("principal_yes"), pJo.optString("interest_yes"),
                                                    pJo.optString("recover_time"), pJo.optString("status_name"), pJo.optInt("status")));
                                        }
                                        mPage = 2;
                                        mAdapter.refreshData();
                                    }

                                } else {
                                    ToastUtil.show(
                                            data.optString("description"));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish() {
                        mListView.onRefreshComplete();
                        super.onFinish();
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                });


    }

    private void initActionBar() {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.creditor_buy_detail_title);
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    private void intiView() {
        mContentView = findViewById(R.id.ll_my_creditor_buy_details);
        mListView = (PullToRefreshListView) findViewById(R.id.lv_my_creditor_buy);
        mListView.setOnRefreshListener(this);
        mListView.setMode(Mode.BOTH);
        mAdapter = new RefundDetailAdapter(this, mList);
        mListView.setAdapter(mAdapter);
        creditor_loadlayout = (LoadingLayout) findViewById(
                R.id.creditor_loadlayout);
        creditor_loadlayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        requestPageData();// 获取数据
                    }
                });
        creditor_loadlayout.setOnStartLoading(mContentView);

        mTvCreditorName = (TextView) findViewById(R.id.tv_buy_creditor_name);
        mTvCreditorStatus = (TextView) findViewById(R.id.tv_buy_status);
        mTvCreditorStatus.setText(buy_repay_status);
        mTvCreditorValue = (TextView) findViewById(R.id.creditor_value);
        mTvCreditorTime = (TextView) findViewById(R.id.repayment_period);
        mTvCreditorPeriod = (TextView) findViewById(R.id.total_period);
        mTvCreditorAmount = (TextView) findViewById(R.id.tv_buy_amount);
        mTvCreditorIncome = (TextView) findViewById(R.id.anticipated_income);

        //查看债权转让协议
        findViewById(R.id.tv_agreement_transfer).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.tv_agreement_transfer:
                Intent intent = new Intent(this, AdvertisingActivity.class);
                intent.putExtra("action", "creditorAgreement");
                intent.putExtra("url", UrlConstants.AGREEMENT_CREDITOR_TRANSFER + mTransferId);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        updateData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        requestPageData();
    }

}
