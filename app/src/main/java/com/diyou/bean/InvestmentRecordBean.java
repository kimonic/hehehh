package com.diyou.bean;

public class InvestmentRecordBean
{
    public Long getAddtime()
    {
        return addtime;
    }

    public void setAddtime(Long addtime)
    {
        this.addtime = addtime;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public double getAccount()
    {
        return account;
    }

    public void setAccount(double account)
    {
        this.account = account;
    }

    Long addtime;

    @Override
    public String toString()
    {
        return "InvestmentRecordBean [addtime=" + addtime + ", username="
                + username + ", account=" + account + "]";
    }

    String username;
    double account;
}
