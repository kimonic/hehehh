package com.diyou.adapter;

import java.util.List;

import com.diyou.bean.SettlementRecordInfo;
import com.diyou.pulltorefresh.PullToRefreshListView;
import com.diyou.util.StringUtils;
import com.diyou.util.ViewHolder;
import com.diyou.v5yibao.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettlementRecordAdapter
        extends QLBaseAdapter<SettlementRecordInfo, PullToRefreshListView>
{

    private Context mContext;
    private List<SettlementRecordInfo> mList;
    private int colorGray;
    private int colorBlue;
    private int colorOrange;
    private int colorRed;

    public SettlementRecordAdapter(Context context,
            List<SettlementRecordInfo> list)
    {
        super(context, list);
        // TODO Auto-generated constructor stub
        mContext = context;
        mList = list;
        initColor();
    }

    private void initColor()
    {
        colorGray = mContext.getResources().getColor(R.color.huise);
        colorBlue = mContext.getResources().getColor(R.color.lanse);
        colorOrange = mContext.getResources().getColor(R.color.chengse);
        colorRed = mContext.getResources().getColor(R.color.red);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_settlement_record_lv, null);
        }
        TextView tvMoney = ViewHolder.get(convertView,
                R.id.tv_settlement_item_money);
        TextView tvTime = ViewHolder.get(convertView,
                R.id.tv_settlement_item_time);
        TextView tvStatus = ViewHolder.get(convertView,
                R.id.tv_settlement_item_status);
        SettlementRecordInfo info = mList.get(position);
        tvMoney.setText(StringUtils.getDoubleFormat(info.getMoney()));
        tvTime.setText(info.getAdd_time());
        // 结算中、结算成功、结算失败
        if ("待结算".equals(info.getVerify_status()))
        {
            tvStatus.setTextColor(colorOrange);
        }
        else if ("已结算".equals(info.getVerify_status()))
        {
            tvStatus.setTextColor(colorBlue);
        }
        else if ("结算失败".equals(info.getVerify_status()))
        {
            tvStatus.setTextColor(colorGray);
        }
        tvStatus.setText(info.getVerify_status());
        return convertView;
    }

}
