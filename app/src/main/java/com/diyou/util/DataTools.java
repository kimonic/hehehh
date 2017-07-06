package com.diyou.util;

import com.diyou.application.MyApplication;
import com.diyou.v5yibao.R;

import android.content.Context;

public class DataTools
{
    /**
     * dip转px
     */
    public static int dip2px(Context context, float dipValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px 转dip
     */
    public static int px2dip(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 判断标种
     */
    public static String JudgeCategory(int type)
    {
        String[] stringArray = MyApplication.getInstance().getResources()
                .getStringArray(R.array.category_id);
        switch (type)
        {
        case 1:
            return stringArray[0];
        case 2:
            return stringArray[1];
        case 3:
            return stringArray[2];
        case 4:
            return stringArray[3];
        case 5:
            return stringArray[4];
        case 6:
            return stringArray[5];
        case 7:
            return stringArray[6];
        default:
            return "";
        }
    }

    /**
     * 判断标状态
     */
    public static String JudgeStatus(int type)
    {
        String[] stringArray = MyApplication.getInstance().getResources()
                .getStringArray(R.array.status);
        switch (type)
        {
        case -1:
            return stringArray[0];
        case -2:
            return stringArray[1];
        case -3:
            return stringArray[2];
        case -4:
            return stringArray[3];
        case -5:
            return stringArray[4];
        case -6:
            return stringArray[5];
        case -7:
            return stringArray[6];
        case -8:
            return stringArray[7];
        case -9:
            return stringArray[8];
        case 1:
            return stringArray[9];
        case 2:
            return stringArray[10];
        case 3:
            return stringArray[11];
        case 4:
            return stringArray[12];
        case 5:
            return stringArray[13];
        case 6:
            return stringArray[14];
        case 7:
            return stringArray[15];
        default:
            return "";
        }
    }

    /**
     * 还款方式类型
     */
    public static String JudgeRepay(int type)
    {
        String[] stringArray = MyApplication.getInstance().getResources()
                .getStringArray(R.array.repay_type);
        switch (type)
        {
        case 1:
            return stringArray[0];
        case 2:
            return stringArray[1];
        case 3:
            return stringArray[2];
        case 4:
            return stringArray[3];
        case 5:
            return stringArray[4];
        case 6:
            return stringArray[5];
        default:
            return "";
        }
    }

}
