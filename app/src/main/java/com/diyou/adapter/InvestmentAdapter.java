package com.diyou.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.diyou.bean.InvestmentListBean;
import com.diyou.util.DataTools;
import com.diyou.util.ViewHolder;
import com.diyou.v5yibao.R;
import com.diyou.view.RoundProgressBar;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class InvestmentAdapter extends BaseAdapter {
    private ArrayList<InvestmentListBean> mArrayList;
    private Context mContext;
    private DecimalFormat df;

    public InvestmentAdapter(ArrayList<InvestmentListBean> mArrayList,
                             Context mContext) {
        this.mArrayList = mArrayList;
        this.mContext = mContext;
        df = new DecimalFormat("######0.00");
    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_homefragment, null);
        }

        TextView interestRate = ViewHolder.get(convertView,
                R.id.homepage_fragment_item_tv_interestRate);
        TextView time = ViewHolder.get(convertView,
                R.id.homepage_fragment_item_tv_time);
        TextView money = ViewHolder.get(convertView,
                R.id.homepage_fragment_item_tv_money);
        TextView name = ViewHolder.get(convertView,
                R.id.homepage_fragment_item_tv_name);
        RoundProgressBar progress = ViewHolder.get(convertView,
                R.id.homepage_fragment_item_pb_progress);

        InvestmentListBean bean = mArrayList.get(position);
        progress.setProgress(bean.getBorrow_account_scale());
        if (bean.getAccount() / 10000 > 0.99) {
            money.setText(changeFontSize(
                    df.format(bean.getAccount() / 10000) + "万", false));
        } else {
            money.setText(
                    changeFontSize(df.format(bean.getAccount()) + "元", false));
        }
        time.setText("期限 " + changeFontSize(bean.getBorrow_period_name(), true)
                + "个月");
        interestRate.setText(changeFontSize(bean.getBorrow_apr() + "%", false));
        name.setText(bean.getName());
        return convertView;
    }

    private Spannable changeFontSize(String str, boolean isMonth) {

        Spannable WordtoSpan = new SpannableString(str);

        if (isMonth) {
            if (str.contains("天")) {
                WordtoSpan.setSpan(
                        new AbsoluteSizeSpan(DataTools.dip2px(mContext, 16)),
                        str.length() - 1, str.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (str.contains("个月")) {
                WordtoSpan.setSpan(
                        new AbsoluteSizeSpan(DataTools.dip2px(mContext, 16)),
                        str.length() - 2, str.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        } else {
            WordtoSpan.setSpan(
                    new AbsoluteSizeSpan(DataTools.dip2px(mContext, 16)),
                    str.length() - 1, str.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return WordtoSpan;

    }

}
