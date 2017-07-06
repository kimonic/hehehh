package com.diyou.bean;

import java.io.Serializable;

public class InvestmentListInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;// 借款标id
    private String pic;// 图片的url
    private String name;// 借款标标题
    private String amount;// 投资总金额
    private String apr;// 利率
    private String period;// 借款期限
    private String category_id;// 借款标种类 1 信用标 2净值标 3天标 4担保标 5抵押标 6秒标 7流转标
    private String category_type; //标的类型:1正常标 2:担保标 3:流转标
    private String progress;// 投资进度
    private String status;// 状态
    private String status_name;// 状态名称
    private String tender_count;// 投资人数
    private String company_name;// 公司名称
    private String is_company;// 表示是否有公司
    private String vouch_company_name;// 担保公司名称
    private String award_status;// 是否奖励 -1否 1按金额，2按比例
    private String url;// 標web地址
    private String repay_type;//还款方式      =5是按天计息到期还本息
    private int total_items;// 总条数
    private int total_pages;// 总页数
    private String share_url;//分享链接
    private String share_title;//分享标题
    private String share_content;//分享内容

    private String additional_apr;//新手附加利率
    private String additional_status;//冗余新手标状态，1表示是新手专享标
    /**
     * '-10' => '初审驳回',
     //'-9' => '后台撤标', //后台撤标(暂时不用)
     '-8' => '复审失败',
     '-7' => '锁定中',
     '-6' => '后台撤标(未过期)', //未过期后台撤标（流标）
     '-5' => '后台撤标(过期)',//'后台撤标', //过期后台撤标（流标）
     '-4' => '用户撤标',
     //'-3' => '复审失败', //复审失败不合格标(暂时不用)
     '-2' => '已过期', //借款到期未满标（过期）
     '-1' => '初审失败',
     '1' => '发标待审',
     '2' => '初审通过',
     '3' => '借款中',
     '4' => '满标待审',
     '5' => '复审后台处理中',
     '6' => '还款中',//'复审通过', //复审通过
     '7' => '已还完',

     */

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_type() {
        return category_type;
    }

    public void setCategory_type(String category_type) {
        this.category_type = category_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotal_items() {
        return total_items;
    }

    public void setTotal_items(int total_items) {
        this.total_items = total_items;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public String getRepay_type() {
        return repay_type;
    }

    public void setRepay_type(String repay_type) {
        this.repay_type = repay_type;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getShare_title() {
        return share_title;
    }

    public void setShare_title(String share_title) {
        this.share_title = share_title;
    }

    public String getShare_content() {
        return share_content;
    }

    public void setShare_content(String share_content) {
        this.share_content = share_content;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getApr() {
        return apr;
    }

    public void setApr(String apr) {
        this.apr = apr;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getTender_count() {
        return tender_count;
    }

    public void setTender_count(String tender_count) {
        this.tender_count = tender_count;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getIs_company() {
        return is_company;
    }

    public void setIs_company(String is_company) {
        this.is_company = is_company;
    }

    public String getVouch_company_name() {
        return vouch_company_name;
    }

    public void setVouch_company_name(String vouch_company_name) {
        this.vouch_company_name = vouch_company_name;
    }
    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
    public String getAward_status() {
        return award_status;
    }

    public void setAward_status(String award_status) {
        this.award_status = award_status;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAdditionalStatus(String additional_status){
        this.additional_status = additional_status;
    }

    public String getAdditionalStatus(){
        return this.additional_status;
    }

    public void setAdditional_apr(String additional_apr){
        this.additional_apr = additional_apr;
    }

    public String getAdditional_apr(){
        return additional_apr;
    }

}
