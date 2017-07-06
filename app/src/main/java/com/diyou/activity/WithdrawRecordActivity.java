package com.diyou.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.adapter.WithdrawRecordAdapter;
import com.diyou.base.BaseActivity;
import com.diyou.bean.WithdrawRecordInfo;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class WithdrawRecordActivity extends BaseActivity implements
        OnRefreshListener2<ListView>, OnItemClickListener, OnClickListener
{
    public static final String EPAGE = "10";
    private PullToRefreshListView mListView;
    private List<WithdrawRecordInfo> mList = new ArrayList<WithdrawRecordInfo>();
    private WithdrawRecordAdapter mAdapter;
    private LoadingLayout mLoadingLayout;
    private boolean isFirst = true;
    private int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_record);
        initView();
        initActionBar();
        requestPageData();
    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.withdraw_title_right);
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    private void requestPageData()
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("page", "" + mPage);
        map.put("epage", EPAGE);
        HttpUtil.post(UrlConstants.WITHDRAW_RECORD, map,
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
                                            mList.add(new WithdrawRecordInfo(
                                                    pJo.optString("add_time"),
                                                    pJo.optString("status"),
                                                    pJo.optString("status_name"),
                                                    pJo.optString("amount")));
                                        }
                                        mPage++;
                                        mAdapter.refreshData();
                                    }
                                    if (isFirst)
                                    {
                                        isFirst = false;
                                        mLoadingLayout.setOnStopLoading(
                                                WithdrawRecordActivity.this,
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
                                    WithdrawRecordActivity.this, mListView);
                        }
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });

    }

    private void initView()
    {

        mListView = (PullToRefreshListView) findViewById(
                R.id.lv_withdraw_record);
        mListView.setOnRefreshListener(this);
        mListView.setMode(Mode.BOTH);
        mListView.setOnItemClickListener(this);
        mAdapter = new WithdrawRecordAdapter(this, mList);
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

    private void updateData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("page", "1");
        map.put("epage", EPAGE);
        HttpUtil.post(UrlConstants.WITHDRAW_RECORD, map,
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
                                            mList.add(new WithdrawRecordInfo(
                                                    pJo.optString("add_time"),
                                                    pJo.optString("status"),
                                                    pJo.optString("status_name"),
                                                    pJo.optString("amount")));
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        // TODO Auto-generated method stub

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
