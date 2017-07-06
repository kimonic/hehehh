package com.diyou.config;

import com.example.encryptionpackages.CreateCode;

public interface UrlConstants {
    /**
     * 网站服务协议
     */
    String AGREEMENT_WEBSITE = CreateCode.getDIYOU_URL() + "/wap/index/appAgreement";
    /**
     * 债权转让协议
     */
    String AGREEMENT_CREDITOR_TRANSFER = CreateCode.getDIYOU_URL() + "/wap/transfer/appTransferAgreement#?id=";//后面加id
    /**
     * 借款协议
     */
    String AGREEMENT_BORROW = CreateCode.getDIYOU_URL() + "/wap/loan/appLoanAgreement#?id=";//后面加id
    /**
     * 用户登录验证
     */
    String LOGIN = CreateCode.getDIYOU_URL() + "/phone/member/login";

    /**
     * 首页信息
     */
    String HOME = CreateCode.getDIYOU_URL() + "/phone/index/index";

    /**
     * 检测手机号码、邮箱、用户名是否存在
     */
    String CHECK = CreateCode.getDIYOU_URL() + "/phone/member/checkExit";

    /**
     * 用户注销
     */
    String LOGINOUT = CreateCode.getDIYOU_URL() + "/phone/member/loginOut";

    /**
     * 获取用户信息
     */
    String INFO = CreateCode.getDIYOU_URL() + "/phone/member/info";

    /**
     * 获取用资金信息
     */
    String ACCOUNT = CreateCode.getDIYOU_URL() + "/phone/member/account";

    /**
     * 获取用户资金记录
     */
    String ACCOUNTLOG = CreateCode.getDIYOU_URL() + "/phone/member/accountLog";

    /**
     * 获取用户投资列表
     */
    String TENDERLIST = CreateCode.getDIYOU_URL() + "/phone/member/tenderList";

    /**
     * 获取用户收款列表
     */
    String COLLECTIONLIST = CreateCode.getDIYOU_URL()
            + "/phone/member/collectionList";

    /**
     * 获取用户借款列表
     */
    String LOANLIST = CreateCode.getDIYOU_URL() + "/phone/member/loanList";

    /**
     * 获取用户还款明细列表
     */
    String REPAYLIST = CreateCode.getDIYOU_URL() + "/phone/member/repayList";
    /**
     * 获取用户还款操作
     */
    String REPAY = CreateCode.getDIYOU_URL() + "/phone/member/repay";

    /**
     * 获取首页需要相关信息
     */
    String INDEX = CreateCode.getDIYOU_URL() + "/phone/index/index";

    /**
     * 投资列表
     */
    String TENDER_INDEX = CreateCode.getDIYOU_URL() + "/phone/tender/index";
    /**
     * 获取认证信息
     */
    String APPROVE = CreateCode.getDIYOU_URL() + "/phone/member/getApprove";
    /**
     * 立即投资
     */
    String TENDER = CreateCode.getDIYOU_URL() + "/phone/tender/tender";

    /**
     * 获取某个标投资记录
     */
    String RECORDLIST = CreateCode.getDIYOU_URL() + "/phone/tender/recordList";

    /**
     * 债权转让列表
     */
    String TRANSFER_INDEX = CreateCode.getDIYOU_URL() + "/phone/transfer/index";

    /**
     * 债权转让详情
     */
    String CREDITER_TRANSFER_DETAILS = CreateCode.getDIYOU_URL() + "/phone/transfer/transferInfo";
    /**
     * 债权购买详情
     */
    String CREDITER_BUY_DETAILS = CreateCode.getDIYOU_URL() + "/phone/transfer/myTransferBuyInfo";
    /**
     * 购买债权
     */
    String BUY = CreateCode.getDIYOU_URL() + "/phone/transfer/buy";

    /**
     * 借标详情
     */
    String DETAIL = CreateCode.getDIYOU_URL() + "/phone/loan/detail";

    /**
     * 获取版本信息
     */
    String VERSION = CreateCode.getDIYOU_URL() + "/phone/extra/version";
    /**
     * 发送注册短信验证码
     */
    String REG_SMS = CreateCode.getDIYOU_URL() + "/phone/public/sendSms";
    /**
     * 提交手机认证
     */
    String APPROVE_PHONE = CreateCode.getDIYOU_URL()
            + "/phone/approve/approvePhone";
    /**
     * 注册
     */
    String REG = CreateCode.getDIYOU_URL() + "/phone/public/reg";
    /**
     * 常见问题
     */
    String ARTICLESLIST = CreateCode.getDIYOU_URL()
            + "/phone/articles/articlesList";
    /**
     * 公告栏目
     */
    String NOTICELIST = CreateCode.getDIYOU_URL()
            + "/phone/articles/noticeList";

