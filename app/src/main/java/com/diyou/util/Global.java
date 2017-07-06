package com.diyou.util;

import android.content.Context;

/**
 * 全局静态参数集合
 * 
 * @data 2014-7-4
 */
public class Global
{
    public static Context mContext;

    public synchronized static void setApplicationContext(Context context)
    {
        mContext = context;
    }

    public static Context getApplicationContext()
    {
        return mContext;
    }
}
