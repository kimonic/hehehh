package com.diyou.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.adapter.InsideLetterAdapter;
import com.diyou.base.BaseActivity;
import com.diyou.bean.InsideLetterInfo;
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

public class InsideLetterActivity extends BaseActivity implements
        OnClickListener, OnRefreshListener2<ListView>, OnItemClickListener

{
    private View title_bar;
    private RelativeLayout title_bar_back;
    private TextView title_name;
    private LoadingLayout inside_loadlayout;
    private PullToRefreshListView listView;
    private boolean isFirst = true;
    private int page = 1;
    private int epage = 10;
    private int total_pages;// 总页数
    private List<InsideLetterInfo> listBean = new ArrayList<InsideLetterInfo>();
    private InsideLetterAdapter adapter;

    @Override
    protected void onCreate(Bundle arg0)
    {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_inside_letter);
        intiView();
        intiData();
    }

    private void intiView()
    {
        title_bar = findViewById(R.id.inside_actionbar);
        title_bar_back = (RelativeLayout) title_bar
                .findViewById(R.id.title_bar_back);
        title_name = (TextView) title_bar.findViewById(R.id.title_name);
        title_name.setText(R.string.inside_letter_title);
        listView = (PullToRefreshListView) findViewById(R.id.inside_listView);
        adapter = new InsideLetterAdapter(InsideLetterActivity.this, listBean);
        listView.setAdapter(adapter);
        inside_loadlayout = (LoadingLayout) findViewById(
                R.id.inside_loadlayout);
        inside_loadlayout.getReloadingTextView()
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
                .getLoginToken(InsideLetterActivity.this));
        map.put("page", String.valueOf(page));
        HttpUtil.post(UrlConstants.INSIDELETTER, map,
                new JsonHttpResponseHandler()
                {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        if (isFirst)
                        {
                            inside_loadlayout.setOnLoadingError(
                                    InsideLetterActivity.this, listView);
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
                                    ToastUtil.show("暂无数据");
                                    inside_loadlayout.setOnStopLoading(
                                            InsideLetterActivity.this,
                                            listView);
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
                                        InsideLetterInfo info = new InsideLetterInfo();
                                        JSONObject object = (JSONObject) loans
                                                .get(i);
                                        info.setId(object.optString("id"));
                                        info.setSend_time(
                                                object.optString("send_time"));
                                        info.setStatus(
                                                object.optInt("status"));
                                        info.setTitle(
                                                object.optString("title"));
                                        info.setContents(
                                                object.optString("contents"));
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
                            inside_loadlayout.setOnStopLoading(
                                    InsideLetterActivity.this, listView);
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
        Intent intent = new Intent();
        intent.setClass(InsideLetterActivity.this, InsideLetterDetailsActivity.class);
        InsideLetterInfo info = listBean.get(position - 1);
        intent.putExtra("title", info.getTitle());
        intent.putExtra("time", info.getSend_time());
        intent.putExtra("contents", info.getContents());
        startActivity(intent);
        if(info.getStatus()==1){
            updateReadStatus(info);
        }

    }

    private void updateReadStatus(final InsideLetterInfo info){

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("id", info.getId());
        HttpUtil.post(UrlConstants.INSIDELETTER_STATUS, map, new JsonHttpResponseHandler()
        {
            @Override
            public void onFinish()
            {
//                hideProgressDialog();
                super.onFinish();
            }

            @Override
            public void onStart()
            {
//                showProgressDialog();
                super.onStart();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
//                ToastUtil.show(R.string.generic_error);
                super.onFailure(statusCode, headers, throwable, errorResponse);
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
                        if ("success".equals(json.getString("result")))
                        {
                            info.setStatus(2); 
                            adapter.notifyDataSetChanged();
                        }
//                        else
//                        {
//                            ToastUtil.show(json.getString("description"));
//                        }
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }
        });
    
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
