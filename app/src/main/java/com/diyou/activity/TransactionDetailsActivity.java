package com.diyou.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.bean.InvestmentListBean;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.pulltorefresh.PullToRefreshBase;
import com.diyou.pulltorefresh.PullToRefreshBase.Mode;
import com.diyou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.diyou.pulltorefresh.PullToRefreshListView;
import com.diyou.util.DateUtils;
import com.diyou.util.ToastUtil;
import com.diyou.util.ViewHolder;
import com.diyou.v5yibao.R;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 交易明细
 * 
 * @author harve
 * 
 */
public class TransactionDetailsActivity extends BaseActivity
        implements OnClickListener
{

    private PullToRefreshListView mListView;
    private ArrayList<InvestmentListBean> mArrayList;
    private BaseAdapter adapter;
    protected int mPage;
    protected int mSumPage;
    private View mLoadlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        initView();
        initListView();
    }

    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    private void initListView()
    {
        mLoadlayout = findViewById(R.id.transactiondetailsactivity_loadlayout);
        mListView = (PullToRefreshListView) findViewById(
                R.id.transactiondetailsactivity_list);
        mArrayList = new ArrayList<InvestmentListBean>();
        adapter = new BaseAdapter()
        {

            @Override
            public View getView(int position, View convertView,
                    ViewGroup parent)
            {
                if (convertView == null)
                {
                    convertView = LayoutInflater
                            .from(TransactionDetailsActivity.this)
                            .inflate(R.layout.transaction_details_item, null);
                }
                TextView time = ViewHolder.get(convertView,
                        R.id.transaction_details_item_time);
                TextView money = ViewHolder.get(convertView,
                        R.id.transaction_details_item_money);
                TextView name = ViewHolder.get(convertView,
                        R.id.transaction_details_item_type);
                InvestmentListBean bean = mArrayList.get(position);
                time.setText(bean.getEnd_time());
                money.setText(bean.getAward_status());
                name.setText(bean.getName());
                return convertView;
            }

            @Override
            public long getItemId(int position)
            {
                return position;
            }

            @Override
            public Object getItem(int position)
            {
                return mArrayList.get(position);
            }

            @Override
            public int getCount()
            {
                return mArrayList.size();
            }
        };
        mListView.setAdapter(adapter);
        mListView.setMode(Mode.BOTH);
        // 下拉刷新
        mListView.setOnRefreshListener(new OnRefreshListener2()
        {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase refreshView)
            {
                mPage = 1;
                requestData(true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase refreshView)
            {
                if (mPage < mSumPage)
                {
                    mPage++;
                    requestData(false);
                }
                else
                {
                    ToastUtil.show(R.string.loading_for_more_tips);
                    mListView.postDelayed(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            mListView.onRefreshComplete();
                        }
                    }, 300);
                }
            }
        });
        requestData(true);
    }

    protected void requestData(final boolean isPullDown)
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("module", "account");
        map.put("q", "get_mobile_log_list");
        map.put("method", "get");
        map.put("page", "" + mPage);
        map.put("epage", "10");
        map.put("user_id", UserConfig.getInstance()
                .getUserId(TransactionDetailsActivity.this));
        HttpUtil.get(CreateCode.getUrl(map), new JsonHttpResponseHandler()
        {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    String responseString, Throwable throwable)
            {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONArray errorResponse)
            {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                if (isPullDown)
                {
                    mPage = 1;
                    mArrayList.clear();
                }
                try
                {
                    mSumPage = response.getInt("total_page");
                    JSONArray jsonArray = response.getJSONArray("list");
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        InvestmentListBean bean = new InvestmentListBean();
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        bean.setName(object.getString("type"));// 标题
                        bean.setEnd_time(DateUtils.dateToStr(new Date(
                                Long.parseLong(object.getString("addtime"))
                                        * 1000)));// 交易时间
                        String account_type = "";
                        if (object.getString("action_name").equals("收入"))
                        {
                            account_type = "+";
                        }
                        else if (object.getString("action_name").equals("支出"))
                        {
                            account_type = "-";
                        }
                        else
                        {
                            account_type = "*";
                        }
                        bean.setAward_status(
                                account_type + object.getString("money"));// 交易金额
                        mArrayList.add(bean);
                    }
                    if (jsonArray.length() == 0)
                    {
                        ToastUtil.show("没有数据");
                    }
                    adapter.notifyDataSetChanged();
                    mListView.onRefreshComplete();
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
                mLoadlayout.setVisibility(View.GONE);
                super.onFinish();
            }

            @Override
            public void onStart()
            {
                super.onStart();
            }

        });
    }

    private void initView()
    {
        findViewById(R.id.transactiondetailsactivity_titlelayout)
                .findViewById(R.id.title_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.transactiondetailsactivity_titlelayout)
                .findViewById(R.id.title_name))
                        .setText(getResources().getString(
                                R.string.title_activity_transaction_details));
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
}
