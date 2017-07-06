package com.diyou.activity;

import java.text.DecimalFormat;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreditorRightsActivity extends BaseActivity
        implements OnClickListener
{

    private String mBorrow_nid;
    private String mTender_id;
    private TextView mTitle;
    private TextView mHighestPrice;
    private TextView mPoundage;
    private DecimalFormat df;
    private Button mSubmit_btn;
    private EditText mInputMoney;
    private double mTotal_account;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        df = new DecimalFormat("######0.00");

        setContentView(R.layout.activity_creditor_rights);
        mBorrow_nid = getIntent().getStringExtra("borrow_nid");
        mTender_id = getIntent().getStringExtra("tender_id");
        mType = getIntent().getStringExtra("type");

        getCreditorRightData();
        initView();
    }

    private void initView()
    {
        findViewById(R.id.creditorrgihty_titlelayout)
                .findViewById(R.id.title_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.creditorrgihty_titlelayout)
                .findViewById(R.id.title_name))
                        .setText(getResources().getString(
                                R.string.title_activity_creditor_rights));
        mTitle = (TextView) findViewById(R.id.creditorrgihty_title);
        mHighestPrice = (TextView) findViewById(
                R.id.creditorrgihty_highestPrice);
        mPoundage = (TextView) findViewById(R.id.creditorrgihty_poundage);

        mInputMoney = (EditText) findViewById(R.id.creditorrgihty_editText);
        mInputMoney.addTextChangedListener(watcher);
        mSubmit_btn = (Button) findViewById(R.id.creditorrgihty_btn_submit);
        mSubmit_btn.setOnClickListener(this);
        if (mType.equals("yes"))
        {
            mInputMoney.setVisibility(View.GONE);
            mSubmit_btn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.title_back:
            finish();
            break;
        case R.id.creditorrgihty_btn_submit:
            setermineTheTransfer();
            break;
        default:
            break;
        }
    }

    private void getCreditorRightData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("module", "borrow");
        map.put("q", "change_check");
        map.put("method", "post");
        map.put("user_id", UserConfig.getInstance().getUserId(this));
        map.put("tender_id", mTender_id);
        map.put("borrow_nid", mBorrow_nid);
        map.put("type", " change");
        HttpUtil.get(CreateCode.getUrl(map), new JsonHttpResponseHandler()
        {

            @Override
            public void onStart()
            {

                super.onStart();
            }

            @Override
            public void onFinish()
            {
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    if (response.getString("result").equals("success"))
                    {
                        mTitle.setText(response.getString("borrow_name"));
                        mTotal_account = response.getDouble("total_account");
                        mHighestPrice.setText(
                                df.format(response.getDouble("total_account"))
                                        + "元");
                        mPoundage.setText(
                                df.format(response.getDouble("change_fee"))
                                        + "元");
                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    ToastUtil.show("网络请求失败,请稍后在试！");
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                ToastUtil.show("网络请求失败,请稍后在试！");
            }

        });

    }

    TextWatcher watcher = new TextWatcher()
    {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {
            mSubmit_btn.setEnabled(s.length() > 0 ? true : false);
            if (s.length() > 0)
            {
                if (s.length() == 1 && s.toString().equals("0")
                        || s.length() == 1 && s.toString().equals("."))
                {
                    mInputMoney.setText("");
                }
                if (!StringUtils.isEmpty(mInputMoney.getText().toString()))
                {
                    if (Double.valueOf(s.toString()) > mTotal_account)
                    {
                        ToastUtil.show("不能大于最高转让价格!");
                        mInputMoney.setText("");

                    }
                }

                if (s.toString().contains("."))
                {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2)
                    {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        mInputMoney.setText(s);
                        mInputMoney.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals("."))
                {
                    s = "0" + s;
                    mInputMoney.setText(s);
                    mInputMoney.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1)
                {
                    if (!s.toString().substring(1, 2).equals("."))
                    {
                        mInputMoney.setText(s.subSequence(0, 1));
                        mInputMoney.setSelection(1);
                        return;
                    }
                }

            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s)
        {
            // TODO Auto-generated method stub

        }
    };
    private String mType;

    private void setermineTheTransfer()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("module", "borrow");
        map.put("q", "change_add");
        map.put("method", "post");
        map.put("user_id", UserConfig.getInstance().getUserId(this));
        map.put("tender_id", mTender_id);
        map.put(" borrow_nid", mBorrow_nid);
        map.put(" paypassword_status", "1");
        map.put("account", mInputMoney.getText().toString());
        HttpUtil.get(CreateCode.getUrl(map), new JsonHttpResponseHandler()
        {

            @Override
            public void onStart()
            {

                super.onStart();
            }

            @Override
            public void onFinish()
            {
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    if (response.getString("result").equals("success"))
                    {
                        ToastUtil.show("转让成功！");
                        finish();
                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    ToastUtil.show("网络请求失败,请稍后在试！");
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                ToastUtil.show("网络请求失败,请稍后在试！");
            }

        });

    }

}
