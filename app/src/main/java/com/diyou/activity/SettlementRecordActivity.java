package com.diyou.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.adapter.SettlementRecordAdapter;
import com.diyou.base.BaseActivity;
import com.diyou.bean.SettlementRecordInfo;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.pulltorefresh.PullToRefreshBase;
import com.diyou.pulltorefresh.PullToRefreshBase.Mode;
import com.diyou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.diyou.pulltorefresh.PullToRefreshListView;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.LoadingLayout;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class SettlementRecordActivity extends BaseActivity
        implements OnClickListener, OnRefreshListener2<ListView>
{

    private PullToRefreshListView mListView;
    private SettlementRecordAdapter mAdapter;
    private List<SettlementRecordInfo> mList = new ArrayList<SettlementRecordInfo>();
    private LoadingLayout mLoadingLayout;
    private boolean isFirst = true;
    private int mPage = 1;
    public static final String EPAGE = "10";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement_record);
        initActionBar();
        initView();
        requestPageData();
    }

    private void initView()
    {
        mListView = (PullToRefreshListView) findViewById(
                R.id.lv_settlement_record);
        mListView.setOnRefreshListener(this);
        mListView.setMode(Mode.BOTH);
        // mListView.setOnItemClickListener(this);
        mAdapter = new SettlementRecordAdapter(this, mList);
        mListView.setAdapter(mAdapter);

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
        mLoadingLayout.setOnStartLoading(mListView);
    }

    private void requestPageData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("page", "" + mPage);
        map.put("epage", EPAGE);
        HttpUtil.post(UrlConstants.SETTLEMENT_RECORD, map,
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
                                            mList.add(new SettlementRecordInfo(
                                                    pJo.optString("money"),
                                                    pJo.optString("add_time"),
                                                    pJo.optString("status")));
                                        }
                                        mPage++;
                                        mAdapter.refreshData();
                                    }
                                    if (isFirst)
                                    {
                                        isFirst = false;
                                        mLoadingLayout.setOnStopLoading(
                                                SettlementRecordActivity.this,
                                                mListView);
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
                        mListView.onRefreshComplete();
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        if (isFirst)
                        {
                            mLoadingLayout.setOnLoadingError(
                                    SettlementRecordActivity.this, mListView);
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
        HttpUtil.post(UrlConstants.SETTLEMENT_RECORD, map,
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
                                            mList.add(new SettlementRecordInfo(
                                                    pJo.optString("money"),
                                                    pJo.optString("add_time"),
                                                    pJo.optString("status")));
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
                        mListView.onRefreshComplete();
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
        tvTitle.setText(R.string.my_promotion_clear_record);
        findViewById(R.id.title_back).setOnClickListener(this);
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

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
    {
        updateData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
    {
        requestPageData();
    }

}