    /**
     * 个人中心首页
     */
    String PERSONAL_CENTER_MAIN = CreateCode.getDIYOU_URL()
            + "/phone/member/memberCenter";
    /**
     * 用户资金详情
     */
    String PERSONAL_FUND_DETAIL = CreateCode.getDIYOU_URL()
            + "/phone/account/memberAccount";
    /**
     * 邮箱检测
     */
    String CHECK_EMAIL = CreateCode.getDIYOU_URL() + "/phone/public/checkEmail";
    /**
     * 手机检测
     */
    String CHECK_PHONE = CreateCode.getDIYOU_URL() + "/phone/public/checkPhone";
    /**
     * 发送邮箱验证码
     */
    String EMAIL_SEND_CODE = CreateCode.getDIYOU_URL()
            + "/phone/approve/sendApproveEmail";
    /**
     * 找回密码发送邮件
     */
    String SEND_RECOVER_EMAIL = CreateCode.getDIYOU_URL()
            + "/phone/member/sendRecoveryEmail";
    /**
     * 邮箱验证
     */
    String EMAIL_BIND = CreateCode.getDIYOU_URL()
            + "/phone/approve/approveEmail";
    /**
     * 我要投资页面
     */
    String INVESTDATA = CreateCode.getDIYOU_URL() + "/phone/tender/investData";
    /**
     * 投资收益
     */
    String INVESTINTEREST = CreateCode.getDIYOU_URL()
            + "/phone/tender/investInterest";
    /**
     * 设置修改登录密码
     */
    String EDITPWD = CreateCode.getDIYOU_URL() + "/phone/member/editPwd";

    /**
     * 判断用户是否设置支付密码
     */
    String GETPAYPWD = CreateCode.getDIYOU_URL() + "/phone/member/getPaypwd";

    /**
     * 设置找回密码新密码
     */
    String EIDT_RECOBER_PWD = CreateCode.getDIYOU_URL()
            + "/phone/member/editRecoverPwd";

    /**
     * 设置支付密码
     */
    String EDITPAYPWD = CreateCode.getDIYOU_URL() + "/phone/member/editPaypwd";
    /**
     * 立即投资
     */
    String INVESTMENT_TENDER = CreateCode.getDIYOU_URL()
            + "/phone/tender/tender";
    /**
     * 我的投资
     */
    String MY_INVESTMENT = CreateCode.getDIYOU_URL()
            + "/phone/tender/myTenderList";
    /**
     * 我的投资详情
     */
    String MY_INVESTMENT_DETAIL = CreateCode.getDIYOU_URL()
            + "/phone/tender/tenderInfo";
    /**
     * 用户交易记录
     */
    String TRADERECORD = CreateCode.getDIYOU_URL()
            + "/phone/account/accountLog";

    /**
     * 充值记录
     */
    String RECHARGERECORD = CreateCode.getDIYOU_URL()
            + "/phone/recharge/rechargelog";

