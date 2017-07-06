package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.bean.InvestDetailsInfo;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.pulltorefresh.PullToRefreshBase;
import com.diyou.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.diyou.util.DoubleUtils;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.util.Utils;
import com.diyou.v5yibao.R;
import com.diyou.view.AddAndSubView;
import com.diyou.view.LoadingLayout;
import com.diyou.view.PayPasswordDialog;
import com.diyou.view.PayPasswordDialog.PayListener;
import com.diyou.view.ProgressDialogBar;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 流转标投标界面
 *
 * @author Administrator
 */
public class FlowBidActivity extends BaseActivity
        implements OnClickListener, OnRefreshListener<ScrollView>, AddAndSubView.OnNumChangeListener {
    private View title_bar;
    private TextView title_name;
    private RelativeLayout title_back;
    private Button bid_buttton;
    private String loan_id;
    private double mMoney;
    private boolean hasInvestPassword = false;
    private View mInvestPassword;
    private EditText mEtPassword;
    /**
     * 借款标名称,利率,奖励比例,起投金额,还款方式,投资上限,借款期限
     */
    private TextView Loan_target, bid_rate, bid_rate_add, prosecution_amount,
            repayment_method, investment_ceiling, loan_term;
    private ImageView award_imageView;
    private LoadingLayout bid_loadlayout;
    private InvestDetailsInfo info;
    /**
     * 可投金额,账户余额,预期收益,收益包含信息,收益包含奖励
     */
    private TextView account_balance, expected_revenue,
            income_includes_interest, earnings_include_rewards;
    private ScrollView layout_ScrollView;
    protected ProgressDialogBar progressDialogBar;
    private double balance_amount;
    private double amount_min;
    private boolean isFirst = true;
    private PayPasswordDialog mPayDialog;
    private String mStrYuan;
    private AddAndSubView addAndSubView;
    private String additional_status;
    private String additional_apr;
    private int flowNumber = 1;//流转标份数

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_flow_bid_layout);
        loan_id = getIntent().getStringExtra("loan_id");
        additional_status = getIntent().getStringExtra("additional_status");
        additional_apr = getIntent().getStringExtra("additional_apr");
        mStrYuan = getString(R.string.common_display_yuan_default);
        intiView();
        investData();// 获取数据
    }

    private void intiView() {

        title_bar = findViewById(R.id.bid_title_bar);
        title_name = (TextView) title_bar.findViewById(R.id.title_name);
        title_back = (RelativeLayout) title_bar
                .findViewById(R.id.title_bar_back);
        bid_buttton = (Button) findViewById(R.id.bid_investment_buttton);
        bid_buttton.setEnabled(false);
        title_name.setText(R.string.bid_title);
        Loan_target = (TextView) findViewById(R.id.Loan_target_name);
        bid_rate = (TextView) findViewById(R.id.bid_rate);
        bid_rate_add = (TextView) findViewById(R.id.bid_rate_add);
        prosecution_amount = (TextView) findViewById(R.id.prosecution_amount);
        repayment_method = (TextView) findViewById(R.id.repayment_method);
        investment_ceiling = (TextView) findViewById(R.id.investment_ceiling);
        loan_term = (TextView) findViewById(R.id.loan_term);
        award_imageView = (ImageView) findViewById(R.id.award_imageView);

        account_balance = (TextView) findViewById(R.id.account_balance);
        expected_revenue = (TextView) findViewById(R.id.expected_revenue);
        income_includes_interest = (TextView) findViewById(
                R.id.income_includes_interest);
        earnings_include_rewards = (TextView) findViewById(
                R.id.earnings_include_rewards);
        layout_ScrollView = (ScrollView) findViewById(R.id.layout_ScrollView);
        // layout_ScrollView.setOnRefreshListener(this);

        bid_loadlayout = (LoadingLayout) findViewById(R.id.bid_loadlayout);
        bid_loadlayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        investData();// 获取数据
                    }
                });

        title_back.setOnClickListener(this);
        bid_buttton.setOnClickListener(this);
        findViewById(R.id.recharge).setOnClickListener(this);

        mInvestPassword = findViewById(R.id.rl_investment_password);
        mEtPassword = (EditText) findViewById(R.id.et_investment_password);
        //投资份数
        LinearLayout container = (LinearLayout) findViewById(R.id.ll_flow_bid_num);
        addAndSubView = new AddAndSubView(this,1);
        addAndSubView.setEditTextBgResource(R.drawable.investment_fragment_item_bg);
        addAndSubView.setOnNumChangeListener(this);
        addAndSubView.setButtonBgResource(R.drawable.add_num_selector_bg, R.drawable.sub_num_selector_bg);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        container.addView(addAndSubView, layoutParams);
    }

    // 获取商业页面传过来的数据
    private void investData() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("loan_id", loan_id);
        map.put("login_token",
                UserConfig.getInstance().getLoginToken(FlowBidActivity.this));
        HttpUtil.post(UrlConstants.INVESTDATA, map,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        if (isFirst) {
                            bid_loadlayout.setOnLoadingError(FlowBidActivity.this,
                                    layout_ScrollView);
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
                                info = new InvestDetailsInfo();
                                JSONObject data = StringUtils
                                        .parseContent(response);
                                if (data.get("result").equals("success")) {
                                    JSONObject jo = data.optJSONObject("data");
                                    info.setName(jo.optString("name"));
                                    info.setApr(jo.optString("apr"));
                                    info.setAward_status(
                                            jo.optString("award_status"));
                                    info.setAward_proportion(
                                            jo.optString("award_proportion"));
                                    info.setRepay_type(
                                            jo.optInt("repay_type"));
                                    info.setRepayTypeName(
                                            jo.optString("repay_type_name"));
                                    info.setTender_amount_max(
                                            jo.optString("tender_amount_max"));
                                    info.setTender_amount_min(
                                            jo.optString("tender_amount_min"));
                                    amount_min = Double.valueOf(
                                            info.getTender_amount_min());
                                    info.setPeriod(jo.optString("period"));
                                    info.setPeriod_name(
                                            jo.optString("period_name"));
                                    info.setBalance_amount(jo
                                            .getJSONObject("member")
                                            .optString("balance_amount"));
                                    info.setWait_amount(
                                            jo.optString("wait_amount"));

                                    // 判断是否有投资密码，有的话显示密码输入框
                                    if ("yes".equalsIgnoreCase(
                                            jo.optString("is_password"))) {
                                        hasInvestPassword = true;
                                        mInvestPassword
                                                .setVisibility(View.VISIBLE);
                                    } else {
                                        hasInvestPassword = false;
                                        mInvestPassword
                                                .setVisibility(View.GONE);
                                    }

                                    // layout_ScrollView.onRefreshComplete();
                                    Loan_target.setText(info.getName());
                                    bid_rate.setText(info.getApr() + "%");
                                    if (info.getWait_amount().equals("0")) {
                                        bid_buttton.setEnabled(false);
                                        bid_buttton.setText(R.string.bid_status_full);
                                    }
                                    account_balance.setText(
                                            StringUtils.getDoubleFormat(info.getBalance_amount()) + mStrYuan);
                                    balance_amount = Double
                                            .valueOf(info.getBalance_amount());
                                    if (!info.getAward_status().equals("-1")) {
                                        bid_rate_add.setText(
                                                "+" + info.getAward_proportion()
                                                        + "%");
                                        award_imageView
                                                .setVisibility(View.VISIBLE);
                                    }

                                    repayment_method.setText(info.getRepayTypeName());
                                    String time = "";
                                    if (info.getRepay_type() >= 5) {
                                        time = "天";
                                    } else {
                                        time = "个月";
                                    }
                                    loan_term.setText(info.getPeriod() + time);

                                    //流转标
                                    JSONObject roam_info = jo.optJSONObject("roam_info");
                                    info.setAmount(roam_info.optString("amount"));
                                    info.setPortion_total(roam_info.optInt("portion_total"));
                                    info.setPortion_yes(roam_info.optInt("portion_yes"));
                                    int maxNum = roam_info.optInt("portion_total") - roam_info.optInt("portion_yes");
                                    //最小流标
                                    prosecution_amount.setText(roam_info.optString("amount") + mStrYuan);
                                    //限投份数
                                    investment_ceiling.setText(maxNum + "份");
                                    addAndSubView.setMaxNum(maxNum);
                                    investInterest(info.getAmount());
                                } else {
                                    ToastUtil.show(
                                            data.optString("description"));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (isFirst) {
                            bid_loadlayout.setOnStopLoading(FlowBidActivity.this,
                                    layout_ScrollView);
                            isFirst = false;
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish() {
                        // layout_ScrollView.onRefreshComplete();
                        super.onFinish();
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_bar_back:
                finish();
                break;
            case R.id.recharge:
                startActivity(new Intent(this, RechargeActivity.class));
                break;

            case R.id.bid_investment_buttton:
                if (checkData()) {
                    // 易宝投资
                    requestBid();
                }
                break;
        }

    }

    private void requestBid() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("id", loan_id);
        map.put("amount", String.valueOf(mMoney));
        map.put("number", String.valueOf(flowNumber));
        if (hasInvestPassword) {
            map.put("password", StringUtils.getString(mEtPassword));
        }
        HttpUtil.post(UrlConstants.YIBAO_FLOW_INVEST, map,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        showProgressDialog();
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        try {
                            if (CreateCode.AuthentInfo(response)) {
                                JSONObject json = StringUtils
                                        .parseContent(response);
                                if (json.optString("result")
                                        .contains("success")) {

                                    JSONObject jo = new JSONObject(
                                            json.optString("data"));
                                    String url = jo.optString("submit_url");
                                    String body = Utils.handleJson(jo);

                                    Intent intent = new Intent(FlowBidActivity.this,
                                            YiBaoInvestActivity.class);
                                    intent.putExtra("url", url);
                                    intent.putExtra("body", body);
                                    startActivity(intent);
                                    finish();
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
                        hideProgressDialog();
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        ToastUtil.show(R.string.generic_error);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });

    }

    private boolean checkData() {
//        if ((mMoney % 10) != 0)
//        {
//            ToastUtil.show(R.string.remind_input_money_ten_multiple);
//            return false;
//        }
//        else
        if (balance_amount < mMoney) {
            ToastUtil.show(R.string.remind_account_no_money);
            return false;
        }
//        else if (mMoney < amount_min) {
//            ToastUtil.show(R.string.remind_bid_less_than_base);
//            return false;
//        }
        else if (hasInvestPassword && StringUtils.isEmpty2(StringUtils.getString(mEtPassword))) {
            ToastUtil.show(R.string.remind_input_bid_password);
            return false;
        }

        return true;
    }


    private void payment(EditText editText, String password) {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token",
                UserConfig.getInstance().getLoginToken(FlowBidActivity.this));
        map.put("loan_id", loan_id);
        map.put("amount", String.valueOf(mMoney));
        map.put("paypassword", password);
        if (hasInvestPassword) {
            map.put("password", StringUtils.getString(mEtPassword));
        }
        HttpUtil.post(UrlConstants.INVESTMENT_TENDER, map,
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
                                if (data.optString("result").equals("success")) {
                                    ToastUtil.show(R.string.bid_succeed);
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    if (mPayDialog != null && mPayDialog.isShowing()) {
                                        mPayDialog.dismiss();
                                    }
                                    ToastUtil.show(data.getString("description"));
                                }
                            } else {
                                ToastUtil.show(R.string.bid_fail);
                            }
                        } catch (Exception e) {
                            ToastUtil.show(R.string.remind_data_parse_exception);
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish() {
                        hideProgressDialog();
                        super.onFinish();
                    }

                    @Override
                    public void onStart() {
                        showProgressDialog();
                        super.onStart();
                    }

                });

    }

    private void showPayDialog() {
        if (mPayDialog == null) {
            mPayDialog = new PayPasswordDialog(this, new PayListener() {
                @Override
                public void payHandle(EditText editText, String password) {
                    payment(editText, password);
                }
            });
        }
        mPayDialog.show();
    }

//    @Override
//    public void afterTextChanged(Editable s)
//    {
//
//    }
//
//    @Override
//    public void beforeTextChanged(CharSequence s, int start, int count,
//            int after)
//    {
//
//    }
//
//    @Override
//    public void onTextChanged(CharSequence s, int start, int before, int count)
//    {
//        money = s.toString();
//        if (StringUtils.isEmpty2(money))
//        {
//            bid_buttton.setEnabled(false);
//        }
//        else
//        {
//            requestInterest(money, info.getPeriod(), info.getApr(),
//                    info.getRepay_type(), info.getAward_proportion());
//            if (Double.valueOf(info.getWait_amount()) > Double
//                    .valueOf(s.toString()))
//            {
//                bid_buttton.setEnabled(true);
//            }
//        }
//    }

    private void investInterest(String money) {
        if (StringUtils.isEmpty2(money) || Double.valueOf(money) <= 0) {
            setInitStatus();
            bid_buttton.setEnabled(false);
        } else {
            requestInterest(money, info.getPeriod(), info.getApr(),
                    info.getRepay_type(), info.getAward_proportion());
            if (Double.valueOf(info.getWait_amount()) >= Double
                    .valueOf(money)) {
                bid_buttton.setEnabled(true);
            }
        }
    }

    private void requestInterest(final String money, String Period, String Apr,
                                 int repay_type, String Award_proportion) {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("amount", money);
        map.put("period", Period);
        map.put("apr", Apr);
        map.put("repay_type", "" + repay_type);
        map.put("award_scale", Award_proportion);
        map.put("additional_status", additional_status);
        if (!StringUtils.isEmpty(additional_apr)) {
            map.put("additional_apr", additional_apr);
        }
        HttpUtil.post(UrlConstants.INVESTINTEREST, map,
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
                                if (data.optString("result").equals("success")) {
                                    if (money.equals("")) {
                                        setInitStatus();
                                    } else {
                                        JSONObject jo = data
                                                .optJSONObject("data");
                                        income_includes_interest.setText(jo.optString("interest_award"));
                                        expected_revenue.setText(StringUtils.getDoubleFormat(jo.optString("interest_total"))
                                                + mStrYuan);
                                        earnings_include_rewards.setText(StringUtils.getDoubleFormat(jo.optString("award_amount"))
                                                + mStrYuan);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                });

    }

    private void setInitStatus() {
        expected_revenue.setText("0.00");
        income_includes_interest
                .setText("0.00" + mStrYuan);
        earnings_include_rewards
                .setText("0.00" + mStrYuan);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
        investData();

    }

    @Override
    public void onNumChange(View view, int num) {
        if (!StringUtils.isEmpty(info.getAmount())) {
            mMoney = Double.valueOf(info.getAmount()) * num;
            flowNumber = num;
            investInterest(String.valueOf(mMoney));
        }
    }
}
