package com.diyou.bean;

import java.io.Serializable;

public class RechargeRecordInfo implements Serializable
{
    private String amount;//充值金额
    private String status;//
    private String ind;
    private String payment_name;
    private String add_time;
    private String verify_remark;
    private String type;
    private String payment_nid;
    private String status_name;
    private String type_name;
    private String remark;

    public String getAmount() {
        return amount;
    }

    public void setAccount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInd() {
        return ind;
    }

    public void setInd(String ind) {
        this.ind = ind;
    }

    public String getPayment_name() {
        return payment_name;
    }

    public void setPayment_name(String payment_name) {
        this.payment_name = payment_name;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getVerify_remark() {
        return verify_remark;
    }

    public void setVerify_remark(String verify_remark) {
        this.verify_remark = verify_remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayment_nid() {
        return payment_nid;
    }

    public void setPayment_nid(String payment_nid) {
        this.payment_nid = payment_nid;
    }

    public void setStatus_name(String stateName) {
        this.status_name = stateName;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setTypeName(String typename) {
        this.type_name = typename;
    }

    public String getTypeName() {
        return type_name;
    }

    public void setRemark(String remark){
        this.remark = remark;
    }

    public String getRemark(){
        return remark;
    }
}
