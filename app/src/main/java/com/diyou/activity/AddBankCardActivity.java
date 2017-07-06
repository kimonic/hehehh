package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.bean.BankInfo;
import com.diyou.bean.CityInfo;
import com.diyou.bean.EventObject;
import com.diyou.config.Constants;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.ConfirmCancelDialog;
import com.diyou.view.ConfirmCancelDialog.onClickListener;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import de.greenrobot.event.EventBus;

public class AddBankCardActivity extends BaseActivity
        implements OnClickListener, onClickListener
{

    private TextView mTvPersonName;
    private String mRealName;
    private EditText mEtAccount;
    private TextView mTvBankType;
    private TextView mTvProvince;
    private TextView mTvCity;
    private EditText mEtBankName;
    private String mAction;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_card);
        initActionBar();
        mRealName = getIntent().getStringExtra("realname");
        mAction = getIntent().getStringExtra("action");
        initView();
    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.add_bank_title);
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    private void initView()
    {
        mTvPersonName = (TextView) findViewById(R.id.tv_add_bank_person_name);
        mTvPersonName.setText(StringUtils.getHideName(mRealName));

        findViewById(R.id.rl_add_bank_select_city).setOnClickListener(this);
        findViewById(R.id.rl_add_bank_select_type).setOnClickListener(this);
        findViewById(R.id.btn_add_bank_submit).setOnClickListener(this);

        mEtAccount = (EditText) findViewById(R.id.et_add_bank_account);
        mEtAccount.addTextChangedListener(new MyWatcher());
        mTvBankType = (TextView) findViewById(R.id.tv_add_bank_type);
        mTvProvince = (TextView) findViewById(R.id.tv_add_bank_province);
        mTvCity = (TextView) findViewById(R.id.tv_add_bank_city);
        mEtBankName = (EditText) findViewById(R.id.tv_add_bank_kh_name);
    }

    class MyWatcher implements TextWatcher
    {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {
            if (s.toString().length() == 6)
            {
                autoCheckBank(s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s)
        {

        }

    }

    private void autoCheckBank(String bankCode)
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("bankcode", bankCode);
        HttpUtil.post(UrlConstants.BANK_CARD_NID, map,
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
                                    mTvBankType.setText(jo.optString("name"));
                                    mTvBankType.setTag(jo.optString("nid"));
                                }
                                // else
                                // {
                                // ToastUtil.show(json.optString("description"));
                                // }
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
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
        case R.id.rl_add_bank_select_city:
            Intent intent = new Intent(this, AreaListActivity.class);
            startActivityForResult(intent,
                    Constants.REQUEST_CODE_PROVINCE_CITY);
            break;
        case R.id.rl_add_bank_select_type:
            startActivityForResult(new Intent(this, BankListActivity.class),
                    Constants.REQUEST_CODE_BANK);
            break;
        case R.id.btn_add_bank_submit:
            if (checkData())
            {
                requestData();
            }
            break;
        default:
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_PROVINCE_CITY
                && resultCode == Constants.RESULT_CODE_PROVINCE_CITY)
        {
            if (data != null)
            {
                mTvProvince.setText(data.getStringExtra("provinceName"));
                CityInfo cityInfo = (CityInfo) data
                        .getSerializableExtra("cityInfo");
                mTvProvince.setTag(cityInfo.getPid());
                mTvCity.setText(cityInfo.getName());
                mTvCity.setTag(cityInfo.getId());
            }
        }
        else if (requestCode == Constants.REQUEST_CODE_BANK
                && resultCode == Constants.RESULT_CODE_BANK)
        {
            if (data != null)
            {
                BankInfo bankInfo = (BankInfo) data
                        .getSerializableExtra("bankInfo");
                mTvBankType.setText(bankInfo.getName());
                mTvBankType.setTag(bankInfo.getCode());
            }
        }
    }

    private boolean checkData()
    {
        if (StringUtils.getString(mEtAccount).length()<Constants.BANKCARD_ACCOUNT_MIN_LENGTH)
        {
            ToastUtil.show(R.string.remind_input_bankcard_sixteen);
            return false;
        }
        else if ("".equals(mTvBankType.getText().toString().trim()))
        {
            ToastUtil.show("请选择开户行");
            return false;
        }
        else if ("".equals(mTvProvince.getText().toString().trim())
                || "".equals(mTvCity.getText().toString().trim()))
        {
            ToastUtil.show("请选择开户行所在省市");
            return false;
        }
        else if (StringUtils.isEmpty(StringUtils.getString(mEtBankName)))
        {
            ToastUtil.show("请输入开户行名称");
            return false;
        }
        return true;
    }

    private void requestData()
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("account", StringUtils.getString(mEtAccount));
        map.put("bank", String.valueOf(mTvBankType.getTag()));
        map.put("branch", StringUtils.getString(mEtBankName));
        map.put("province", String.valueOf(mTvProvince.getTag()));
        map.put("city", String.valueOf(mTvCity.getTag()));
        HttpUtil.post(UrlConstants.BIND_BANK_CARD, map,
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
                                    // JSONObject jo= new
                                    // JSONObject(json.optString("data"));
                                    EventBus.getDefault()
                                            .post(new EventObject("refresh"));
                                    ToastUtil.show("绑定银行卡成功");
                                    setResult(RESULT_OK);
//                                    requestHasPaypassword();
                                    // 如果是取现界面过来的，需要跳回去
                                    if (WithdrawActivity.ACTION_WITHDRAW
                                            .equals(mAction))
                                    {
                                        startActivity(
                                                new Intent(AddBankCardActivity.this,
                                                        WithdrawActivity.class));
                                    }
                                    finish();

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
                        hideProgressDialog();
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });

    }

    private void requestHasPaypassword()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        HttpUtil.post(UrlConstants.GETPAYPWD, map, new JsonHttpResponseHandler()
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
                        JSONObject json = StringUtils.parseContent(response);
                        if (json.optString("result").contains("success"))
                        {
                            JSONObject jo = new JSONObject(
                                    json.optString("data"));
                            if ("1".equals(jo.optString("status")))
                            {
                                // 如果是取现界面过来的，需要跳回去
                                if (WithdrawActivity.ACTION_WITHDRAW
                                        .equals(mAction))
                                {
                                    startActivity(
                                            new Intent(AddBankCardActivity.this,
                                                    WithdrawActivity.class));
                                }
                                finish();
                            }
                            else
                            {
                                ConfirmCancelDialog dialog = new ConfirmCancelDialog(
                                        AddBankCardActivity.this, "设置支付密码",
                                        "您还未设置支付密码，是否现在去设置");
                                dialog.setOnClickListener(
                                        AddBankCardActivity.this);
                                dialog.show();
                            }
                        }
                        else
                        {
                            ToastUtil.show(json.optString("description"));
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
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }

    @Override
    public void confirmClick()
    {
        Intent intent = new Intent(AddBankCardActivity.this,
                SettingPaymentActivity.class);
        intent.putExtra("action", mAction);
        startActivity(intent);
        finish();
    }

    @Override
    public void cancelClick()
    {
        finish();
    }
}
