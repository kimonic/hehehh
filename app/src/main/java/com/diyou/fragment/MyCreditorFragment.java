package com.diyou.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.diyou.activity.MyCreditorBuyDetailsActivity;
import com.diyou.activity.MyCreditorTransferDetailsActivity;
import com.diyou.adapter.MyCreditorAdapter;
import com.diyou.adapter.MyPurchaseAdapter;
import com.diyou.base.BaseActivity;
import com.diyou.bean.EventObject;
import com.diyou.bean.MyProjectInfo;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.pulltorefresh.PullToRefreshBase;
import com.diyou.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.diyou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.diyou.pulltorefresh.PullToRefreshScrollView;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.LoadingLayout;
import com.diyou.view.ScrollableListView;
import com.example.encryptionpackages.CreateCode;
import com.lidroid.xutils.util.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import de.greenrobot.event.EventBus;

public class MyCreditorFragment extends Fragment implements OnClickListener,
        OnRefreshListener<ScrollView>
{
    public static final int REQUEST_CODE_DETAIL=1278;
    private View view;
    private RelativeLayout transfer_record_relayout;
    private RelativeLayout Purchase_records_layout;
    private TextView success_transfer_amount;// 成功转让金额
    private TextView creditor_transfer;// 债权转出盈亏
    private TextView successful_import_amount;
    private TextView creditor_import;
    private ScrollableListView transfer_listview;
    private ScrollableListView purchase_records_listview;
    private int page = 1;
    private int epage = 9999;
    private int total_pages;// 总页数
    private int purchase_total_pages;// 总页数
    private boolean isFirst = true;
    private LoadingLayout loadlayout;
    private List<MyProjectInfo> projectList = new ArrayList<MyProjectInfo>();
    private List<MyProjectInfo> purchaseList = new ArrayList<MyProjectInfo>();
    private MyCreditorAdapter creditorAdapter;
    private MyPurchaseAdapter purchaseAdapter;
    private PullToRefreshScrollView layout_ScrollView;
    private int ColoeWhite;
    private int ColorGray;
    private TextView transfer_record_textView;
    private TextView Purchase_records_textView;
    private BaseActivity mMa;
    private int CurrentPage = 1;// 表示在刷新
    private String mStrYuan;
    private int mTransferPosition=-1;
//    private TextView tab_display;

    @Override
    public View onCreateView(LayoutInflater inflater,
             ViewGroup container,  Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.activity_creditor_fragment, null);
        ColoeWhite = getResources().getColor(R.color.white);
        ColorGray = getResources().getColor(R.color.dark_gray);
        EventBus.getDefault().register(this);
        mMa = (BaseActivity) getActivity();
        intiView();
        requestTransfer();
        mStrYuan = getString(R.string.common_display_yuan_default);
        return view;
    }


    private void requestCreditor()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token",
                UserConfig.getInstance().getLoginToken(getActivity()));
        map.put("page", String.valueOf(page));
        map.put("status_nid", "buy");
        map.put("epage", String.valueOf(epage));
        HttpUtil.post(UrlConstants.CREDITER_LIST, map,
                new JsonHttpResponseHandler()
                {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        if (isFirst)
                        {
                            loadlayout.setOnLoadingError(getActivity(),
                                    layout_ScrollView);
                        }
                        layout_ScrollView.onRefreshComplete();
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
                                if (data.get("result").equals("success"))
                                {
                                    // success_transfer_amount.setText(data
                                    // .getJSONObject("data").optString(
                                    // "transfer_total")
                                    // + "元");
                                    // creditor_transfer.setText(data
                                    // .getJSONObject("data").optString(
                                    // "transfer_interest_total")
                                    // + "元");
                                    // successful_import_amount.setText(data
                                    // .getJSONObject("data").optString(
                                    // "transfer_buy_total")
                                    // + "元");
                                    // creditor_import.setText(data.getJSONObject(
                                    // "data").optString(
                                    // "transfer_buy_interest_total")
                                    // + "元");
                                    purchase_total_pages = data
                                            .getJSONObject("data")
                                            .getInt("total_pages");

                                    if (purchase_total_pages == 0)
                                    {
                                        // ToastUtil.show("没有购买记录哦");
                                        loadlayout.setOnStopLoading(
                                                getActivity(),
                                                layout_ScrollView);
                                        return;
                                    }
                                    if (page == 1)
                                    {
                                        purchaseList.clear();
                                    }
                                    JSONArray items = data.getJSONObject("data")
                                            .getJSONArray("items");
                                    for (int i = 0; i < items.length(); i++)
                                    {
                                        MyProjectInfo info = new MyProjectInfo();
                                        JSONObject object = (JSONObject) items
                                                .get(i);
                                        info.setTransfer_amount(object
                                                .optString("transfer_money"));
                                        info.setWait_period(object
                                                .optString("wait_period"));
                                        info.setPeriod(
                                                object.optString("period"));
                                        info.setTransfer_status_name(
                                                object.optString(
                                                        "transfer_status_name"));
                                        info.setId(object.optString("id"));
                                        info.setTransfer_status(object
                                                .optString("transfer_status"));
                                        info.setBuy_repay_status(object
                                                .optString("buy_repay_status"));
                                        info.setTransfer_id(object.optString("transfer_id"));
                                        info.setWait_principal_interest(object.optString("wait_principal_interest"));
                                        purchaseList.add(info);
                                    }
                                    purchaseAdapter.notifyDataSetChanged();
                                    loadlayout.setVisibility(View.GONE);
                                }
                                else
                                {
                                    ToastUtil.show(data.optString("description"));
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        if (isFirst)
                        {
                            loadlayout.setOnStopLoading(getActivity(),
                                    layout_ScrollView);
                            loadlayout.setVisibility(View.GONE);
                            isFirst = false;
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish()
                    {
                        layout_ScrollView.onRefreshComplete();
                        super.onFinish();
                    }

                    @Override
                    public void onStart()
                    {
                        super.onStart();
                    }

                });

    }

    private void requestTransfer()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token",
                UserConfig.getInstance().getLoginToken(getActivity()));
        map.put("page", String.valueOf(page));
        map.put("epage", String.valueOf(epage));
        HttpUtil.post(UrlConstants.CREDITER_LIST, map,
                new JsonHttpResponseHandler()
                {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        if (isFirst)
                        {
                            loadlayout.setOnLoadingError(getActivity(),
                                    layout_ScrollView);
                        }
                        layout_ScrollView.onRefreshComplete();
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
                                if (data.get("result").equals("success"))
                                {
                                    requestCreditor();
                                    success_transfer_amount
                                            .setText(data.getJSONObject("data")
                                                    .optString("transfer_total")
                                                    + mStrYuan);
                                    creditor_transfer.setText(data
                                            .getJSONObject("data").optString(
                                                    "transfer_interest_total")
                                            + mStrYuan);
                                    successful_import_amount.setText(data
                                            .getJSONObject("data")
                                            .optString("transfer_buy_total")
                                            + mStrYuan);
                                    creditor_import.setText(data
                                            .getJSONObject("data").optString(
                                                    "transfer_buy_interest_total")
                                            + mStrYuan);
                                    total_pages = data.getJSONObject("data")
                                            .getInt("total_pages");
                                    if (total_pages == 0)
                                    {
                                        // ToastUtil.show("转让记录没有数据哦");
                                        loadlayout.setOnStopLoading(
                                                getActivity(),
                                                layout_ScrollView);
                                        return;
                                    }
                                    if (page == 1)
                                    {
                                        projectList.clear();
                                    }
                                    JSONArray items = data.getJSONObject("data")
                                            .getJSONArray("items");
                                    for (int i = 0; i < items.length(); i++)
                                    {
                                        MyProjectInfo info = new MyProjectInfo();
                                        JSONObject object = (JSONObject) items
                                                .get(i);
                                        info.setTransfer_amount(object
                                                .optString("transfer_amount"));
                                        info.setWait_period(object
                                                .optString("wait_period"));
                                        info.setPeriod(
                                                object.optString("period"));
                                        info.setTransfer_status_name(
                                                object.optString(
                                                        "transfer_status_name"));
                                        info.setId(object.optString("id"));
                                        info.setTransfer_status(object
                                                .optString("transfer_status"));
                                        info.setTransfer_money(object
                                                .optString("transfer_money"));
                                        info.setTransfer_cancel_count(object.optInt("transfer_cancel_count"));
                                        projectList.add(info);
                                    }
                                    creditorAdapter.notifyDataSetChanged();

                                    if (isFirst)
                                    {
                                        loadlayout.setOnStopLoading(getActivity(),
                                                layout_ScrollView);
                                        isFirst = false;
                                    }
                                }
                                else
                                {
                                    ToastUtil.show(data.optString("description"));
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }


                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish()
                    {
                        layout_ScrollView.onRefreshComplete();
                        super.onFinish();
                    }

                    @Override
                    public void onStart()
                    {
                        super.onStart();
                    }

                });

    }

    private void intiView()
    {
        // View creditor_next_fragment = view
        // .findViewById(R.id.creditor_next_fragment);
        layout_ScrollView = (PullToRefreshScrollView) view
                .findViewById(R.id.layout_ScrollView);
        layout_ScrollView.setOnRefreshListener(this);
        transfer_listview = (ScrollableListView) view
                .findViewById(R.id.transfer_listview);

        creditorAdapter = new MyCreditorAdapter(getActivity(), projectList);
        transfer_listview.setAdapter(creditorAdapter);
        // transfer_listview.setOnRefreshListener(this);
        // transfer_listview.setMode(Mode.BOTH);
        purchase_records_listview = (ScrollableListView) view
                .findViewById(R.id.purchase_records_listview);
        purchaseAdapter = new MyPurchaseAdapter(getActivity(), purchaseList);
        purchase_records_listview.setAdapter(purchaseAdapter);
        // purchase_records_listview.setOnRefreshListener(this);
        // purchase_records_listview.setMode(Mode.BOTH);

        transfer_record_relayout = (RelativeLayout) view
                .findViewById(R.id.transfer_record_relayout);
        transfer_record_relayout
                .setBackgroundResource(R.drawable.shape_blue_btn_left_press);
        transfer_record_relayout.setOnClickListener(this);
        Purchase_records_layout = (RelativeLayout) view
                .findViewById(R.id.Purchase_records_layout);
        Purchase_records_layout.setOnClickListener(this);
        transfer_record_textView = (TextView) view
                .findViewById(R.id.transfer_record_textView);
        Purchase_records_textView = (TextView) view
                .findViewById(R.id.Purchase_records_textView);
        success_transfer_amount = (TextView) view
                .findViewById(R.id.success_transfer_amount);
        creditor_transfer = (TextView) view
                .findViewById(R.id.creditor_transfer);
        successful_import_amount = (TextView) view
                .findViewById(R.id.successful_import_amount);
//        tab_display = (TextView) view
//                .findViewById(R.id.tv_my_creditor_tab);
        creditor_import = (TextView) view.findViewById(R.id.creditor_import);
        loadlayout = (LoadingLayout) view.findViewById(R.id.project_loadlayout);
        loadlayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        requestTransfer();

                    }
                });
        transfer_listview.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                mTransferPosition=position;
                MyProjectInfo info = projectList.get(position);
                if("3".equals(info.getTransfer_status())){
                    ToastUtil.show(R.string.creditor_transfer_undo_three_times);
                }else{
                    Intent intent = new Intent(getActivity(), MyCreditorTransferDetailsActivity.class);
                    intent.putExtra("id", info.getId());
                    intent.putExtra("transfer_status",
                            info.getTransfer_status());
                    mMa.startActivityForResult(intent,REQUEST_CODE_DETAIL);
                }

            }
        });
        purchase_records_listview
                .setOnItemClickListener(new OnItemClickListener()
                {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id)
                    {

                        Intent intent = new Intent(getActivity(),
                                MyCreditorBuyDetailsActivity.class);
                        intent.putExtra("transfer_id",
                                purchaseList.get(position).getTransfer_id());
                        intent.putExtra("buy_repay_status", purchaseList
                                .get(position).getBuy_repay_status());
                        mMa.startActivity(intent);
                    }
                });
    }

    /**
     * eventBus事件处理
     * 
     * onEventPostThread， onEventMainThread，onEventBackgroundThread，onEventAsync
     */
    public void onEventMainThread(EventObject object)
    {
        LogUtils.e("刷新我的债权列表");
        if (object != null)
        {
            if ("refreshMyCreditorTransferRecord".equals(object.getAction()))
            {
//                String status = object.getData().getString("transfer_status");
//                MyProjectInfo info = projectList.get(mTransferPosition);
//                info.setTransfer_status(status);
//                creditorAdapter.notifyDataSetChanged();
                requestTransfer();
            }
        }
    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.transfer_record_relayout:
            transfer_record_relayout.setBackgroundResource(
                    R.drawable.shape_blue_btn_left_press);
            Purchase_records_layout.setBackgroundResource(R.color.transparent);
            transfer_listview.setVisibility(View.VISIBLE);
            purchase_records_listview.setVisibility(View.GONE);
            transfer_record_textView.setTextColor(ColoeWhite);
            Purchase_records_textView.setTextColor(ColorGray);
//            tab_display.setText("待收本息（元）");
            CurrentPage = 1;
            break;

        case R.id.Purchase_records_layout:
            Purchase_records_layout.setBackgroundResource(
                    R.drawable.shape_blue_btn_right_press);
            transfer_record_relayout.setBackgroundResource(R.color.transparent);
            transfer_listview.setVisibility(View.GONE);
            purchase_records_listview.setVisibility(View.VISIBLE);
            transfer_record_textView.setTextColor(ColorGray);
            Purchase_records_textView.setTextColor(ColoeWhite);
//            tab_display.setText("转让价格（元）");
            CurrentPage = 2;
            break;
        }

    }




    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView)
    {
        requestTransfer();
    }

}
