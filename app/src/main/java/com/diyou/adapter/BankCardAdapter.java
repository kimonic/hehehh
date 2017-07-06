package com.diyou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.diyou.bean.BankCardInfo;
import com.diyou.pulltorefresh.PullToRefreshListView;
import com.diyou.util.StringUtils;
import com.diyou.util.ViewHolder;
import com.diyou.v5yibao.R;

import java.util.List;

import static com.diyou.v5yibao.R.*;

/**
 * Created by Administrator on 2015-11-3.
 */
public class BankCardAdapter extends QLBaseAdapter<BankCardInfo,PullToRefreshListView> {
    private final Context context;
    private final List<BankCardInfo> list;

    public BankCardAdapter(Context context, List<BankCardInfo> list) {
        super(context, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BankCardInfo info = list.get(position);
        View layout =null;
        if(info.isExist()){

                layout = LayoutInflater.from(context).inflate(R.layout.item_bank_card_lv,null);
                TextView tvType = (TextView) layout.findViewById(id.tv_bank_card_type);
                TextView tvAccount = (TextView) layout.findViewById(id.tv_bank_card_account);
                TextView tvName = (TextView) layout.findViewById(id.tv_bank_card_name);
                tvType.setText(info.getBank_name());
                tvAccount.setText(StringUtils.getHideBankAccount(info.getAccount()));
                tvName.setText(StringUtils.getHideName(info.getRealname()));
        }else{
            layout = LayoutInflater.from(context).inflate(R.layout.item_bank_card_no,null);
        }
        return layout;
    }
}
