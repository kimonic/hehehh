package com.diyou.bean;

public class CretiorListInfo
{
    private String id;// 借款标id
    private String amount;// 投资总金额
    private String apr;// 利率
    private String period;// 借款期限
    private String category_id;// 标的种类
    private String progress;// 投资进度
    private String tender_count;// 投资人数
    private String company_name;// 担保公司名称
    private String is_company;// 表示是否有担保公司
    private String award_status;// 是否奖励 -1否 1按金额，2按比例
    private String url;// 標web地址
    private int total_items;// 总条数
    private int total_pages;// 总页数

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getCategory_id()
    {
        return category_id;
    }

    public void setCategory_id(String category_id)
    {
        this.category_id = category_id;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public int getTotal_items()
    {
        return total_items;
    }

    public void setTotal_items(int total_items)
    {
        this.total_items = total_items;
    }

    public int getTotal_pages()
    {
        return total_pages;
    }

    public void setTotal_pages(int total_pages)
    {
        this.total_pages = total_pages;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getApr()
    {
        return apr;
    }

    public void setApr(String apr)
    {
        this.apr = apr;
    }

    public String getPeriod()
    {
        return period;
    }

    public void setPeriod(String period)
    {
        this.period = period;
    }

    public String getProgress()
    {
        return progress;
    }

    public void setProgress(String progress)
    {
        this.progress = progress;
    }

    public String getTender_count()
    {
        return tender_count;
    }

    public void setTender_count(String tender_count)
    {
        this.tender_count = tender_count;
    }

    public String getCompany_name()
    {
        return company_name;
    }

    public void setCompany_name(String company_name)
    {
        this.company_name = company_name;
    }

    public String getIs_company()
    {
        return is_company;
    }

    public void setIs_company(String is_company)
    {
        this.is_company = is_company;
    }

    public String getAward_status()
    {
        return award_status;
    }

    public void setAward_status(String award_status)
    {
        this.award_status = award_status;
    }

}
