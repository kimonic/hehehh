package com.diyou.bean;

import java.io.Serializable;

public class CityInfo implements Serializable
{

    private static final long serialVersionUID = 1L;
    private String id;
    private String pid;
    private String name;
    private String province;
    private String city;
    private String sort_index;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getPid()
    {
        return pid;
    }

    public void setPid(String pid)
    {
        this.pid = pid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getProvince()
    {
        return province;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getSort_index()
    {
        return sort_index;
    }

    public void setSort_index(String sort_index)
    {
        this.sort_index = sort_index;
    }

    public CityInfo(String id, String pid, String name, String province,
            String city, String sort_index)
    {
        super();
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.province = province;
        this.city = city;
        this.sort_index = sort_index;
    }
}
