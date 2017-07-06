package com.diyou.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.diyou.bean.RechargeRecordInfo;
import com.diyou.pulltorefresh.PullToRefreshListView;
import com.diyou.util.DataTools;
import com.diyou.util.DateUtils;
import com.diyou.util.StringUtils;
import com.diyou.util.ViewHolder;
import com.diyou.v5yibao.R;

import java.util.List;

public class RechargeRecordAdapter
        extends QLBaseAdapter<RechargeRecordInfo, PullToRefreshListView> {
    private List<RechargeRecordInfo> list;
    private Context mContext;
    private int colorGray;
    private int colorBlue;
    private int colorOrange;
    private int colorRed;

    public RechargeRecordAdapter(Context context, List<RechargeRecordInfo> list) {
        super(context, list);
        this.list = list;
        this.mContext = context;
        initColor();
    }

    private void initColor() {
        colorGray = mContext.getResources().getColor(R.color.huise);
        colorBlue = mContext.getResources().getColor(R.color.lanse);
        colorOrange = mContext.getResources().getColor(R.color.chengse);
        colorRed = mContext.getResources().getColor(R.color.red);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_withdraw_record_lv_1, null);
        }

        TextView tvMoney = ViewHolder.get(convertView,
                R.id.tv_withdraw_item_money);
        TextView tvTime = ViewHolder.get(convertView,
                R.id.tv_withdraw_item_time);
        TextView tvStatus = ViewHolder.get(convertView,
                R.id.tv_withdraw_item_status);
        TextView tvType = ViewHolder.get(convertView, R.id.textView2);
        RechargeRecordInfo info = list.get(position);

        tvType.setText(StringUtils.getDoubleFormat(info.getAmount()) +
                mContext.getString(R.string.common_display_yuan_default));
        tvMoney.setText(numberFormad(info.getInd()));
        tvTime.setText(DateUtils.stampToNowDate(info.getAdd_time()));
        tvStatus.setText(info.getStatus_name());
        // -3用户撤销 -2待审核 -1失败 1成功
        if ("-3".equals(info.getStatus())) {
            tvStatus.setTextColor(colorGray);
        } else if ("-2".equals(info.getStatus())) {
            tvStatus.setTextColor(colorOrange);
        } else if ("1".equals(info.getStatus())) {
            tvStatus.setTextColor(colorBlue);
        } else if ("-1".equals(info.getStatus())) {
            tvStatus.setTextColor(colorRed);
        }

        return convertView;
    }

    private String numberFormad(String number){
        String tmpStr = null;
        StringBuilder strBuf = new StringBuilder();
        if(number.length() > 14){
            strBuf.append("NO:").append(number.substring(0, 4)).append("******").
                    append(number.substring(number.length() - 4, number.length()));
        }

        return strBuf.toString();
    }

    private class Hondler {
        TextView tvState;
        TextView add_time;
        TextView tvMoney;
        TextView tvOrderNumber;
        TextView tvPayName;
        ;
    }

    private Spannable changeFontSize(String str, boolean isMonth) {

        Spannable WordtoSpan = new SpannableString(str);

        if (isMonth) {
            if (str.contains("天")) {
                WordtoSpan.setSpan(
                        new AbsoluteSizeSpan(DataTools.dip2px(context, 16)),
                        str.length() - 1, str.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (str.contains("个月")) {
                WordtoSpan.setSpan(
                        new AbsoluteSizeSpan(DataTools.dip2px(context, 16)),
                        str.length() - 2, str.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        } else {
            WordtoSpan.setSpan(
                    new AbsoluteSizeSpan(DataTools.dip2px(context, 16)),
                    str.length() - 1, str.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return WordtoSpan;

    }
}
