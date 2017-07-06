package com.diyou.fragment;

import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.activity.CreditorRightsDetailsActivity;
import com.diyou.adapter.ListCreditorRightsTransferAdapter;
import com.diyou.application.MyApplication;
import com.diyou.bean.CreditorRightsTransferBean;
import com.diyou.bean.EventObject;
import com.diyou.config.UrlConstants;
import com.diyou.http.HttpUtil;
import com.diyou.pulltorefresh.PullToRefreshBase;
import com.diyou.pulltorefresh.PullToRefreshBase.Mode;
import com.diyou.pulltorefresh.PullToRefreshListView;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.example.encryptionpackages.CreateCode;
import com.lidroid.xutils.util.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import de.greenrobot.event.EventBus;

public class ListCreditorRightsTransferFragment extends Fragment {
    /**
     * 债权转让列表
     *
     * @author 林佳荣
     */
    private PullToRefreshListView mListview;
    private ListCreditorRightsTransferAdapter mAdapter;
    private ArrayList<CreditorRightsTransferBean> mArrayList = new ArrayList<CreditorRightsTransferBean>();
    private int mTotalPage;
    private int mPage = 1;
    private View mLoadlayout;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyApplication application = new MyApplication();
        application.setListCreditorRightsTransferFragment(this);
        container = (ViewGroup) inflater
                .inflate(R.layout.list_creditorrightstransfer, null);
        initView(container);
        EventBus.getDefault().register(this);
        getCreditorRightsTransferDate(mPage, "", "", "", true);
        return container;
    }

    /**
     * eventBus事件处理
     * <p/>
     * onEventPostThread， onEventMainThread，onEventBackgroundThread，onEventAsync
     */
    public void onEventMainThread(EventObject object) {
        LogUtils.e("刷新债权列表");
        if (object != null) {
            if ("creditorSuccess".equals(object.getAction())) {
                updateCreditorRightsTransferDate();
            }
        }
    }

    @Override
    public void onResume() {
//        getCreditorRightsTransferDate(mPage, "", "", "", true);
        super.onResume();
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings(
            {"unchecked", "rawtypes"})
    public void initView(ViewGroup container) {
        mLoadlayout = container
                .findViewById(R.id.creditorrightstransferlist_loadlayout);

        mListview = (PullToRefreshListView) container
                .findViewById(R.id.creditorrightstransferlist_list);
        mListview.setMode(Mode.BOTH);
        mListview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(),
                        CreditorRightsDetailsActivity.class);
                CreditorRightsTransferBean bean = mArrayList.get(position - 1);
                intent.putExtra("creditorInfo", bean);
                startActivity(intent);
            }
        });
        mListview
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase refreshView) {
                        mPage = 1;
                        getCreditorRightsTransferDate(mPage, "", "", "", true);
                    }

                    @Override
                    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
                        if (mPage < mTotalPage) {
                            mPage++;
                            getCreditorRightsTransferDate(mPage, "", "", "",
                                    false);
                        } else {
                            ToastUtil.show(R.string.loading_for_more_tips);
                            mListview.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    mListview.onRefreshComplete();
                                }
                            }, 300);

                        }
                    }
                });
        mAdapter = new ListCreditorRightsTransferAdapter(getActivity(),
                mArrayList);
        mListview.setAdapter(mAdapter);

    }

    private void getCreditorRightsTransferDate(int page, String account_status,
                                               String borrow_interestrate, String order, final boolean isPullDown) {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("epage", "10");
        map.put("page", page + "");
        HttpUtil.post(UrlConstants.TRANSFER_INDEX, map,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFinish() {
                        mListview.onRefreshComplete();
                        mLoadlayout.setVisibility(View.GONE);
                        super.onFinish();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        try {
                            if (CreateCode.AuthentInfo(response)) {
                                JSONObject data = StringUtils
                                        .parseContent(response);
                                if (data.getString("result")
                                        .contains("success")) {
                                    if (isPullDown) {
                                        mArrayList.clear();
                                    }
                                    mTotalPage = data.getJSONObject("data")
                                            .optInt("total_pages");
                                    JSONArray loans = data.getJSONObject("data")
                                            .getJSONArray("items");
                                    for (int i = 0; i < loans.length(); i++) {
                                        JSONObject jsonObject = loans
                                                .getJSONObject(i);

                                        CreditorRightsTransferBean bean = new CreditorRightsTransferBean();
                                        bean.setTransfer_id(
                                                jsonObject.optString("id"));
                                        bean.setLoan_name(jsonObject.optString("loan_name"));
                                        bean.setCategory_id(jsonObject.optString("category_id"));
                                        bean.setBorrow_nid(jsonObject
                                                .optString("loan_id"));
                                        bean.setTransfer_info_url(
                                                jsonObject.optString(
                                                        "transfer_info_url"));

                                        bean.setBorrow_apr(
                                                jsonObject.optDouble("apr"));
                                        bean.setPic(jsonObject.optString("pic"));
                                        bean.setAmount(
                                                jsonObject.optString("amount"));
                                        bean.setAmount_money(jsonObject
                                                .optString("amount_money"));

                                        bean.setPeriod(
                                                jsonObject.optInt("period"));

                                        bean.setTotal_period(jsonObject
                                                .optInt("total_period"));

                                        bean.setStatus_name(
                                                jsonObject.optInt("status"));
                                        bean.setVouch_company_name(
                                                jsonObject.optString("vouch_company_name"));
                                        bean.setShare_url(jsonObject.optString("share_url"));
                                        bean.setShare_title(jsonObject.optString("share_title"));
                                        bean.setShare_content(jsonObject.optString("share_content"));
                                        mArrayList.add(bean);

                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        ToastUtil.show("网络请求失败,请稍后在试！");

                    }

                });

    }

    public void updateCreditorRightsTransferDate() {
        mPage = 1;
        getCreditorRightsTransferDate(mPage, "", "", "", true);

    }
}
