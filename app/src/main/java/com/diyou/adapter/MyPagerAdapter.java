package com.diyou.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class MyPagerAdapter extends PagerAdapter
{
    private ArrayList<View> mArrayList;

    public MyPagerAdapter(ArrayList<View> mArrayList)
    {
        this.mArrayList = mArrayList;
    }

    @Override
    public int getItemPosition(Object object)
    {

        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(View container, int position)
    {
        if (mArrayList.size() == 1)
        {
            ((ViewPager) container).addView(mArrayList.get(0), 0);
            return mArrayList.get(0);
        }
        else if (mArrayList.size() == 2)
        {
            ((ViewPager) container).addView(mArrayList.get(position), 0);
            return mArrayList.get(position);
        }
        else if (mArrayList.size() != 0 && position != 0 && position != 1)
        {
            if (((ViewPager) container).getChildCount() != 0
                    && ((ViewPager) container).getChildCount() == mArrayList
                            .size())
            {
                ((ViewPager) container).removeView(
                        mArrayList.get(Math.abs(position) % mArrayList.size()));
            }
            try
            {
                position = position % mArrayList.size();
                ((ViewPager) container).addView(
                        mArrayList.get(Math.abs(position) % mArrayList.size()),
                        0);
            }
            catch (Exception e)
            {
            }
            return mArrayList.get(Math.abs(position) % mArrayList.size());

        }
        else
        {
            return null;
        }
    }

    @Override
    public void destroyItem(View container, int position, Object object)
    {
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1)
    {
        return arg0 == arg1;
    }

    @Override
    public int getCount()
    {
        if (mArrayList.size() == 1)
        {
            return 1;
        }
        else if (mArrayList.size() == 2)
        {
            return 2;
        }
        else
        {
            return Integer.MAX_VALUE;
        }
    }
}
