package com.diyou.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.diyou.v5yibao.R;
import com.diyou.view.CalculatorFinancialDialog;
import com.diyou.view.PagerSlidingTabStrip;
import com.diyou.view.ZoomOutPageTransformer;

import java.util.ArrayList;

public class InvestmentFragment extends Fragment implements OnClickListener
{
    private PagerSlidingTabStrip mIndicator;
    private ViewPager mViewPage;
    private TextView title_tv;
    public CharSequence[] mTitle =
    { "投资列表", "债权列表" };
    private ImageView calculator;
    private boolean isLoad = false;
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    private ListInvestmentFragment investmentFragment;
    private ListCreditorRightsTransferFragment rightsTransferFragment;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        container = (ViewGroup) inflater.inflate(R.layout.fragment_investment,
                null);
        initView(container);
        return container;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        if (isVisibleToUser && !isLoad)
        {
            mViewPage.setPageTransformer(true, new ZoomOutPageTransformer());
            mViewPage.setOffscreenPageLimit(mTitle.length - 1);
            investmentFragment = new ListInvestmentFragment();
            rightsTransferFragment = new ListCreditorRightsTransferFragment();


            mFragmentList.add(investmentFragment);


            mFragmentList.add(rightsTransferFragment);
            mViewPage.setAdapter(new MyFragmentPagerAdapter(
                    getActivity().getSupportFragmentManager()));
            mIndicator.setViewPager(mViewPage);
            mIndicator.setDividerColorResource(R.color.gray);
            isLoad = true;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void initView(ViewGroup container)
    {
        title_tv = (TextView) container.findViewById(R.id.investmentfragment_title_tv);
        mIndicator = (PagerSlidingTabStrip) container
                .findViewById(R.id.investmentfragment_indicator);
        mViewPage = (ViewPager) container
                .findViewById(R.id.investmentfragment_viewPage);
        calculator = (ImageView) container
                .findViewById(R.id.investment_show_calculator);
        calculator.setOnClickListener(this);
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                           }

            @Override
            public void onPageSelected(int position) {
//                title_tv.setText(mTitle[position].toString());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public boolean getIsLoad()
    {
        return isLoad;
    }

    public void setLoad(boolean isLoad)
    {
        this.isLoad = isLoad;
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

    public void updataListInvestmentFragment()
    {
        if (investmentFragment == null)
        {
            investmentFragment = new ListInvestmentFragment();
        }
        // investmentFragment.updateListInvestmentFragment();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.investment_show_calculator:
            showDialog();
            break;
        default:
            break;
        }
    }

    private void showDialog()
    {
        CalculatorFinancialDialog dialog = new CalculatorFinancialDialog(
                getActivity());
        dialog.show();
    }
}
