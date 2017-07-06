package com.diyou.util;

import com.diyou.v5yibao.R;
import com.diyou.view.HandyTextView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class ToastUtil
{
    private static Toast toast;

    /**
     * 默认通用tost提示
     * 
     * @author LiuYun
     * @data 2014-6-5 下午5:30:11
     * @param msg
     */
    public static void show(String msg)
    {
        if (Global.getApplicationContext() == null)
        {
            return;
        }
        if (toast == null)
        {
            toast = Toast.makeText(Global.getApplicationContext(), msg,
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else
        {
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setText(msg);
            toast.show();
        }
    }

    /**
     * 默认通用tost提示
     * 
     * @author LiuYun
     * @data 2014-6-5 下午5:30:11
     * @param msg
     */
    public static void show(int resId)
    {
        if (Global.getApplicationContext() == null)
        {
            return;
        }
        if (toast == null)
        {
            toast = Toast.makeText(Global.getApplicationContext(),
                    getToastString(resId), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else
        {
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setText(getToastString(resId));
            toast.show();
        }
    }

    /** 短暂显示Toast提示(来自res) **/
    public static void showShortToast(int resId)
    {
        if (toast == null)
        {
            toast = Toast.makeText(Global.getApplicationContext(),
                    getToastString(resId), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else
        {
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setText(getToastString(resId));
            toast.show();
        }
    }

    /** 短暂显示Toast提示(来自String) **/
    public static void showShortToast(String text)
    {
        if (toast == null)
        {
            toast = Toast.makeText(Global.getApplicationContext(), text,
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else
        {
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setText(text);
            toast.show();
        }
    }

    /** 短暂显示Toast提示(来自String) **/
    public static void showBottomShortToast(String text)
    {
        if (toast == null)
        {
            toast = Toast.makeText(Global.getApplicationContext(), text,
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else
        {
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setText(text);
            toast.show();
        }
    }

    /** 长时间显示Toast提示(来自res) **/
    public static void showLongToast(int resId)
    {
        if (toast == null)
        {
            toast = Toast.makeText(Global.getApplicationContext(),
                    getToastString(resId), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else
        {
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setText(getToastString(resId));
            toast.show();
        }
    }

    /** 长时间显示Toast提示(来自String) **/
    public static void showLongToast(String text)
    {
        if (toast == null)
        {
            toast = Toast.makeText(Global.getApplicationContext(), text,
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else
        {
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setText(text);
            toast.show();
        }
    }

    private static CharSequence getToastString(int resId)
    {
        return Global.getApplicationContext().getResources().getString(resId);
    }

    /** 显示自定义Toast提示(来自res) **/
    public static void showCustomToast(int resId)
    {
        View toastRoot = LayoutInflater.from(Global.getApplicationContext())
                .inflate(R.layout.common_toast, null);
        ((HandyTextView) toastRoot.findViewById(R.id.toast_text))
                .setText(getToastString(resId));
        Toast toast = new Toast(Global.getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastRoot);
        toast.show();
    }

    /** 显示自定义Toast提示(来自String) **/
    public static void showCustomToast(String text)
    {
        View toastRoot = LayoutInflater.from(Global.getApplicationContext())
                .inflate(R.layout.common_toast, null);
        ((HandyTextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
        Toast toast = new Toast(Global.getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastRoot);
        toast.show();
    }

    /**
     * 通用tost提示(离顶部偏移项x，y)
     * 
     * @author LiuYun
     * @data 2014-6-5 下午5:30:47
     * @param context
     * @param msg
     * @param X
     * @param Y
     */
    public static void show(String msg, int X, int Y)
    {
        if (Global.getApplicationContext() == null)
        {
            return;
        }
        // 如：X= 0; Y=400;
        if (toast == null)
        {
            toast = Toast.makeText(Global.getApplicationContext(), msg,
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, X, Y);
            toast.show();
        }
        else
        {
            toast.setGravity(Gravity.TOP | Gravity.LEFT, X, Y);
            toast.setText(msg);
            toast.show();
        }
    }

    /**
     * 通用tost提示(对应的setMargin（x，y）)
     * 
     * @author LiuYun
     * @data 2014-6-5 下午5:31:29
     * @param context
     * @param x
     * @param y
     * @param msg
     */
    public static void show(float x, float y, String msg)
    {
        if (Global.getApplicationContext() == null)
        {
            return;
        }
        if (toast == null)
        {
            toast = Toast.makeText(Global.getApplicationContext(), msg,
                    Toast.LENGTH_SHORT);
            toast.setMargin(x, y);
            toast.show();
        }
        else
        {
            toast.setMargin(x, y);
            toast.setText(msg);
            toast.show();
        }
    }
}