    /**
     * 站内信
     */
    String INSIDELETTER = CreateCode.getDIYOU_URL()
            + "/phone/member/messageLog";
    /**
     * 站内信更新阅读状态
     */
    String INSIDELETTER_STATUS = CreateCode.getDIYOU_URL()
            + "/phone/member/messageView";
    /**
     * 修改手机绑定
     */
    String CHANGE_PHONE = CreateCode.getDIYOU_URL()
            + "/phone/approve/resetPhone";
    /**
     * 修改邮箱绑定
     */
    String CHANGE_EMAIL = CreateCode.getDIYOU_URL()
            + "/phone/approve/resetEmail";
    /**
     * 用户信息
     */
    String USER_INFO = CreateCode.getDIYOU_URL() + "/phone/member/userInfo";
    /**
     * 版本更新
     */
    String UPDATE_VERSION = CreateCode.getDIYOU_URL()
            + "/phone/member/updateVersion";
    /**
     * 债权转让
     */
    String CRETIOR_INDEX = CreateCode.getDIYOU_URL() + "/phone/transfer/index";
    /**
     * 债权转让购买详情
     */
    String TransferBuyInfo = CreateCode.getDIYOU_URL()
            + "/phone/transfer/TransferBuyInfo";
    /**
     * 购买债权详情
     */
    String CRETIOR_DETAILS = CreateCode.getDIYOU_URL()
            + "/phone/transfer/TransferBuyInfo";
    /**
     * 我的债权转让列表
     */
    String CREDITER_LIST = CreateCode.getDIYOU_URL()
            + "/phone/transfer/myTransferList";
    /**
     * 实名认证
     */
    String REAL_NAME_AUTH = CreateCode.getDIYOU_URL()
            + "/phone/approve/approveRealName";
    /**
     * 债权转让
     */
    String CREDITER_DETAILS = CreateCode.getDIYOU_URL()
            + "/phone/transfer/transferInfo";
    /**
     * 绑定银行卡
     */
    String BIND_BANK_CARD = CreateCode.getDIYOU_URL() + "/phone/bank/addBank";
    /**
     * 更换银行卡
     */
    String CHANGE_BANK_CARD = CreateCode.getDIYOU_URL()
            + "/phone/bank/editBank";
    /**
     * 获取银行卡信息
     */
    String BANK_CARD_INFO = CreateCode.getDIYOU_URL() + "/phone/bank/index";
    /**
     * 获取银行卡头信息
     */
    String BANK_CARD_NID = CreateCode.getDIYOU_URL() + "/phone/bank/getBankNid";
    /**
     * 获取银行列表
     */
    String BANK_LIST_INFO = CreateCode.getDIYOU_URL() + "/phone/bank/bankList";
    /**
     * 获取省市信息
     */
    String PROVINCE_CITY_INFO = CreateCode.getDIYOU_URL()
            + "/phone/common/getProvinceCity";
    /**
     * 立即转让
     */
    String IMMEDIATE_TRANSFER = CreateCode.getDIYOU_URL()
            + "/phone/transfer/transferSub";
    /**
     * 撤销转让
     */
    String CANCEL_TRANSFER = CreateCode.getDIYOU_URL()
            + "/phone/transfer/cancel";
    /**
     * 获取取现手续费
     */
    String WITHDRAW_FEE = CreateCode.getDIYOU_URL()
            + "/phone/member/getCashFee";
    /**
     * 提现
     */
    String WITHDRAW = CreateCode.getDIYOU_URL() + "/phone/member/withDraw";
    /**
     * 提现记录
     */
    String WITHDRAW_RECORD = CreateCode.getDIYOU_URL()
            + "/phone/member/withDrawLog";
    /**
     * 推广记录
     */
    String PROMOTION_RECORD = CreateCode.getDIYOU_URL()
            + "/phone/spread/mySpreadLog";
    /**
     * 结算记录
     */
    String SETTLEMENT_RECORD = CreateCode.getDIYOU_URL()
            + "/phone/spread/getSettleLog";
    /**
     * 立即结算
     */
    String SETTLEMENT_NOW = CreateCode.getDIYOU_URL()
            + "/phone/spread/doAccount";
    /**
     * 我的推广
     */
    String MY_PROMOTION = CreateCode.getDIYOU_URL()
            + "/phone/spread/mySpreadAll";
    /**
     * 判断是否是自己的借款标
     */
    String IS_MY_LOAN = CreateCode.getDIYOU_URL() + "/phone/loan/isMyLoan";
    /**
     * 判断是否是自己的债权
     */
    String IS_MY_TRANSFER = CreateCode.getDIYOU_URL() + "/phone/transfer/isMyTransfer";
    /**
     * 托管注册
     */
    String TRUST_REGISTER = CreateCode.getDIYOU_URL() + "/phone/trust/register";
    /**
     * 是否注册托管
     */
    String IS_TRUST_REGISTER = CreateCode.getDIYOU_URL()
            + "/phone/trust/isregister";
    /**
     * 获取充值方式
     */
    String RECHARGE_TYPE = CreateCode.getDIYOU_URL()
            + "/phone/recharge/getList";
    /**
     * 获取充值手续费
     */
    String RECHARGE_FEE = CreateCode.getDIYOU_URL()
            + "/phone/recharge/rechargeFee";
    /**
     * 易宝充值
     */
    String YIBAO_RECHARGE = CreateCode.getDIYOU_URL() + "/phone/trust/recharge";
    /**
     * 易宝提现
     */
    String YIBAO_WITHDRAW = CreateCode.getDIYOU_URL() + "/phone/trust/withdraw";
    /**
     * 易宝绑定银行卡
     */
    String YIBAO_BIND_CARD = CreateCode.getDIYOU_URL()
            + "/phone/trust/bindBankCard";
    /**
     * 易宝解绑银行卡
     */
    String YIBAO_UNBIND_CARD = CreateCode.getDIYOU_URL()
            + "/phone/trustDirect/unBindBankCard";
    /**
     * 易宝投资
     */
    String YIBAO_INVEST = CreateCode.getDIYOU_URL() + "/phone/trust/tender";

    /**
     * 流转标易宝投资
     */
    String YIBAO_FLOW_INVEST = CreateCode.getDIYOU_URL() + "/phone/trust/tenderRoam";
    /**
     *
     * 易宝债权购买JAVA
     */
    String YIBAO_BUY_JAVA = CreateCode.getDIYOU_URL() + "/phone/trust/buyTransfer";
    /**
     * 我的支付账号
     */
    String YIBAO_MY_ACCOUNT = CreateCode.getDIYOU_URL()
            + "/phone/trust/myTrust";

    /**
     * 上传头像
     */
    String YIBAO_AVATAR_SUBMIT = CreateCode.getDIYOU_URL()
            + "/phone/member/avatarSubmit";
    /**
     * 获取协议的内容
     */
    String XIE_YI_URL = CreateCode.getDIYOU_URL()
            + "/phone/loan/loanType";


    /**
     * 计算器还款方式列表
     */
    String GET_REPAY_TYPE_LIST=CreateCode.getDIYOU_URL() + "/phone/loan/getRepayTypeList";

    /**
     * 计算器计算结果
     */
    String CALCULATOR_RESULT =CreateCode.getDIYOU_URL() + "/phone/common/calculator";
}
