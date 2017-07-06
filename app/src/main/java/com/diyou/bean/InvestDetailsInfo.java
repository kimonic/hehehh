package com.diyou.bean;

public class InvestDetailsInfo {
    private String name;// 借款标名称
    private String apr;// 利率
    private String award_status;// 奖励状态-1否 1按金额，2按比例
    private String award_proportion;// 奖励比例
    private String progress;// 投资进度
    private int repay_type;// 还款方式
    private String repayTypeName;// 还款方式名称
    private String tender_amount_max;// 最高投资金额
    private String tender_amount_min;// 最低投资金额
    private String period_name;// 借款期限名称
    private String period;// 借款期限
    private String wait_amount;// 可投金额
    private String balance_amount;// 账户余额
    private String additional_status;

    // private String award_amount;//投资奖励金额

    public int getPortion_total() {
        return portion_total;
    }

    public void setPortion_total(int portion_total) {
        this.portion_total = portion_total;
    }

    public int getPortion_yes() {
        return portion_yes;
    }

    public void setPortion_yes(int portion_yes) {
        this.portion_yes = portion_yes;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    private int portion_total;//总流转份数
    private int portion_yes;//已流转份数
    private String amount;//最小流转
    private String additional_apr;

    public String getAdditional_status() {
        return additional_status;
    }

    public void setAdditional_status(String additional_status) {
        this.additional_status = additional_status;
    }

    public int getRepay_type() {
        return repay_type;
    }

    public String getWait_amount() {
        return wait_amount;
    }

    public void setWait_amount(String wait_amount) {
        this.wait_amount = wait_amount;
    }

    public String getBalance_amount() {
        return balance_amount;
    }

    public void setBalance_amount(String balance_amount) {
        this.balance_amount = balance_amount;
    }

    public void setRepay_type(int repay_type) {
        this.repay_type = repay_type;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getApr() {
        return apr;
    }

    public void setApr(String apr) {
        this.apr = apr;
    }

    public String getAward_status() {
        return award_status;
    }

    public void setAward_status(String award_status) {
        this.award_status = award_status;
    }

    public String getAward_proportion() {
        return award_proportion;
    }

    public void setAward_proportion(String award_proportion) {
        this.award_proportion = award_proportion;
    }

    public String getTender_amount_min() {
        return tender_amount_min;
    }

    public void setTender_amount_min(String tender_amount_min) {
        this.tender_amount_min = tender_amount_min;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getRepayTypeName() {
        return repayTypeName;
    }

    public void setRepayTypeName(String repayTypeName) {
        this.repayTypeName = repayTypeName;
    }

    public String getTender_amount_max() {
        return tender_amount_max;
    }

    public void setTender_amount_max(String tender_amount_max) {
        this.tender_amount_max = tender_amount_max;
    }

    public String getPeriod_name() {
        return period_name;
    }

    public void setPeriod_name(String period_name) {
        this.period_name = period_name;
    }


    public void setAdditional_apr(String additional_apr) {
        this.additional_apr = additional_apr;
    }

    public String getAdditional_apr() {
        return additional_apr;
    }

}
