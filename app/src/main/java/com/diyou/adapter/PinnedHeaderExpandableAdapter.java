package com.diyou.adapter;

import java.util.HashMap;
import java.util.List;

import com.diyou.bean.ProvinceInfo;
import com.diyou.v5yibao.R;
import com.diyou.view.PinnedHeaderExpandableListView;
import com.diyou.view.PinnedHeaderExpandableListView.HeaderAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class PinnedHeaderExpandableAdapter extends BaseExpandableListAdapter
        implements HeaderAdapter
{
    private Context context;
    private PinnedHeaderExpandableListView listView;
    private LayoutInflater inflater;
    private List<ProvinceInfo> mProvinceInfos;

    public PinnedHeaderExpandableAdapter(List<ProvinceInfo> provinceInfos,
            Context context, PinnedHeaderExpandableListView listView)
    {
        this.context = context;
        this.listView = listView;
        this.mProvinceInfos = provinceInfos;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return mProvinceInfos.get(groupPosition).getCitys().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent)
    {
        View view = null;
        if (convertView != null)
        {
            view = convertView;
        }
        else
        {
            view = createChildrenView();
        }
        TextView text = (TextView) view.findViewById(R.id.childto);
        text.setText(mProvinceInfos.get(groupPosition).getCitys()
                .get(childPosition).getName());
        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return mProvinceInfos.get(groupPosition).getCitys().size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return mProvinceInfos.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return mProvinceInfos.size();
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent)
    {
        View view = null;
        if (convertView != null)
        {
            view = convertView;
        }
        else
        {
            view = createGroupView();
        }

        TextView text = (TextView) view.findViewById(R.id.groupto);
        text.setText(mProvinceInfos.get(groupPosition).getName());
        return view;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

    private View createChildrenView()
    {
        return inflater.inflate(R.layout.child, null);
    }

    private View createGroupView()
    {
        return inflater.inflate(R.layout.group, null);
    }

    @Override
    public int getHeaderState(int groupPosition, int childPosition)
    {
        final int childCount = getChildrenCount(groupPosition);
        if (childPosition == childCount - 1)
        {
            return PINNED_HEADER_PUSHED_UP;
        }
        else if (childPosition == -1
                && !listView.isGroupExpanded(groupPosition))
        {
            return PINNED_HEADER_GONE;
        }
        else
        {
            return PINNED_HEADER_VISIBLE;
        }
    }

    @Override
    public void configureHeader(View header, int groupPosition,
            int childPosition, int alpha)
    {
        String groupData = this.mProvinceInfos.get(groupPosition).getName();
        ((TextView) header.findViewById(R.id.groupto)).setText(groupData);

    }

    private HashMap<Integer, Integer> groupStatusMap = new HashMap<Integer, Integer>();

    @Override
    public void setGroupClickStatus(int groupPosition, int status)
    {
        groupStatusMap.put(groupPosition, status);
    }

    @Override
    public int getGroupClickStatus(int groupPosition)
    {
        if (groupStatusMap.containsKey(groupPosition))
        {
            return groupStatusMap.get(groupPosition);
        }
        else
        {
            return 0;
        }
    }
}
