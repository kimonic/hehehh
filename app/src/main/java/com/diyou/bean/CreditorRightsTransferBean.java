package com.diyou.bean;

import java.io.Serializable;

public class CreditorRightsTransferBean implements Serializable
{
    private static final long serialVersionUID = 1L;


    public String getChange_period()
    {
        return change_period;
    }

    public void setChange_period(String change_period)
    {
        this.change_period = change_period;
    }

    public double getRecover_account_capital_wait()
    {
        return recover_account_capital_wait;
    }

    public void setRecover_account_capital_wait(
            double recover_account_capital_wait)
    {
        this.recover_account_capital_wait = recover_account_capital_wait;
    }

    public double getRecover_account_interest_wait()
    {
        return recover_account_interest_wait;
    }

    public void setRecover_account_interest_wait(
            double recover_account_interest_wait)
    {
        this.recover_account_interest_wait = recover_account_interest_wait;
    }

    public double getBorrow_apr()
    {
        return borrow_apr;
    }

    public void setBorrow_apr(double borrow_apr)
    {
        this.borrow_apr = borrow_apr;
    }

    public int getBorrow_period()
    {
        return borrow_period;
    }

    public void setBorrow_period(int borrow_period)
    {
        this.borrow_period = borrow_period;
    }

    public String getBorrow_type()
    {
        return borrow_type;
    }

    public void setBorrow_type(String borrow_type)
    {
        this.borrow_type = borrow_type;
    }

    public double getChange_account()
    {
        return change_account;
    }

    public void setChange_account(double change_account)
    {
        this.change_account = change_account;
    }

    public String getChange_username()
    {
        return change_username;
    }

    public void setChange_username(String change_username)
    {
        this.change_username = change_username;
    }

    public String getBorrow_username()
    {
        return borrow_username;
    }

    public void setBorrow_username(String borrow_username)
    {
        this.borrow_username = borrow_username;
    }

    public String getChange_status()
    {
        return change_status;
    }

    public void setChange_status(String change_status)
    {
        this.change_status = change_status;
    }

    public String getLoan_name() {
        return loan_name;
    }

    public void setLoan_name(String loan_name) {
        this.loan_name = loan_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    private String loan_name;//标名
    private String category_id;// 借款标种类 1 信用标 2净值标 3天标 4担保标 5抵押标 6秒标 7流转标
    private String change_period;// 转让期数
    private double recover_account_capital_wait;// 待收本金
    private double recover_account_interest_wait;// 待收利息
    private double borrow_apr;// 借款年利率
    private int borrow_period;// 总期数
    private String borrow_type;//标种 信用标：credit 流转标：roam 担保标：vouch 天标：day 秒标：second 抵押标：pawn 净值标：worth
    private double change_account;// 转让价格
    private String change_username;// 债权人
    private String borrow_username;// 债务人
    private String change_status;// 对应页面显示，转让成功或者显示“购买”按钮
    private long repay_last_time;
    private int status_name;
    private String borrow_nid;
    private String amount_money;
    private int period;
    private int total_period;
    private String transfer_id;
    private String pic;
    private String vouch_company_name;//担保方
    private String share_url;//分享链接
    private String share_title;//分享标题
    private String share_content;//分享内容

    public String getTransfer_id()
    {
        return transfer_id;
    }

    public void setTransfer_id(String transfer_id)
    {
        this.transfer_id = transfer_id;
    }

    private String amount;

    public int getTotal_period()
    {
        return total_period;
    }

    public void setTotal_period(int total_period)
    {
        this.total_period = total_period;
    }

    public int getPeriod()
    {
        return period;
    }

    public void setPeriod(int period)
    {
        this.period = period;
    }

    public String getAmount_money()
    {
        return amount_money;
    }

    public void setAmount_money(String amount_money)
    {
        this.amount_money = amount_money;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    private String transfer_info_url;

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTransfer_info_url()
    {
        return transfer_info_url;
    }

    public void setTransfer_info_url(String transfer_info_url)
    {
        this.transfer_info_url = transfer_info_url;
    }

    public String getTender_id()
    {
        return tender_id;
    }

    public void setTender_id(String tender_id)
    {
        this.tender_id = tender_id;
    }

    private String tender_id;

    public String getBorrow_nid()
    {
        return borrow_nid;
    }

    public void setBorrow_nid(String borrow_nid)
    {
        this.borrow_nid = borrow_nid;
    }

    public long getRepay_last_time()
    {
        return repay_last_time;
    }

    public void setRepay_last_time(long repay_last_time)
    {
        this.repay_last_time = repay_last_time;
    }

    public int getStatus_name()
    {
        return status_name;
    }

    public void setStatus_name(int status_name)
    {
        this.status_name = status_name;
    }

    public String getVouch_company_name()
    {
        return vouch_company_name;
    }

    public void setVouch_company_name(String vouch_company_name)
    {
        this.vouch_company_name = vouch_company_name;
    }

    public String getShare_url()
    {
        return share_url;
    }

    public void setShare_url(String share_url)
    {
        this.share_url = share_url;
    }

    public String getShare_title()
    {
        return share_title;
    }

    public void setShare_title(String share_title)
    {
        this.share_title = share_title;
    }

    public String getShare_content()
    {
        return share_content;
    }

    public void setShare_content(String share_content)
    {
        this.share_content = share_content;
    }

}
