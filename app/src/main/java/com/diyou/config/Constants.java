package com.diyou.config;

public interface Constants
{
    int GREY = 0xFF00CCFF;
    int SKYBLUE = 0xFFEAEAEA;
    int LOGINREQUESTCODE = 1;
    int EXIT_LOGIN_CODE = 4;
    int INVESTMENTDETAILSACTIVITY = 6;
    int CREDITORRIGHTSDETAILSACTIVITY = 7;
    int THETENDERACTIVITY = 2001;
    int SUCCESS = 2002;
    int ERROR = 2003;
    int EXIT_LOGIN_CODE_BACK = 1001;
    int LOGOUT = 2002;
    int PHONECALL = 2001;
    int APPEXIT = 2000;
    int SETGESTUREPASSWORD = 5001;
    int LOGINSUCCESS = 3001;
    int REGISTERSUCCESS = 3002;
    int FORGOTPASSWORD = 4001;
    String SHARE_MENU = "dysp";
    String SHARE_ISLOGIN = "islogin";
    String SHARE_USERKEY = "userkey";
    String SHARE_USERID = "userid";
    String SHARE_LOGINTOKEN = "loginToken";
    String SHARE_USERNAME = "username";
    String LOANAGREEMENTURL = "http://test.gong-e.com/?ajax&t=borrow_contract";
    String IMAGEURL = "http://test.gong-e.com";
    String FILENAME = "/com.diyou.V5yibao";
    String LOCK = "lock";
    String LOCK_KEY = "lock_key";

    String ACTION_PROCESS_YBREG="yibaoReg";
    // request_code
    int I_AAGGREMENT_CODE=1111;
    int I_CAGGREMENT_CODE=1112;
    int REQUEST_CODE_PROVINCE_CITY = 4015;
    int RESULT_CODE_PROVINCE_CITY = 4016;
    int REQUEST_CODE_NEW_PASSWORD = 3321;
    int REQUEST_CODE_BANK = 4025;
    int RESULT_CODE_BANK = 4026;
    
    //支付密码长度范围6-15
    int PAY_PASSWORD_MIN_LENGTH=6;
    int PAY_PASSWORD_MAX_LENGTH=15;
    //银行卡号最少位数
    int BANKCARD_ACCOUNT_MIN_LENGTH=16;
  //大于一百万的数值单位用万元
    double MORE_THAN_DISPLAY_MONEY=1000000;

    //key
    String KEY_SERVICE_TEL="serviceTel";
    String KEY_SERVICE_HOURS="serviceHours";

    //易宝操作url成功标识
    String STATUS_RECHARGE_SUCCESS="recharge_status=success";//充值成功
    String STATUS_WITHDRAW_SUCCESS="withdraw_status=success";//提现成功
    String STATUS_REGISTER_SUCCESS="register_status=success";//注册成功
    String STATUS_BINDCARD_SUCCESS="bindcard_status=success";//绑卡成功
    String STATUS_UNBINDCARD_SUCCESS="unbindcard_status=success";//解绑成功
    String STATUS_INVEST_SUCCESS="invest_status=success";//投资成功
    String STATUS_FLOW_INVEST_SUCCESS="tenderroam_status=success";//投资成功
    String STATUS_BUY_SUCCESS="buy_status=success";//购买债权成功
    //易宝操作url失败标识
    String STATUS_RECHARGE_FAIL="recharge_status=fail";//充值失败
    String STATUS_WITHDRAW_FAIL="withdraw_status=fail";//提现失败
    String STATUS_REGISTER_FAIL="register_status=fail";//注册失败
    String STATUS_BINDCARD_FAIL="bindcard_status=fail";//绑卡失败
    String STATUS_UNBINDCARD_FAIL="unbindcard_status=fail";//解绑失败
    String STATUS_INVEST_FAIL="invest_status=fail";//投资失败
    String STATUS_BUY_FAIL="buy_status=fail";//购买债权失败

    //wap页面直接立即关闭
    String STATUS_CLOSE="/wap";
}
