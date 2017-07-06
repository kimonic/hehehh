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
import com.diyou.util.StringUtils;
import com.diyou.util.TextWatcherUtil;
import com.diyou.util.ToastUtil;
import com.diyou.util.Utils;
import com.diyou.v5yibao.R;
import com.diyou.view.LoadingLayout;
import com.diyou.view.PayPasswordDialog;
import com.diyou.view.PayPasswordDialog.PayListener;
import com.diyou.view.ProgressDialogBar;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 立即投标界面
 *
 * @author Administrator
 */
public class BidActivity extends BaseActivity
        implements OnClickListener, TextWatcher, OnRefreshListener<ScrollView> {
    private View title_bar;
    private TextView title_name;
    private RelativeLayout title_back;
    private Button bid_buttton;
    private String loan_id;
    private String money;
    private boolean hasInvestPassword = false;
    private View mInvestPassword;
    private EditText mEtPassword;
    /**
     * 借款标名称,利率,奖励比例,起投金额,还款方式,投资上限,借款期限
     */
    private TextView Loan_target, bid_rate, bid_rate_add, prosecution_amount,
            repayment_method, investment_ceiling, loan_term;
    private ImageView award_imageView;
    private EditText investment_amount;// 投资金额
    private LoadingLayout bid_loadlayout;
    private InvestDetailsInfo info;
    /**
     * 可投金额,账户余额,预期收益,收益包含信息,收益包含奖励
     */
    private TextView amount_of_investment, account_balance, expected_revenue,
            income_includes_interest, earnings_include_rewards;
    private ScrollView layout_ScrollView;
    protected ProgressDialogBar progressDialogBar;
    private double balance_amount;
    private double amount_min;
    private boolean isFirst = true;
    private PayPasswordDialog mPayDialog;
    private String additional_status;
    private TextView income_includes_interest_award;
    private TextView expected_revenue_award;
    private String additional_apr;
    private View rl_bid_rate;
    private View ll_bid_rate;
    private TextView bid_rate1;
    private TextView bid_rate_new;
    private ImageView new_imageView;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_bid_layout);
        loan_id = getIntent().getStringExtra("loan_id");
        additional_status = getIntent().getStringExtra("additional_status");
        additional_apr = getIntent().getStringExtra("additional_apr");
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
        title_name.setText("投标");
        Loan_target = (TextView) findViewById(R.id.Loan_target_name);
        bid_rate = (TextView) findViewById(R.id.bid_rate);
        bid_rate_add = (TextView) findViewById(R.id.bid_rate_add);
        bid_rate1 = (TextView) findViewById(R.id.bid_rate1);
        bid_rate_new = (TextView) findViewById(R.id.bid_rate_new);
        prosecution_amount = (TextView) findViewById(R.id.prosecution_amount);
        repayment_method = (TextView) findViewById(R.id.repayment_method);
        investment_ceiling = (TextView) findViewById(R.id.investment_ceiling);
        loan_term = (TextView) findViewById(R.id.loan_term);
        award_imageView = (ImageView) findViewById(R.id.award_imageView);

        rl_bid_rate = findViewById(R.id.rl_bid_rate);
        ll_bid_rate = findViewById(R.id.ll_bid_rate);
        new_imageView = (ImageView) findViewById(R.id.new_imageView);
        investment_amount = (EditText) findViewById(R.id.investment_amount);
        investment_amount.addTextChangedListener(this);
        amount_of_investment = (TextView) findViewById(
                R.id.amount_of_investment);
        account_balance = (TextView) findViewById(R.id.account_balance);
        expected_revenue = (TextView) findViewById(R.id.expected_revenue);
        income_includes_interest = (TextView) findViewById(
                R.id.income_includes_interest);
        earnings_include_rewards = (TextView) findViewById(
                R.id.earnings_include_rewards);
        income_includes_interest_award = (TextView) findViewById(
                R.id.income_includes_interest_award);
        expected_revenue_award = (TextView) findViewById(
                R.id.expected_revenue_award);

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

        mInvestPassword = findViewById(R.id.rl_investment_password);
        mEtPassword = (EditText) findViewById(R.id.et_investment_password);
        findViewById(R.id.recharge).setOnClickListener(this);
    }

    // 获取商业页面传过来的数据
    private void investData() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("loan_id", loan_id);
        map.put("login_token",
                UserConfig.getInstance().getLoginToken(BidActivity.this));
        HttpUtil.post(UrlConstants.INVESTDATA, map,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        if (isFirst) {
                            bid_loadlayout.setOnLoadingError(BidActivity.this,
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

                                    info.setAdditional_apr(jo.optString("additional_apr"));
                                    info.setAdditional_status(jo.optString("additional_status"));
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
                                    amount_of_investment.setText(
                                            info.getWait_amount() + "元");
                                    if (Double.valueOf(info.getWait_amount())==0) {
                                        bid_buttton.setEnabled(false);
                                        bid_buttton.setText("已满标");
                                    }
                                    account_balance.setText(
                                            StringUtils.getDoubleFormat(info.getBalance_amount()) + "元");
                                    balance_amount = Double
                                            .valueOf(info.getBalance_amount());
                                    if ("-1".equals(info.getAward_status()) && !"1".equals(info.getAdditional_status())) {
                                        ll_bid_rate.setVisibility(View.GONE);
                                        rl_bid_rate.setVisibility(View.VISIBLE);
                                        bid_rate.setText(info.getApr() + "%");
                                    } else {
                                        ll_bid_rate.setVisibility(View.VISIBLE);
                                        rl_bid_rate.setVisibility(View.GONE);
                                        bid_rate1.setText(info.getApr() + "%");
                                        if (!"-1".equals(info.getAward_status())) {
                                            bid_rate_add.setText(
                                                    "+" + info.getAward_proportion()
                                                            + "%");
                                            award_imageView
                                                    .setVisibility(View.VISIBLE);
                                        }
                                        if ("1".equals(info.getAdditional_status())) {
                                            bid_rate_new.setText(
                                                    "+" + info.getAdditional_apr()
                                                            + "%");
                                            new_imageView
                                                    .setVisibility(View.VISIBLE);
                                        }

                                    }
                                    prosecution_amount.setText(
                                            info.getTender_amount_min() + "元");
                                    repayment_method
                                            .setText(info.getRepayTypeName());
                                    if (Double.valueOf(info.getTender_amount_max())==0) {
                                        investment_ceiling.setText("不限");
                                    } else {
                                        investment_ceiling.setText(
                                                info.getTender_amount_max()
                                                        + "元");
                                    }
                                    String time = "";
                                    if (info.getRepay_type() >= 5) {
                                        time = "天";
                                    } else {
                                        time = "个月";
                                    }
                                        loan_term.setText(info.getPeriod()+time);

                                    //可投小于起投时
                                    if (Double.valueOf(info.getWait_amount()) <= Double.valueOf(info.getTender_amount_min())) {
                                        investment_amount.setText(info.getWait_amount());
                                        Utils.setEditTextEnable(investment_amount,false);
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
                            bid_loadlayout.setOnStopLoading(BidActivity.this,
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
                    // getApprove();

                    // 易宝投资
                    requestBid();
                }
                break;
        }

    }

    private void requestBid() {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("loan_id", loan_id);
        map.put("amount", money);
        if (hasInvestPassword) {
            map.put("password", StringUtils.getString(mEtPassword));
        }
        HttpUtil.post(UrlConstants.YIBAO_INVEST, map,
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

                                    Intent intent = new Intent(BidActivity.this,
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
        double input_money = Double.valueOf(money);
        double investMax = Double.valueOf(info.getTender_amount_max());
//        if ((input_money % 10) != 0) {
//            ToastUtil.show("输入金额必须为10的倍数");
//            return false;
//        } else
        if (balance_amount < input_money) {
            ToastUtil.show("账户余额不足");
            return false;
        }
//        else if (input_money < amount_min) {
//            ToastUtil.show("投资金额不能小于起投金额");
//            return false;
//        } else if (investMax!=0 && input_money >investMax ) {
//            ToastUtil.show("投资金额不能大于投资上限");
//            return false;
//        }else if (input_money > Double.valueOf(info.getWait_amount())) {
//            ToastUtil.show("投资金额不能大于可投金额");
//            return false;
//        }
        else if (hasInvestPassword
                && StringUtils.isEmpty2(StringUtils.getString(mEtPassword))) {
            ToastUtil.show("请输入投资密码");
            return false;
        }

        return true;
    }

    /**
     * //托管版本不需要
     */
    private void getApprove() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token",
                UserConfig.getInstance().getLoginToken(BidActivity.this));
        HttpUtil.post(UrlConstants.APPROVE, map, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                ToastUtil.show(R.string.generic_error);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                try {
                    if (CreateCode.AuthentInfo(response)) {
                        JSONObject data = StringUtils.parseContent(response);
                        if (data.optString("result").equals("success")) {
                            JSONObject info = data.getJSONObject("data");
                            if (StringUtils.isEmpty(info.optString("phone"))) {
                                ToastUtil.show("请绑定手机");
                                // 手机未绑定
                                Intent intent = new Intent();
                                intent.putExtra("payPwd",
                                        info.optString("paypassword"));
                                intent.setClass(BidActivity.this,
                                        PhoneBindActivity.class);
                                startActivity(intent);
                            } else if (StringUtils
                                    .isEmpty(info.optString("paypassword"))) {
                                // 未设置支付密码
                                ToastUtil.show("请设置支付密码");
                                startActivity(new Intent(BidActivity.this,
                                        SettingPaymentActivity.class));
                            } else {
                                showPayDialog();
                            }

                        } else {
                            ToastUtil.show(data.optString("description"));
                        }
                    }
                } catch (Exception e) {
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

    private void payment(EditText editText, String password) {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token",
                UserConfig.getInstance().getLoginToken(BidActivity.this));
        map.put("loan_id", loan_id);
        map.put("amount", money);
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
                                    ToastUtil.show("投资成功！");
                                    finish();
                                } else {
                                    if (mPayDialog != null && mPayDialog.isShowing()) {
                                        mPayDialog.dismiss();
                                    }
                                    ToastUtil.show(
                                            data.getString("description"));
                                }
                            } else {
                                ToastUtil.show("投资失败！");
                            }
                        } catch (Exception e) {
                            ToastUtil.show("数据处理异常");
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
        mPayDialog = new PayPasswordDialog(this, new PayListener() {
            @Override
            public void payHandle(EditText editText, String password) {
                payment(editText, password);
            }
        });
        mPayDialog.show();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        money = TextWatcherUtil.getMoneyFormat(s.toString(), investment_amount);
        if (StringUtils.isEmpty2(money)) {
            resetIncome();
            bid_buttton.setEnabled(false);
        } else {
            investInterest(money, info.getPeriod(), info.getApr(),
                    info.getRepay_type(), info.getAward_proportion());
            if (Double.valueOf(info.getWait_amount()) >= Double
                    .valueOf(s.toString())) {
                bid_buttton.setEnabled(true);
            }
        }
    }

    private void resetIncome() {
        expected_revenue.setText("");
        income_includes_interest
                .setText("");
        earnings_include_rewards
                .setText("");
    }

    private void investInterest(final String money, String Period, String Apr,
                                int repay_type, String Award_proportion) {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("amount", money);
        map.put("period", Period);
        map.put("apr", Apr);
        map.put("repay_type", ""+repay_type);
        map.put("award_scale", Award_proportion);
        map.put("additional_status", additional_status);
        if (!StringUtils.isEmpty(additional_apr) && Double.valueOf(additional_apr)>0) {
            map.put("additional_apr", additional_apr);
        }
        HttpUtil.post(UrlConstants.INVESTINTEREST, map,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        ToastUtil.show("访问失败！");
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
                                        resetIncome();
                                    } else {
                                        JSONObject jo = data
                                                .optJSONObject("data");
                                        String award = "";
                                        if (Double.valueOf(jo.optString("additional_amount")) > 0) {
                                            award = "+" + jo.optString("additional_amount") + "元";
                                        }
                                        expected_revenue.setText(jo.optString("interest_total"));
                                        expected_revenue_award.setText(award);
                                        income_includes_interest.setText(StringUtils.getDoubleFormat(jo.optString("interest_award"))
                                                + "元");
                                        income_includes_interest_award.setText(award);
                                        earnings_include_rewards.setText(StringUtils.getDoubleFormat(jo.optString("award_amount"))
                                                + "元");
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

    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
        investData();

    }

}
