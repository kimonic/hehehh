package com.diyou.adapter;

import java.util.List;

import com.diyou.bean.PromotionRecordInfo;
import com.diyou.pulltorefresh.PullToRefreshListView;
import com.diyou.util.StringUtils;
import com.diyou.util.ViewHolder;
import com.diyou.v5yibao.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PromotionRecordAdapter
        extends QLBaseAdapter<PromotionRecordInfo, PullToRefreshListView>
{

    private Context mContext;
    private List<PromotionRecordInfo> mList;

    public PromotionRecordAdapter(Context context,
            List<PromotionRecordInfo> list)
    {
        super(context, list);
        // TODO Auto-generated constructor stub
        mContext = context;
        mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_promotion_record_lv, null);
        }

        TextView tvName = ViewHolder.get(convertView,
                R.id.tv_promotion_item_user);
        TextView tvTime = ViewHolder.get(convertView,
                R.id.tv_promotion_item_time);
        TextView tvType = ViewHolder.get(convertView,
                R.id.tv_promotion_item_type);
        TextView tvFundType = ViewHolder.get(convertView,
                R.id.tv_promotion_item_fund_type);
        TextView tvMoney = ViewHolder.get(convertView,
                R.id.tv_promotion_item_money);
        TextView tvPercent = ViewHolder.get(convertView,
                R.id.tv_promotion_item_percent);
        PromotionRecordInfo info = mList.get(position);

        tvName.setText(info.getSpreaded_member_name());
        tvTime.setText(info.getAdd_time());
        tvType.setText(info.getSpread_type());
        tvFundType.setText(info.getAmount_type());
        tvMoney.setText(StringUtils.getDoubleFormat(info.getAward()) + "元");
        tvPercent.setText(info.getProportion());
        return convertView;
    }

}
