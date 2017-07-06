package com.diyou.adapter;

import java.util.List;

import com.diyou.bean.MyInvestmentInfo;
import com.diyou.util.DateUtils;
import com.diyou.util.StringUtils;
import com.diyou.util.ViewHolder;
import com.diyou.v5yibao.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyProjectAdapter extends BaseAdapter
{

    private Context mContext;
    private List<MyInvestmentInfo> myInvestList;

    public MyProjectAdapter(Context context,
            List<MyInvestmentInfo> myInvestList)
    {
        this.mContext = context;
        this.myInvestList = myInvestList;
    }

    @Override
    public int getCount()
    {
        return myInvestList == null ? 0 : myInvestList.size();
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_my_project_lv, null);
        }
        TextView tvTitle = ViewHolder.get(convertView,
                R.id.tv_my_invest_item_title);
        TextView tvTime = ViewHolder.get(convertView,
                R.id.tv_my_invest_item_time);
        TextView tvAmount = ViewHolder.get(convertView,
                R.id.tv_my_invest_item_amount);
        TextView tvInterest = ViewHolder.get(convertView,
                R.id.tv_my_invest_item_award_interest);
        TextView tvStatus = ViewHolder.get(convertView,
                R.id.tv_my_invest_item_status);
        MyInvestmentInfo info = myInvestList.get(position);

        tvTitle.setText(info.getLoan_name());
        tvTime.setText(DateUtils.getDateTimeFormat(info.getAdd_time()));
        tvAmount.setText(StringUtils.getMoneyFormat(info.getAmount()));
        tvInterest
                .setText(StringUtils.getMoneyFormat(info.getAward_interest()));
        tvStatus.setText(info.getStatus_name());
        return convertView;
    }

}
