package com.diyou.bean;

public class MyProjectInfo
{
    private String id;
    private String transfer_amount;// 转让价格
    private String wait_period;// 待还期数
    private String period;// 总期数
    private String transfer_status_name;// 转让状态
    private String transfer_status;//-1=撤销/可转让，1=转让中，2=已转让,3=撤销次数已用完
    private String buy_repay_status;// 购买记录状态
    private String transfer_money;// 债权价值
    private String transfer_id;
    private String wait_principal_interest;//待收本息
    private int transfer_cancel_count;//撤销次数

    public String getTransfer_status()
    {
        return transfer_status;
    }

    public void setTransfer_status(String transfer_status)
    {
        this.transfer_status = transfer_status;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTransfer_amount()
    {
        return transfer_amount;
    }

    public void setTransfer_amount(String transfer_amount)
    {
        this.transfer_amount = transfer_amount;
    }

    public String getWait_principal_interest() {
        return wait_principal_interest;
    }

    public void setWait_principal_interest(String wait_principal_interest) {
        this.wait_principal_interest = wait_principal_interest;
    }

    public String getWait_period()
    {
        return wait_period;
    }

    public void setWait_period(String wait_period)
    {
        this.wait_period = wait_period;
    }

    public String getPeriod()
    {
        return period;
    }

    public void setPeriod(String period)
    {
        this.period = period;
    }

    public String getTransfer_status_name()
    {
        return transfer_status_name;
    }

    public void setTransfer_status_name(String transfer_status_name)
    {
        this.transfer_status_name = transfer_status_name;
    }

    public String getBuy_repay_status()
    {
        return buy_repay_status;
    }

    public void setBuy_repay_status(String buy_repay_status)
    {
        this.buy_repay_status = buy_repay_status;
    }

    public String getTransfer_money()
    {
        return transfer_money;
    }

    public void setTransfer_money(String transfer_money)
    {
        this.transfer_money = transfer_money;
    }

    public String getTransfer_id()
    {
        return transfer_id;
    }

    public void setTransfer_id(String transfer_id)
    {
        this.transfer_id = transfer_id;
    }

    public int getTransfer_cancel_count()
    {
        return transfer_cancel_count;
    }

    public void setTransfer_cancel_count(int transfer_cancel_count)
    {
        this.transfer_cancel_count = transfer_cancel_count;
    }


}
