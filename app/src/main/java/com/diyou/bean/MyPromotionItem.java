package com.diyou.bean;

import java.io.Serializable;

public class MyPromotionItem implements Serializable
{

    private static final long serialVersionUID = 1L;

    private String spreaded_member_name;// 被推广人用户名
    private String tender_success_amount;// 投资金额
    private String repay_amount_yes;// 还款金额

    public MyPromotionItem(String spreaded_member_name,
            String tender_success_amount, String repay_amount_yes)
    {
        super();
        this.spreaded_member_name = spreaded_member_name;
        this.tender_success_amount = tender_success_amount;
        this.repay_amount_yes = repay_amount_yes;
    }

    public String getSpreaded_member_name()
    {
        return spreaded_member_name;
    }

    public void setSpreaded_member_name(String spreaded_member_name)
    {
        this.spreaded_member_name = spreaded_member_name;
    }

    public String getTender_success_amount()
    {
        return tender_success_amount;
    }

    public void setTender_success_amount(String tender_success_amount)
    {
        this.tender_success_amount = tender_success_amount;
    }

    public String getRepay_amount_yes()
    {
        return repay_amount_yes;
    }

    public void setRepay_amount_yes(String repay_amount_yes)
    {
        this.repay_amount_yes = repay_amount_yes;
    }

    public MyPromotionItem()
    {
    }

}
