package com.diyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.diyou.adapter.RechargeRecordAdapter;
import com.diyou.base.BaseActivity;
import com.diyou.bean.RechargeRecordInfo;
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

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class RechargeRecordActivity extends BaseActivity implements
        OnClickListener, OnRefreshListener2<ListView>, AdapterView.OnItemClickListener

{
    private View title_bar;
    private RelativeLayout title_bar_back;
    private TextView title_name;
    private LoadingLayout trade_loadlayout;
    private PullToRefreshListView listView;
    private boolean isFirst = true;
    private int page = 1;
    private int epage = 10;
    private RechargeRecordAdapter adapter;
    private int total_pages;// 总页数
    DecimalFormat df;
    private List<RechargeRecordInfo> listBean = new ArrayList<RechargeRecordInfo>();

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_recharge_record);
        intiView();
        intiData();
        df = new DecimalFormat("######0.00");
    }

    private void intiView() {
        title_bar = findViewById(R.id.trade_actionbar);
        title_bar_back = (RelativeLayout) title_bar
                .findViewById(R.id.title_bar_back);
        title_name = (TextView) title_bar.findViewById(R.id.title_name);
        title_name.setText(R.string.recharge_title_right);
        listView = (PullToRefreshListView) findViewById(R.id.trade_listView);
        adapter = new RechargeRecordAdapter(RechargeRecordActivity.this, listBean);
        listView.setAdapter(adapter);
        trade_loadlayout = (LoadingLayout) findViewById(R.id.trade_loadlayout);
        trade_loadlayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        intiData();
                    }
                });
        title_bar_back.setOnClickListener(this);
        listView.setOnRefreshListener(this);
        listView.setMode(Mode.BOTH);
        listView.setOnItemClickListener(this);

    }

    private void intiData() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance()
                .getLoginToken(RechargeRecordActivity.this));
        map.put("page", String.valueOf(page));
        map.put("epage", String.valueOf(epage));
        HttpUtil.post(UrlConstants.RECHARGERECORD, map,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        if (isFirst) {
                            trade_loadlayout.setOnLoadingError(
                                    RechargeRecordActivity.this, listView);
                        }
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        try {
                            if (CreateCode.AuthentInfo(response)) {
                                JSONObject data = StringUtils
                                        .parseContent(response);
                                total_pages = data.getJSONObject("data")
                                        .getInt("total_pages");
                                if (total_pages == 0) {
                                    ToastUtil.show("没有数据哦");
                                    trade_loadlayout.setOnStopLoading(
                                            RechargeRecordActivity.this, listView);
                                    return;
                                }
//                                String amountTotal = data.getJSONObject("data")
//                                        .getString("amount_total");

                                if (data.optString("result").equals("success")) {
                                    if (page == 1) {
                                        listBean.clear();
                                    }

                                    JSONArray loans = data.optJSONObject("data")
                                            .getJSONArray("items");
                                    for (int i = 0; i < loans.length(); i++) {
                                        RechargeRecordInfo info = new RechargeRecordInfo();
                                        JSONObject object = (JSONObject) loans
                                                .get(i);
                                        info.setAccount(
                                                object.optString("amount"));
                                        info.setStatus(
                                                object.optString("status"));
                                        info.setInd(
                                                object.optString("ind"));
                                        info.setPayment_name(
                                                object.optString("payment_name"));
                                        info.setAdd_time(
                                                object.optString("add_time"));
                                        info.setVerify_remark(
                                                object.optString("verify_remark"));
                                        info.setType(
                                                object.optString("type"));
                                        info.setPayment_nid(
                                                object.optString("payment_nid"));
                                        info.setStatus_name(object.optString("status_name"));
                                        info.setTypeName(object.optString("type_name"));
                                        info.setRemark(object.optString("remark"));
                                        listBean.add(info);
                                    }

                                    adapter.notifyDataSetChanged();

                                } else {
                                    ToastUtil.show(
                                            data.optString("description"));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (isFirst) {
                            trade_loadlayout.setOnStopLoading(
                                    RechargeRecordActivity.this, listView);
                            isFirst = false;
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish() {
                        listView.onRefreshComplete();
                        super.onFinish();
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_bar_back:
                finish();
                break;

        }

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        page = 1;
        intiData();

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                page++;

                if (page <= total_pages) {
                    intiData();
                } else {
                    ToastUtil.show("没有更多数据");
                    listView.onRefreshComplete();

                }
            }
        }, 300);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(position - 1 >= 0){
            RechargeRecordInfo info = listBean.get(position - 1);
            Intent i = new Intent(this, RechargeRecordDetailActivity.class);
            i.putExtra("RechargeInfo", info);
            startActivity(i);
        }
    }
}
