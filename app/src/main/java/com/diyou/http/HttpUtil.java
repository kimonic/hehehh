package com.diyou.http;

import java.util.TreeMap;

import com.diyou.util.StringUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtil
{
    private static AsyncHttpClient client; // 实例话对象

    private static synchronized AsyncHttpClient getinstance()
    {
        if (client == null)
        {
            client = new AsyncHttpClient();
            client.setTimeout(15000);
        }
        return client;
    }

    public static void get(String urlString, AsyncHttpResponseHandler res) // 用一个完整url获取一个string对象
    {
        getinstance().get(urlString, res);
    }

    public static void post(String urlString, AsyncHttpResponseHandler res)
    // 用一个完整url获取一个string对象
    {
        getinstance().post(urlString, res);
    }

    public static void get(String urlString, RequestParams params,
            AsyncHttpResponseHandler res) // url里面带参数
    {
        getinstance().get(urlString, params, res);
    }

    public static void post(String urlString, RequestParams params,
            AsyncHttpResponseHandler res) // url里面带参数
    {
        getinstance().post(urlString, params, res);
    }

    public static void get(String urlString, JsonHttpResponseHandler res) // 不带参数，获取json对象或者数组
    {
        getinstance().get(urlString, res);
    }

    public static void post(String urlString, JsonHttpResponseHandler res) //
    // 不带参数，获取json对象或者数组
    {
        getinstance().post(urlString, res);
    }

    public static void get(String urlString, RequestParams params,
            JsonHttpResponseHandler res) // 带参数，获取json对象或者数组
    {
        getinstance().get(urlString, params, res);
    }

    public static void post(String urlString, RequestParams params,
            JsonHttpResponseHandler res) // 带参数，获取json对象或者数组
    {
        getinstance().post(urlString, params, res);
    }

    public static void post(String urlString, TreeMap<String, String> map,
            JsonHttpResponseHandler res)
    {
        post(urlString, StringUtils.getRequestParams(map), res);
    }

    public static void get(String uString, BinaryHttpResponseHandler bHandler) // 下载数据使用，会返回byte数据
    {
        getinstance().get(uString, bHandler);
    }

    public static void post(String uString, BinaryHttpResponseHandler bHandler) // 下载数据使用，会返回byte数据
    {
        getinstance().get(uString, bHandler);
    }

    public static AsyncHttpClient getClient()
    {
        return getinstance();
    }
}