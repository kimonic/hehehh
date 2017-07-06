package com.diyou.bean;

import java.io.Serializable;

public class SettlementRecordInfo implements Serializable
{

    private static final long serialVersionUID = 1L;

    private String money;// 结算金额
    private String add_time;// 添加时间,Y-m-d H:i
    private String verify_status;// 状态

    public SettlementRecordInfo(String money, String add_time,
            String verify_status)
    {
        super();
        this.money = money;
        this.add_time = add_time;
        this.verify_status = verify_status;
    }

    public String getMoney()
    {
        return money;
    }

    public void setMoney(String money)
    {
        this.money = money;
    }

    public String getAdd_time()
    {
        return add_time;
    }

    public void setAdd_time(String add_time)
    {
        this.add_time = add_time;
    }

    public String getVerify_status()
    {
        return verify_status;
    }

    public void setVerify_status(String verify_status)
    {
        this.verify_status = verify_status;
    }

    public SettlementRecordInfo()
    {
    }

}
