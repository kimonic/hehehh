package com.diyou.pull2refresh;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class ZhsReleaseViewPager extends PullToRefreshBase<ViewPager>
{

    public ZhsReleaseViewPager(Context context)
    {
        this(context, null);
    }

    public ZhsReleaseViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        setMode(Mode.BOTH);
        setLoadingDrawable(null);
        setOnRefreshListener(new OnRefreshListener<ViewPager>()
        {

            @Override
            public void onRefresh(PullToRefreshBase<ViewPager> refreshView)
            {
                onRefreshComplete();
            }
        });
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection()
    {
        return Orientation.HORIZONTAL;
    }

    @Override
    protected ViewPager createRefreshableView(Context context,
            AttributeSet attrs)
    {
        ViewPager viewPager = new ViewPager(context, attrs);
        // viewPager.setId(R.id.viewpager);
        return viewPager;
    }

    @Override
    protected boolean isReadyForPullStart()
    {
        ViewPager refreshableView = getRefreshableView();

        PagerAdapter adapter = refreshableView.getAdapter();
        if (null != adapter)
        {
            return refreshableView.getCurrentItem() == 0;
        }

        return false;
    }

    @Override
    protected boolean isReadyForPullEnd()
    {
        ViewPager refreshableView = getRefreshableView();

        PagerAdapter adapter = refreshableView.getAdapter();
        if (null != adapter)
        {
            return refreshableView.getCurrentItem() == adapter.getCount() - 1;
        }

        return false;
    }
}
