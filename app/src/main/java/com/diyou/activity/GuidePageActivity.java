package com.diyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.diyou.base.BaseActivity;
import com.diyou.util.SharedPreUtils;
import com.diyou.v5yibao.R;

import java.util.ArrayList;

/**
 * 初次打开app时的引导activity
 */

public class GuidePageActivity extends BaseActivity
{
    /**viewpager中添加的子空间集合*/
    private ArrayList<View> mList = new ArrayList<View>();
    private ViewPager mPager;
    /**viewpager的滑动状态*/
    boolean misScrolled;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_page);
        //记录已经打开过该页面下次启动app时不再打开
        SharedPreUtils.putBoolean("isFirstStart", true, GuidePageActivity.this);
        initView();
    }

    @Override
    public void onBackPressed()
    {
        //引导页按下返回键时直接启动mainactivity
        startActivity(new Intent(GuidePageActivity.this, MainActivity.class));
        finish();
        super.onBackPressed();
    }

    private void initView()
    {
        //初始化展示图片的imageview
        LayoutInflater inflater = getLayoutInflater();
        ImageView view1 = (ImageView) inflater.inflate(R.layout.guidepage_item,
                null);
        view1.setImageResource(R.drawable.concept1);
        ImageView view2 = (ImageView) inflater.inflate(R.layout.guidepage_item,
                null);
        view2.setImageResource(R.drawable.concept2);
        ImageView view3 = (ImageView) inflater.inflate(R.layout.guidepage_item,
                null);
        view3.setImageResource(R.drawable.concept3);
        ImageView view4 = (ImageView) inflater.inflate(R.layout.guidepage_item,
                null);
        view4.setImageResource(R.drawable.concept4);
        mList.add(view1);
        mList.add(view2);
        mList.add(view3);
        mList.add(view4);
        //引导页的最后一张图片设置点击时间进入mainactivity
        mList.get(mList.size()-1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GuidePageActivity.this,
                        MainActivity.class));
                finish();
            }
        });

        mPager = (ViewPager) findViewById(R.id.guidePage_viewpager);
        //监听viewpager滑动状态改变
        mPager.setOnPageChangeListener(new OnPageChangeListener()
        {

            @Override
            public void onPageSelected(int arg0)
            {

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2)
            {
            }

            @Override
            public void onPageScrollStateChanged(int arg0)
            {
//                SCROLL_STATE_IDLE : 值为0，表示当前页已经选定。
//                SCROLL_STATE_DRAGGING: 值为1，表示当前页面正在拖动。
//                SCROLL_STATE_SETTLING: 值为2，表示当前页面正在拖动中，已有选中位置。
                switch (arg0)
                {
                case ViewPager.SCROLL_STATE_DRAGGING://正在拖动
                    misScrolled = false;
                    break;
                case ViewPager.SCROLL_STATE_SETTLING://正在拖动,已有选中位置,自动滑动
                    misScrolled = true;
                    break;
                case ViewPager.SCROLL_STATE_IDLE://选定
                    //viewpager处在最后一个并且正在继续拖动,启动mainactivity
                    //viewpager的第一个位置继续左拖动和最后一个位置继续右拖动,无自动滑动状态
                    if (mPager
                            .getCurrentItem() == mPager.getAdapter().getCount()
                                    - 1
                            && !misScrolled)
                    {
                        startActivity(new Intent(GuidePageActivity.this,
                                MainActivity.class));
                        finish();
                    }
                    misScrolled = true;
                    break;
                }
            }
        });
        //设置viewpager适配器
        mPager.setAdapter(new PagerAdapter()
        {
            @Override
            public Object instantiateItem(View container, int position)
            {
                View layout = mList.get(position);
                mPager.addView(layout);
                return layout;

            }

            @Override
            public void destroyItem(View container, int position, Object object)
            {
                View layout = mList.get(position);
                mPager.removeView(layout);

            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1)
            {
                return arg0 == arg1;

            }

            @Override
            public int getCount()
            {
                // TODO Auto-generated method stub
                return mList.size();
            }
        });
    }
}
