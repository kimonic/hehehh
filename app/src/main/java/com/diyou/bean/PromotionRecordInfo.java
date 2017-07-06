package com.diyou.bean;

import java.io.Serializable;

public class PromotionRecordInfo implements Serializable
{

    private static final long serialVersionUID = 1L;

    private String spreaded_member_name;// 被推广人用户名
    private String member_name;// 推广人用户名
    private String add_time;// 添加时间,Y-m-d H:i
    private String type;
    private String amount_type;// 资金类型
    private String amount_all;// 提成金额（舍弃，现在使用推广总额）
    private String award;// 推广总额
    private String proportion;// 提成比例，例如0.02%
    private String spread_type;// 提成类型

    public PromotionRecordInfo()
    {
    }

    public PromotionRecordInfo(String spreaded_member_name, String member_name,
            String add_time, String type, String amount_type, String award,
            String proportion,String spread_type)
    {
        super();
        this.spreaded_member_name = spreaded_member_name;
        this.member_name = member_name;
        this.add_time = add_time;
        this.type = type;
        this.amount_type = amount_type;
        this.award = award;
        this.proportion = proportion;
        this.spread_type = spread_type;
    }

    public String getSpreaded_member_name()
    {
        return spreaded_member_name;
    }

    public void setSpreaded_member_name(String spreaded_member_name)
    {
        this.spreaded_member_name = spreaded_member_name;
    }

    public String getMember_name()
    {
        return member_name;
    }

    public void setMember_name(String member_name)
    {
        this.member_name = member_name;
    }

    public String getAdd_time()
    {
        return add_time;
    }

    public void setAdd_time(String add_time)
    {
        this.add_time = add_time;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getAmount_type()
    {
        return amount_type;
    }

    public void setAmount_type(String amount_type)
    {
        this.amount_type = amount_type;
    }

    public String getAmount_all()
    {
        return amount_all;
    }

    public void setAmount_all(String amount_all)
    {
        this.amount_all = amount_all;
    }

    public String getAward()
    {
        return award;
    }

    public void setAward(String award)
    {
        this.award = award;
    }

    public String getProportion()
    {
        return proportion;
    }

    public void setProportion(String proportion)
    {
        this.proportion = proportion;
    }

    public String getSpread_type()
    {
        return spread_type;
    }

    public void setSpread_type(String spread_type)
    {
        this.spread_type = spread_type;
    }

}
