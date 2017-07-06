package com.diyou.bean;

import java.io.Serializable;

public class WithdrawRecordInfo implements Serializable
{

    private static final long serialVersionUID = 1L;

    private String add_time;
    private String status;
    private String status_name;
    private String amount;

    public String getAdd_time()
    {
        return add_time;
    }

    public void setAdd_time(String add_time)
    {
        this.add_time = add_time;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getStatus_name()
    {
        return status_name;
    }

    public void setStatus_name(String status_name)
    {
        this.status_name = status_name;
    }

    public WithdrawRecordInfo(String add_time, String status,
            String status_name, String amount)
    {
        super();
        this.add_time = add_time;
        this.status = status;
        this.status_name = status_name;
        this.amount = amount;
    }

}
