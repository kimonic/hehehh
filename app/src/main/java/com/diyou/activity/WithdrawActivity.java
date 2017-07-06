package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.ImageUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.TextWatcherUtil;
import com.diyou.util.ToastUtil;
import com.diyou.util.Utils;
import com.diyou.v5yibao.R;
import com.diyou.view.ConfirmCancelDialog;
import com.diyou.view.ConfirmCancelDialog.onClickListener;
import com.diyou.view.LoadingLayout;
import com.diyou.view.PayPasswordDialog.PayListener;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class WithdrawActivity extends BaseActivity
        implements OnClickListener, PayListener
{

    public static final String ACTION_WITHDRAW = "withdraw";
    private LoadingLayout mLoadingLayout;
    private TextView mTvBankType;
    private TextView mTvBankAccount;
    private TextView mTvName;
    private TextView mTvBankName;
    private EditText mEtMoney;
    private TextView mTvFee;
    private TextView mTvReal;
    private Button mBtnSubmit;
    private ImageView mIvBankIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        initActionBar();
        initView();
        requestData();
    }

    private void initView()
    {
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
        mLoadingLayout.setOnStartLoading(null);

        mIvBankIcon = (ImageView) findViewById(R.id.iv_bank_icon);
        mBtnSubmit = (Button) findViewById(R.id.btn_withdraw_submit);
        mBtnSubmit.setOnClickListener(this);
        mTvBankType = (TextView) findViewById(R.id.tv_withdraw_bank_type);
        mTvBankAccount = (TextView) findViewById(R.id.tv_withdraw_bank_account);
        mTvName = (TextView) findViewById(R.id.tv_withdraw_name);
        mTvBankName = (TextView) findViewById(R.id.tv_withdraw_bank_name);
        mTvFee = (TextView) findViewById(R.id.tv_withdraw_fee);
        mTvReal = (TextView) findViewById(R.id.tv_withdraw_real);
        mEtMoney = (EditText) findViewById(R.id.et_withdraw_money);
        mEtMoney.addTextChangedListener(new MyTextWatcher());
    }

    class MyTextWatcher implements TextWatcher
    {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {
            String money = TextWatcherUtil.getMoneyFormat(s.toString(), mEtMoney);
            setFee(money);
        }

        @Override
        public void afterTextChanged(Editable s)
        {
            // TODO Auto-generated method stub

        }

    }

    private void setFee(String money) {
        if (StringUtils.isEmpty(money))
        {
            mBtnSubmit.setEnabled(false);
//            mTvFee.setText("");
//            mTvReal.setText("");
        }
        else
        {
            mBtnSubmit.setEnabled(true);
//            requestWithdrawFee(money);
        }
    }

    private void requestWithdrawFee(String money)
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("account", money);
        HttpUtil.post(UrlConstants.WITHDRAW_FEE, map,
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
                                    JSONObject jo = json.optJSONObject("data");
                                    mTvFee.setText(jo.optString("fee"));
                                    mTvReal.setText(
                                            jo.optString("account_yes"));
                                }
                                else
                                {
                                    ToastUtil.show(
                                            json.optString("description"));
                                    mTvFee.setText("0");
                                    mTvReal.setText("0");
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

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.withdraw_title);
        findViewById(R.id.title_back).setOnClickListener(this);
        View rightView = findViewById(R.id.rl_title_bar_right);
        rightView.setVisibility(View.VISIBLE);
        rightView.setOnClickListener(this);
        TextView tvTitleRight = (TextView) findViewById(
                R.id.tv_title_bar_right);
        tvTitleRight.setText(R.string.withdraw_title_right);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.title_back:
            finish();
            break;
        case R.id.btn_withdraw_submit:
            // 易宝提现
            requestWithdraw();
            break;
        case R.id.rl_title_bar_right:
            startActivity(new Intent(this, WithdrawRecordActivity.class));
            break;
        default:
            break;
        }
    }

    private void requestWithdraw()
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("amount", StringUtils.getString(mEtMoney));
        map.put("withdraw_type", "NORMAL");
        HttpUtil.post(UrlConstants.YIBAO_WITHDRAW, map,
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
                                    String url = jo.optString("submit_url");
                                    String body= Utils.handleJson(jo);
                                    Intent intent = new Intent(WithdrawActivity.this, YiBaoWithdrawActivity.class);
                                    intent.putExtra("url", url);
                                    intent.putExtra("body", body);
                                    startActivity(intent);
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
                        ToastUtil.show(R.string.generic_error);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });

    }
    private void requestData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        HttpUtil.post(UrlConstants.BANK_CARD_INFO, map,
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
                                    /*
                                     * {"data":{"is_bind":1,"bank_info":
                                     * {"status":"1","bank_nid":"ABC","bankName"
                                     * :"农业银行","city":"103","member_id":"56",
                                     * "bank_id":"0","id":"5",
                                     * "update_time":null,"name":"厦门开户行",
                                     * "province":"102","account":
                                     * "6222***********8668","add_time":
                                     * "1438229300",
                                     * "realname":"李白","member_name":
                                     * "gk18030041411"}},
                                     * "result":"success","description":"",
                                     * "code":200}
                                     */
                                    JSONObject jo = json.optJSONObject("data");
                                    if ("1".equals(jo.optString("is_bind")))
                                    {
                                        /*JSONArray ja = jo
                                                .optJSONArray("bank_info");
                                        JSONObject j = ja.optJSONObject(0);
                                        mTvBankType.setText(
                                                j.optString("bank_name"));
                                        mTvBankAccount.setText(
                                                StringUtils.getHideCardID(j
                                                        .optString("account")));
                                        mTvName.setText(
                                                StringUtils.getHideName(j.optString("realname")));
                                        mTvBankName
                                                .setText(j.optString("name"));
                                        ImageLoader.getInstance().displayImage(j.optString("bank_img"), mIvBankIcon, ImageUtil.getImageOptions()
                                                );*/
//                                        requestHasPaypassword();//托管版本不需要
                                    }
                                    else
                                    {
                                        Intent intent = new Intent(
                                                WithdrawActivity.this,
                                                BankCardManagerActivity.class);
                                        intent.putExtra("action",
                                                ACTION_WITHDRAW);
                                        startActivity(intent);
                                        finish();
                                    }

                                    mLoadingLayout.setOnStopLoading(
                                            WithdrawActivity.this, null);
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
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        mLoadingLayout.setOnLoadingError(WithdrawActivity.this,
                                null);
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

                            }
                            else
                            {
                                showPayPasswordDialog();
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

    private void showPayPasswordDialog()
    {
        ConfirmCancelDialog dialog = new ConfirmCancelDialog(this, "支付密码",
                "您还未设置支付密码，是否现在设置?");
        dialog.setOnClickListener(new onClickListener()
        {

            @Override
            public void confirmClick()
            {
                Intent intent = new Intent(WithdrawActivity.this,
                        SettingPaymentActivity.class);
                intent.putExtra("action", ACTION_WITHDRAW);
                startActivity(intent);
                finish();
            }

            @Override
            public void cancelClick()
            {
                finish();
            }
        });
        dialog.show();
    }

    @Override
    public void payHandle(EditText editText,String password)
    {
       withdrawSubmit(password);
    }

    private void withdrawSubmit(String payPassword)
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("paypassword", payPassword);
        map.put("amount", StringUtils.getString(mEtMoney));
        HttpUtil.post(UrlConstants.WITHDRAW, map, new JsonHttpResponseHandler()
        {
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
                            mEtMoney.setText("");
                            ToastUtil.show("提现成功");
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
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                ToastUtil.show(R.string.generic_error);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }

}
