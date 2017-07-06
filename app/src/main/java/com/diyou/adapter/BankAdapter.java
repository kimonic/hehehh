package com.diyou.adapter;

import java.util.List;

import com.diyou.bean.BankInfo;
import com.diyou.util.ImageUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ViewHolder;
import com.diyou.v5yibao.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class BankAdapter extends QLBaseAdapter<BankInfo, ListView>
{

    private Context mContext;
    private List<BankInfo> mList;

    public BankAdapter(Context context, List<BankInfo> list)
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
                    .inflate(R.layout.item_bank_lv, null);
        }

        final ImageView ivIcon = ViewHolder.get(convertView, R.id.iv_bank_item_icon);
        TextView tvName = ViewHolder.get(convertView, R.id.tv_bank_item_name);
        BankInfo bankInfo = mList.get(position);
        tvName.setText(bankInfo.getName());
        tvName.setTag(bankInfo.getCode());
        if(StringUtils.isEmpty(bankInfo.getUrl())){
            ivIcon.setVisibility(View.INVISIBLE);
        }else{
            ivIcon.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(bankInfo.getUrl(), ivIcon, ImageUtil.getImageOptions(),new ImageLoadingListener()
            {
                
                @Override
                public void onLoadingStarted(String imageUri, View view)
                {
                    // TODO Auto-generated method stub
                    
                }
                
                @Override
                public void onLoadingFailed(String imageUri, View view,
                        FailReason failReason)
                {
                    ivIcon.setVisibility(View.INVISIBLE);
                }
                
                @Override
                public void onLoadingComplete(String imageUri, View view,
                        Bitmap loadedImage)
                {
                    // TODO Auto-generated method stub
                    
                }
                
                @Override
                public void onLoadingCancelled(String imageUri, View view)
                {
                    // TODO Auto-generated method stub
                    
                }
            });
        }
        return convertView;
    }

}
