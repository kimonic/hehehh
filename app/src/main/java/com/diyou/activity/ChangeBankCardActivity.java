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
import com.diyou.view.PayPasswordDialog;
import com.diyou.view.PayPasswordDialog.PayListener;
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

/**
 * 修改银行卡
 * 
 * @author Administrator
 *
 */
public class ChangeBankCardActivity extends BaseActivity
        implements OnClickListener, PayListener
{

    private String mAccount;
    private EditText mEtOld;
    private EditText mEtNew;
    private EditText mEtNewSubmit;
    private EditText mEtBankName;
    private TextView mTvProvince;
    private TextView mTvCity;
    private TextView mTvBankType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_bank_card);
        initActionBar();
        mAccount = getIntent().getStringExtra("account");
        initView();
    }

    private void initView()
    {
        TextView tvOldAccount = (TextView) findViewById(
                R.id.tv_change_bank_old_account);
        tvOldAccount.setText(StringUtils.getHideBankAccount(mAccount));
        mEtOld = (EditText) findViewById(R.id.et_change_bank_old);
        mEtNew = (EditText) findViewById(R.id.et_change_bank_new);
        mEtNew.addTextChangedListener(new MyWatcher());
        mEtNewSubmit = (EditText) findViewById(R.id.et_change_bank_new_submit);
        mEtBankName = (EditText) findViewById(R.id.et_change_bank_name);
        findViewById(R.id.rl_change_bank_select_city).setOnClickListener(this);
        findViewById(R.id.rl_change_bank_select_bank).setOnClickListener(this);
        findViewById(R.id.btn_change_bank_submit).setOnClickListener(this);

        mTvProvince = (TextView) findViewById(R.id.tv_change_bank_province);
        mTvCity = (TextView) findViewById(R.id.tv_change_bank_city);
        mTvBankType = (TextView) findViewById(R.id.tv_change_bank_name);
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
            // TODO Auto-generated method stub

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

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.change_bank_title);
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    private boolean checkData()
    {
        if (StringUtils.isEmpty(StringUtils.getString(mEtOld)))
        {
            ToastUtil.show("请输入原卡号");
            return false;
        }
        else if (!mAccount.equals(StringUtils.getString(mEtOld)))
        {
            ToastUtil.show("原卡号错误,请重新输入");
            return false;
        }
        else if (StringUtils.isEmpty(StringUtils.getString(mEtNew)))
        {
            ToastUtil.show("请输入新卡号");
            return false;
        }
        else if (StringUtils.isEmpty(StringUtils.getString(mEtNewSubmit)))
        {
            ToastUtil.show("请确认新卡号");
            return false;
        }
        else if (!StringUtils.getString(mEtNew)
                .equals(StringUtils.getString(mEtNewSubmit)))
        {
            ToastUtil.show("新卡号输入不一致");
            return false;
        }
        else if (mAccount.equals(StringUtils.getString(mEtNew)))
        {
            ToastUtil.show("新卡号不能与原卡号一致");
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.title_back:
            finish();
            break;
        case R.id.rl_change_bank_select_city:
            startActivityForResult(new Intent(this, AreaListActivity.class),
                    Constants.REQUEST_CODE_PROVINCE_CITY);
            break;
        case R.id.rl_change_bank_select_bank:
            startActivityForResult(new Intent(this, BankListActivity.class),
                    Constants.REQUEST_CODE_BANK);
            break;
        case R.id.btn_change_bank_submit:
            if (checkData())
            {
                PayPasswordDialog dialog = new PayPasswordDialog(
                        ChangeBankCardActivity.this,
                        ChangeBankCardActivity.this);
                dialog.show();
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

    @Override
    public void payHandle(EditText editText,String password)
    {
       submitChange(password);
    }

    private void submitChange(String payPassword)
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("now_account", mAccount);
        map.put("account", StringUtils.getString(mEtNew));
        map.put("confirm_account", StringUtils.getString(mEtNewSubmit));
        map.put("paypassword", payPassword);
        map.put("bank", String.valueOf(mTvBankType.getTag()));
        map.put("branch", StringUtils.getString(mEtBankName));
        map.put("province", String.valueOf(mTvProvince.getTag()));
        map.put("city", String.valueOf(mTvCity.getTag()));

        HttpUtil.post(UrlConstants.CHANGE_BANK_CARD, map,
                new JsonHttpResponseHandler()
                {
                    @Override
                    public void onFinish()
                    {
                        hideProgressDialog();
                        super.onFinish();
                    }

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
                                    EventBus.getDefault()
                                            .post(new EventObject("refresh"));
                                    ToastUtil.show("更换银行卡成功");
                                    setResult(RESULT_OK);
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
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        ToastUtil.show(R.string.generic_error);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });

    }

}
