package com.diyou.adapter;

import java.util.List;

import com.diyou.bean.MyProjectInfo;
import com.diyou.pulltorefresh.PullToRefreshListView;
import com.diyou.util.StringUtils;
import com.diyou.v5yibao.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyPurchaseAdapter
        extends QLBaseAdapter<MyProjectInfo, PullToRefreshListView>
{
    private List<MyProjectInfo> list;
    private Context context;

    public MyPurchaseAdapter(Context context, List<MyProjectInfo> list)
    {
        super(context, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Hondler hondler = null;
        if (convertView == null)
        {
            hondler = new Hondler();
            convertView = View.inflate(context,
                    R.layout.activity_my_project_adapter, null);
            hondler.transfer_amount = (TextView) convertView
                    .findViewById(R.id.transfer_amount);
            hondler.wait_period = (TextView) convertView
                    .findViewById(R.id.wait_period);
            hondler.period = (TextView) convertView.findViewById(R.id.period);
            hondler.transfer_status_name = (TextView) convertView
                    .findViewById(R.id.transfer_status_name);
            convertView.setTag(hondler);
        }
        else
        {
            hondler = (Hondler) convertView.getTag();
        }
        hondler.transfer_amount
                .setText(StringUtils.getDoubleFormat(list.get(position).getWait_principal_interest()));
        hondler.wait_period.setText(list.get(position).getWait_period());
        hondler.period.setText("/" + list.get(position).getPeriod());
        if (list.get(position).getBuy_repay_status().equals("已回收"))
        {
            hondler.transfer_status_name.setTextColor(
                    context.getResources().getColor(R.color.light_gray));
        }
        else if (list.get(position).getBuy_repay_status().equals("可回收"))
        {
            hondler.transfer_status_name.setTextColor(
                    context.getResources().getColor(R.color.light_blue));
        }
        else if (list.get(position).getBuy_repay_status().equals("回收中"))
        {
            hondler.transfer_status_name.setTextColor(
                    context.getResources().getColor(R.color.yellow));
        }
        hondler.transfer_status_name
                .setText(list.get(position).getBuy_repay_status());
        return convertView;
    }

    private class Hondler
    {
        TextView transfer_amount;
        TextView wait_period;
        TextView period;
        TextView transfer_status_name;
    }
}
