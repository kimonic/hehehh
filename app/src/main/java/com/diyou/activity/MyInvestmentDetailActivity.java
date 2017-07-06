package com.diyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.diyou.adapter.RefundDetailAdapter;
import com.diyou.base.BaseActivity;
import com.diyou.bean.RefundDetailItem;
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
import com.diyou.view.ScrollableListView;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * 我的投资详情
 *
 * @author Administrator
 */
public class MyInvestmentDetailActivity extends BaseActivity
        implements OnClickListener, OnRefreshListener<ScrollView> {

    private LoadingLayout mLoadinglayout;
    private String mId;
    private RelativeLayout transfer_record_relayout;
    private RelativeLayout Purchase_records_layout;
    private TextView success_transfer_amount;
    private TextView creditor_transfer;
    private TextView successful_import_amount;
    private TextView creditor_import;
    private ScrollableListView purchase_records_listview;
    private View mBorrowDetailView;
    private boolean isFirst = true;
    private RefundDetailAdapter mAdapter;
    private PullToRefreshScrollView mScrollView;
    private int ColoeWhite;
    private int ColorGray;
    private TextView transfer_record_textView;
    private TextView Purchase_records_textView;
    private TextView invest_title;
    private TextView invest_status;
    private TextView invest_money;
    private TextView invest_sum_income;
    private TextView invest_already_income;
    private TextView invest_wait_capital;
    private TextView invest_award_money;
    private String mStrYuan;
    private List<RefundDetailItem> mList = new ArrayList<RefundDetailItem>();
    private String status_name;
    private View mViewProtocol;
    private String mLoanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_investment_detail);
        ColoeWhite = getResources().getColor(R.color.white);
        ColorGray = getResources().getColor(R.color.dark_gray);
        mStrYuan = getString(R.string.common_display_yuan_default);
        initActionBar();
        mId = getIntent().getStringExtra("id");
        status_name = getIntent().getStringExtra("status_name");
        initView();
        requestData();
    }

    private void requestData() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("id", mId);
        HttpUtil.post(UrlConstants.MY_INVESTMENT_DETAIL, map,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        try {
                            if (CreateCode.AuthentInfo(response)) {
                                JSONObject json = StringUtils
                                        .parseContent(response);
                                if (json.optString("result")
                                        .contains("success")) {
                                    JSONObject data = new JSONObject(
                                            json.optString("data"));

                                    JSONObject loanInfo = data.optJSONObject("loan_info");
                                    JSONObject tenderInfo = data.optJSONObject("tender_info");

                                    mLoanId = tenderInfo.optString("loan_id");
                                    int loanInfoStatus = loanInfo.optInt("status");
                                    String category_type = loanInfo.optString("category_type");
                                    if ((-1 == loanInfoStatus || -2 == loanInfoStatus || -3 == loanInfoStatus || -4 == loanInfoStatus || -5 == loanInfoStatus || -6 == loanInfoStatus || -7 == loanInfoStatus || loanInfoStatus == 4 ||
                                            (!"3".equals(category_type)&&loanInfoStatus == 3))) {
                                        mViewProtocol.setVisibility(View.GONE);
                                    }
                                    //投资详情
                                    invest_title.setText(loanInfo.optString("name"));
                                    invest_status.setText(status_name);
                                    success_transfer_amount.setText(loanInfo.optString("serialno"));
                                    creditor_transfer.setText(loanInfo.optString("apr") + "%");
                                    successful_import_amount.setText(loanInfo.optString("repay_type_name"));
                                    if ("5".equals(loanInfo.optString("repay_type"))) {//按天计息
                                        creditor_import.setText(loanInfo.optString("period") + getString(R.string.common_day));
                                    } else {
                                        creditor_import.setText(loanInfo.optString("period") + getString(R.string.common_month));
                                    }
                                    //借款详情
                                    invest_money.setText(StringUtils.getDoubleFormat(tenderInfo.optString("amount")) + mStrYuan);
                                    invest_sum_income.setText(StringUtils.getDoubleFormat(tenderInfo.optString("recover_income_all")) + mStrYuan);
                                    invest_already_income.setText(StringUtils.getDoubleFormat(tenderInfo.optString("recover_income")) + mStrYuan);
                                    invest_wait_capital.setText(StringUtils.getDoubleFormat(tenderInfo.optString("wait_principal")) + mStrYuan);
                                    invest_award_money.setText(StringUtils.getDoubleFormat(tenderInfo.optString("award_amount")) + mStrYuan);
                                    //还款详情
                                    mList.clear();
                                    JSONArray recoverInfo = data.optJSONArray("recover_info");
                                    for (int i = 0; i < recoverInfo.length(); i++) {
                                        JSONObject jo = recoverInfo.optJSONObject(i);
                                        mList.add(new RefundDetailItem(jo.optInt("period_no"), jo.optInt("period"),
                                                jo.optString("amount"), jo.optString("principal_yes"),
                                                jo.optString("interest_yes"), jo.optString("recover_time"),
                                                jo.optString("status_name"), jo.optInt("status")));
                                    }
                                    mAdapter.refreshData();

                                    if (isFirst) {
                                        isFirst = false;
                                        mLoadinglayout.setOnStopLoading(
                                                MyInvestmentDetailActivity.this,
                                                mScrollView);
                                    }
                                } else {
                                    ToastUtil.show(
                                            json.optString("description"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish() {
                        mScrollView.onRefreshComplete();
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        if (isFirst) {
                            mLoadinglayout.setOnLoadingError(
                                    MyInvestmentDetailActivity.this, mScrollView);
                        }
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });

    }

    private void initActionBar() {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.invest_detail_title);
        findViewById(R.id.title_back).setOnClickListener(this);

    }

    private void initView() {
        mLoadinglayout = (LoadingLayout) findViewById(
                R.id.my_investment_detail_loadlayout);
        mScrollView = (PullToRefreshScrollView) findViewById(R.id.sc_my_investment_detail);
        mScrollView.setOnRefreshListener(this);
        mBorrowDetailView = findViewById(R.id.ll_invest_borrow_detail);
        purchase_records_listview = (ScrollableListView) findViewById(R.id.purchase_records_listview);
        mAdapter = new RefundDetailAdapter(this, mList);
        purchase_records_listview.setAdapter(mAdapter);

        transfer_record_relayout = (RelativeLayout) findViewById(R.id.transfer_record_relayout);
        transfer_record_relayout
                .setBackgroundResource(R.drawable.shape_blue_btn_left_press);
        transfer_record_relayout.setOnClickListener(this);
        Purchase_records_layout = (RelativeLayout) findViewById(R.id.Purchase_records_layout);
        Purchase_records_layout.setOnClickListener(this);

        transfer_record_textView = (TextView) findViewById(R.id.transfer_record_textView);
        Purchase_records_textView = (TextView) findViewById(R.id.Purchase_records_textView);
        success_transfer_amount = (TextView) findViewById(R.id.success_transfer_amount);
        creditor_transfer = (TextView) findViewById(R.id.creditor_transfer);
        successful_import_amount = (TextView) findViewById(R.id.successful_import_amount);
        creditor_import = (TextView) findViewById(R.id.creditor_import);
        invest_title = (TextView) findViewById(R.id.tv_invest_detail_title);
        invest_status = (TextView) findViewById(R.id.tv_invest_detail_status);
        //借款详情
        invest_money = (TextView) findViewById(R.id.tv_invest_money);
        invest_sum_income = (TextView) findViewById(R.id.tv_invest_sum_income);
        invest_already_income = (TextView) findViewById(R.id.tv_invest_already_income);
        invest_wait_capital = (TextView) findViewById(R.id.tv_invest_wait_capital);
        invest_award_money = (TextView) findViewById(R.id.tv_invest_award_money);

        mLoadinglayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        requestData();

                    }
                });
        mLoadinglayout.setOnStartLoading(mScrollView);

        //查看借款协议
        findViewById(R.id.tv_agreement_borrow).setOnClickListener(this);
        mViewProtocol = findViewById(R.id.rl_protocolView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.transfer_record_relayout:
                transfer_record_relayout.setBackgroundResource(
                        R.drawable.shape_blue_btn_left_press);
                Purchase_records_layout.setBackgroundResource(R.color.transparent);
                mBorrowDetailView.setVisibility(View.VISIBLE);
                purchase_records_listview.setVisibility(View.GONE);
                transfer_record_textView.setTextColor(ColoeWhite);
                Purchase_records_textView.setTextColor(ColorGray);
                break;

            case R.id.Purchase_records_layout:
                Purchase_records_layout.setBackgroundResource(
                        R.drawable.shape_blue_btn_right_press);
                transfer_record_relayout.setBackgroundResource(R.color.transparent);
                mBorrowDetailView.setVisibility(View.GONE);
                purchase_records_listview.setVisibility(View.VISIBLE);
                transfer_record_textView.setTextColor(ColorGray);
                Purchase_records_textView.setTextColor(ColoeWhite);
                break;
            case R.id.tv_agreement_borrow:
                Intent intent = new Intent(this, AdvertisingActivity.class);
                intent.putExtra("action", "borrowAgreement");
                intent.putExtra("url", UrlConstants.AGREEMENT_BORROW +mLoanId+"&tenderId="+mId);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
        requestData();
    }

}
