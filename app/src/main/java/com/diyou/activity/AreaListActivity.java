package com.diyou.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.adapter.PinnedHeaderExpandableAdapter;
import com.diyou.base.BaseActivity;
import com.diyou.bean.CityInfo;
import com.diyou.bean.ProvinceInfo;
import com.diyou.config.Constants;
import com.diyou.config.UrlConstants;
import com.diyou.http.HttpUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.LoadingLayout;
import com.diyou.view.PinnedHeaderExpandableListView;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class AreaListActivity extends BaseActivity
        implements OnClickListener, OnChildClickListener
{

    private List<ProvinceInfo> mList = new ArrayList<ProvinceInfo>();
    private PinnedHeaderExpandableAdapter mAdapter;
    private boolean isFirst = true;
    private LoadingLayout mLoadingLayout;
    private PinnedHeaderExpandableListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_list);
        initActionBar();
        initView();
        requestCityInfo();
    }

    private void initView()
    {
        mListView = (PinnedHeaderExpandableListView) findViewById(R.id.lv_area);
        // 设置悬浮头部VIEW
        mListView.setHeaderView(
                getLayoutInflater().inflate(R.layout.group, null));
        mAdapter = new PinnedHeaderExpandableAdapter(mList, this, mListView);
        mListView.setAdapter(mAdapter);
        mListView.setOnChildClickListener(this);
        mLoadingLayout = (LoadingLayout) findViewById(
                R.id.fund_detail_loadlayout);
        mLoadingLayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        requestCityInfo();
                    }
                });
        mLoadingLayout.setOnStartLoading(mListView);
    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.area_list_title);
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

    private void requestCityInfo()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        HttpUtil.post(UrlConstants.PROVINCE_CITY_INFO, map,
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
                                        JSONObject joProvince = ja
                                                .optJSONObject(i);
                                        JSONArray jaCity = joProvince
                                                .optJSONArray("city_list");
                                        List<CityInfo> list = new ArrayList<CityInfo>();
                                        for (int j = 0; j < jaCity
                                                .length(); j++)
                                        {
                                            JSONObject joCity = jaCity
                                                    .optJSONObject(j);
                                            CityInfo cityInfo = new CityInfo(
                                                    joCity.optString("id"),
                                                    joCity.optString("pid"),
                                                    joCity.optString("name"),
                                                    joCity.optString(
                                                            "province"),
                                                    joCity.optString("city"),
                                                    joCity.optString(
                                                            "sort_index"));
                                            list.add(cityInfo);
                                            // Log.e("----------",
                                            // "list大小："+list.size());
                                        }
                                        mList.add(new ProvinceInfo(
                                                joProvince.optString("id"),
                                                joProvince.optString("name"),
                                                joProvince
                                                        .optString("province"),
                                                list, joProvince.optString(
                                                        "sort_index")));
                                    }
                                    // Log.e("----------",
                                    // "mList大小："+mList.size());
                                    mAdapter.notifyDataSetChanged();
                                    if (isFirst)
                                    {
                                        isFirst = false;
                                        mLoadingLayout.setOnStopLoading(
                                                AreaListActivity.this,
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
                        // mScrollView.onRefreshComplete();
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        if (isFirst)
                        {
                            mLoadingLayout.setOnLoadingError(
                                    AreaListActivity.this, mListView);
                        }
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });

    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
            int groupPosition, int childPosition, long id)
    {
        ProvinceInfo provinceInfo = mList.get(groupPosition);
        Intent data = new Intent();
        data.putExtra("provinceName", provinceInfo.getName());
        data.putExtra("cityInfo", provinceInfo.getCitys().get(childPosition));
        setResult(Constants.RESULT_CODE_PROVINCE_CITY, data);
        finish();
        return true;
    }

}
