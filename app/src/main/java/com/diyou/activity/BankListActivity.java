package com.diyou.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.adapter.BankAdapter;
import com.diyou.base.BaseActivity;
import com.diyou.bean.BankInfo;
import com.diyou.config.Constants;
import com.diyou.config.UrlConstants;
import com.diyou.http.HttpUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.LoadingLayout;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class BankListActivity extends BaseActivity
        implements OnClickListener, OnItemClickListener
{

    private List<BankInfo> mList = new ArrayList<BankInfo>();
    private BankAdapter mAdapter;
    private LoadingLayout mLoadingLayout;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_list);
        initActionBar();
        initView();
        requestBankList();
    }

    private void requestBankList()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        HttpUtil.post(UrlConstants.BANK_LIST_INFO, map,
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
                                    JSONArray ja = json.optJSONArray("data");
                                    mList.clear();
                                    for (int i = 0; i < ja.length(); i++)
                                    {
                                        JSONObject jo = ja.optJSONObject(i);
                                        mList.add(new BankInfo(
                                                jo.optString("code"),
                                                jo.optString("name"),
                                                jo.optString("url")));
                                    }
                                    mAdapter.refreshData();
                                    mLoadingLayout.setOnStopLoading(
                                            BankListActivity.this, mListView);
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
                        // mScrollView.onRefreshComplete();
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        mLoadingLayout.setOnLoadingError(BankListActivity.this,
                                mListView);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });

    }

    private void initView()
    {
        mListView = (ListView) findViewById(R.id.lv_bank_list);
        mAdapter = new BankAdapter(this, mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mLoadingLayout = (LoadingLayout) findViewById(
                R.id.fund_detail_loadlayout);
        mLoadingLayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        requestBankList();
                    }
                });
        mLoadingLayout.setOnStartLoading(mListView);
    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.bank_list_title);
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
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        BankInfo bankInfo = mList.get(position);
        Intent data = new Intent();
        data.putExtra("bankInfo", bankInfo);
        setResult(Constants.RESULT_CODE_BANK, data);
        finish();
    }
}
