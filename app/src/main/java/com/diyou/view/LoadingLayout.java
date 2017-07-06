package com.diyou.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.diyou.v5yibao.R;

/**
 * Caoxiangyu 2015.7.12 自定义loading View
 * 
 */
public class LoadingLayout extends RelativeLayout
{

    private View mLoadlayout;
    private CircularProgress myfriendfragment_pb;
    private TextView myfriendfragment_loading_tv;
    private TextView myfriendfragment_reloading_tv;

    public LoadingLayout(Context context)
    {
        super(context);
        initView(context);
    }

    public LoadingLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(context);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initView(context);
    }

    @SuppressLint("InflateParams")
    private void initView(Context context)
    {
        LayoutInflater mInflater = LayoutInflater.from(context);
        mLoadlayout = mInflater.inflate(R.layout.loading, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        addView(mLoadlayout, lp);
        myfriendfragment_pb = (CircularProgress) mLoadlayout
                .findViewById(R.id.myfriendfragment_pb);
        myfriendfragment_loading_tv = (TextView) mLoadlayout
                .findViewById(R.id.myfriendfragment_loading_tv);
        myfriendfragment_reloading_tv = (TextView) mLoadlayout
                .findViewById(R.id.myfriendfragment_reloading_tv);
    }

    /**
     * 進入頁面访问网络数据接口的时候执行该方法
     */
    public void setOnStartLoading(View visibleView)
    {
        myfriendfragment_pb.setVisibility(View.VISIBLE);
        myfriendfragment_loading_tv.setVisibility(View.VISIBLE);
        myfriendfragment_reloading_tv.setVisibility(View.GONE);
        setVisibility(View.VISIBLE);
        if (visibleView != null)
            visibleView.setVisibility(View.GONE);
    }

    /**
     * 接口正确返回数据后调用此方法隐藏Loading
     */
    public void setOnStopLoading(Context context, View visibleView)
    {
        if(context == null){
            return;
        }
        myfriendfragment_pb.setVisibility(View.VISIBLE);
        myfriendfragment_loading_tv.setVisibility(View.VISIBLE);
        myfriendfragment_reloading_tv.setVisibility(View.GONE);
        if(isShown()){
            Animation mHiddenAction = AnimationUtils.loadAnimation(context,
                    R.anim.fade_out);
            startAnimation(mHiddenAction);
            setVisibility(View.GONE);
        }
        if (visibleView != null)
        {
            Animation mShowAction = AnimationUtils.loadAnimation(context,
                    R.anim.fade_in);
            visibleView.startAnimation(mShowAction);
            visibleView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 接口访问错误时调用此方法，刷新按钮的监听使用getReloadingTextView()获取到view后自行设置
     */
    public void setOnLoadingError(Context context, View visibleView)
    {
        myfriendfragment_pb.setVisibility(View.GONE);
        myfriendfragment_loading_tv.setVisibility(View.GONE);
        myfriendfragment_reloading_tv.setVisibility(View.VISIBLE);
        Animation mHiddenAction = AnimationUtils.loadAnimation(context,
                R.anim.fade_out);
        Animation mShowAction = AnimationUtils.loadAnimation(context,
                R.anim.fade_in);
        startAnimation(mShowAction);
        setVisibility(View.VISIBLE);
        if (visibleView != null)
        {
            visibleView.startAnimation(mHiddenAction);
            visibleView.setVisibility(View.GONE);
        }
    }

    /**
     * 获取重新加载按钮的view
     */
    public View getReloadingTextView()
    {
        return myfriendfragment_reloading_tv;
    }

}
