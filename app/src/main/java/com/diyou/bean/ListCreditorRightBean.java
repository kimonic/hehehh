package com.diyou.bean;

/**
 * 已投项目子页面list对象
 * 
 * @author 曹翔宇
 * 
 */
public class ListCreditorRightBean
{
    private String titleName; // 标名
    private String changePeriod; // 转让期数
    private String borrowPeriod; // 总期数
    private String date; // 日期
    private String recover_account_capital_wait; // 待收本金
    private String recover_account_interest_wait; // 待收利息
    private String change_account; // 转让价格
    private String borrowNid; // 债权ID
    private String statusTypeName; // 状态名称

    public String getTender_id()
    {
        return tender_id;
    }

    public void setTender_id(String tender_id)
    {
        this.tender_id = tender_id;
    }

    private String tender_id;

    public String getTitleName()
    {
        return titleName;
    }

    public void setTitleName(String titleName)
    {
        this.titleName = titleName;
    }

    public String getChangePeriod()
    {
        return changePeriod;
    }

    public void setChangePeriod(String changePeriod)
    {
        this.changePeriod = changePeriod;
    }

    public String getBorrowPeriod()
    {
        return borrowPeriod;
    }

    public void setBorrowPeriod(String borrowPeriod)
    {
        this.borrowPeriod = borrowPeriod;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getRecover_account_capital_wait()
    {
        return recover_account_capital_wait;
    }

    public void setRecover_account_capital_wait(
            String recover_account_capital_wait)
    {
        this.recover_account_capital_wait = recover_account_capital_wait;
    }

    public String getRecover_account_interest_wait()
    {
        return recover_account_interest_wait;
    }

    public void setRecover_account_interest_wait(
            String recover_account_interest_wait)
    {
        this.recover_account_interest_wait = recover_account_interest_wait;
    }

    public String getChange_account()
    {
        return change_account;
    }

    public void setChange_account(String change_account)
    {
        this.change_account = change_account;
    }

    public String getBorrowNid()
    {
        return borrowNid;
    }

    public void setBorrowNid(String borrowNid)
    {
        this.borrowNid = borrowNid;
    }

    public String getStatusTypeName()
    {
        return statusTypeName;
    }

    public void setStatusTypeName(String statusTypeName)
    {
        this.statusTypeName = statusTypeName;
    }

}
