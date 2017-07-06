package com.diyou.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.diyou.base.BaseActivity;
import com.diyou.bean.EventObject;
import com.diyou.config.Constants;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.pulltorefresh.PullToRefreshBase;
import com.diyou.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.diyou.pulltorefresh.PullToRefreshScrollView;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.LoadingLayout;
import com.diyou.view.MyDialog;
import com.example.encryptionpackages.CreateCode;
import com.lidroid.xutils.util.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.TreeMap;

import de.greenrobot.event.EventBus;

/**
 * 账户详情
 *
 * @author Administrator
 */
public class AccountDetailActivity extends BaseActivity
        implements OnClickListener, OnRefreshListener<ScrollView> {

    private TextView user_info;
    private TextView bank_card;
    private TextView yibao;
    private TextView phone_bind;
    private TextView email_auth;
    private TextView realname_auth;
    private TextView meterial_auth;
    private TextView login_password;
    private TextView gesture_password;
    private TextView password;
    private PullToRefreshScrollView mScrollView;
    private LoadingLayout mLoadingLayout;
    private MyDialog dialog;
    private boolean isFirst = true;
    private String mEnterprise;//1普通用户 2担保公司 3企业用户

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);
        initActionBar();
        initView();
        EventBus.getDefault().register(this);
        requestData();
    }

    /**
     * eventBus事件处理
     * <p/>
     * onEventPostThread， onEventMainThread，onEventBackgroundThread，onEventAsync
     */
    public void onEventMainThread(EventObject object) {
        LogUtils.e("刷新账户详情");
        if (object != null) {
            if ("refresh".equals(object.getAction())) {
                requestData();
            }
        }
    }

    private void initActionBar() {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.account_detail_title);
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    private void initListener() {
        findViewById(R.id.account_detail_layout_user_info)
                .setOnClickListener(this);
        findViewById(R.id.account_detail_layout_bank_card)
                .setOnClickListener(this);
        findViewById(R.id.account_detail_layout_yibao).setOnClickListener(this);
        findViewById(R.id.account_detail_layout_phone_bind)
                .setOnClickListener(this);
        findViewById(R.id.account_detail_layout_email_auth)
                .setOnClickListener(this);
        findViewById(R.id.account_detail_layout_realname_auth)
                .setOnClickListener(this);
        findViewById(R.id.account_detail_layout_meterial_auth).setOnClickListener(this);
        findViewById(R.id.account_detail_layout_login_password)
                .setOnClickListener(this);
        findViewById(R.id.account_detail_layout_gesture_password)
                .setOnClickListener(this);
        findViewById(R.id.account_detail_layoutpay_password)
                .setOnClickListener(this);
    }

    private void initView() {
        mScrollView = (PullToRefreshScrollView) findViewById(
                R.id.sv_account_detail);
        mScrollView.setOnRefreshListener(this);
        mLoadingLayout = (LoadingLayout) findViewById(
                R.id.account_detail_loadlayout);
        mLoadingLayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        requestData();
                    }
                });
        mLoadingLayout.setOnStartLoading(mScrollView);

        user_info = (TextView) findViewById(
                R.id.account_detail_layout_user_info_setup);
        bank_card = (TextView) findViewById(
                R.id.account_detail_layout_bank_card_setup);
        yibao = (TextView) findViewById(R.id.account_detail_layout_yibao_setup);
        phone_bind = (TextView) findViewById(
                R.id.account_detail_layout_phone_bind_setup);
        email_auth = (TextView) findViewById(
                R.id.account_detail_layout_email_auth_setup);
        realname_auth = (TextView) findViewById(
                R.id.account_detail_layout_realname_auth_setup);
        meterial_auth = (TextView) findViewById(
                R.id.account_detail_layout_meterial_auth_setup);
        login_password = (TextView) findViewById(
                R.id.account_detail_layout_login_password_setup);
        gesture_password = (TextView) findViewById(
                R.id.account_detail_layout_gesture_password_setup);
        gesture_password.setText(hasGesturePassWord() ? "未设置" : "修改");
        gesture_password.setTextColor(
                hasGesturePassWord() ? getResources().getColor(R.color.huise)
                        : getResources().getColor(R.color.lanse));
        password = (TextView) findViewById(
                R.id.account_detail_layoutpay_password_setup);

    }

    private boolean hasGesturePassWord() {
        SharedPreferences preferences = getSharedPreferences(Constants.LOCK,
                MODE_PRIVATE);
        String lockPattenString = preferences.getString(Constants.LOCK_KEY,
                null);
        return lockPattenString == null;

    }

    protected void requestData() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        HttpUtil.post(UrlConstants.APPROVE, map, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                if (isFirst) {
                    mLoadingLayout.setOnLoadingError(AccountDetailActivity.this,
                            mScrollView);
                }
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                try {
                    if (CreateCode.AuthentInfo(response)) {
                        JSONObject data = StringUtils.parseContent(response);
                        if (data.optString("result").equals("success")) {
                            JSONObject info = data.getJSONObject("data");
                            mEnterprise = info.optString("enterprise");

                            // 用户信息
                            user_info.setText(info.getString("member_name"));
                            // 银行卡管理
                            if (StringUtils
                                    .isEmpty(info.optString("bank_name")
                                            .toString())
                                    || StringUtils.isEmpty(info
                                    .getString("account").toString())) {
                                bank_card.setText("未设置");
                                bank_card.setTextColor(
                                        getResources().getColor(R.color.huise));
                            } else {
                                bank_card.setText(info.optString("bank_name")
                                        + StringUtils.getHideBankAccount(
                                        info.optString("account")));
                                bank_card.setTextColor(
                                        getResources().getColor(R.color.lanse));
                            }
                            // 易宝托管
                            if ("1".equals(info.optString("register"))) {
                                yibao.setText(R.string.account_detail_register);
                                yibao.setTextColor(
                                        getResources().getColor(R.color.lanse));
                            } else {
                                yibao.setText(
                                        R.string.account_detail_unregister);
                                yibao.setTextColor(
                                        getResources().getColor(R.color.huise));
                            }
                            if (StringUtils.isEmpty(info.optString("phone"))) {
                                phone_bind.setText("未绑定");
                                phone_bind.setTextColor(
                                        getResources().getColor(R.color.huise));
                            } else {
                                phone_bind.setText("已绑定");
                                phone_bind.setTag(info.optString("phone"));
                                phone_bind.setTextColor(
                                        getResources().getColor(R.color.lanse));

                            }
                            if (StringUtils.isEmpty(info.optString("email"))) {
                                email_auth.setText("未认证");
                                email_auth.setTextColor(
                                        getResources().getColor(R.color.huise));
                            } else {
                                email_auth.setText("已认证");
                                email_auth.setTag(info.optString("email"));
                                email_auth.setTextColor(
                                        getResources().getColor(R.color.lanse));

                            }
                            if (StringUtils
                                    .isEmpty(info.optString("realname_status"))) {
                                realname_auth.setText("未认证");
                                realname_auth.setTextColor(
                                        getResources().getColor(R.color.huise));
                            } else if (info.optString("realname_status")
                                    .equals("-2")) {
                                realname_auth.setText("待审核");
                                realname_auth.setTextColor(
                                        getResources().getColor(R.color.huise));

                            } else if (info.optString("realname_status")
                                    .equals("-1")) {
                                realname_auth.setText("审核失败");
                                realname_auth.setTextColor(
                                        getResources().getColor(R.color.huise));

                            } else if (info.optString("realname_status")
                                    .equals("1")) {
                                realname_auth.setText("已认证");
                                realname_auth.setTextColor(
                                        getResources().getColor(R.color.lanse));

                            }

                            if (StringUtils.isEmpty(info.optString("password"))) {
                                login_password.setText("未设置");
                                login_password.setTextColor(
                                        getResources().getColor(R.color.huise));
                            } else {
                                login_password.setText("修改");
                                login_password.setTextColor(
                                        getResources().getColor(R.color.lanse));

                            }
                            if (StringUtils.isEmpty(info.optString("paypassword"))) {
                                password.setText("未设置");
                                password.setTextColor(
                                        getResources().getColor(R.color.huise));
                            } else {
                                password.setText("修改");
                                password.setTextColor(
                                        getResources().getColor(R.color.lanse));

                            }
                            initListener();

                            if (isFirst) {
                                isFirst = false;
                                mLoadingLayout.setOnStopLoading(
                                        AccountDetailActivity.this,
                                        mScrollView);
                            }
                        } else {
                            ToastUtil.show(data.optString("description"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFinish() {
                mScrollView.onRefreshComplete();
                super.onFinish();
            }

            @Override
            public void onStart() {
                super.onStart();
            }

        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.account_detail_layout_yibao:
                if ("3".equals(mEnterprise)) {
                    ToastUtil.show("企业用户请到PC端操作");
                } else {
                    requestYibaoRegistered(new Runnable() {

                        @Override
                        public void run() {
                            startActivity(new Intent(AccountDetailActivity.this,
                                    MyPayAccountActivity.class));
                        }
                    });
                }

                break;
            case R.id.account_detail_layout_user_info:
//            intent.setClass(this, UserInfoActivity.class);
//            startActivity(intent);
                break;
            case R.id.account_detail_layout_bank_card:
                intent.setClass(this, BankCardManagerActivity.class);
                startActivity(intent);
                break;
            case R.id.account_detail_layout_gesture_password:
                if (hasGesturePassWord()) {
                    dialog = new MyDialog(AccountDetailActivity.this, true, false,
                            false, false);
                } else {
                    dialog = new MyDialog(AccountDetailActivity.this, false, true,
                            true, true);
                }
                dialog.show();
                break;

            case R.id.account_detail_layout_phone_bind:
                if (phone_bind.getText().toString().equals("未绑定")) {
                    intent.setClass(AccountDetailActivity.this,
                            PhoneBindActivity.class);
                    startActivity(intent);
                } else {
                    String phoneNum = String.valueOf(phone_bind.getTag());
                    if (phoneNum != null && !StringUtils.isEmpty(phoneNum)) {
                        intent.putExtra("phoneNum", phoneNum);
                        intent.setClass(AccountDetailActivity.this,
                                PhoneChangeActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.account_detail_layout_email_auth:
            if (email_auth.getText().toString().equals("未认证"))
            {
                intent.setClass(AccountDetailActivity.this,
                        EmailBindActivity.class);
                startActivity(intent);
            }
            else if (email_auth.getText().toString().equals("已认证"))
            {
                intent.setClass(AccountDetailActivity.this,
                        EmailChangeActivity.class);
                intent.putExtra("email", String.valueOf(email_auth.getTag()));
                startActivity(intent);
            }
                break;
            case R.id.account_detail_layout_meterial_auth:
                ToastUtil.show(R.string.common_operate_on_pc);
                break;
            case R.id.title_back:
                finish();
                break;
       /* case R.id.account_detail_layoutpay_password:
            if (password.getText().toString().trim().equals("未设置"))
            {
                intent.setClass(AccountDetailActivity.this,
                        SettingPaymentActivity.class);
                startActivity(intent);
            }
            else
            {
                intent.setClass(AccountDetailActivity.this,
                        UpdatePaymentActivity.class);
                startActivity(intent);
            }

            break;*/
            case R.id.account_detail_layout_realname_auth:
                String realnameState = realname_auth.getText().toString();
                if ("已认证".equals(realnameState) || "待审核".equals(realnameState)) {
                    intent.setClass(AccountDetailActivity.this,
                            RealNameInfoActivity.class);
                /*
                 * intent.putExtra("realName",
                 * String.valueOf(realname_auth.getTag(R.id.real_name)));
                 * intent.putExtra("cardId",
                 * String.valueOf(realname_auth.getTag(R.id.card_id)));
                 */
                    startActivity(intent);
                } else if ("未认证".equals(realnameState)
                        || "审核失败".equals(realnameState)) {
                    intent.setClass(AccountDetailActivity.this,
                            RealNameAuthActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.account_detail_layout_login_password:
                startActivity(new Intent(AccountDetailActivity.this,
                        UpdateLoginPasswordActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Constants.SETGESTUREPASSWORD:
                gesture_password.setText(hasGesturePassWord() ? "未设置" : "修改");
                gesture_password.setTextColor(hasGesturePassWord()
                        ? getResources().getColor(R.color.huise)
                        : getResources().getColor(R.color.lanse));
                break;

            default:
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
        requestData();
    }

}
