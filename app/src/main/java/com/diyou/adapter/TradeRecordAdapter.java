package com.diyou.adapter;

import java.text.DecimalFormat;
import java.util.List;

import com.diyou.bean.TradeRecordInfo;
import com.diyou.pulltorefresh.PullToRefreshListView;
import com.diyou.util.DataTools;
import com.diyou.util.DateUtils;
import com.diyou.v5yibao.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TradeRecordAdapter
        extends QLBaseAdapter<TradeRecordInfo, PullToRefreshListView>
{
    private List<TradeRecordInfo> list;
    private Context context;
    private DecimalFormat df;

    public TradeRecordAdapter(Context context, List<TradeRecordInfo> list)
    {
        super(context, list);
        this.list = list;
        this.context = context;
        df = new DecimalFormat("######0.00");
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Hondler hondler = null;
        if (convertView == null)
        {
            hondler = new Hondler();
            convertView = View.inflate(context, R.layout.activity_trade_adapter,
                    null);
            hondler.fee_name = (TextView) convertView
                    .findViewById(R.id.fee_name);
            hondler.add_time = (TextView) convertView
                    .findViewById(R.id.add_time);
            hondler.balance = (TextView) convertView.findViewById(R.id.balance);
            hondler.freeze = (TextView) convertView.findViewById(R.id.freeze);
            hondler.loan_name = (TextView) convertView
                    .findViewById(R.id.trading_loan_name);
            convertView.setTag(hondler);
        }
        else
        {
            hondler = (Hondler) convertView.getTag();
        }
        TradeRecordInfo recordInfo = list.get(position);
        hondler.fee_name.setText(recordInfo.getFee_name());
        hondler.add_time.setText(DateUtils.getDateTimeFormat(recordInfo.getAdd_time()));
        hondler.loan_name.setText(recordInfo.getLoan_name());
//        if (Double.valueOf(recordInfo.getBalance()) / 10000 > 1)
//        {
//            hondler.balance
//                    .setText(changeFontSize(df
//                            .format(Double.valueOf(
//                                    recordInfo.getBalance()) / 10000)
//                    + "万元", false));
//        }
//        else
//        {
//            hondler.balance.setText(changeFontSize(
//                    df.format(Double.valueOf(recordInfo.getBalance()))
//                            + "元",
//                    false));
//        }

        hondler.balance.setText(recordInfo.getBalance()+"元");
        if (recordInfo.getNew_balance() > 0)
        {
            hondler.freeze.setTextColor(context.getResources()
                    .getColorStateList(R.color.green));
            hondler.freeze.setText("+" + df.format(Double.valueOf(recordInfo.getFreeze())) + "元");
//            if (Double.valueOf(recordInfo.getFreeze()) / 10000 > 1)
//            {
//                hondler.freeze
//                        .setText(changeFontSize("+"
//                                + df.format(Double.valueOf(
//                                        recordInfo.getFreeze()) / 10000)
//                        + "万", false));
//            }
//            else
//            {
//                hondler.freeze
//                        .setText(changeFontSize(
//                                "+" + df.format(Double.valueOf(
//                                        recordInfo.getFreeze())) + "元",
//                        false));
//            }
        }
        else if (recordInfo.getNew_balance() < 0)
        {
            hondler.freeze.setTextColor(context.getResources()
                    .getColorStateList(R.color.trade_red));
            hondler.freeze.setText("-" + df.format(Double.valueOf(recordInfo.getFreeze())) + "元");
//            if (Double.valueOf(recordInfo.getFreeze()) / 10000 > 1)
//            {
//                hondler.freeze
//                        .setText(changeFontSize("-"
//                                + df.format(Double.valueOf(
//                                        recordInfo.getFreeze()) / 10000)
//                        + "万", false));
//            }
//            else
//            {
//                hondler.freeze
//                        .setText(changeFontSize(
//                                "-" + df.format(Double.valueOf(
//                                        recordInfo.getFreeze())) + "元",
//                        false));
//            }
        }
        else
        {
            hondler.freeze.setTextColor(context.getResources()
                    .getColorStateList(R.color.trade_orange));
            hondler.freeze.setText(df.format(Double.valueOf(recordInfo.getFreeze())) + "元");
//            if (Double.valueOf(recordInfo.getFreeze()) / 10000 > 1)
//            {
//                hondler.freeze
//                        .setText(changeFontSize(df
//                                .format(Double.valueOf(
//                                        recordInfo.getFreeze()) / 10000)
//                        + "万", false));
//            }
//            else
//            {
//                hondler.freeze
//                        .setText(changeFontSize(
//                                df.format(Double.valueOf(
//                                        recordInfo.getFreeze())) + "元",
//                        false));
//            }
        }

        return convertView;
    }

    private class Hondler
    {
        TextView fee_name;
        TextView add_time;
        TextView balance;
        TextView freeze;
        TextView loan_name;
    }

    private Spannable changeFontSize(String str, boolean isMonth)
    {

        Spannable WordtoSpan = new SpannableString(str);

        if (isMonth)
        {
            if (str.contains("天"))
            {
                WordtoSpan.setSpan(
                        new AbsoluteSizeSpan(DataTools.dip2px(context, 16)),
                        str.length() - 1, str.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else if (str.contains("个月"))
            {
                WordtoSpan.setSpan(
                        new AbsoluteSizeSpan(DataTools.dip2px(context, 16)),
                        str.length() - 2, str.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }
        else
        {
            WordtoSpan.setSpan(
                    new AbsoluteSizeSpan(DataTools.dip2px(context, 16)),
                    str.length() - 1, str.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return WordtoSpan;

    }
}
