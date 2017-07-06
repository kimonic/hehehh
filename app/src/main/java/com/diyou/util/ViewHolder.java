package com.diyou.util;

import android.util.SparseArray;
import android.view.View;

/**
 * SparseArray这个类，优化过的存储integer和object键�?对的hashmap 只需静�?调用get这个方法填入当前Adapter�?
 * convertView �?子控件的id,就可以实现复用�?
 * 
 * @author Administrator
 * 
 */

public class ViewHolder
{

    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id)
    {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null)
        {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null)
        {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}