package com.diyou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.diyou.bean.WithdrawRecordInfo;
import com.diyou.pulltorefresh.PullToRefreshListView;
import com.diyou.util.DateUtils;
import com.diyou.util.StringUtils;
import com.diyou.util.ViewHolder;
import com.diyou.v5yibao.R;

import java.util.List;

public class WithdrawRecordAdapter
        extends QLBaseAdapter<WithdrawRecordInfo, PullToRefreshListView>
{

    private Context mContext;
    private List<WithdrawRecordInfo> mList;
    private int colorGray;
    private int colorBlue;
    private int colorOrange;
    private int colorRed;

    public WithdrawRecordAdapter(Context context, List<WithdrawRecordInfo> list)
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
                    .inflate(R.layout.item_withdraw_record_lv, null);
        }

        TextView tvMoney = ViewHolder.get(convertView,
                R.id.tv_withdraw_item_money);
        TextView tvTime = ViewHolder.get(convertView,
                R.id.tv_withdraw_item_time);
        TextView tvStatus = ViewHolder.get(convertView,
                R.id.tv_withdraw_item_status);
        WithdrawRecordInfo info = mList.get(position);
        tvMoney.setText(StringUtils.getDoubleFormat(info.getAmount()) + "元");
        tvTime.setText(DateUtils.getDateTimeFormat(info.getAdd_time()));
        tvStatus.setText(info.getStatus_name());
        // -3用户撤销 -2待审核 -1失败 1成功
        if ("-3".equals(info.getStatus()))
        {
            tvStatus.setTextColor(colorGray);
        }
        else if ("-2".equals(info.getStatus()))
        {
            tvStatus.setTextColor(colorOrange);
        }
        else if ("1".equals(info.getStatus()))
        {
            tvStatus.setTextColor(colorBlue);
        }
        else if ("-1".equals(info.getStatus()))
        {
            tvStatus.setTextColor(colorRed);
        }

        return convertView;
    }

}
