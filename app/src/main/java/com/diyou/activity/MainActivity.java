package com.diyou.activity;

import java.util.ArrayList;
import java.util.List;

import com.diyou.application.MyApplication;
import com.diyou.base.BaseMainActivity;
import com.diyou.config.Constants;
import com.diyou.fragment.HomeFragment;
import com.diyou.fragment.InvestmentFragment;
import com.diyou.fragment.MoreFragment;
import com.diyou.fragment.PersonalCenterFragment;
import com.diyou.util.Global;
import com.diyou.util.ToastUtil;
import com.diyou.util.UpdateAPK;
import com.diyou.v5yibao.R;
import com.diyou.view.CustomDialog;
import com.diyou.view.ZoomOutPageTransformer;
import com.igexin.sdk.PushManager;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import javax.security.auth.login.LoginException;

/**
 * 主页activity,由底部导航按钮和viewpager加fragment组合
 */

public class MainActivity extends BaseMainActivity
        implements OnClickListener, OnPageChangeListener
{
    /**fragment容器*/
    public ViewPager mViewPager;
    /**fragment集合*/
    private List<Fragment> mTabs = new ArrayList<>();
    /**viewpager适配器*/
    private FragmentPagerAdapter mAdapter;
    /**首页fragment*/
    private HomeFragment mHomeFragment;
    /**投资fragment*/
    private InvestmentFragment mInvestmentFragment;
    /**个人中心fragment*/
    private PersonalCenterFragment personalCenterFragment;
    /**更多额外的fragment*/
    private MoreFragment mMoreFragmenmt;
    /**app入口自定义application*/
    private MyApplication mPpplication;
    /**单选按钮容器*/
    private RadioGroup mIndicator_sum;
    /**???*/
    private int mClicks;
    /**自定义dialog*/
    private CustomDialog dialog;
    /**???*/
    private boolean mOtherLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //打开锁屏文件
        SharedPreferences preferences = getSharedPreferences(Constants.LOCK,
                MODE_PRIVATE);
        //取出lock_key值
        String lockPattenString = preferences.getString(Constants.LOCK_KEY,
                null);
        //如果设置了锁屏,就打开锁屏activity
//        if (lockPattenString != null)
//        {
            Intent intent = new Intent(this, LockActivity.class);
            startActivity(intent);

//        }
        mOtherLogin = getIntent().getBooleanExtra("otherLogin", false);
        mPpplication = (MyApplication) getApplication();
        //设置全局应用上下文
        Global.setApplicationContext(mPpplication);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 从AndroidManifest.xml的meta-data中读取SDK配置信息,启动推送服务
        PushManager.getInstance().initialize(this.getApplicationContext());
        initView();
        // 检测版本更新
        UpdateAPK.getInstance(this, false).updataVersion();

    }

    private void initView()
    {
        initViewPager();
        initTabIndicators();
    }
    /**初始化底部导航*/
    private void initTabIndicators()
    {

        mIndicator_sum = (RadioGroup) findViewById(R.id.indicator_group);
        findViewById(R.id.indicator_one).setOnClickListener(this);
        findViewById(R.id.indicator_two).setOnClickListener(this);
        // findViewById(R.id.indicator_three).setOnClickListener(this);
        findViewById(R.id.indicator_four).setOnClickListener(this);
        RadioButton radioButton = (RadioButton) mIndicator_sum.getChildAt(0);
        radioButton.setChecked(true);
    }

    /**
     * 初始化所有事件
     */
    private void initEvent()
    {
        mViewPager.setOnPageChangeListener(this);
    }

    /**
     * 初始化fragment
     */
    private void initDatas()
    {
        mHomeFragment = new HomeFragment();
        mInvestmentFragment = new InvestmentFragment();
        personalCenterFragment = new PersonalCenterFragment();
        mMoreFragmenmt = new MoreFragment();

        //设置到application?????
        mPpplication.setFirstFragment(mHomeFragment);
        mPpplication.setSecondFragment(mInvestmentFragment);
        mPpplication.setThirdFragment(personalCenterFragment);
        mPpplication.setFourthFragment(mMoreFragmenmt);


        mTabs.add(mHomeFragment);
        mTabs.add(mInvestmentFragment);
        // mTabs.add(mMoreFragmenmt);
        mTabs.add(personalCenterFragment);

    }
    /**初始化viewpager*/
    private void initViewPager()
    {
        initDatas();
        // dealRegisterSuccess();
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        //设置ViewPager切换时的动画效果
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mViewPager.setOffscreenPageLimit(3);//不显示时不销毁
        // mViewPager.setCurrentItem(0, false);
        initEvent();
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
        {

            @Override
            public int getCount()
            {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position)
            {
                return mTabs.get(position);
            }
        };
        mViewPager.setAdapter(mAdapter);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
            int positionOffsetPixels)
    {

    }

    @Override
    public void onPageSelected(int position)
    {
        if (mTabs != null && mTabs.size() > 0)
        {
            // if (position == 2) {
            // if (CheckIsLogin()) {
            // mViewPager.setCurrentItem(2, false);
            // now_select = 2;
            // } else {
            // ((RadioButton) mIndicator_sum.getChildAt(now_select))
            // .setChecked(true);
            // mViewPager.setCurrentItem(now_select, false);
            // }
            // } else {
            // now_select = position;
            RadioButton radioButton = (RadioButton) mIndicator_sum
                    .getChildAt(position);
            radioButton.setChecked(true);
            // }

        }
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.indicator_one:
            mViewPager.setCurrentItem(0, false);
            // now_select = 0;
            break;
        case R.id.indicator_two:
            mViewPager.setCurrentItem(1, false);
            // now_select = 1;
            break;
        // case R.id.indicator_three:
        // mViewPager.setCurrentItem(2, false);
        // // now_select = 3;
        // break;
        case R.id.indicator_four:
            // if (CheckIsLogin()) {
            mViewPager.setCurrentItem(2, false);
            // now_select = 2;
            // } else {
            // ((RadioButton) mIndicator_sum.getChildAt(now_select))
            // .setChecked(true);
            // }
            break;

        default:
            break;
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent intent)
    {
        switch (arg1)
        {
        case Constants.EXIT_LOGIN_CODE:
            startActivityForResult(
                    new Intent(MainActivity.this, BeforeLoginActivity.class),
                    Constants.LOGINREQUESTCODE);
            mViewPager.setCurrentItem(0, false);
            break;
        }
        super.onActivityResult(arg0, arg1, intent);
    }

    /**
     * 捕捉返回键，如果当前显示菜单，就隐藏
     */
    @Override
    public void onBackPressed()
    {
        exitApp();
    }

    // 在2秒内 重复点击2次back键 就退出
    private void exitApp()
    {
        mClicks++;
        if (mClicks == 1)
        {
            ToastUtil.show("再按一次退出");
            mIndicator_sum.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mClicks = 0;
                }
            }, 2000);
        }
        else if (mClicks == 2)
        {
            NotificationManager nm = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);
            nm.cancel(UpdateAPK.ID_DOWNLOAD_NOTIFI);
            finish();
            int nPid = android.os.Process.myPid();
            android.os.Process.killProcess(nPid);
        }
    }
}
