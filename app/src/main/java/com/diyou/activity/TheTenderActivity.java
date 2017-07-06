package com.diyou.activity;

import java.text.DecimalFormat;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.application.MyApplication;
import com.diyou.base.BaseActivity;
import com.diyou.config.Constants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.huifu.HuiFuPayActivity;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TheTenderActivity extends BaseActivity implements OnClickListener
{

    /**
     * 投标页面
     * 
     * @author harve
     * 
     */
    private TextView mAmount_available;
    private EditText mBid_amount;
    private View mLoading;
    private DecimalFormat df;
    private Button mSubmit;
    private String mBorrow_nid;
    private int mFragmentID;
    private MyApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        application = new MyApplication();
        application.setTenderActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_tender);
        mBorrow_account_wait = getIntent().getDoubleExtra("borrow_account_wait",
                0);
        mBorrow_nid = getIntent().getStringExtra("borrow_nid");
        mBorrow_password = getIntent().getStringExtra("borrow_password");
        mChange_account_all = getIntent().getStringExtra("change_account_all");
        mFragmentID = getIntent().getIntExtra("fragmentID", 0);
        mTender_id = getIntent().getStringExtra("tender_id");
        df = new DecimalFormat("######0.00");
        initView();
        getBalance();
    }

    @Override
    protected void onDestroy()
    {
        application.setTenderActivity(null);
        super.onDestroy();
    }

    public void finishThis()
    {
        finish();
    }

    private void initView()
    {

        mPassword = (EditText) findViewById(R.id.thetenderactivity_password);
        mPassword.setFocusable(
                StringUtils.isEmpty(mBorrow_password) ? false : true);
        mPassword.setHint(StringUtils.isEmpty(mBorrow_password) ? "此标不需要投资密码!"
                : "此标需要投资密码!");
        mLoading = findViewById(R.id.thetenderactivity_loading);
        View titlelayout = findViewById(R.id.thetenderactivity_titlelayout);
        titlelayout.findViewById(R.id.title_back).setOnClickListener(this);
        TextView title = (TextView) titlelayout.findViewById(R.id.title_name);
        title.setText("投标");
        mSubmit = (Button) findViewById(R.id.thetenderactivity_btn_submit);
        mSubmit.setOnClickListener(this);
        mAmount_available = (TextView) findViewById(
                R.id.thetenderactivity_amount_available);
        mBid_amount = (EditText) findViewById(
                R.id.thetenderactivity_bid_amount);
        if (mFragmentID == Constants.CREDITORRIGHTSDETAILSACTIVITY)
        {
            mBid_amount.setFocusable(false);
            mBid_amount.setText(mChange_account_all);
            mSubmit.setEnabled(true);
            mPassword.setVisibility(View.GONE);
        }
        else
        {
            mBid_amount.addTextChangedListener(watcher);
            mPassword.setVisibility(View.VISIBLE);

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
        case R.id.thetenderactivity_btn_submit:

            Intent intent = new Intent(this, HuiFuPayActivity.class);

            if (mFragmentID == Constants.CREDITORRIGHTSDETAILSACTIVITY)
            {
                intent.putExtra("borrow_nid", mBorrow_nid);
                intent.putExtra("tender_id", mTender_id);
                intent.putExtra("fragmentID",
                        Constants.CREDITORRIGHTSDETAILSACTIVITY);

            }
            else
            {
                intent.putExtra("fragmentID", mFragmentID);
                intent.putExtra("tender_account",
                        mBid_amount.getText().toString());
                intent.putExtra("borrow_nid", mBorrow_nid);
                intent.putExtra("borrow_password", mBorrow_password);
                intent.putExtra("password", mPassword.getText().toString());

            }
            startActivityForResult(intent, Constants.THETENDERACTIVITY);

            break;

        default:
            break;
        }
    }

    private void getBalance()
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("module", "users");
        map.put("q", "mobile_get_user_result");
        map.put("user_id", UserConfig.getInstance().getUserId(this));
        map.put("method", "get");
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
                    if (response.getString("result").contains("success"))
                    {

                        mAmount_available.setText(
                                df.format(response.getLong("_balance")));
                        mLoading.setVisibility(View.GONE);
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
            mSubmit.setEnabled(s.length() > 0 ? true : false);
            if (s.length() > 0)
            {
                if (s.length() == 1 && s.toString().equals("0")
                        || s.length() == 1 && s.toString().equals("."))
                {
                    mBid_amount.setText("");
                }
                if (Double.valueOf(s.toString()) > mBorrow_account_wait)
                {
                    mBid_amount.setText(mBorrow_account_wait + "");
                    mBid_amount.setSelection(s.length() - 1);
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
    private String mChange_account_all;
    private String mBorrow_password;
    private EditText mPassword;
    private Double mBorrow_account_wait;
    private String mTender_id;

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data)
    {
        switch (resultCode)
        {
        case Constants.SUCCESS:
            getBalance();
            setResult(Constants.SUCCESS);
            break;

        default:
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
