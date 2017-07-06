package com.diyou.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.diyou.activity.InvestmentDetailsActivity;
import com.diyou.activity.MainActivity;
import com.diyou.adapter.InvestmentItemsAdapter;
import com.diyou.bean.EventObject;
import com.diyou.bean.InvestmentListInfo;
import com.diyou.config.UrlConstants;
import com.diyou.http.HttpUtil;
import com.diyou.pulltorefresh.PullToRefreshBase;
import com.diyou.pulltorefresh.PullToRefreshBase.Mode;
import com.diyou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.diyou.pulltorefresh.PullToRefreshListView;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.LoadingLayout;
import com.diyou.view.ViewDialogUtils;
import com.example.encryptionpackages.CreateCode;
import com.lidroid.xutils.util.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import de.greenrobot.event.EventBus;

/**
 * 我要投资-》投资列表
 * @author Administrator
 *
 */
public class ListInvestmentFragment extends Fragment implements
        OnItemClickListener, OnClickListener, OnRefreshListener2<ListView>
{
    private PullToRefreshListView listView;
    private boolean isFirst = true;
    private LoadingLayout investment_loadlayout;
    private InvestmentItemsAdapter adapter;
    private MainActivity mMa;
    private ViewDialogUtils dialog;
    private int page = 1;
    private int epage = 10;
    private int total_pages;// 总页数
    private List<InvestmentListInfo> listBean = new ArrayList<InvestmentListInfo>();

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        container = (ViewGroup) inflater
                .inflate(R.layout.fragment_listinvestment, null);
        initView(container);
        mMa = (MainActivity) getActivity();
        EventBus.getDefault().register(this);
        return container;
    }

    /**
     * eventBus事件处理
     *
     * onEventPostThread， onEventMainThread，onEventBackgroundThread，onEventAsync
     */
    public void onEventMainThread(EventObject object)
    {
        LogUtils.e("刷新投资列表");
        if (object != null)
        {
            if ("investSuccess".equals(object.getAction()))
            {
                updateData();
            }
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        if (isVisibleToUser && isFirst)
        {
            getInvestmentList();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void initView(ViewGroup container)
    {
        dialog = new ViewDialogUtils(getActivity(), R.style.MyDialog);
        listView = (PullToRefreshListView) container
                .findViewById(R.id.investment_listView);
        adapter = new InvestmentItemsAdapter(listBean, getActivity());
        listView.setAdapter(adapter);
        investment_loadlayout = (LoadingLayout) container
                .findViewById(R.id.investment_loadlayout);
        investment_loadlayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        getInvestmentList();
                    }
                });
        listView.setOnRefreshListener(this);
        listView.setMode(Mode.BOTH);
        listView.setOnItemClickListener(this);
    }

    /**
     * listView的点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        Intent intent = new Intent();
        InvestmentListInfo info = listBean.get(position - 1);
        intent.putExtra("loan_id", info.getId());
        intent.putExtra("loan_url", info.getUrl());
        intent.putExtra("apr", info.getApr());
        intent.putExtra("category_id", info.getCategory_id());
        intent.putExtra("category_type", info.getCategory_type());
        intent.putExtra("status", info.getStatus());
        intent.putExtra("status_name", info.getStatus_name());
        intent.putExtra("progress", info.getProgress());
        intent.putExtra("additional_status", info.getAdditionalStatus());
        intent.putExtra("additional_apr", info.getAdditional_apr());
        intent.putExtra("share_url", info.getShare_url());
        intent.putExtra("share_title", info.getShare_title());
        intent.putExtra("share_content", info.getShare_content());
        if (info.getCategory_id().equals("3"))
        {
            intent.putExtra("period", transfor2mouth(info.getPeriod()));
        }
        else
        {
            intent.putExtra("period", info.getPeriod());
        }
        intent.setClass(getActivity(), InvestmentDetailsActivity.class);
        mMa.startActivity(intent);

    }
    
    private String transfor2mouth(String str)
    {

        return Integer.parseInt(str) % 30 > 0
                ? "" + (Integer.parseInt(str) / 30 + 1)
                : "" + Integer.parseInt(str) / 30;
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.calculator_dialog_false:
            dialog.Closedialog();
            break;
        default:
            break;
        }

    }

    private void getInvestmentList()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("page", String.valueOf(page));
        map.put("epage", String.valueOf(epage));
        HttpUtil.post(UrlConstants.TENDER_INDEX, map,
                new JsonHttpResponseHandler()
                {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        if (isFirst)
                        {
                            investment_loadlayout.setOnLoadingError(mMa,
                                    listView);
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
                                    investment_loadlayout.setOnStopLoading(mMa,
                                            listView);
                                    return;
                                }
                                if (data.get("result").equals("success"))
                                {
                                    if (page == 1)
                                    {
                                        listBean.clear();
                                    }

                                    JSONArray loans = data.getJSONObject("data")
                                            .getJSONArray("items");
                                    for (int i = 0; i < loans.length(); i++)
                                    {
                                        InvestmentListInfo bean = new InvestmentListInfo();
                                        JSONObject object = (JSONObject) loans
                                                .get(i);
                                        bean.setId(object.optString("id"));
                                        bean.setPic(object.optString("pic"));
                                        bean.setUrl(object.optString("url"));
                                        bean.setAmount(
                                                object.optString("amount"));
                                        bean.setName(object.optString("name"));
                                        bean.setApr(object.optString("apr"));
                                        bean.setCategory_id(object
                                                .optString("category_id"));
                                        bean.setCategory_type(object.optString("category_type"));
                                        bean.setPeriod(
                                                object.optString("period"));
                                        bean.setProgress(
                                                object.optString("progress"));
                                        bean.setTender_count(object
                                                .optString("tender_count"));
                                        bean.setAward_status(object
                                                .optString("award_status"));
                                        bean.setIs_company(
                                                object.optString("is_company"));
                                        bean.setCompany_name(object
                                                .optString("company_name"));
                                        bean.setVouch_company_name(object
                                                .optString("vouch_company_name"));
                                        bean.setStatus(object
                                                .optString("status"));
                                        bean.setStatus_name(object
                                                .optString("status_name"));
                                        bean.setRepay_type(object.optString("repay_type"));
                                        bean.setShare_url(object.optString("share_url"));
                                        bean.setShare_title(object.optString("share_title"));
                                        bean.setShare_content(object.optString("share_content"));
                                        bean.setAdditionalStatus(object.optString("additional_status"));
                                        bean.setAdditional_apr(object.optString("additional_apr"));
                                        listBean.add(bean);
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
                            investment_loadlayout.setOnStopLoading(mMa,
                                    listView);
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

    // 下拉刷新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
    {
        updateData();
    }

    private void updateData() {
        page = 1;
        getInvestmentList();
    }

    // 上拉刷新
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
                    getInvestmentList();
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
