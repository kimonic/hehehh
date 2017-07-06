package com.diyou.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.adapter.MyPromotionAdapter;
import com.diyou.base.BaseActivity;
import com.diyou.bean.MyPromotionItem;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.pulltorefresh.PullToRefreshBase;
import com.diyou.pulltorefresh.PullToRefreshBase.Mode;
import com.diyou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.diyou.pulltorefresh.PullToRefreshScrollView;
import com.diyou.util.ShareSDKUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.ListViewForScrollView;
import com.diyou.view.LoadingLayout;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 我的推广
 * 
 * @author Administrator
 *
 */
public class MyPromotionActivity extends BaseActivity implements
        OnClickListener, OnRefreshListener2<ScrollView>, OnItemClickListener
{

    public static final String EPAGE = "10";

    private PullToRefreshScrollView mScrollView;
    private TextView mTvPersonNum;
    private TextView mTvInvestNum;
    private TextView mTvInvestMoney;
    private TextView mTvBorrowRefund;
    private TextView mTvResidueMoney;

    private LoadingLayout mLoadingLayout;
    private boolean isFirst = true;
    private int mPage = 1;

    private ListViewForScrollView mListView;

    private List<MyPromotionItem> mList = new ArrayList<MyPromotionItem>();

    private MyPromotionAdapter mAdapter;

    private TextView mTvLimit;

    private TextView mTvSettling;

    private Button mBtnSettle;

    private String mPromotionUrl;

    private String mPromotionTitle;

    private String mPromotionContent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_promotion);
        initActionBar();
        initView();
        requestPageData();
    }

    private void initView()
    {
        mScrollView = (PullToRefreshScrollView) findViewById(
                R.id.sv_my_promotion);
        mScrollView.setOnRefreshListener(this);
        mScrollView.setMode(Mode.BOTH);
        findViewById(R.id.tv_promotion_record).setOnClickListener(this);
        findViewById(R.id.tv_promotion_settlement_record)
                .setOnClickListener(this);

        mListView = (ListViewForScrollView) findViewById(R.id.lv_my_promotion);
        mListView.setOnItemClickListener(this);
        mAdapter = new MyPromotionAdapter(this, mList);
        mListView.setAdapter(mAdapter);

        mTvPersonNum = (TextView) findViewById(R.id.tv_promotion_person_num);
        mTvInvestNum = (TextView) findViewById(R.id.tv_promotion_invest_sum);
        mTvInvestMoney = (TextView) findViewById(
                R.id.tv_promotion_invest_money);
        mTvBorrowRefund = (TextView) findViewById(
                R.id.tv_promotion_borrow_refund);

        mTvResidueMoney = (TextView) findViewById(
                R.id.tv_promotion_residue_money);
        mTvLimit = (TextView) findViewById(R.id.tv_promotion_limit);
        mTvSettling = (TextView) findViewById(R.id.tv_promotion_clearing);

        mBtnSettle = (Button) findViewById(R.id.btn_promotion_settle_now);
        mBtnSettle.setOnClickListener(this);
        mLoadingLayout = (LoadingLayout) findViewById(
                R.id.fund_detail_loadlayout);
        mLoadingLayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        requestPageData();
                    }
                });
        mLoadingLayout.setOnStartLoading(mScrollView);
    }

    private void requestPageData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("page", "" + mPage);
        map.put("epage", EPAGE);
        HttpUtil.post(UrlConstants.MY_PROMOTION, map,
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
                                    JSONArray ja = jo.optJSONArray("items");
                                    setView(jo);

                                    if (mPage > jo.optInt("total_pages"))
                                    {
                                        ToastUtil.show("无更多记录");
                                    }
                                    else
                                    {
                                        for (int i = 0; i < ja.length(); i++)
                                        {
                                            JSONObject pJo = ja
                                                    .optJSONObject(i);
                                            mList.add(new MyPromotionItem(
                                                    pJo.optString(
                                                            "spreaded_member_name"),
                                                    pJo.optString(
                                                            "tender_success_amount"),
                                                    pJo.optString(
                                                            "repay_amount_yes")));
                                        }
                                        mPage++;
                                        mAdapter.refreshData();
                                    }
                                    if (isFirst)
                                    {
                                        isFirst = false;
                                        mLoadingLayout.setOnStopLoading(
                                                MyPromotionActivity.this,
                                                mScrollView);
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
                        mScrollView.onRefreshComplete();
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        if (isFirst)
                        {
                            mLoadingLayout.setOnLoadingError(
                                    MyPromotionActivity.this, mScrollView);
                        }
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });

    }

    private void updateData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("page", "1");
        map.put("epage", EPAGE);
        HttpUtil.post(UrlConstants.MY_PROMOTION, map,
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
                                    JSONArray ja = jo.optJSONArray("items");
                                    setView(jo);
                                    mList.clear();
                                    if (1 > jo.optInt("total_pages"))
                                    {
                                        ToastUtil.show("无更多记录");
                                    }
                                    else
                                    {
                                        for (int i = 0; i < ja.length(); i++)
                                        {
                                            JSONObject pJo = ja
                                                    .optJSONObject(i);
                                            mList.add(new MyPromotionItem(
                                                    pJo.optString(
                                                            "spreaded_member_name"),
                                                    pJo.optString(
                                                            "tender_success_amount"),
                                                    pJo.optString(
                                                            "repay_amount_yes")));
                                        }
                                    }
                                    mPage = 2;
                                    mAdapter.refreshData();
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
                        mScrollView.onRefreshComplete();
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

    private void setView(JSONObject jo)
    {
        JSONObject settlementjo = jo.optJSONObject("total");

        mTvPersonNum.setText(jo.optInt("person_count") + "人");
        mTvInvestMoney.setText(
                StringUtils.getDoubleFormat(jo.optString("tender_income"))
                        + "元");
        mTvInvestNum.setText(
                StringUtils.getDoubleFormat(jo.optString("income")) + "元");
        mTvBorrowRefund.setText(
                StringUtils.getDoubleFormat(jo.optString("repay_income"))
                        + "元");
        mPromotionUrl = jo.optString("share_url");
        mPromotionTitle = jo.optString("share_title");
        mPromotionContent = jo.optString("share_content");
        mTvLimit.setText(jo.optString("limit") + "元");
        mTvResidueMoney.setText(StringUtils
                .getDoubleFormat(settlementjo.optString("unAaccount")) + "元");
        mTvSettling.setText(StringUtils
                .getDoubleFormat(settlementjo.optString("accounting")) + "元");

        if (Double.valueOf(settlementjo.optString("unAaccount")) >= Double
                .valueOf(jo.optString("limit")))
        {
            mBtnSettle.setEnabled(true);
        }
        else
        {
            mBtnSettle.setEnabled(false);
        }
    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.my_promotion_title);
        findViewById(R.id.title_back).setOnClickListener(this);
        View rightView = findViewById(R.id.rl_title_bar_right);
        rightView.setVisibility(View.VISIBLE);
        rightView.setOnClickListener(this);
        TextView tvTitleRight = (TextView) findViewById(
                R.id.tv_title_bar_right);
        tvTitleRight.setText(R.string.my_promotion_title_right);
        tvTitleRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.title_back:
            finish();
            break;
        case R.id.tv_promotion_record:// 推广记录
            startActivity(new Intent(this, PromotionRecordActivity.class));
            break;
        case R.id.tv_promotion_settlement_record:// 结算记录
            startActivity(new Intent(this, SettlementRecordActivity.class));
            break;
        case R.id.btn_promotion_settle_now:
            requestYibaoRegistered(new Runnable()
            {
                @Override
                public void run()
                {
                    requestSettle();
                }
            });
            break;
        case R.id.tv_title_bar_right:// 推广赚钱
            if (!StringUtils.isEmpty2(mPromotionUrl))
            {
                ShareSDKUtil.getInstance(this).share(mPromotionTitle, mPromotionContent, mPromotionUrl);
            }
            else
            {
                ToastUtil.show(R.string.remind_promotion_url_error);
            }
            break;
        default:
            break;
        }
    }

    private void requestSettle()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        HttpUtil.post(UrlConstants.SETTLEMENT_NOW, map,
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
                                    ToastUtil.show("结算成功");
                                    updateData();
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

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView)
    {
        updateData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView)
    {
        requestPageData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        MyPromotionItem info = mList.get(position);
        Intent intent = new Intent(this, PromotionRecordActivity.class);
        intent.putExtra("name", info.getSpreaded_member_name());
        startActivity(intent);
    }
}
