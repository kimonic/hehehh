package com.diyou.bean;

public class AuthenticationInfo
{
    private String memberName;// 用户名
    private String email;// 邮箱认证
    private String phone;// 手机认证
    private String pwd;// 密码
    private String payPwd;// 支付密码
    private String realName;// 真实姓名
    private String cardId;// 身份证号
    private String positiveCard;// 身份证证明图片
    private String realNameStatus;// 实名认证状态
    private String realNameRemark;// 审核备注
    private String account;// 银行卡号
    private String bankName;// 银行名称

    public String getMemberName()
    {
        return memberName;
    }

    public void setMemberName(String memberName)
    {
        this.memberName = memberName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPwd()
    {
        return pwd;
    }

    public void setPwd(String pwd)
    {
        this.pwd = pwd;
    }

    public String getPayPwd()
    {
        return payPwd;
    }

    public void setPayPwd(String payPwd)
    {
        this.payPwd = payPwd;
    }

    public String getRealName()
    {
        return realName;
    }

    public void setRealName(String realName)
    {
        this.realName = realName;
    }

    public String getCardId()
    {
        return cardId;
    }

    public void setCardId(String cardId)
    {
        this.cardId = cardId;
    }

    public String getPositiveCard()
    {
        return positiveCard;
    }

    public void setPositiveCard(String positiveCard)
    {
        this.positiveCard = positiveCard;
    }

    public String getRealNameStatus()
    {
        return realNameStatus;
    }

    public void setRealNameStatus(String realNameStatus)
    {
        this.realNameStatus = realNameStatus;
    }

    public String getRealNameRemark()
    {
        return realNameRemark;
    }

    public void setRealNameRemark(String realNameRemark)
    {
        this.realNameRemark = realNameRemark;
    }

    public String getAccount()
    {
        return account;
    }

    public void setAccount(String account)
    {
        this.account = account;
    }

    public String getBankName()
    {
        return bankName;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

}
