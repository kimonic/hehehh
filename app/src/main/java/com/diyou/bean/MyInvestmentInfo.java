package com.diyou.bean;

import java.io.Serializable;

public class MyInvestmentInfo implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String loan_name;
    private String add_time;
    private String amount;
    private String award_interest;
    private String status_name;
    private String url;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getLoan_name()
    {
        return loan_name;
    }

    public void setLoan_name(String loan_name)
    {
        this.loan_name = loan_name;
    }

    public String getAdd_time()
    {
        return add_time;
    }

    public void setAdd_time(String add_time)
    {
        this.add_time = add_time;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getAward_interest()
    {
        return award_interest;
    }

    public void setAward_interest(String award_interest)
    {
        this.award_interest = award_interest;
    }

    public String getStatus_name()
    {
        return status_name;
    }

    public void setStatus_name(String status_name)
    {
        this.status_name = status_name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public MyInvestmentInfo(String id,String loan_name, String add_time, String amount,
            String award_interest, String status_name, String url)
    {
        super();
        this.id=id;
        this.loan_name = loan_name;
        this.add_time = add_time;
        this.amount = amount;
        this.award_interest = award_interest;
        this.status_name = status_name;
        this.url = url;
    }

}
