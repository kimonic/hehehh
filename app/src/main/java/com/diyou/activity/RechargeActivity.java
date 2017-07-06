package com.diyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.diyou.base.BaseActivity;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.ImageUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.TextWatcherUtil;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.LoadingLayout;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TreeMap;

/**
 * 充值页面
 * 
 * @author Administrator
 *
 */
public class RechargeActivity extends BaseActivity implements OnClickListener
{

    public static final int REQUEST_CODE_RECHARGE = 2121;
    private EditText mEtMoney;
    private Button mBtnSubmit;
    private TextView mTvFee;
    private TextView mTvReal;
    private TextView mTvType;
    private String mNid;
    private LoadingLayout mLoadingLayout;
    private TextView mTvAccount;
    private ImageView mIvLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        initView();
        initActionBar();
        initData();
    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.recharge_title);
        findViewById(R.id.title_back).setOnClickListener(this);
        View rightView = findViewById(R.id.rl_title_bar_right);
        rightView.setVisibility(View.VISIBLE);
        rightView.setOnClickListener(this);
        TextView tvTitleRight = (TextView) findViewById(
                R.id.tv_title_bar_right);
        tvTitleRight.setText(R.string.recharge_title_right);
    }

    private void initView()
    {
        mEtMoney = (EditText) findViewById(R.id.et_recharge_money);
        mEtMoney.addTextChangedListener(new MyTextWatcher());
        mBtnSubmit = (Button) findViewById(R.id.btn_withdraw_submit);
        mBtnSubmit.setOnClickListener(this);

        mTvType = (TextView) findViewById(R.id.tv_withdraw_bank_type);
        mTvFee = (TextView) findViewById(R.id.tv_withdraw_fee);
        mTvReal = (TextView) findViewById(R.id.tv_withdraw_real);

        mLoadingLayout = (LoadingLayout) findViewById(
                R.id.fund_detail_loadlayout);
        mLoadingLayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initData();
                    }
                });
        mLoadingLayout.setOnStartLoading(null);

        mTvAccount = (TextView) findViewById(R.id.tv_withdraw_bank_account);
        mIvLogo = (ImageView) findViewById(R.id.iv_type_logo);
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

        }

    }

    private void setFee(String money){
        if (StringUtils.isEmpty(money) || StringUtils.isEmpty(mNid) || Double.valueOf(money)<=0)
        {
            mBtnSubmit.setEnabled(false);
            mTvFee.setText("");
            mTvReal.setText("");
        }
        else
        {
            mBtnSubmit.setEnabled(true);
            requestRechargeFee(money);
        }
    }
    private void requestRechargeFee(String money)
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("amount", money);
        map.put("payment_nid", mNid);
        HttpUtil.post(UrlConstants.RECHARGE_FEE, map,
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
                                    mTvReal.setText(jo.optString("income"));
                                    mBtnSubmit.setEnabled(true);
                                }
                                else
                                {
                                    ToastUtil.show(
                                            json.optString("description"));
                                    mTvFee.setText("0");
                                    mTvReal.setText("0");
                                    mBtnSubmit.setEnabled(false);
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

    private void initData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        HttpUtil.post(UrlConstants.RECHARGE_TYPE, map,
                new JsonHttpResponseHandler()
                {
                    @Override
                    public void onStart()
                    {
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
                                    // int total_items =
                                    // jo.optInt("total_items");
                                    JSONObject j = jo.optJSONArray("items")
                                            .getJSONObject(0);
                                    mNid = j.optString("nid");
                                    mTvType.setText(j.optString("name"));
                                    mTvAccount.setText(j.optString("trust_account"));

                                    ImageLoader.getInstance().displayImage(j.optString("thumbs"), mIvLogo, ImageUtil.getImageOptions());

                                    mLoadingLayout.setOnStopLoading(
                                            RechargeActivity.this, null);
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
                        mLoadingLayout.setOnLoadingError(RechargeActivity.this,
                                null);
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
        case R.id.btn_withdraw_submit:
            Intent intent = new Intent(this, YiBaoRechargeActivity.class);
            intent.putExtra("amount", StringUtils.getString(mEtMoney));
            intent.putExtra("payment_type", mNid);
            // startActivityForResult(intent, REQUEST_CODE_RECHARGE);
            startActivity(intent);
            finish();
            break;
            case R.id.rl_title_bar_right:
                startActivity(new Intent(RechargeActivity.this,
                        RechargeRecordActivity.class));
            break;
        default:
            break;
        }
    }

}
