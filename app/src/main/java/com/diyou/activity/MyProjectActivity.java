package com.diyou.activity;

import java.util.ArrayList;

import com.diyou.base.BaseActivity;
import com.diyou.fragment.MyCreditorFragment;
import com.diyou.fragment.MyInvestmentFragment;
import com.diyou.v5yibao.R;
import com.diyou.view.PagerSlidingTabStrip;
import com.diyou.view.ZoomOutPageTransformer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 我的项目
 * 
 * @author Administrator
 *
 */
public class MyProjectActivity extends BaseActivity implements OnClickListener
{
    private MyInvestmentFragment investmentFragment;
    private MyCreditorFragment myCreditorFragment;
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();

    public CharSequence[] mTitle =
    { "我的投资", "我的债权" };
    private PagerSlidingTabStrip mIndicator;
    private ViewPager mViewPage;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.activity_my_project);
        intiView();
    }

    private void intiView()
    {
        View titlebar = findViewById(R.id.titlebar);
        titlebar.findViewById(R.id.title_bar_back).setOnClickListener(this);
        TextView title_name = (TextView) titlebar.findViewById(R.id.title_name);
        title_name.setText("我的项目");
        mIndicator = (PagerSlidingTabStrip) findViewById(
                R.id.project_indicator);
        mViewPage = (ViewPager) findViewById(R.id.project_viewpager);
        mViewPage.setPageTransformer(true, new ZoomOutPageTransformer());
        mViewPage.setOffscreenPageLimit(mTitle.length - 1);
        investmentFragment = new MyInvestmentFragment();
        myCreditorFragment = new MyCreditorFragment();
        mFragmentList.add(investmentFragment);
        mFragmentList.add(myCreditorFragment);
        mViewPage.setAdapter(
                new MyFragmentPagerAdapter(getSupportFragmentManager()));
        mIndicator.setViewPager(mViewPage);
        mIndicator.setDividerColorResource(R.color.gray);
        mIndicator.setTextColor(getResources().getColor(R.color.hight_bule));
    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter
    {

        public MyFragmentPagerAdapter(FragmentManager fm)
        {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount()
        {
            return mTitle.length;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mTitle[position];
        }

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.title_bar_back:
            finish();
            break;

        default:
            break;
        }

    }

}
