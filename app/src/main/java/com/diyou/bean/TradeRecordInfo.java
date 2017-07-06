package com.diyou.bean;

public class TradeRecordInfo
{
    private String id;// 标的id
    private String fee_id;// 区分类别303表示投资标
    private String fee_name;// 费用或操作名称
    private String freeze;// 本次冻结金额
    private String add_time;// 添加时间
    private String balance;// 本次可用总额
    private int new_balance;
    private String url;
    private String progress;
    private String loan_name;// 项目名称
    private String remark;//关联项目名称

    public String getLoan_name()
    {
        return loan_name;
    }

    public void setLoan_name(String loan_name)
    {
        this.loan_name = loan_name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProgress()
    {
        return progress;
    }

    public void setProgress(String progress)
    {
        this.progress = progress;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public int getNew_balance()
    {
        return new_balance;
    }

    public void setNew_balance(int new_balance)
    {
        this.new_balance = new_balance;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getFee_id()
    {
        return fee_id;
    }

    public void setFee_id(String fee_id)
    {
        this.fee_id = fee_id;
    }

    public String getFee_name()
    {
        return fee_name;
    }

    public void setFee_name(String fee_name)
    {
        this.fee_name = fee_name;
    }

    public String getFreeze()
    {
        return freeze;
    }

    public void setFreeze(String freeze)
    {
        this.freeze = freeze;
    }

    public String getAdd_time()
    {
        return add_time;
    }

    public void setAdd_time(String add_time)
    {
        this.add_time = add_time;
    }

    public String getBalance()
    {
        return balance;
    }

    public void setBalance(String balance)
    {
        this.balance = balance;
    }

}
