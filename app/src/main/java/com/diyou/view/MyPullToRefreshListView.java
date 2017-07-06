package com.diyou.view;

import com.diyou.pulltorefresh.PullToRefreshListView;

import android.content.Context;
import android.util.AttributeSet;

public class MyPullToRefreshListView extends PullToRefreshListView
{

    public MyPullToRefreshListView(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public MyPullToRefreshListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MyPullToRefreshListView(Context context, Mode mode)
    {
        super(context, mode);
    }

    public MyPullToRefreshListView(Context context, Mode mode,
            AnimationStyle style)
    {
        super(context, mode, style);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
