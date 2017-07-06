package com.diyou.adapter;

import java.util.List;

import com.diyou.bean.RefundDetailItem;
import com.diyou.pulltorefresh.PullToRefreshListView;
import com.diyou.util.DateUtils;
import com.diyou.util.StringUtils;
import com.diyou.util.ViewHolder;
import com.diyou.v5yibao.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RefundDetailAdapter extends QLBaseAdapter<RefundDetailItem, PullToRefreshListView>
{

    private Context mContext;
    private List<RefundDetailItem> mList;

    public RefundDetailAdapter(Context context,
            List<RefundDetailItem> list)
    {
        super(context, list);
        mContext = context;
        mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView==null){
            convertView=LayoutInflater.from(mContext).inflate(R.layout.item_creditor_buy_lv, null);
        }

        TextView tvPeriod = ViewHolder.get(convertView, R.id.tv_item_period);
        TextView tvStatus = ViewHolder.get(convertView, R.id.tv_item_status);
        TextView tvPrincipal = ViewHolder.get(convertView, R.id.tv_item_principal);
        TextView tvPrincipal_yes = ViewHolder.get(convertView, R.id.tv_item_principal_yes);
        TextView tvInterest_yes = ViewHolder.get(convertView, R.id.tv_item_interest_yes);
        TextView tvTime = ViewHolder.get(convertView, R.id.tv_item_time);
        RefundDetailItem info = mList.get(position);
        tvPeriod.setText(mContext.getString(R.string.common_di)+info.getPeriod_no()+"/"+
        info.getPeriod()+mContext.getString(R.string.common_peroid));
        tvStatus.setText(info.getStatus_name());
        tvPrincipal.setText(StringUtils.getDoubleFormat(info.getAmount()));
        tvPrincipal_yes.setText(StringUtils.getDoubleFormat(info.getPrincipal_yes()));
        tvInterest_yes.setText(StringUtils.getDoubleFormat(info.getInterest_yes()));
        tvTime.setText(info.getRecover_time());
        return convertView;
    }

}
