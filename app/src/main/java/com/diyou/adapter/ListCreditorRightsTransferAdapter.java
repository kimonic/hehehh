package com.diyou.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.diyou.bean.CreditorRightsTransferBean;
import com.diyou.util.DataTools;
import com.diyou.util.ImageUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ViewHolder;
import com.diyou.v5yibao.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListCreditorRightsTransferAdapter extends BaseAdapter
{
    private Activity mContext;
    private ArrayList<CreditorRightsTransferBean> mArrayList;
    private DecimalFormat df;

    public ListCreditorRightsTransferAdapter(Activity context,
            ArrayList<CreditorRightsTransferBean> mArrayList)
    {
        this.mArrayList = mArrayList;
        mContext = context;
        df = new DecimalFormat("######0.00");

    }

    @Override
    public int getCount()
    {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = mContext.getLayoutInflater()
                    .inflate(R.layout.item_creditorrightstransfer, null);
        }
        TextView tvTitle = ViewHolder.get(convertView, R.id.tv_bid_title);
        TextView change_account = ViewHolder.get(convertView,
                R.id.listcreditorrightstransfer_transferValue);
        TextView Price_account = ViewHolder.get(convertView,
                R.id.listcreditorrightstransfer_transferPrice);
        TextView borrow_apr = ViewHolder.get(convertView,
                R.id.listcreditorrightstransfer_borrowingRates);
        TextView change_period = ViewHolder.get(convertView,
                R.id.listcreditorrightstransfer_transferNper);
        TextView borrow_period = ViewHolder.get(convertView,
                R.id.listcreditorrightstransfer_totalperiods);
        TextView danbao_company = ViewHolder.get(convertView,
                R.id.tv_creditor_item_danbao);
        ImageView ivType = ViewHolder.get(convertView, R.id.iv_bid_type);
        View rlSoldoutContainer = ViewHolder.get(convertView, R.id.rl_soldoutcontainer);
        CreditorRightsTransferBean bean = mArrayList.get(position);
        tvTitle.setText(bean.getLoan_name());
        //担保公司
        if(StringUtils.isEmpty(bean.getVouch_company_name())){
            danbao_company.setText("无");
        }else{
            danbao_company.setText(bean.getVouch_company_name());
        }

        if(StringUtils.isEmpty(bean.getAmount_money())){
            change_account.setText("0");
        }else{
            if (Double.valueOf(bean.getAmount_money()) / 10000 > 1)
            {
                change_account.setText(changeFontSize(
                        df.format(Double.valueOf(bean.getAmount_money()) / 10000)
                                + getString(R.string.common_ten_thousand_yuan)));
            }
            else
            {
                change_account
                        .setText(changeFontSize(bean.getAmount_money() + getString(R.string.common_display_yuan_default)));
            }
        }

        if(StringUtils.isEmpty(bean.getAmount())){
            Price_account.setText("0");
        }else{

            if (Double.valueOf(bean.getAmount()) / 10000 > 1)
            {
                Price_account.setText(changeFontSize(
                        df.format(Double.valueOf(bean.getAmount()) / 10000) + getString(R.string.common_ten_thousand)));
            }
            else
            {
                Price_account.setText(changeFontSize(bean.getAmount() + getString(R.string.common_display_yuan_default)));
            }
        }

        borrow_apr.setText(changeFontSize(
                StringUtils.getDoubleFormat(bean.getBorrow_apr()) + "%"));
        change_period.setText("" + bean.getPeriod());
        borrow_period.setText("" + bean.getTotal_period());
        if (!StringUtils.isEmpty(bean.getPic())) {
            ivType.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(bean.getPic(), ivType, ImageUtil.getImageOptions());
        }
        //标种类图标设置(借款标种类 1 信用标 2净值标 3天标 4担保标 5抵押标 6秒标 7流转标)
        else if("1".equals(bean.getCategory_id())){
            ivType.setVisibility(View.VISIBLE);
            ivType.setImageResource(R.drawable.bid_type_xin);
        }else if("2".equals(bean.getCategory_id())){
            ivType.setVisibility(View.VISIBLE);
            ivType.setImageResource(R.drawable.bid_type_jin);
        }else if("3".equals(bean.getCategory_id())){
            ivType.setVisibility(View.VISIBLE);
            ivType.setImageResource(R.drawable.bid_type_tian);
        }else if("4".equals(bean.getCategory_id())){
            ivType.setVisibility(View.VISIBLE);
            ivType.setImageResource(R.drawable.bid_type_dan);
        }else if("5".equals(bean.getCategory_id())){
            ivType.setVisibility(View.VISIBLE);
            ivType.setImageResource(R.drawable.bid_type_di);
        }else if("6".equals(bean.getCategory_id())){
            ivType.setVisibility(View.VISIBLE);
            ivType.setImageResource(R.drawable.bid_type_miao);
        }else if("7".equals(bean.getCategory_id())){
            ivType.setVisibility(View.VISIBLE);
            ivType.setImageResource(R.drawable.bid_type_liu);
        }else{
            ivType.setVisibility(View.INVISIBLE);
        }

        //是否已售罄
        if(2 == bean.getStatus_name()){
            rlSoldoutContainer.setVisibility(View.VISIBLE);
        }else{
            rlSoldoutContainer.setVisibility(View.GONE);
        }
        return convertView;
    }

    private Spannable changeFontSize(String str)
    {

        Spannable WordtoSpan = new SpannableString(str);

        WordtoSpan.setSpan(new AbsoluteSizeSpan(DataTools.dip2px(mContext, 12)),
                str.length() - 1, str.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        return WordtoSpan;

    }

    private String getString(int id){
        return mContext.getString(id);
    }
}
