package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.ImageUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.LoadingLayout;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MyPayAccountActivity extends BaseActivity
        implements OnClickListener
{

    private LoadingLayout mLoadingLayout;
    private boolean isFirst = true;
    private TextView mTvAllMoney;
    private TextView mTvFrostMoney;
    private TextView mTvCanMoney;
    private TextView mTvMyAccount;
    private ImageView mIvLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pay_account);
        initActionBar();
        initView();
        initData();
    }

    private void initView()
    {
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

        mTvMyAccount = (TextView) findViewById(R.id.tv_my_pay_account);
        mTvAllMoney = (TextView) findViewById(R.id.tv_my_pay_all_money);
        mTvCanMoney = (TextView) findViewById(R.id.tv_my_pay_can_money);
        mTvFrostMoney = (TextView) findViewById(R.id.tv_my_pay_frost_money);
        mIvLogo = (ImageView) findViewById(R.id.iv_type_logo);
    }

    private void initData()
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        HttpUtil.post(UrlConstants.YIBAO_MY_ACCOUNT, map,
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
                                    mTvMyAccount.setText(
                                            jo.optString("trust_account"));
                                    mTvAllMoney
                                            .setText(StringUtils.getMoneyFormat(
                                                    jo.optString("balance")));
                                    mTvCanMoney.setText(StringUtils
                                            .getMoneyFormat(jo.optString(
                                                    "availableamount")));
                                    mTvFrostMoney.setText(StringUtils
                                            .getMoneyFormat(jo.optString(
                                                    "freezeamount")));
                                    ImageLoader.getInstance().displayImage(jo.optString("image_url"),mIvLogo, ImageUtil.getImageOptions());
                                    if (isFirst)
                                    {
                                        isFirst = false;
                                        mLoadingLayout.setOnStopLoading(
                                                MyPayAccountActivity.this,
                                                null);
                                    }
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
                        if (isFirst)
                        {
                            mLoadingLayout.setOnLoadingError(
                                    MyPayAccountActivity.this, null);
                        }
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });

    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.yibao_my_account_title);
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_pay_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.title_back:
            finish();
            break;

        default:
            break;
        }
    }
}
