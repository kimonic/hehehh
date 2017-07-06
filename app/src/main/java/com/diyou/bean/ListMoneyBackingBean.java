package com.diyou.bean;

/**
 * 已投项目子页面list对象
 * 
 * @author 曹翔宇
 * 
 */
public class ListMoneyBackingBean
{
    private String titleName; // 标名
    private String amount; // 投资金额
    private String date; // 日期
    private String totleProfit; // 总收益
    private String hasProfit; // 已收益
    private String waitProfit; // 待收本金
    private String id; // 投资记录id
    private String borrowNid; // 借款nid
    private String borrowStatusNid; // 状态标识
    private String statusTypeName; // 状态名称

    public String getTitleName()
    {
        return titleName;
    }

    public void setTitleName(String titleName)
    {
        this.titleName = titleName;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getTotleProfit()
    {
        return totleProfit;
    }

    public void setTotleProfit(String totleProfit)
    {
        this.totleProfit = totleProfit;
    }

    public String getHasProfit()
    {
        return hasProfit;
    }

    public void setHasProfit(String hasProfit)
    {
        this.hasProfit = hasProfit;
    }

    public String getWaitProfit()
    {
        return waitProfit;
    }

    public void setWaitProfit(String waitProfit)
    {
        this.waitProfit = waitProfit;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getBorrowNid()
    {
        return borrowNid;
    }

    public void setBorrowNid(String borrowNid)
    {
        this.borrowNid = borrowNid;
    }

    public String getBorrowStatusNid()
    {
        return borrowStatusNid;
    }

    public void setBorrowStatusNid(String borrowStatusNid)
    {
        this.borrowStatusNid = borrowStatusNid;
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
