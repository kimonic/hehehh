package com.diyou.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.activity.MyInvestmentDetailActivity;
import com.diyou.adapter.MyProjectAdapter;
import com.diyou.bean.MyInvestmentInfo;
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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 我的项目-》我的投资
 * 
 * @author Administrator
 *
 */
public class MyInvestmentFragment extends Fragment implements
        OnRefreshListener2<ListView>, OnClickListener, OnItemClickListener
{
    public static final String EPAGE = "10";
    private PullToRefreshListView mRefreshListView;
    private LoadingLayout mLoadingLayout;
    private List<MyInvestmentInfo> myInvestList = new ArrayList<MyInvestmentInfo>();
    private int mPage = 1;
    private boolean isFirst = true;
    private MyProjectAdapter mAdapter;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_my_investment, null);
        initView();
        requestPageData();
        return view;
    }

    private void initView()
    {
        mRefreshListView = (PullToRefreshListView) view
                .findViewById(R.id.lv_my_project);
        mRefreshListView.setOnRefreshListener(this);
        mRefreshListView.setMode(Mode.BOTH);
        mLoadingLayout = (LoadingLayout) view
                .findViewById(R.id.my_investment_loadlayout);
        mLoadingLayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        if (isFirst)
                            requestPageData();
                    }
                });
        mLoadingLayout.setOnStartLoading(mRefreshListView);
        ListView mListView = mRefreshListView.getRefreshableView();
        mAdapter = new MyProjectAdapter(getActivity(), myInvestList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

    }

    private int mTotalPage = 0;

    private void requestPageData()
    {
        if (!isFirst)
        {
            if (mPage > mTotalPage)
            {
                ToastUtil.show("无更多数据");
                mRefreshListView.onRefreshComplete();
                return;
            }
        }
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token",
                UserConfig.getInstance().getLoginToken(getActivity()));
        map.put("page", "" + mPage);
        map.put("epage", EPAGE);
        HttpUtil.post(UrlConstants.MY_INVESTMENT, map,
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
                                    if (isFirst)
                                    {
                                        isFirst = false;
                                        mTotalPage = jo.optInt("total_pages");
                                        mLoadingLayout.setOnStopLoading(
                                                getActivity(),
                                                mRefreshListView);
                                    }
                                    addInvestInfo(jo);
                                    mPage++;
                                    mAdapter.notifyDataSetChanged();

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
                        mRefreshListView.onRefreshComplete();
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            String responseString, Throwable throwable)
                    {
                        if (isFirst)
                        {
                            mLoadingLayout.setOnLoadingError(getActivity(),
                                    mRefreshListView);
                        }
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                    }
                });

    }

    private void updateData()
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token",
                UserConfig.getInstance().getLoginToken(getActivity()));
        map.put("page", "" + 1);
        map.put("epage", EPAGE);
        HttpUtil.post(UrlConstants.MY_INVESTMENT, map,
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
                                    myInvestList.clear();
                                    JSONObject jo = new JSONObject(
                                            json.optString("data"));
                                    mTotalPage = jo.optInt("total_pages");
                                    if (1 > jo.optInt("total_pages"))
                                    {
                                        ToastUtil.show("无更多数据");
                                        mAdapter.notifyDataSetChanged();
                                        return;
                                    }

                                    addInvestInfo(jo);
                                    mPage = 2;
                                    mAdapter.notifyDataSetChanged();
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
                        mRefreshListView.onRefreshComplete();
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            String responseString, Throwable throwable)
                    {
                        ToastUtil.show(responseString);
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                    }
                });

    }

    private void addInvestInfo(JSONObject jo) throws JSONException
    {
        JSONArray ja = jo.getJSONArray("items");
        for (int i = 0; i < ja.length(); i++)
        {
            JSONObject pJson = ja.getJSONObject(i);
            MyInvestmentInfo info = new MyInvestmentInfo(pJson.optString("id"),
                    pJson.optString("loan_name"), pJson.optString("add_time"),
                    pJson.optString("amount"),
                    pJson.optString("award_interest"),
                    pJson.optString("status_name"), pJson.optString("url"));
            myInvestList.add(info);
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

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        Intent intent = new Intent(getActivity(),MyInvestmentDetailActivity.class);
        MyInvestmentInfo info = myInvestList.get(position-1);
        intent.putExtra("id", info.getId());
        intent.putExtra("status_name", info.getStatus_name());
        getActivity().startActivity(intent);
    }
}
