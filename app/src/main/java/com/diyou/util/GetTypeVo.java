package com.diyou.util;

public class GetTypeVo
{

    private Double account;// 借款金额
    private Long period; // 期限
    private Double apr; // lilv利率
    private String style; // 还款方式
    private String type;// 还款方式
    private Long time; // 当前时间

    public Double getAccount()
    {
        return account;
    }

    public void setAccount(Double account)
    {
        this.account = account;
    }

    public Long getPeriod()
    {
        return period;
    }

    public void setPeriod(Long period)
    {
        this.period = period;
    }

    public Double getApr()
    {
        return apr;
    }

    public void setApr(Double apr)
    {
        this.apr = apr;
    }

    public String getStyle()
    {
        return style;
    }

    public void setStyle(String style)
    {
        this.style = style;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Long getTime()
    {
        return time;
    }

    public void setTime(Long time)
    {
        this.time = time;
    }

}
