package com.diyou.view;

import com.diyou.v5yibao.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

public class LoadPopupWindow extends PopupWindow
{
    private Activity mActivity;

    @SuppressLint("InflateParams")
    public LoadPopupWindow(Activity activity)
    {
        this.mActivity = activity;
        this.setFocusable(false);
        View inflate = mActivity.getLayoutInflater()
                .inflate(R.layout.loadprogressbar, null, false);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        this.setContentView(inflate);
        this.setFocusable(true);

    }
}