package com.diyou.bean;

/**
 * 接收到的推送消息对象
 * 
 * 
 */
public class MyPushMessage
{
    private String title; // 标题
    private String body; // 内容
    private String time; // 时间

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

}
