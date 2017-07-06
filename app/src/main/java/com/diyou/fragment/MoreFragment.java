package com.diyou.fragment;

import com.diyou.util.ViewHolder;
import com.diyou.v5yibao.R;
import com.diyou.view.ProgressDialogBar;
import com.diyou.view.PullToZoomListView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MoreFragment extends Fragment
{

    private PullToZoomListView mListview;
    private ProgressDialogBar progressBar;
    private String phone;
    private BaseAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        container = (ViewGroup) inflater.inflate(R.layout.fragment_more,
                container, false);
        initView(container);
        getWorkPhone();
        return container;
    }

    private void initView(final ViewGroup container)
    {
        mListview = (PullToZoomListView) container
                .findViewById(R.id.morefragment_listview);
        adapter = new BaseAdapter()
        {

            @SuppressLint("InflateParams")
            @Override
            public View getView(int position, View convertView,
                    ViewGroup parent)
            {
                if (convertView == null)
                {
                    convertView = getActivity().getLayoutInflater()
                            .inflate(R.layout.item_morefragment, null);
                }

                RelativeLayout telephone_layout = ViewHolder.get(convertView,
                        R.id.morefragment_item_telephone_layout);
                telephone_layout.setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {

                        new AlertDialog.Builder(getActivity())
                                .setTitle("是否需要拨打客服电话？").setCancelable(false)
                                .setPositiveButton("拨打",
                                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,
                                    int whichButton)
                            {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + phone));
                                startActivity(intent);
                            }
                        }).setNegativeButton("算了",
                                new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,
                                    int whichButton)
                            {
                            }
                        }).show();
                    }

                });
                TextView updateVersion = ViewHolder.get(convertView,
                        R.id.morefragment_version_number_tv);
                updateVersion.setText("V" + getVersion(getActivity()));
                TextView contactPhone = ViewHolder.get(convertView,
                        R.id.morefragment_item_contactPhone);
                contactPhone.setText(phone);
                RelativeLayout inspection_layout = ViewHolder.get(convertView,
                        R.id.morefragment_update_inspection_layout);
                inspection_layout.setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        // new UpdateAPK(getActivity(), true);

                    }
                });
                return convertView;
            }

            @Override
            public long getItemId(int position)
            {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public Object getItem(int position)
            {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getCount()
            {
                // TODO Auto-generated method stub
                return 1;
            }
        };
        mListview.setAdapter(adapter);
        mListview.getHeaderView().setImageResource(R.drawable.more);
        mListview.getHeaderView().setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    private void getWorkPhone()
    {
    }

    private String getVersion(Context context)// 获取版本号
    {
        try
        {
            PackageInfo pi = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        }
        catch (NameNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "1.0.1";
        }
    }
}
