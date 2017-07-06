package com.diyou.bean;

public class EventObject
{

    private String name;
    private String action;

    public EventObject()
    {
    }

    public EventObject(String action, String name)
    {
        super();
        this.action = action;
        this.name = name;
    }

    public EventObject(String action)
    {
        super();
        this.action = action;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
