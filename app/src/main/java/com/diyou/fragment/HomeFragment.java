package com.diyou.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.diyou.activity.AdvertisingActivity;
import com.diyou.activity.BeforeLoginActivity;
import com.diyou.activity.InvestmentDetailsActivity;
import com.diyou.activity.MainActivity;
import com.diyou.activity.SettingActivity;
import com.diyou.adapter.MyPagerAdapter;
import com.diyou.bean.InvestmentListBean;
import com.diyou.config.Constants;
import com.diyou.config.UrlConstants;
import com.diyou.http.HttpUtil;
import com.diyou.pulltorefresh.PullToRefreshBase;
import com.diyou.pulltorefresh.PullToRefreshBase.Mode;
import com.diyou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.diyou.pulltorefresh.PullToRefreshScrollView;
import com.diyou.util.DateUtils;
import com.diyou.util.ImageUtil;
import com.diyou.util.SharedPreUtils;
import com.diyou.util.StringUtils;
import com.diyou.v5yibao.R;
import com.diyou.view.LoadingLayout;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment implements OnClickListener
{
    private ArrayList<InvestmentListBean> mArrayList;
    private View mLoginLayout, mMyinformationLayout;
    private MainActivity mActivity;
    private ArrayList<Integer> mPoints = new ArrayList<Integer>();
    private ArrayList<String> mUrls = new ArrayList<String>();
    private ArrayList<View> mImgs = new ArrayList<View>();
    private ViewPager mPager;
    private Handler mPageHandler;
    private int mDircretion = 0;
    private RadioGroup mGroup;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private boolean isBannerTouch = false;
    private boolean isBannerStop = false;
    private boolean isInitView = true;
    private boolean isFirstOpen = true;
    private String loan_id;
    private String progress;
    private String url;
    private String status;
    private String share_url;
    private String share_title;
    private String share_content;
    private DecimalFormat df;
    private LoadingLayout mLoadlayout;
    // private TextView login;
    private TextView title_name;
    private TextView annual_percentage_rate;
    private TextView fragment_home_validate;
    private TextView fragment_home_tender_amount_min;
    private TextView fragment_home_repayTypeName;
    private PullToRefreshScrollView mScrollView;
    private BannerTimerTask bannerTimerTask;
    private Timer bannerTimer;
    private boolean isFirstLoad = true;
    private View mHomeNewLayout;
    private TextView mTvNewPercent;
    private TextView mTvNewMoney;
    private TextView mTvNewMonth;
    private TextView mTvNewTime;
    private TextView mTvNewWay;
    private TextView mTvNewTimeUnit;
    private TextView mTvNewMoneyUnit;
    private MyPagerAdapter mPagerAdapter;
    private String status_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        df = new DecimalFormat("######0.00");
        bannerTimer = new Timer();
        initView(inflater);
        container = (ViewGroup) initView(inflater);
        if (mActivity == null)
        {
            mActivity = (MainActivity) getActivity();
        }
        return container;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        if (isVisibleToUser)
        {
            if (isFirstOpen && isFirstLoad)
            {
                isFirstLoad = false;
                requestData();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private View initView(LayoutInflater inflater)
    {
        View container = inflater.inflate(R.layout.fragment_home, null, false);
        title_name = (TextView) container
                .findViewById(R.id.fragment_home_title);
        fragment_home_validate = (TextView) container
                .findViewById(R.id.fragment_home_validate);
        fragment_home_tender_amount_min = (TextView) container
                .findViewById(R.id.fragment_home_tender_amount_min);
        fragment_home_repayTypeName = (TextView) container
                .findViewById(R.id.fragment_home_repayTypeName);
        annual_percentage_rate = (TextView) container
                .findViewById(R.id.fragment_home_annual_percentage_rate);
        container.findViewById(R.id.rl_home_more).setOnClickListener(this);
        // login = (TextView) container.findViewById(R.id.login);
        // login.setOnClickListener(this);
        // if (!StringUtils.isEmpty(UserConfig.getInstance().getLoginToken(
        // MyApplication.app)))
        // {
        // setLogindimss();
        // }
        // if (!StringUtils.isEmpty(UserConfig.getInstance().getLoginToken(
        // MyApplication.app)))
        // {
        // setLogindimss();
        // }
        container.findViewById(R.id.homefragment_immediately_earning)
                .setOnClickListener(this);
        mLoadlayout = (LoadingLayout) container
                .findViewById(R.id.homefragment_loadlayout);
        mLoadlayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        requestData();
                    }
                });
        mPager = (ViewPager) container
                .findViewById(R.id.homefragment_viewPager);
        mPagerAdapter = new MyPagerAdapter(mImgs);
        mGroup = (RadioGroup) container
                .findViewById(R.id.homefragment_radiogroup);

        mArrayList = new ArrayList<InvestmentListBean>();
        mScrollView = (PullToRefreshScrollView) container
                .findViewById(R.id.content_layout);
        mScrollView.setMode(Mode.PULL_FROM_START);
        // 下拉刷新
        mScrollView.setOnRefreshListener(new OnRefreshListener2()
        {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase refreshView)
            {
                requestData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase refreshView)
            {

            }
        });

        mHomeNewLayout = container.findViewById(R.id.ll_home_new_layout);
        mHomeNewLayout.setOnClickListener(this);
        mTvNewPercent = (TextView) container
                .findViewById(R.id.tv_home_new_percent);
        mTvNewMoney = (TextView) container.findViewById(R.id.tv_home_new_money);
        mTvNewMoneyUnit = (TextView) container.findViewById(R.id.tv_home_new_money_unit);
        mTvNewMonth = (TextView) container.findViewById(R.id.tv_home_new_month);
        mTvNewTimeUnit = (TextView) container.findViewById(R.id.tv_home_new_time_unit);
        mTvNewTime = (TextView) container
                .findViewById(R.id.tv_home_new_start_time);
        mTvNewWay = (TextView) container.findViewById(R.id.tv_home_new_way);
        return container;
    }

    // public void setLogindimss()
    // {
    // if (!(login == null))
    // {
    // login.setVisibility(View.GONE);
    // }
    // }
    //
    // public void setLoginShow()
    // {
    // if (!(login == null))
    // {
    // login.setVisibility(View.VISIBLE);
    // }
    // }

    protected void requestData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        // map.put("key", "value");
        HttpUtil.post(UrlConstants.HOME, map, new JsonHttpResponseHandler()
        {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                if (isFirstOpen)
                {
                    mLoadlayout.setOnLoadingError(mActivity, mScrollView);
                }
                mScrollView.onRefreshComplete();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    if (CreateCode.AuthentInfo(response))
                    {
                        mArrayList.clear();
                        if (mGroup != null && mGroup.getChildCount() > 0)
                        {
                            mGroup.removeAllViews();
                            mImgs.clear();
                            mPoints.clear();
                            mUrls.clear();
                        }
                        ArrayList<String> urls = new ArrayList<String>();
                        urls.clear();
                        JSONObject jsonData = StringUtils.parseContent(response);
                        if (jsonData.get("result").equals("success"))
                        {

                            JSONObject data = jsonData.optJSONObject("data");
                            //保存客服电话和客服时间
                            if(!StringUtils.isEmpty(data.optString("service_tel"))){
                                SharedPreUtils.putString(Constants.KEY_SERVICE_TEL, data.optString("service_tel"), mActivity);
                            }
                            if(!StringUtils.isEmpty(data.optString("service_hours"))){
                                SharedPreUtils.putString(Constants.KEY_SERVICE_HOURS, data.optString("service_hours"), mActivity);
                            }
                            JSONArray banners = data
                                    .getJSONArray("banner");
                            if (banners != null)
                            {
                                for (int i = 0; i < banners.length(); i++)
                                {
                                    JSONObject object = (JSONObject) banners
                                            .get(i);
                                    urls.add(object.optString("image"));
                                    mUrls.add(object.optString("jumpurl"));
                                }
                            }
                            initViewPager(urls);
                            JSONObject loans = data
                                    .optJSONObject("loan");
                            if (loans != null)
                            {
                                title_name.setText(loans.optString("name"));
                                annual_percentage_rate
                                        .setText(loans.optString("apr"));
                                fragment_home_validate
                                        .setText(loans.optString("slogan_one"));
                                fragment_home_tender_amount_min.setText(
                                        loans.optString("slogan_three"));
                                fragment_home_repayTypeName
                                        .setText(loans.optString("slogan_two"));
                                loan_id = loans.optString("id");
                                progress = loans.optString("progress");
                                url = loans.optString("url");
                                status = loans.optString("status");
                                status_name = loans.optString("status_name");
                              share_title = loans.optString("share_title");
                              share_content = loans.optString("share_content");
                            }
                            initHomeNew(jsonData);
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                if (isFirstOpen)
                {
                    mPager.setFocusable(true);
                    mPager.setFocusableInTouchMode(true);
                    mPager.requestFocus();
                    isFirstOpen = false;
                    mLoadlayout.setOnStopLoading(mActivity, mScrollView);
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFinish()
            {
                mScrollView.onRefreshComplete();
                super.onFinish();
            }

            @Override
            public void onStart()
            {
                super.onStart();
            }

        });
    }

    /**
     * 新标预告
     * 
     * @param data
     */
    private void initHomeNew(JSONObject data)
    {
        JSONObject previewJo = data.optJSONObject("data")
                .optJSONObject("preview");
        if (previewJo != null)
        {
            if (StringUtils.isEmpty(previewJo.optString("loan"))
                    || StringUtils.isEmpty(previewJo.optString("apr"))
                    || StringUtils.isEmpty(previewJo.optString("period")))
            {
                mHomeNewLayout.setVisibility(View.GONE);
            }
            else
            {
                mHomeNewLayout.setVisibility(View.VISIBLE);
                mHomeNewLayout.setTag(previewJo.optString("url"));
                mTvNewPercent.setText(previewJo.optString("apr"));
                //大于百万用万元显示
                if(Constants.MORE_THAN_DISPLAY_MONEY<=Double.valueOf(previewJo.optString("loan"))){
                    double money=Double.valueOf(previewJo.optString("loan"))/10000;
                    mTvNewMoney.setText(StringUtils.getDoubleFormat(money));
                    mTvNewMoneyUnit.setText(R.string.common_ten_thousand_yuan);
                }else{
                    mTvNewMoney.setText(previewJo.optString("loan"));
                    mTvNewMoneyUnit.setText(R.string.common_display_yuan_default);
                }
                mTvNewMonth.setText(previewJo.optString("period"));
              //当repay_type_id='5'时，还款方式为按天计息方式
                if("5".equals(previewJo.optString("repay_type_id"))){
                    mTvNewTimeUnit.setText(R.string.common_day);
                }else{
                    mTvNewTimeUnit.setText(R.string.common_month);
                }
                mTvNewTime.setText(DateUtils
                        .getDateFormat(previewJo.optString("begin_time")));
                mTvNewWay.setText(previewJo.optString("repay_type_name"));
            }
        }
    }

    public void onActivityCreated(Bundle savedInstanceState)
    {
        setUserVisibleHint(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.login:
            mActivity.startActivityForResult(
                    (new Intent(getActivity(), BeforeLoginActivity.class)), 0);
            break;
        case R.id.homefragment_immediately_earning:
            Intent intent = new Intent();
            intent.setClass(getActivity(), InvestmentDetailsActivity.class);
            intent.putExtra("loan_id", loan_id);
            intent.putExtra("progress", progress);
            intent.putExtra("loan_url", url);
            intent.putExtra("status", status);
          intent.putExtra("status_name", status_name);
          intent.putExtra("share_title", share_title);
          intent.putExtra("share_content", share_content);
            mActivity.startActivity(intent);
            break;
        case R.id.rl_home_more:
            mActivity.startActivity(
                    new Intent(mActivity, SettingActivity.class));
            break;
        case R.id.ll_home_new_layout:
            String url = String.valueOf(mHomeNewLayout.getTag());
            if(!StringUtils.isEmpty(url)){
                Intent i = new Intent(mActivity,AdvertisingActivity.class);
                i.putExtra("action", "newForecast");
                i.putExtra("url", url);
                mActivity.startActivity(i);
            }
            break;
        default:
            break;
        }
    }

    public void isLoGinGoneOrVisibleLayout(String user_id)
    {
        if (!StringUtils.isEmpty(user_id))
        {
            // getUserName();
        }
        if (mMyinformationLayout != null)
        {
            mMyinformationLayout.setVisibility(
                    StringUtils.isEmpty(user_id) ? View.GONE : View.VISIBLE);
        }

        if (mLoginLayout != null)
        {
            mLoginLayout.setVisibility(
                    StringUtils.isEmpty(user_id) ? View.VISIBLE : View.GONE);
        }

    }

    private void initViewPager(ArrayList<String> imgs)
    {
        if(imgs==null || imgs.size()<=0)return;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30,
                30);
        params.setMargins(3, 3, 3, 3);
        for (int i = 0; i < imgs.size(); i++)
        {
            RadioButton item = new RadioButton(getActivity());
            item.setButtonDrawable(R.drawable.btn_radio_holo_light);
            item.setId(i);
            item.setPadding(3, 3, 3, 3);
            mPoints.add(i);
            mGroup.addView(item, params);
        }
        // 添加view
        for (int i = 0; i < imgs.size(); i++)
        {
            mImgs.add(initViewPageItem(imgs.get(i)));
        }
        mPageHandler = new Handler()
        {
            public void handleMessage(android.os.Message msg)
            {
                if (msg.what == 0)
                {
                    int count = mImgs.size();
                    int currentItem = mPager.getCurrentItem();
                    if (mDircretion == 0)
                    {
                        if (currentItem == count - 1)
                        {
                            mDircretion = -1;
                            currentItem--;
                        }
                        else
                        {
                            currentItem++;
                        }
                    }
                    else
                    {
                        if (currentItem == 0)
                        {
                            mDircretion = 0;
                            currentItem++;
                        }
                        else
                        {
                            currentItem--;
                        }
                    }
                    mPager.setCurrentItem(currentItem);
                    if (isBannerStop)
                    {
                        bannerStartPlay();
                        isBannerStop = false;
                    }
                }
            }
        };
        if (bannerTimerTask == null)
        {
            mPager.setAdapter(mPagerAdapter);
            mPager.setCurrentItem(mImgs.size() * 10000);
            if (mImgs.size() == 1)
            {
                mImgs.get(mPoints.get(0)).findViewById(R.id.viewpage_item_img)
                        .setOnClickListener(new OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {

                                if(!StringUtils.isEmpty(mUrls.get(0))){
                                    Intent intent = new Intent(getActivity(),
                                            AdvertisingActivity.class);
                                    intent.putExtra("url", mUrls.get(0));
                                    startActivity(intent);
                                }
                            }
                        });
            }
            mGroup.check(mPoints.get(0));
            bannerStartPlay();
        }

        // 监听viewpager
        mPager.setOnPageChangeListener(new OnPageChangeListener()
        {
            @Override
            public void onPageSelected(final int arg0)
            {
                if (mImgs != null && mImgs.size() > 0)
                {
                    final int index = arg0 % mImgs.size();
                    mGroup.check(mPoints.get(index));
                    // viewpage item 点击事件
                    mImgs.get(mPoints.get(index))
                            .findViewById(R.id.viewpage_item_img)
                            .setOnClickListener(new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if(!StringUtils.isEmpty(mUrls.get(index))){
                                Intent intent = new Intent(getActivity(),
                                        AdvertisingActivity.class);
                                intent.putExtra("url",
                                        mUrls.get(index));
                                mActivity.startActivity(intent);
                            }
                        }
                    });
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2)
            {
                if (isInitView)
                {
                    isInitView = false;
                    isBannerTouch = false;
                }
                else
                {
                    isBannerTouch = true;
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0)
            {
                isBannerTouch = false;
            }
        });

        mPagerAdapter.notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
    public View initViewPageItem(String imgUrl)
    {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.homepage_fragment_viewpage_item, null);
        ImageView imageView = (ImageView) view
                .findViewById(R.id.viewpage_item_img);
        imageLoader.displayImage(imgUrl, imageView, ImageUtil.getImageOptions());

        return view;
    }

    class BannerTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            if (mPager != null)
            {
                try
                {
                    Thread.sleep(4000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                while (isBannerTouch)
                {
                    try
                    {
                        isBannerStop = true;
                        bannerStopPlay();
                        Thread.sleep(4000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                mPageHandler.sendEmptyMessage(0);
            }
        }

    }

    // 启动banner自动轮播
    public void bannerStartPlay()
    {
        if (bannerTimer != null)
        {
            if (bannerTimerTask != null)
                bannerTimerTask.cancel();
            bannerTimerTask = new BannerTimerTask();
            bannerTimer.schedule(bannerTimerTask, 4000, 4000);// 5秒后执行，每隔5秒执行一次
        }
    }

    // 暂停banner自动轮播
    public void bannerStopPlay()
    {
        if (bannerTimerTask != null)
            bannerTimerTask.cancel();
    }
}
