package com.diyou.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.diyou.bean.InvestmentListInfo;
import com.diyou.util.DataTools;
import com.diyou.util.ImageUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ViewHolder;
import com.diyou.v5yibao.R;
import com.diyou.view.RoundProgressBar;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;
import java.util.List;

public class InvestmentItemsAdapter extends BaseAdapter {
    private List<InvestmentListInfo> mArrayList;
    private Context mContext;
    private DecimalFormat df;

    public InvestmentItemsAdapter(List<InvestmentListInfo> mArrayList,
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
                    .inflate(R.layout.investment_item_homefragment, null);
        }

        TextView interestRate = ViewHolder.get(convertView,
                R.id.homepage_fragment_item_interestRate);
        TextView tvTitle = ViewHolder.get(convertView, R.id.tv_bid_title);
        ImageView award = ViewHolder.get(convertView, R.id.investment_award);
        ImageView novice = ViewHolder.get(convertView, R.id.investment_novice);
        ImageView ivType = ViewHolder.get(convertView, R.id.iv_bid_type);
        TextView mouth = ViewHolder.get(convertView,
                R.id.homepage_fragment_item_tv_month);
        TextView mouth_textView = ViewHolder.get(convertView,
                R.id.homepage_fragment_item_tv_month_textView);

        TextView money = ViewHolder.get(convertView,
                R.id.homepage_fragment_item_tv_money);
        TextView money_textView = ViewHolder.get(convertView,
                R.id.homepage_fragment_item_tv_money_textView);
        TextView people_amount = ViewHolder.get(convertView,
                R.id.homepage_fragment_item_people_amount);
        TextView name = ViewHolder.get(convertView,
                R.id.homepage_fragment_item_tv_name);
        RoundProgressBar progressBar = ViewHolder.get(convertView,
                R.id.homepage_fragment_item_pb_progress);

        View rlInvestmentNoviceContainer = ViewHolder.get(convertView, R.id.rl_investment_novice_container);
        TextView tvAdditionalApr = ViewHolder.get(convertView, R.id.tv_additional_apr);

        InvestmentListInfo info = mArrayList.get(position);
        double count_money = Double
                .valueOf(info.getAmount());
//        if (position == 0) {
//            // novice.setVisibility(View.VISIBLE);
//            award.setVisibility(View.VISIBLE);
//        } else {
//            // novice.setVisibility(View.GONE);
//            award.setVisibility(View.GONE);
//        }
        if (count_money / 10000 >= 1) {
            money.setText(df.format(count_money / 10000) + getString(R.string.common_ten_thousand));
        } else {
            money.setText(df.format(count_money));
        }
        if (!StringUtils.isEmpty(info.getVouch_company_name())) {
            name.setText(info.getVouch_company_name());
        } else {
            name.setText(R.string.common_null);
        }
        if (info.getAward_status().equals("-1")) {
            award.setVisibility(View.GONE);
        } else {
            award.setVisibility(View.VISIBLE);
        }
        tvTitle.setText(info.getName());
        interestRate.setText(info.getApr());

        if (!StringUtils.isEmpty(info.getPic())) {
            ivType.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(info.getPic(), ivType, ImageUtil.getImageOptions());
        }
        //标的类型:1正常标 2:担保标 3:流转标
        else if ("1".equals(info.getCategory_type())) {
            //标种类图标设置(借款标种类 1 信用标 2净值标 3天标 4担保标 5抵押标 6秒标 7流转标 )
            if ("1".equals(info.getCategory_id())) {
                ivType.setVisibility(View.VISIBLE);
                ivType.setImageResource(R.drawable.bid_type_xin);
            } else if ("2".equals(info.getCategory_id())) {
                ivType.setVisibility(View.VISIBLE);
                ivType.setImageResource(R.drawable.bid_type_jin);
            } else if ("3".equals(info.getCategory_id())) {
                ivType.setVisibility(View.VISIBLE);
                ivType.setImageResource(R.drawable.bid_type_tian);
            } else if ("4".equals(info.getCategory_id())) {
                ivType.setVisibility(View.VISIBLE);
                ivType.setImageResource(R.drawable.bid_type_dan);
            }else if ("5".equals(info.getCategory_id())) {
                ivType.setVisibility(View.VISIBLE);
                ivType.setImageResource(R.drawable.bid_type_di);
            } else if ("6".equals(info.getCategory_id())) {
                ivType.setVisibility(View.VISIBLE);
                ivType.setImageResource(R.drawable.bid_type_miao);
            } else if ("7".equals(info.getCategory_id())) {
                ivType.setVisibility(View.VISIBLE);
                ivType.setImageResource(R.drawable.bid_type_liu);
            } else {
                ivType.setVisibility(View.INVISIBLE);
            }
        } else if ("2".equals(info.getCategory_type())) {
                ivType.setVisibility(View.VISIBLE);
                ivType.setImageResource(R.drawable.bid_type_dan);
        } else if ("3".equals(info.getCategory_type())) {
                ivType.setVisibility(View.VISIBLE);
                ivType.setImageResource(R.drawable.bid_type_liu);
        } else {
            ivType.setVisibility(View.INVISIBLE);
        }

        //还款类型=5是按天计息到期还本息
        if (info.getRepay_type().equals("5")) {
            mouth_textView.setText(R.string.common_day);
        } else {
            mouth_textView.setText(R.string.common_month);
        }
        mouth.setText(info.getPeriod());
        Double progress = Double.valueOf(info.getProgress());
        if (progress >= 100 || !"3".equals(info.getStatus())) {
            progressBar.setText(info.getStatus_name());
        } else {
            progressBar.setText(null);//不设置内容默认显示进度
        }
        progressBar.setProgress(progress);
        people_amount.setText(getString(R.string.investmentfragment_population) + info.getTender_count());


        if ("1".equals(info.getAdditionalStatus())) {
            rlInvestmentNoviceContainer.setVisibility(View.VISIBLE);
            if (StringUtils.isEmpty(info.getAdditional_apr())) {
                tvAdditionalApr.setVisibility(View.GONE);
            } else {
                tvAdditionalApr.setVisibility(View.VISIBLE);
                double additionalApr = Double
                        .valueOf(info.getAdditional_apr());

                tvAdditionalApr.setText(String.format(mContext.getString(R.string.fmd_additional_apr), additionalApr));
            }
        } else {
            rlInvestmentNoviceContainer.setVisibility(View.GONE);
            tvAdditionalApr.setVisibility(View.GONE);
        }

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

    private String getString(int id) {
        return mContext.getString(id);
    }

}
