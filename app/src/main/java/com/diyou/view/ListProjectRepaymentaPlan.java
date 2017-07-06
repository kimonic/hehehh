package com.diyou.view;

import com.diyou.pulltorefresh.PullToRefreshBase;
import com.diyou.pulltorefresh.PullToRefreshBase.Mode;
import com.diyou.pulltorefresh.PullToRefreshListView;
import com.diyou.v5yibao.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

@SuppressLint("InflateParams")
public class ListProjectRepaymentaPlan extends View
{
    private int mysum = 15;
    private PullToRefreshListView listview;
    private BaseAdapter adapter;
    private Activity mActivity;

    public ListProjectRepaymentaPlan(Context context)
    {
        super(context);
        this.mActivity = (Activity) context;
    }

    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    public View init()
    {

        View inflate = mActivity.getLayoutInflater()
                .inflate(R.layout.list_money_backing, null);
        listview = (PullToRefreshListView) inflate
                .findViewById(R.id.moneybackinglist_list);
        listview.setMode(Mode.BOTH);
        listview.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                // mActivity.startActivity(new Intent(mActivity,
                // InvestmentDetailsActivity.class));
            }
        });
        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2()
        {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase refreshView)
            {
                // Toast.makeText(mActivity, "isHeaderShown", 1000).t();
                LoadMoreDataTask dataTask = new LoadMoreDataTask(true);
                dataTask.execute("");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase refreshView)
            {
                LoadMoreDataTask dataTask = new LoadMoreDataTask(false);
                dataTask.execute("");
            }
        });

        adapter = new BaseAdapter()
        {

            @Override
            public View getView(int position, View convertView,
                    ViewGroup parent)
            {
                if (convertView == null)
                {
                    convertView = mActivity.getLayoutInflater().inflate(
                            R.layout.item_project_repayment_plan, null);
                }
                return convertView;
            }

            @Override
            public long getItemId(int position)
            {
                return 0;
            }

            @Override
            public Object getItem(int position)
            {
                return null;
            }

            @Override
            public int getCount()
            {
                return mysum;
            }
        };
        listview.setAdapter(adapter);

        return inflate;

    }

    private class LoadMoreDataTask extends AsyncTask<String, Void, String>
    {

        private Boolean isshuaxin;

        public LoadMoreDataTask(Boolean isshuaxin)
        {
            this.isshuaxin = isshuaxin;
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
            }
            return "123";
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (isshuaxin)
            {
                mysum = 15;
            }
            else
            {
                mysum += 10;
            }

            adapter.notifyDataSetChanged();
            listview.onRefreshComplete();
            super.onPostExecute(result);
        }
    }
}
