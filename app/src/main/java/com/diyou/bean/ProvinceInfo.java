package com.diyou.bean;

import java.io.Serializable;
import java.util.List;

public class ProvinceInfo implements Serializable
{

    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String province;
    private List<CityInfo> citys;
    private String sort_index;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
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

    public String getSort_index()
    {
        return sort_index;
    }

    public void setSort_index(String sort_index)
    {
        this.sort_index = sort_index;
    }

    public List<CityInfo> getCitys()
    {
        return citys;
    }

    public void setCitys(List<CityInfo> citys)
    {
        this.citys = citys;
    }

    public ProvinceInfo(String id, String name, String province,
            List<CityInfo> citys, String sort_index)
    {
        super();
        this.id = id;
        this.name = name;
        this.province = province;
        this.citys = citys;
        this.sort_index = sort_index;
    }

}
