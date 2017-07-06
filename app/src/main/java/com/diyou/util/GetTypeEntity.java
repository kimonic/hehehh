package com.diyou.util;

public class GetTypeEntity
{

    private double account_total; // 还款本息总额
    private double interest_total; // 还款利息总额
    private double capital_total; // 月还款总额
    private double repay_month; // 每个月将偿还
    private double repay_season; // 每个季将偿还
    private double repay_year;
    private double month_apr; // 月利率
    private double day_apr; // 日利率?
    private double account_all; // 月还款本息
    private double account_interest; // 利息
    private double account_capital; // 月还款本金
    private double account_other; // 余额
    private long repay_time;
    private boolean result;

    public boolean isResult()
    {
        return result;
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public double getDay_apr()
    {
        return day_apr;
    }

    public void setDay_apr(double day_apr)
    {
        this.day_apr = day_apr;
    }

    public double getAccount_total()
    {
        return account_total;
    }

    public void setAccount_total(double account_total)
    {
        this.account_total = account_total;
    }

    public double getInterest_total()
    {
        return interest_total;
    }

    public void setInterest_total(double interest_total)
    {
        this.interest_total = interest_total;
    }

    public double getCapital_total()
    {
        return capital_total;
    }

    public void setCapital_total(double capital_total)
    {
        this.capital_total = capital_total;
    }

    public double getRepay_month()
    {
        return repay_month;
    }

    public void setRepay_month(double repay_month)
    {
        this.repay_month = repay_month;
    }

    public double getRepay_season()
    {
        return repay_season;
    }

    public void setRepay_season(double repay_season)
    {
        this.repay_season = repay_season;
    }

    public double getMonth_apr()
    {
        return month_apr;
    }

    public void setMonth_apr(double month_apr)
    {
        this.month_apr = month_apr;
    }

    public double getAccount_all()
    {
        return account_all;
    }

    public void setAccount_all(double account_all)
    {
        this.account_all = account_all;
    }

    public double getAccount_interest()
    {
        return account_interest;
    }

    public void setAccount_interest(double account_interest)
    {
        this.account_interest = account_interest;
    }

    public double getAccount_capital()
    {
        return account_capital;
    }

    public void setAccount_capital(double account_capital)
    {
        this.account_capital = account_capital;
    }

    public long getRepay_time()
    {
        return repay_time;
    }

    public void setRepay_time(long repay_time)
    {
        this.repay_time = repay_time;
    }

    public double getAccount_other()
    {
        return account_other;
    }

    public void setAccount_other(double account_other)
    {
        this.account_other = account_other;
    }

    public double getRepay_year()
    {
        return repay_year;
    }

    public void setRepay_year(double repay_year)
    {
        this.repay_year = repay_year;
    }
}
