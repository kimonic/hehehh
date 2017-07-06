package com.diyou.adapter;

import java.util.List;

import com.diyou.bean.MyProjectInfo;
import com.diyou.util.StringUtils;
import com.diyou.v5yibao.R;
import com.diyou.view.MyPullToRefreshListView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyCreditorAdapter
        extends QLBaseAdapter<MyProjectInfo, MyPullToRefreshListView>
{
    private List<MyProjectInfo> list;
    private Context context;

    public MyCreditorAdapter(Context context, List<MyProjectInfo> list)
    {
        super(context, list);
        this.context = context;
        this.list = list;
    }

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
        MyProjectInfo info = list.get(position);
        hondler.transfer_amount
                .setText(StringUtils.getDoubleFormat(info.getTransfer_money()));
        hondler.wait_period.setText(info.getWait_period());
        hondler.period.setText("/" + info.getPeriod());
        
        if ("-1".equals(info.getTransfer_status()))
        {
            hondler.transfer_status_name.setTextColor(
                    context.getResources().getColor(R.color.light_blue));
        }
        else if ("1".equals(info.getTransfer_status()))
        {

            hondler.transfer_status_name.setTextColor(
                    context.getResources().getColor(R.color.yellow));
        }else if ("2".equals(info.getTransfer_status()))
        {
            hondler.transfer_status_name.setTextColor(
                    context.getResources().getColor(R.color.light_gray));
        }else if ("3".equals(info.getTransfer_status()))
        {
            hondler.transfer_status_name.setTextColor(
                    context.getResources().getColor(R.color.light_gray));
        }
        hondler.transfer_status_name
                .setText(info.getTransfer_status_name());
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
