package com.diyou.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.diyou.adapter.TradeRecordAdapter;
import com.diyou.base.BaseActivity;
import com.diyou.bean.TradeRecordInfo;
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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TradeRecordActivity extends BaseActivity implements
        OnClickListener, OnRefreshListener2<ListView>, OnItemClickListener

{
    private View title_bar;
    private RelativeLayout title_bar_back;
    private TextView title_name;
    private LoadingLayout trade_loadlayout;
    private PullToRefreshListView listView;
    private boolean isFirst = true;
    private int page = 1;
    private int epage = 10;
    private TradeRecordAdapter adapter;
    private int total_pages;// 总页数
    private List<TradeRecordInfo> listBean = new ArrayList<TradeRecordInfo>();

    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_trade_record);
        intiView();
        intiData();
    }

    private void intiView()
    {
        title_bar = findViewById(R.id.trade_actionbar);
        title_bar_back = (RelativeLayout) title_bar
                .findViewById(R.id.title_bar_back);
        title_name = (TextView) title_bar.findViewById(R.id.title_name);
        title_name.setText("交易记录");
        listView = (PullToRefreshListView) findViewById(R.id.trade_listView);
        adapter = new TradeRecordAdapter(TradeRecordActivity.this, listBean);
        listView.setAdapter(adapter);
        trade_loadlayout = (LoadingLayout) findViewById(R.id.trade_loadlayout);
        trade_loadlayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        intiData();
                    }
                });
        title_bar_back.setOnClickListener(this);
        listView.setOnRefreshListener(this);
        listView.setMode(Mode.BOTH);
        listView.setOnItemClickListener(this);

    }

    private void intiData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance()
                .getLoginToken(TradeRecordActivity.this));
        map.put("page", String.valueOf(page));
        map.put("epage", String.valueOf(epage));
        HttpUtil.post(UrlConstants.TRADERECORD, map,
                new JsonHttpResponseHandler()
                {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        if (isFirst)
                        {
                            trade_loadlayout.setOnLoadingError(
                                    TradeRecordActivity.this, listView);
                        }
                        listView.onRefreshComplete();
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                            JSONObject response)
                    {
                        try
                        {
                            if (CreateCode.AuthentInfo(response))
                            {
                                JSONObject data = StringUtils
                                        .parseContent(response);
                                total_pages = data.getJSONObject("data")
                                        .getInt("total_pages");
                                if (total_pages == 0)
                                {
                                    ToastUtil.show("没有数据哦");
                                    trade_loadlayout.setOnStopLoading(
                                            TradeRecordActivity.this, listView);
                                    return;
                                }
                                if (data.optString("result").equals("success"))
                                {
                                    if (page == 1)
                                    {
                                        listBean.clear();
                                    }

                                    JSONArray loans = data.getJSONObject("data")
                                            .getJSONArray("items");
                                    for (int i = 0; i < loans.length(); i++)
                                    {
                                        TradeRecordInfo info = new TradeRecordInfo();
                                        JSONObject object = (JSONObject) loans
                                                .get(i);
                                        info.setFee_id(
                                                object.optString("fee_id"));
                                        info.setId(object.optString("id"));
                                        info.setFee_name(
                                                object.optString("fee_name"));
                                        info.setAdd_time(
                                                object.optString("add_time"));
                                        info.setBalance(
                                                object.optString("balance"));
                                        info.setFreeze(object
                                                .optString("amount_money"));
                                        info.setNew_balance(
                                                object.optInt("new_balance"));
                                        info.setUrl(object
                                                .optString("loan_info_url"));
                                        info.setProgress(object
                                                .optString("loan_progress"));
                                        info.setLoan_name(
                                                object.optString("loan_name"));
                                        info.setRemark(object.optString("remark"));
                                        listBean.add(info);
                                    }

                                    adapter.notifyDataSetChanged();

                                }
                                else
                                {
                                    ToastUtil.show(
                                            data.optString("description"));
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        if (isFirst)
                        {
                            trade_loadlayout.setOnStopLoading(
                                    TradeRecordActivity.this, listView);
                            isFirst = false;
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish()
                    {
                        listView.onRefreshComplete();
                        super.onFinish();
                    }

                    @Override
                    public void onStart()
                    {
                        super.onStart();
                    }

                });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.title_bar_back:
            finish();
            break;

        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
//        if (listBean.get(position - 1).getFee_id().equals("303"))
//        {
//            Intent intent = new Intent(TradeRecordActivity.this,
//                    InvestmentDetailsActivity.class);
//            intent.putExtra("loan_url", listBean.get(position).getUrl());
//            intent.putExtra("progress", listBean.get(position).getProgress());
//            startActivity(intent);
//        }

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
    {
        page = 1;
        intiData();

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
    {
        listView.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                page++;

                if (page <= total_pages)
                {
                    intiData();
                }
                else
                {
                    ToastUtil.show("没有更多数据");
                    listView.onRefreshComplete();

                }
            }
        }, 300);

    }

}
