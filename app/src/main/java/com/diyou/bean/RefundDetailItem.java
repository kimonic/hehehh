package com.diyou.bean;

import java.io.Serializable;

public class RefundDetailItem implements Serializable
{

    private static final long serialVersionUID = 1L;
    
    private int period_no;//当前期数
    private int period;//总期数
    //    private String principal;//应收本金
    private String amount;//应收总额
    private String principal_yes;//实收本金
    private String interest_yes;//实收利息
    private String recover_time;//预还款日
    private String status_name;//还款状态:已还，待收
    private int status;//状态 1已还 -1待收
    
    public RefundDetailItem(int period_no, int period, String amount,
            String principal_yes, String interest_yes, String recover_time,
            String status_name, int status)
    {
        super();
        this.period_no = period_no;
        this.period = period;
        this.amount = amount;
        this.principal_yes = principal_yes;
        this.interest_yes = interest_yes;
        this.recover_time = recover_time;
        this.status_name = status_name;
        this.status = status;
    }

    public int getPeriod_no()
    {
        return period_no;
    }

    public void setPeriod_no(int period_no)
    {
        this.period_no = period_no;
    }

    public int getPeriod()
    {
        return period;
    }

    public void setPeriod(int period)
    {
        this.period = period;
    }

//    public String getPrincipal()
//    {
//        return principal;
//    }

//    public void setPrincipal(String principal)
//    {
//        this.principal = principal;
//    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
    public String getPrincipal_yes()
    {
        return principal_yes;
    }

    public void setPrincipal_yes(String principal_yes)
    {
        this.principal_yes = principal_yes;
    }

    public String getInterest_yes()
    {
        return interest_yes;
    }

    public void setInterest_yes(String interest_yes)
    {
        this.interest_yes = interest_yes;
    }

    public String getRecover_time()
    {
        return recover_time;
    }

    public void setRecover_time(String recover_time)
    {
        this.recover_time = recover_time;
    }

    public String getStatus_name()
    {
        return status_name;
    }

    public void setStatus_name(String status_name)
    {
        this.status_name = status_name;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

}
