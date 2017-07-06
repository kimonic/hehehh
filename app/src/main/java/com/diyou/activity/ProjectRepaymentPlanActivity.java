package com.diyou.activity;

import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.bean.ProjectRepaymentPlanBean;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.pull2refresh.ChiPullToRefreshListView;
import com.diyou.pull2refresh.PullToRefreshBase.OnRefreshListener;
import com.diyou.util.DateUtils;
import com.diyou.util.ToastUtil;
import com.diyou.util.ViewHolder;
import com.diyou.v5yibao.R;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 项目还款计划
 * 
 * @author 曹翔宇
 * 
 */
public class ProjectRepaymentPlanActivity extends BaseActivity
        implements OnClickListener
{

    private View titleView;
    private TextView titleName;
    // private ImageView titleBack;
    private ChiPullToRefreshListView listview;
    private BaseAdapter adapter;
    private ArrayList<ProjectRepaymentPlanBean> mArrayList = new ArrayList<ProjectRepaymentPlanBean>();
    private String borrow_nid;
    private String tender_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_repayment_plan);
        initData();
        initView();
    }

    private void initData()
    {
        borrow_nid = getIntent().getStringExtra("borrow_nid");
        tender_id = getIntent().getStringExtra("tender_id");
    }

    private void initView()
    {
        titleView = findViewById(R.id.projectrepaymentaplanctivity_titlelayout);
        titleName = (TextView) titleView.findViewById(R.id.title_name);
        titleView.findViewById(R.id.title_back).setOnClickListener(this);
        // titleBack.setOnClickListener(this);
        titleName.setText("项目还款计划");
        listview = (ChiPullToRefreshListView) findViewById(
                R.id.projectrepaymentaplanctivity_list);
        adapter = new BaseAdapter()
        {

            @Override
            public View getView(int position, View convertView,
                    ViewGroup parent)
            {
                ProjectRepaymentPlanBean bean = mArrayList.get(position);
                if (convertView == null)
                {
                    convertView = getLayoutInflater().inflate(
                            R.layout.item_project_repayment_plan, null);
                }
                TextView item_projectrepaymentaplanctivity_repayment_period = ViewHolder
                        .get(convertView,
                                R.id.item_projectrepaymentaplanctivity_repayment_period);
                TextView item_projectrepaymentaplanctivity_plan_repayment_time = ViewHolder
                        .get(convertView,
                                R.id.item_projectrepaymentaplanctivity_plan_repayment_time);
                TextView item_projectrepaymentaplanctivity_repayment_money = ViewHolder
                        .get(convertView,
                                R.id.item_projectrepaymentaplanctivity_repayment_money);
                TextView item_projectrepaymentaplanctivity_actual_money = ViewHolder
                        .get(convertView,
                                R.id.item_projectrepaymentaplanctivity_actual_money);
                TextView item_projectrepaymentaplanctivity_actual_interest = ViewHolder
                        .get(convertView,
                                R.id.item_projectrepaymentaplanctivity_actual_interest);
                item_projectrepaymentaplanctivity_repayment_period
                        .setText("第" + bean.getRepay_period() + "期");
                item_projectrepaymentaplanctivity_plan_repayment_time
                        .setText(bean.getRepay_time());
                item_projectrepaymentaplanctivity_repayment_money
                        .setText("¥" + bean.getRepay_account());
                item_projectrepaymentaplanctivity_actual_money
                        .setText("¥" + bean.getRepay_capital_yes());
                item_projectrepaymentaplanctivity_actual_interest
                        .setText("¥" + bean.getRepay_interest_yes());
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
        listview.setAdapter(adapter);
        // listview.setOnItemClickListener(new OnItemClickListener() {
        //
        // @Override
        // public void onItemClick(AdapterView<?> parent, View view,
        // int position, long id) {
        // startActivity(new Intent(ProjectRepaymentPlanActivity.this,
        // CreditorRightsActivity.class));
        // }
        // });
        listview.setOnRefreshListener(new OnRefreshListener<ListView>()
        {

            @Override
            public void onRefresh(
                    com.diyou.pull2refresh.PullToRefreshBase<ListView> refreshView)
            {
                getProjectRepaymentPlanData(false);
            }

        });
        listview.setRefreshing();
    }

    private void getProjectRepaymentPlanData(final boolean isLoadMore)
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("module", "borrow");
        map.put("q", "get_recover_list");
        map.put("borrow_nid", borrow_nid);
        map.put("tender_id", tender_id);
        map.put("user_id", UserConfig.getInstance()
                .getUserId(ProjectRepaymentPlanActivity.this));
        map.put("order", "repay_period");
        map.put("method", "get");
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
                        if (!isLoadMore)
                        {
                            mArrayList.clear();
                        }
                        JSONArray jsonArray = response.getJSONArray("list");
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ProjectRepaymentPlanBean bean = new ProjectRepaymentPlanBean();
                            bean.setRepay_period(
                                    jsonObject.optString("recover_period"));
                            bean.setRepay_time(DateUtils.convert(
                                    jsonObject.optString("recover_time")));
                            bean.setRepay_account(
                                    jsonObject.optString("recover_account"));
                            bean.setRepay_capital_yes(
                                    jsonObject.optString("recover_capital"));
                            bean.setRepay_interest_yes(
                                    jsonObject.optString("recover_interest"));
                            mArrayList.add(bean);
                        }
                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    ToastUtil.show("网络请求失败,请稍后在试！");
                }
                finally
                {
                    adapter.notifyDataSetChanged();
                    listview.onRefreshComplete();
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
