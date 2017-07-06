package com.diyou.bean;

public class InsideLetterInfo
{
    private String id;
    private String title;
    private String Send_time;
    private int status;//表示是否阅读:1=未阅读    2=已阅读
    private String contents;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSend_time()
    {
        return Send_time;
    }

    public void setSend_time(String send_time)
    {
        Send_time = send_time;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getContents()
    {
        return contents;
    }

    public void setContents(String contents)
    {
        this.contents = contents;
    }

}
