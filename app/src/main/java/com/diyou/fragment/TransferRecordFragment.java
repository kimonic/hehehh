package com.diyou.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.activity.CreditorRightsDetailsActivity;
import com.diyou.bean.InvestmentRecordBean;
import com.diyou.http.HttpUtil;
import com.diyou.util.DateUtils;
import com.diyou.util.SubStringTime;
import com.diyou.util.ToastUtil;
import com.diyou.util.ViewHolder;
import com.diyou.v5yibao.R;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TransferRecordFragment extends Fragment
{
    /**
     * 债权转让详情->转让记录
     * 
     * @author harve
     * 
     */
    private ArrayList<InvestmentRecordBean> mArrayList = new ArrayList<InvestmentRecordBean>();
    private BaseAdapter adapter;
    private ListView listview;
    private DecimalFormat df;
    private View mLoading;
    private View mTitlelayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        CreditorRightsDetailsActivity activity = (CreditorRightsDetailsActivity) getActivity();
        container = (ViewGroup) inflater
                .inflate(R.layout.fragment_transferrecord, null);
        df = new DecimalFormat("######0.00");
        initView(container);
        getStyleTitle(activity);
        return container;
    }

    private void initView(ViewGroup container)
    {
        mTitlelayout = container
                .findViewById(R.id.transferrecordfragmen_titlelayout);

        mLoading = container.findViewById(R.id.transferrecordfragment_loading);

        listview = (ListView) container
                .findViewById(R.id.transferrecordfragment_list);
        listview.setFocusable(false);

        adapter = new BaseAdapter()
        {

            @SuppressLint("ViewHolder")
            @Override
            public View getView(int position, View convertView,
                    ViewGroup parent)
            {
                if (convertView == null)
                {
                    convertView = getActivity().getLayoutInflater().inflate(
                            R.layout.item_investmentrecordfragment, null);
                }
                TextView name = ViewHolder.get(convertView,
                        R.id.investmentrecordfragment_item_name);
                TextView money = ViewHolder.get(convertView,
                        R.id.investmentrecordfragment_item_money);
                TextView data = ViewHolder.get(convertView,
                        R.id.investmentrecordfragment_item_data);
                InvestmentRecordBean bean = mArrayList.get(position);
                name.setText(bean.getUsername());
                money.setText("¥" + df.format(bean.getAccount()));
                Date date = new Date(bean.getAddtime() * 1000);
                data.setText(SubStringTime
                        .subStringTime(DateUtils.dateToStrLong(date)));
                return convertView;
            }

            @Override
            public long getItemId(int position)
            {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public Object getItem(int position)
            {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getCount()
            {
                // TODO Auto-generated method stub
                return mArrayList.size();
            }
        };

    }

    public void setListViewHeightBasedOnChildren(ListView listView)
    {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++)
        { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    // 获取待收本息 的类型
    private void getStyleTitle(CreditorRightsDetailsActivity activity)
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("module", "borrow");
        map.put("q", "get_tender_list");
        map.put("method", "get");
        // map.put("borrow_nid", activity.getBorrow_nid());
        map.put("order", "tender_addtime");
        HttpUtil.get(CreateCode.getUrl(map), new JsonHttpResponseHandler()
        {
            @Override
            public void onStart()
            {

                super.onStart();
            }

            @Override
            public void onFinish()
            {

                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    if (response.getString("result").contains("success"))
                    {
                        JSONArray jsonArray = response.getJSONArray("list");
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            InvestmentRecordBean bean = new InvestmentRecordBean();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            bean.setAddtime(jsonObject.getLong("addtime"));
                            bean.setUsername(jsonObject.getString("username"));
                            bean.setAccount(jsonObject.getDouble("account"));
                            mArrayList.add(bean);
                        }
                        listview.setAdapter(adapter);
                        setListViewHeightBasedOnChildren(listview);
                        mLoading.setVisibility(View.GONE);
                        mTitlelayout.setVisibility(View.VISIBLE);
                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                ToastUtil.show("网络请求失败,请稍后在试！");

            }

        });

    }
}
