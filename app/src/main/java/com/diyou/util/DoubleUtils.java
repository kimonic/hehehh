package com.diyou.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Double 工具类封装
 * 
 */
public class DoubleUtils
{

    private static DecimalFormat FORMAT = new DecimalFormat("0.00");

    public static void main(String[] agr)
    {
        System.out.println(decimalNum(2.55666666, 3));
    }

    /**
     * 处理小数点位数
     * 
     * @param d
     * @param num
     *            小数点后num位
     * @return
     */
    public static Double decimalNum(Double d, int num)
    {
        BigDecimal bg = new BigDecimal(d);
        double f1 = bg.setScale(num, BigDecimal.ROUND_HALF_UP).doubleValue();

        return f1;
    }

    /**
     * 处理小数点位数
     * 
     * 默认为小数点后两位
     * 
     * @param d
     * @return
     */
    public static Double decimalNum(Double d)
    {
        if (d == null)
        {
            d = 0d;
        }
        BigDecimal bg = new BigDecimal(d);
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        return f1;
    }

    /**
     * 将double类型的数据格式化成字符串表示,保留两位小数
     * 
     * @param d
     * @return
     */
    public static String format(Double d)
    {
        if (d == null)
            return null;
        return FORMAT.format(d);
    }

    /**
     * 将object类型对象转换为double类型
     * 
     * @param obj
     *            要转换成double类型的对象
     * @param decimal
     *            保留的小数位数
     * @return
     */
    public static double toDouble(Object obj, int decimal)
    {
        if (decimal < 0)
        {
            throw new IllegalArgumentException(
                    "illegal decimal value [" + decimal + "]");
        }
        String s = "0";
        if (obj != null)
        {
            s = obj.toString().trim();
        }
        if (s.length() == 0)
        {
            s = "0";
        }
        BigDecimal bg = new BigDecimal(s);
        return bg.setScale(decimal, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 将object类型对象转换为double类型,保留两位小数
     * 
     * @param obj
     *            要转换成double类型的对象
     * @return
     */
    public static double toDouble(Object obj)
    {
        return toDouble(obj, 2);
    }

    /**
     * 精确的乘法运算
     * 
     * @param d1
     * @param d2
     * @return
     */
    public static double mul(Double d1, Double d2)
    {
        BigDecimal bgd1 = new BigDecimal(Double.toString(d1 == null ? 0 : d1));
        BigDecimal bgd2 = new BigDecimal(Double.toString(d2 == null ? 0 : d2));
        return bgd1.multiply(bgd2).doubleValue();
    }

    /**
     * 精确的减法运算
     * 
     * @param d1
     * @param d2
     * @return
     */
    public static double sub(Double d1, Double d2)
    {
        BigDecimal bgd1 = new BigDecimal(Double.toString(d1 == null ? 0 : d1));
        BigDecimal bgd2 = new BigDecimal(Double.toString(d2 == null ? 0 : d2));
        return bgd1.subtract(bgd2).doubleValue();
    }

    /**
     * 精确的加法运算
     * 
     * @param d1
     * @param d2
     * @return
     */
    public static double add(Double d1, Double d2)
    {
        BigDecimal bgd1 = new BigDecimal(Double.toString(d1 == null ? 0 : d1));
        BigDecimal bgd2 = new BigDecimal(Double.toString(d2 == null ? 0 : d2));
        return bgd1.add(bgd2).doubleValue();
    }

    /**
     * 精确的除法运算
     * 
     * @param d1
     * @param d2
     * @return
     */
    public static double div(Double d1, Double d2)
    {
        BigDecimal bgd1 = new BigDecimal(Double.toString(d1 == null ? 0 : d1));
        BigDecimal bgd2 = new BigDecimal(Double.toString(d2 == null ? 1 : d2));
        return bgd1.divide(bgd2, 6, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }
}
