package com.diyou.adapter;

import java.util.List;

import com.diyou.bean.MyPromotionItem;
import com.diyou.pulltorefresh.PullToRefreshListView;
import com.diyou.util.StringUtils;
import com.diyou.util.ViewHolder;
import com.diyou.v5yibao.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyPromotionAdapter
        extends QLBaseAdapter<MyPromotionItem, PullToRefreshListView>
{

    private Context mContext;
    private List<MyPromotionItem> mList;

    public MyPromotionAdapter(Context context, List<MyPromotionItem> list)
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
                    .inflate(R.layout.item_my_promotion_record_lv, null);
        }
        TextView tvUser = ViewHolder.get(convertView,
                R.id.tv_my_promotion_item_user);
        TextView tvInvest = ViewHolder.get(convertView,
                R.id.tv_my_promotion_item_invest);
        TextView tvRefund = ViewHolder.get(convertView,
                R.id.tv_my_promotion_item_refund);
        MyPromotionItem info = mList.get(position);

        tvUser.setText(info.getSpreaded_member_name());
        tvInvest.setText(
                StringUtils.getDoubleFormat(info.getTender_success_amount()));
        tvRefund.setText(
                StringUtils.getDoubleFormat(info.getRepay_amount_yes()));
        return convertView;
    }

}
