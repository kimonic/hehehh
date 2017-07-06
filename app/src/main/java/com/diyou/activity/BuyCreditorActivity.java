package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONObject;

import com.diyou.application.MyApplication;
import com.diyou.base.BaseActivity;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.pulltorefresh.PullToRefreshBase;
import com.diyou.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.LoadingLayout;
import com.diyou.view.ProgressDialogBar;
import com.diyou.view.ViewDialogUtils;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 购买债权
 *
 * @author Administrator
 *
 */
public class BuyCreditorActivity extends BaseActivity
        implements OnClickListener, OnRefreshListener<ScrollView>
{
    private View title_bar;
    private TextView title_name;
    private RelativeLayout title_back;
    private Button bid_buttton;
    private ViewDialogUtils dialog;
    private String loan_id;
    private LoadingLayout bid_loadlayout;
    private ScrollView layout_ScrollView;
    protected ProgressDialogBar progressDialogBar;
    private double Balance_amount;
    private double amount_min;
    private boolean isFirst = true;
    private TextView creditorrightsdetailsActivity_name;
    private TextView creditorrightsdetailsActivity_repayment_method;
    private TextView creditorrightsdetailsActivity_transfer_time;
    private TextView creditorrightsdetailsActivity_loan_term;
    private TextView creditorrightsdetailsActivity_repayment_period;
    private TextView creditorrightsdetailsActivity_amount_editView;
    private TextView account_balance;
    private TextView expected_revenue;
    private String transfer_id;// 转让id

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.activity_buy_creditor_layout);
        transfer_id = getIntent().getStringExtra("transfer_id");
        loan_id = getIntent().getStringExtra("loan_id");
        intiView();
        investData();// 获取数据
    }

    private void intiView()
    {
        dialog = new ViewDialogUtils(this, R.style.MyDialog);

        title_bar = findViewById(R.id.bid_title_bar);
        title_name = (TextView) title_bar.findViewById(R.id.title_name);
        title_back = (RelativeLayout) title_bar
                .findViewById(R.id.title_bar_back);
        bid_buttton = (Button) findViewById(
                R.id.creditorrightsdetailsActivity_submit);
        bid_buttton.setOnClickListener(this);
        bid_buttton.setEnabled(false);
        title_name.setText("购买债权");

        creditorrightsdetailsActivity_name = (TextView) findViewById(
                R.id.creditorrightsdetailsActivity_name);
        // 还款方式
        creditorrightsdetailsActivity_repayment_method = (TextView) findViewById(
                R.id.creditorrightsdetailsActivity_repayment_method);
        // 转让期数
        creditorrightsdetailsActivity_transfer_time = (TextView) findViewById(
                R.id.creditorrightsdetailsActivity_transfer_time);
        // 借款期限
        creditorrightsdetailsActivity_loan_term = (TextView) findViewById(
                R.id.creditorrightsdetailsActivity_loan_term);
        // 还款期限
        creditorrightsdetailsActivity_repayment_period = (TextView) findViewById(
                R.id.creditorrightsdetailsActivity_repayment_period);
        // 转让价格
        creditorrightsdetailsActivity_amount_editView = (TextView) findViewById(
                R.id.creditorrightsdetailsActivity_amount_editView);
        // 账户余额
        account_balance = (TextView) findViewById(R.id.account_balance);
        // 充值
        findViewById(R.id.recharge).setOnClickListener(this);
        // 预期收益
        expected_revenue = (TextView) findViewById(R.id.expected_revenue);

        layout_ScrollView = (ScrollView) findViewById(R.id.layout_ScrollView);
        bid_loadlayout = (LoadingLayout) findViewById(R.id.bid_loadlayout);
        bid_loadlayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        investData();// 获取数据
                    }
                });

        title_back.setOnClickListener(this);
    }

    private void investData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("transfer_id", transfer_id);
        map.put("login_token", UserConfig.getInstance()
                .getLoginToken(BuyCreditorActivity.this));
        HttpUtil.post(UrlConstants.TransferBuyInfo, map,
                new JsonHttpResponseHandler()
                {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        if (isFirst)
                        {
                            bid_loadlayout.setOnLoadingError(
                                    BuyCreditorActivity.this,
                                    layout_ScrollView);
                        }
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                            JSONObject response)
                    {
                        try
                        {
                            if (CreateCode.AuthentInfo(response))
                            {
                                JSONObject data = StringUtils
                                        .parseContent(response);
                                if (data.get("result").equals("success"))
                                {
                                    JSONObject jsonObject = data
                                            .optJSONObject("data");
                                    creditorrightsdetailsActivity_name.setText(
                                            jsonObject.optString("loan_name"));
                                    creditorrightsdetailsActivity_repayment_method
                                            .setText(jsonObject
                                                    .optString("repay_type"));
                                    creditorrightsdetailsActivity_transfer_time
                                            .setText(jsonObject
                                                    .optString("period") + "期"
                                                    + "/" + "共"
                                                    + jsonObject.optString(
                                                            "total_period")
                                                    + "期");
                                    creditorrightsdetailsActivity_loan_term
                                            .setText(jsonObject
                                                    .optString("loan_period"));
                                    creditorrightsdetailsActivity_repayment_period
                                            .setText(jsonObject
                                                    .optString("recover_time"));
                                    creditorrightsdetailsActivity_amount_editView
                                            .setText(jsonObject
                                                    .optString("amount"));
                            account_balance.setText(jsonObject
                                    .optString("balance_amount")+getString(R.string.common_display_yuan_default));
                                    expected_revenue.setText(
                                            jsonObject.optString("income"));
                                    bid_buttton.setEnabled(true);
                                }
                                else
                                {
                                    ToastUtil.show(
                                            data.optString("description"));
                                    finish();
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        if (isFirst)
                        {
                            bid_loadlayout.setOnStopLoading(
                                    BuyCreditorActivity.this,
                                    layout_ScrollView);
                            isFirst = false;
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish()
                    {
                        super.onFinish();
                    }

                    @Override
                    public void onStart()
                    {
                        super.onStart();
                    }

                });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.title_bar_back:
            finish();
            break;
        case R.id.recharge:
            startActivity(new Intent(this, RechargeActivity.class));
            break;
        case R.id.creditorrightsdetailsActivity_submit:
            // getApprove();
            if(Double.valueOf(account_balance.getText().toString().replace("元",""))<Double.valueOf(creditorrightsdetailsActivity_amount_editView.getText().toString().replace("元",""))){
                ToastUtil.show("账户余额不足");
                return;
            }
            // 易宝债权购买
            Intent intent = new Intent(this, YiBaoBuyActivity.class);
            intent.putExtra("transfer_id", transfer_id);
            startActivity(intent);
            finish();
            break;
        case R.id.sure_button:
            payment();
            dialog.dismiss();
            break;
        case R.id.cancel_button:
            dialog.dismiss();
            break;
        case R.id.dialog_close:
            dialog.dismiss();
            break;
        }

    }

    /**
     * //托管版本不需要
     */
    private void getApprove()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance()
                .getLoginToken(BuyCreditorActivity.this));
        HttpUtil.post(UrlConstants.APPROVE, map, new JsonHttpResponseHandler()
        {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                ToastUtil.show("访问失败！");
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    if (CreateCode.AuthentInfo(response))
                    {
                        JSONObject data = StringUtils.parseContent(response);
                        if (data.optString("result").equals("success"))
                        {
                            JSONObject info = data.getJSONObject("data");
                            if (StringUtils.isEmpty(info.optString("phone")))
                            {
                                ToastUtil.show("请绑定手机");
                                // 手机未绑定
                                Intent intent = new Intent();
                                intent.putExtra("payPwd",
                                        info.optString("paypassword"));
                                intent.setClass(BuyCreditorActivity.this,
                                        PhoneBindActivity.class);
                                startActivity(intent);
                            }
                            else if (StringUtils
                                    .isEmpty(info.optString("paypassword")))
                            {
                                // 未设置支付密码
                                ToastUtil.show("请设置支付密码");
                                startActivity(
                                        new Intent(BuyCreditorActivity.this,
                                                SettingPaymentActivity.class));
                            }
                            else
                            {
                                showDialog();
                            }

                        }
                        else
                        {
                            ToastUtil.show(data.optString("description"));
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFinish()
            {
                if (progressDialogBar.isShowing())
                {
                    progressDialogBar.dismiss();
                }
                super.onFinish();
            }

            @Override
            public void onStart()
            {
                if (progressDialogBar == null)
                {
                    progressDialogBar = ProgressDialogBar
                            .createDialog(BuyCreditorActivity.this);
                }
                progressDialogBar.setProgressMsg("连接中...");
                progressDialogBar.show();
                super.onStart();
            }

        });

    }

    /**
     * 支付密码
     */
    private void payment()
    {
        dialog.Closedialog();
        bid_loadlayout.setVisibility(View.GONE);
        EditText payment = (EditText) dialog.findViewById(R.id.payment);
        if (payment.getText().toString().trim().length() < 6
                || payment.getText().toString().trim().length() > 16)
        {
            ToastUtil.show("请输入6-16位的支付密码");
            return;
        }
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance()
                .getLoginToken(BuyCreditorActivity.this));
        map.put("loan_id", loan_id);
        map.put("transfer_id", transfer_id);
        map.put("paypassword", payment.getText().toString().trim());
        HttpUtil.post(UrlConstants.BUY, map, new JsonHttpResponseHandler()
        {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                ToastUtil.show("访问失败！");
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    if (CreateCode.AuthentInfo(response))
                    {
                        JSONObject data = StringUtils.parseContent(response);
                        if (data.getString("result").equals("success"))
                        {
                            ToastUtil.show("购买成功！");
                            MyApplication.getInstance()
                                    .getCreditorRightsDetailsActivity()
                                    .finishThis();
                            finish();
                        }
                        else
                        {
                            ToastUtil.show(data.getString("description"));
                        }
                    }
                    else
                    {
                        ToastUtil.show("购买失败！");
                    }
                }
                catch (Exception e)
                {
                    ToastUtil.show("购买失败！");
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFinish()
            {
                if (progressDialogBar.isShowing())
                {
                    progressDialogBar.dismiss();
                }
                super.onFinish();
            }

            @Override
            public void onStart()
            {
                if (progressDialogBar == null)
                {
                    progressDialogBar = ProgressDialogBar
                            .createDialog(BuyCreditorActivity.this);
                }
                progressDialogBar.setProgressMsg("支付中...");
                progressDialogBar.show();
                super.onStart();
            }

        });

    }

    private void showDialog()
    {
        dialog.setDialog(R.layout.dialog_payment);
        dialog.show();
        dialog.findViewById(R.id.dialog_close).setOnClickListener(this);
        dialog.findViewById(R.id.sure_button).setOnClickListener(this);
        dialog.findViewById(R.id.cancel_button).setOnClickListener(this);
        mHandler.sendEmptyMessage(R.id.payment);
    }

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            case R.id.payment:
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            }
        }
    };

    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView)
    {
        investData();

    }

}
