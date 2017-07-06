package com.diyou.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.diyou.bean.InsideLetterInfo;
import com.diyou.pulltorefresh.PullToRefreshListView;
import com.diyou.util.DateUtils;
import com.diyou.v5yibao.R;

import java.util.List;

public class InsideLetterAdapter
        extends QLBaseAdapter<InsideLetterInfo, PullToRefreshListView> {
    private Context context;
    private List<InsideLetterInfo> list;

    public InsideLetterAdapter(Context context, List<InsideLetterInfo> list) {
        super(context, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Hondler hondler = null;
        if (convertView == null) {
            hondler = new Hondler();
            convertView = View.inflate(context,
                    R.layout.activity_inside_adapter, null);
            hondler.time = (TextView) convertView
                    .findViewById(R.id.inside_time);
            hondler.title = (TextView) convertView
                    .findViewById(R.id.inside_title);
            hondler.inside_imageView = (ImageView) convertView
                    .findViewById(R.id.inside_imageView);
            convertView.setTag(hondler);
        } else {
            hondler = (Hondler) convertView.getTag();
        }
        if (list.get(position).getStatus() == 1) {
            hondler.inside_imageView.setVisibility(View.VISIBLE);
        } else {
            hondler.inside_imageView.setVisibility(View.INVISIBLE);
        }

        hondler.time.setText(
                DateUtils.stampToNowDate(list.get(position).getSend_time()));
        hondler.title.setText(list.get(position).getTitle());
        return convertView;
    }

    private class Hondler {
        TextView time;
        TextView title;
        ImageView inside_imageView;
    }
}
