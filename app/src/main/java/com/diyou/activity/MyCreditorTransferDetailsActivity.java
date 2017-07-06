package com.diyou.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.diyou.base.BaseActivity;
import com.diyou.bean.EventObject;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.pulltorefresh.PullToRefreshBase;
import com.diyou.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.ConfirmCancelDialog;


import com.diyou.view.ConfirmCreditorCancelDialog;
import com.diyou.view.LoadingLayout;
import com.diyou.view.ProgressDialogBar;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.TreeMap;

import de.greenrobot.event.EventBus;

/**
 * 我的项目-》我的债权-》转让记录—》债权转让详情
 * 
 * @author Administrator
 *
 */
public class MyCreditorTransferDetailsActivity extends BaseActivity
        implements OnClickListener, TextWatcher, OnRefreshListener<ScrollView>
{
    private String id;
    private boolean isFirst = true;
    private LoadingLayout creditor_loadlayout;
    private ScrollView layout_ScrollView;
    private TextView creditor_name;// 名称
    private TextView creditor_value;// 债权价值
    private TextView repayment_period;// 还款期限
    private TextView total_period;// 总期数
    private String transfer_status;
    private Button immediate_transfer;
    private String creditor_anount;
    private String coefficient;// 转让系数
    private EditText transfer_amount;
    private TextView anticipated_income;
    private double anticipated_amount;// 预计收益
    protected ProgressDialogBar progressDialogBar;
    private String transfer_id;// 撤销时候使用的id
    private String money;
    private TextView percent_range;
    private String transfer_coefficient_min;
    private String transfer_coefficient_max;
    private TextView deadline_time;
    private TextView tvFee;
    private String transfer_fee;
    private  String cancel_count;
    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.activity_creditor_transfer_details);
        id = getIntent().getStringExtra("id");
        transfer_status = getIntent().getStringExtra("transfer_status");
        intiView();
        intiData();
    }

    private void intiView()
    {
        View titlebar = findViewById(R.id.creditor_view);
        titlebar.findViewById(R.id.title_bar_back).setOnClickListener(this);
        TextView title_name = (TextView) titlebar.findViewById(R.id.title_name);
        title_name.setText(R.string.creditor_transfer_detail_title);
        mContentView = findViewById(R.id.ll_content);
        creditor_loadlayout = (LoadingLayout) findViewById(
                R.id.creditor_loadlayout);
        creditor_loadlayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        intiData();// 获取数据
                    }
                });
        creditor_loadlayout.setOnStartLoading(mContentView);
        creditor_name = (TextView) findViewById(R.id.creditor_name);
        creditor_value = (TextView) findViewById(R.id.creditor_value);
        repayment_period = (TextView) findViewById(R.id.repayment_period);
        total_period = (TextView) findViewById(R.id.total_period);
        anticipated_income = (TextView) findViewById(R.id.anticipated_income);
        tvFee = (TextView) findViewById(R.id.tv_transfer_detail_fee);
        transfer_amount = (EditText) findViewById(R.id.transfer_amount);
        percent_range = (TextView) findViewById(R.id.percent_range);
        transfer_amount.addTextChangedListener(this);
        immediate_transfer = (Button) findViewById(R.id.immediate_transfer);
        deadline_time = (TextView) findViewById(R.id.repayment_period_textview);
        if (transfer_status.equals("-1"))
        {
            immediate_transfer.setText(R.string.creditor_transfer_status_transfer);
            immediate_transfer.setEnabled(true);
        }
        else if (transfer_status.equals("1"))
        {
            setEditTextEnable(false);
            immediate_transfer.setText(R.string.creditor_transfer_status_undo);
            immediate_transfer.setEnabled(true);
        }
        else if (transfer_status.equals("2"))
        {
            setEditTextEnable(false);
            immediate_transfer.setText(R.string.creditor_transfer_status_already);
            immediate_transfer.setEnabled(false);
            deadline_time.setText(R.string.transfer_time);
        }
        else if (transfer_status.equals("3"))
        {
            immediate_transfer.setText(R.string.creditor_transfer_status_transfer);
            immediate_transfer.setEnabled(true);
        }

        immediate_transfer.setOnClickListener(this);
    }

    private void setEditTextEnable(boolean isEnable){
        transfer_amount.setFocusable(isEnable);
        transfer_amount.setEnabled(isEnable);
        transfer_amount.setFocusableInTouchMode(isEnable);
        if(isEnable){
            transfer_amount.requestFocus();
        }
    }
    private void intiData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance()
                .getLoginToken(MyCreditorTransferDetailsActivity.this));
        map.put("tender_id", id);
        HttpUtil.post(UrlConstants.CREDITER_TRANSFER_DETAILS, map,
                new JsonHttpResponseHandler()
                {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        if (isFirst)
                        {
                            creditor_loadlayout.setOnLoadingError(
                                    MyCreditorTransferDetailsActivity.this,
                                    mContentView);
                        }
                        // layout_ScrollView.onRefreshComplete();
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
                                    JSONObject jo = data.optJSONObject("data");
                                    cancel_count=jo.optString("cancel_count");
                                    creditor_name
                                            .setText(jo.optString("loan_name"));
                                    creditor_anount = jo.optString("amount_money");
                                    creditor_value.setText(StringUtils.getDoubleFormat(creditor_anount)+getString(R.string.common_display_yuan_default));
                                    repayment_period.setText(
                                            jo.optString("recover_time"));
                                    total_period.setText(jo.optString("period")
                                            + "/"
                                            + jo.optString("total_period"));
                                    transfer_id = jo.optString("transfer_id");
                                    transfer_fee = jo.optString("transfer_fee");
                                    percent_range.setText("("
                                            + jo.optString(
                                                    "transfer_coefficient_min")
                                            + "%-" + jo.optString(
                                                    "transfer_coefficient_max")
                                            + "%" + ")");
                                    transfer_coefficient_min=jo.optString("transfer_coefficient_min");
                                    transfer_coefficient_max=jo.optString("transfer_coefficient_max");
                                    if("1".equals(transfer_status)||"2".equals(transfer_status)){
                                        transfer_amount.setText(jo.optString("coefficient"));
                                        anticipated_income.setText(jo.optString("amount")+getString(R.string.common_display_yuan_default));
                                    }
                                    if (isFirst)
                                    {
                                        isFirst=false;
                                        creditor_loadlayout.setOnStopLoading(
                                                MyCreditorTransferDetailsActivity.this,
                                                mContentView);
                                    }
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

                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish()
                    {
                        // layout_ScrollView.onRefreshComplete();
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
        case R.id.immediate_transfer:
            if (transfer_status.equals("-1"))
            {
                // 立即转让
                if (StringUtils.isEmpty(money))
                {
                    ToastUtil.show(R.string.creditor_transfer_coefficient_null);
                }else{
                    ConfirmCancelDialog dialog=new ConfirmCancelDialog(MyCreditorTransferDetailsActivity.this,"温馨提示","是否立即转让？");
                    dialog.setOnClickListener(new ConfirmCancelDialog.onClickListener() {
                        @Override
                        public void confirmClick() {
                            immediateTransfer();//托管不需要支付密码
                        }

                        @Override
                        public void cancelClick() {

                        }
                    });
                    dialog.show();
                }
            }
            else if (transfer_status.equals("1"))
            {
                // 撤销
                showUndoDialog();
            }
            else if (transfer_status.equals("3"))
            {
                ToastUtil.show(R.string.creditor_transfer_three_times);
                return;
            }

            break;
        }

    }

    private void showUndoDialog()
    {
        ConfirmCreditorCancelDialog dialog=new ConfirmCreditorCancelDialog(this, R.string.creditor_transfer_undo_title, "是否确认撤销债权转让，债权转让撤销3次将不可再进行转让，该债权您已撤销"+cancel_count+"次");
        dialog.setOnClickListener(new ConfirmCreditorCancelDialog.onClickListener() {

            @Override
            public void confirmClick() {
                cancelTransfer();
            }

            @Override
            public void cancelClick() {
                // TODO Auto-generated method stub

            }
        });
        dialog.show();

    }

    private void cancelTransfer()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance()
                .getLoginToken(MyCreditorTransferDetailsActivity.this));
        map.put("transfer_id", transfer_id);
        HttpUtil.post(UrlConstants.CANCEL_TRANSFER, map,
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
                                    ToastUtil.show(R.string.creditor_transfer_undo_succeed);
                                    String cancel_count = data
                                            .getJSONObject("data")
                                            .optString("cancel_count");// 取消次数
                                    if (cancel_count.equals("3")) {
                                        transfer_status = "3";
                                    } else {
                                        transfer_status = "-1";
                                    }
                                    postEvent();
                                    finish();
//                                    transfer_amount.setText("");
//                                    immediate_transfer.setText(R.string.creditor_transfer_status_transfer);
//                                    immediate_transfer.setEnabled(true);
//                                    setEditTextEnable(true);
                                    finish();
                                } else {
                                    ToastUtil.show(
                                            data.optString("description"));
                                }
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
                        showProgressDialog(getString(R.string.remind_dialog_transfer));
                        super.onStart();
                    }

                });

    }

    private void postEvent(){
        EventObject eventObject=new EventObject("refreshMyCreditorTransferRecord");
//        Bundle data = new Bundle();
//        data.putString("transfer_status", transfer_status);
//        eventObject.setData(data);
        EventBus.getDefault().post(eventObject);
    }

    private View mContentView;

    private void immediateTransfer()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance()
                .getLoginToken(MyCreditorTransferDetailsActivity.this));
        map.put("tender_id", id);
        map.put("coefficient", coefficient);
        HttpUtil.post(UrlConstants.IMMEDIATE_TRANSFER, map,
                new JsonHttpResponseHandler()
                {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        ToastUtil.show(R.string.generic_error);
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
                                    ToastUtil.show(R.string.creditor_transfer_transfer_succeed);
                                    immediate_transfer.setText(R.string.creditor_transfer_status_undo);
                                    setEditTextEnable(false);
                                    transfer_status = "1";
                                    postEvent();
                                    finish();
                                }
                                else
                                {
                                    ToastUtil.show(
                                            data.optString("description"));
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            ToastUtil.show(R.string.remind_data_parse_exception);
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
                    public void onStart()
                    {
                        showProgressDialog(getString(R.string.remind_dialog_transfer));
                        super.onStart();
                    }

                });

    }


    @Override
    public void afterTextChanged(Editable s)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        money = s.toString();
        if (StringUtils.isEmpty(money))
        {
            anticipated_income.setText("");
            tvFee.setText("");
        }
        else
        {
            coefficient = money;
            //转让价格计算
            if(!StringUtils.isEmpty(creditor_anount)){
                anticipated_amount = Double.valueOf(money) / 100
                        * Double.valueOf(creditor_anount);
                anticipated_income.setText(StringUtils.getDoubleFormat(anticipated_amount)+getString(R.string.common_display_yuan_default));
            }
            //手续费计算
            if(!StringUtils.isEmpty(transfer_fee)){
                double fee = anticipated_amount*Double.valueOf(transfer_fee);
                tvFee.setText(StringUtils.getDoubleFormat(fee)+getString(R.string.common_display_yuan_default));
            }
        }
        
        //输入的转让系数必须在规定范围内
        if("-1".equals(transfer_status)){
            if(!StringUtils.isEmpty(transfer_coefficient_min) && !StringUtils.isEmpty(transfer_coefficient_max)&&!StringUtils.isEmpty(s.toString())){
                if(Double.valueOf(transfer_coefficient_min)<=Double.valueOf(s.toString()) && Double.valueOf(s.toString())<=Double.valueOf(transfer_coefficient_max)){
                    immediate_transfer.setEnabled(true);
                }else{
                    immediate_transfer.setEnabled(false);
                }
            }else{
                immediate_transfer.setEnabled(false);
            }
        }

    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView)
    {
        intiData();

    }
}
