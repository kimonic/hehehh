package com.diyou.bean;

public class InvestmentListBean
// 投资列表对象
{

    public double getAccount()
    {
        return account;
    }

    public void setAccount(double account)
    {
        this.account = account;
    }

    public Double getBorrow_account_scale()
    {
        return borrow_account_scale;
    }

    public void setBorrow_account_scale(Double borrow_account_scale)
    {
        this.borrow_account_scale = borrow_account_scale;
    }

    public String getBorrow_apr()
    {
        return borrow_apr;
    }

    public void setBorrow_apr(String borrow_apr)
    {
        this.borrow_apr = borrow_apr;
    }

    public String getAward_status()
    {
        return award_status;
    }

    public void setAward_status(String award_status)
    {
        this.award_status = award_status;
    }

    public int getAward_account()
    {
        return award_account;
    }

    public void setAward_account(int award_account)
    {
        this.award_account = award_account;
    }

    public double getAward_scale()
    {
        return award_scale;
    }

    public void setAward_scale(double award_scale)
    {
        this.award_scale = award_scale;
    }

    public String getBorrow_period_name()
    {
        return borrow_period_name;
    }

    public void setBorrow_period_name(String borrow_period_name)
    {
        this.borrow_period_name = borrow_period_name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getBorrow_nid()
    {
        return borrow_nid;
    }

    public void setBorrow_nid(String borrow_nid)
    {
        this.borrow_nid = borrow_nid;
    }

    public String getBorrow_type()
    {
        return borrow_type;
    }

    public void setBorrow_type(String borrow_type)
    {
        this.borrow_type = borrow_type;
    }

    public String getStatus_type()
    {
        return status_type;
    }

    public void setStatus_type(String status_type)
    {
        this.status_type = status_type;
    }

    public String getSetstatus_type_name()
    {
        return setstatus_type_name;
    }

    public void setSetstatus_type_name(String setstatus_type_name)
    {
        this.setstatus_type_name = setstatus_type_name;
    }

    private String setstatus_type_name;

    public String getEnd_time()
    {
        return end_time;
    }

    public void setEnd_time(String end_time)
    {
        this.end_time = end_time;
    }

    public String getCredit_rank()
    {
        return credit_rank;
    }

    public void setCredit_rank(String credit_rank)
    {
        this.credit_rank = credit_rank;
    }

    public String getLoan_kind()
    {
        return loan_kind;
    }

    public void setLoan_kind(String loan_kind)
    {
        this.loan_kind = loan_kind;
    }

    private String loan_kind;
    private String credit_rank;
    private double award_scale;
    private String borrow_period_name;
    private String name;
    private String borrow_nid;
    private String borrow_type;
    private String status_type;
    private String end_time;
    private double account;
    private Double borrow_account_scale;
    private String borrow_apr;
    private String award_status;
    private int award_account;

}
