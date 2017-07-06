package com.diyou.fragment;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.activity.AccountDetailActivity;
import com.diyou.activity.BankCardManagerActivity;
import com.diyou.activity.BeforeLoginActivity;
import com.diyou.activity.FundsDetailActivity;
import com.diyou.activity.InsideLetterActivity;
import com.diyou.activity.MainActivity;
import com.diyou.activity.MyProjectActivity;
import com.diyou.activity.MyPromotionActivity;
import com.diyou.activity.PhoneBindActivity;
import com.diyou.activity.RechargeActivity;
import com.diyou.activity.WithdrawActivity;
import com.diyou.base.BaseFragment;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.pulltorefresh.PullToRefreshBase;
import com.diyou.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.diyou.pulltorefresh.PullToRefreshScrollView;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.ConfirmCancelDialog;
import com.diyou.view.LoadingLayout;
import com.diyou.view.ProgressDialogBar;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class PersonalCenterFragment extends BaseFragment
        implements OnClickListener, OnRefreshListener<ScrollView>
{
    private static final int ACTION_RECHARGE = 1;
    private static final int ACTION_WITHDRAW = 2;
    private MainActivity mActivity;
    private View mNoLoginlayout;
    private LoadingLayout mLoadlayout;
    private TextView mTvIntrest;
    private TextView mTvMoneySum;
    private TextView mTvCanBalance;
    private PullToRefreshScrollView mScrollView;

    private boolean isFirst = true;
    // private ImageView mIvSetting;
    private View mViewMsg;
    private TextView mMsgRed;
    private int isRecharge=1;

    public PersonalCenterFragment()
    {
        // Required empty public constructor
    }

    private ProgressDialogBar progressDialogBar;

    public void showProgressDialog()
    {
        if (progressDialogBar == null)
        {
            progressDialogBar = ProgressDialogBar.createDialog(mActivity);
        }
        progressDialogBar.setProgressMsg("加载中...");
        progressDialogBar.show();
    }

    public void hideProgressDialog()
    {
        if (progressDialogBar != null && progressDialogBar.isShowing())
        {
            progressDialogBar.dismiss();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        mActivity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_personal_center,
                container, false);
        initView(view);
        return view;
    }

    private void initView(View view)
    {
        view.findViewById(R.id.rl_personal_fund_detail)
                .setOnClickListener(this);
        view.findViewById(R.id.rl_personal_my_project).setOnClickListener(this);
        view.findViewById(R.id.rl_personal_my_borrowing).setOnClickListener(this);
        view.findViewById(R.id.rl_personal_my_promotion)
                .setOnClickListener(this);
        view.findViewById(R.id.rl_personal_account_detail)
                .setOnClickListener(this);
        // mIvSetting = (ImageView)
        // view.findViewById(R.id.personal_center_setting);
        mViewMsg = view.findViewById(R.id.personal_center_msg);
        mViewMsg.setOnClickListener(this);
        // mIvSetting.setOnClickListener(this);
        mScrollView = (PullToRefreshScrollView) view
                .findViewById(R.id.accountfragment_list);
        mScrollView.setOnRefreshListener(this);
        mTvIntrest = (TextView) view
                .findViewById(R.id.tv_personal_interest_sum);

        mTvMoneySum = (TextView) view.findViewById(R.id.tv_personal_money_sum);
        mTvCanBalance = (TextView) view
                .findViewById(R.id.tv_personal_can_balance);
        view.findViewById(R.id.rl_personal_center_withdraw)
                .setOnClickListener(this);
        view.findViewById(R.id.rl_personal_center_recharge)
                .setOnClickListener(this);
        mLoadlayout = (LoadingLayout) view
                .findViewById(R.id.personal_fragment_loadlayout);
        mLoadlayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        requestData();
                    }
                });
        mNoLoginlayout = view.findViewById(R.id.myfragment_no_login_layout);
        mNoLoginlayout.setOnClickListener(this);

        mMsgRed = (TextView) view.findViewById(R.id.personal_center_msg_red);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        if (isVisibleToUser) {
            if (StringUtils.isEmpty(
                    UserConfig.getInstance().getLoginToken(getActivity()))) {
                mLoadlayout.setVisibility(View.GONE);
                // mIvSetting.setVisibility(View.GONE);
                mViewMsg.setVisibility(View.GONE);
                mNoLoginlayout.setVisibility(View.VISIBLE);
            } else {
                mNoLoginlayout.setVisibility(View.GONE);
                mViewMsg.setVisibility(View.VISIBLE);
                if (isFirst) {
                    mLoadlayout.setOnStartLoading(mScrollView);
                    // mIvSetting.setVisibility(View.VISIBLE);
                } else {
                    mLoadlayout.setOnStopLoading(mActivity, mScrollView);
                }
                requestData();
            }

        }
        super.setUserVisibleHint(isVisibleToUser);
    }
    /**使用login_token,获取用户信息*/
    private void requestData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token",
                UserConfig.getInstance().getLoginToken(mActivity));
        HttpUtil.post(UrlConstants.PERSONAL_CENTER_MAIN, map,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        try {
                            if (CreateCode.AuthentInfo(response)) {
                                JSONObject json = StringUtils
                                        .parseContent(response);
                                if (json.optString("result")
                                        .contains("success")) {

                                    JSONObject jo = new JSONObject(
                                            json.optString("data"));
                                    mTvIntrest.setText(StringUtils
                                            .getMoneyFormat(jo.optString(
                                                    "interest_award")));
                                    mTvMoneySum.setText(StringUtils
                                            .getMoneyFormat(jo.optString(
                                                    "total_amount")));
                                    mTvCanBalance.setText(StringUtils
                                            .getMoneyFormat(jo.optString(
                                                    "balance_amount")));
                                    String num = jo.optString("count");
                                    if (!StringUtils.isEmpty(num) && Integer.valueOf(num) > 0) {
                                        mMsgRed.setVisibility(View.VISIBLE);
                                    } else {
                                        mMsgRed.setVisibility(View.GONE);
                                    }
                                    if (isFirst) {
                                        isFirst = false;
                                        mLoadlayout.setOnStopLoading(mActivity,
                                                mScrollView);
                                    }
                                } else {
                                    ToastUtil.show(
                                            json.getString("description"));
                                }
                            }
                        } catch (JSONException e) {
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
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        if (isFirst) {
                            mLoadlayout.setOnLoadingError(mActivity,
                                    mScrollView);
                        }
                        // ToastUtil.show(R.string.generic_error);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.rl_personal_fund_detail:
            Intent intent = new Intent(mActivity, FundsDetailActivity.class);
            mActivity.startActivity(intent);
            break;
        case R.id.rl_personal_my_borrowing:
            ToastUtil.show(R.string.common_operate_on_pc);
            break;
        case R.id.rl_personal_my_project:
            Intent intent1 = new Intent(mActivity, MyProjectActivity.class);
            mActivity.startActivity(intent1);
            break;
        case R.id.rl_personal_my_promotion:
            mActivity.startActivity(
                    new Intent(mActivity, MyPromotionActivity.class));
            break;
        case R.id.rl_personal_account_detail:
            mActivity.startActivity(
                    new Intent(mActivity, AccountDetailActivity.class));
            break;
        // case R.id.personal_center_setting:
        // mActivity
        // .startActivity(new Intent(mActivity, SettingActivity.class));
        // break;
        case R.id.myfragment_no_login_layout:
            mActivity.startActivityForResult(
                    new Intent(mActivity, BeforeLoginActivity.class), 0);
            break;
        case R.id.personal_center_msg:
            mActivity.startActivity(
                    new Intent(mActivity, InsideLetterActivity.class));
            break;
        case R.id.rl_personal_center_recharge:
            /*isRecharge=1;
            getApprove(isRecharge);*/
            requestYibaoRegistered(new Runnable()
            {

                @Override
                public void run()
                {
                    startActivity(
                            new Intent(mActivity, RechargeActivity.class));
                }
            });
            break;
        case R.id.rl_personal_center_withdraw:
//            isRecharge=2;
//            getApprove(isRecharge);
            requestYibaoRegistered(new Runnable()
            {

                @Override
                public void run()
                {
                    getBankCardInfo();
                }
            });
            break;
        default:
            break;
        }
    }

    public void loginOut()
    {
        mNoLoginlayout.setVisibility(View.VISIBLE);
        mScrollView.setVisibility(View.GONE);
        // mIvSetting.setVisibility(View.GONE);
        mViewMsg.setVisibility(View.GONE);
    }
    /**为登陆状态时,布局调整和获取用户信息*/
    public void loginIn()
    {
        mNoLoginlayout.setVisibility(View.GONE);
        mScrollView.setVisibility(View.VISIBLE);
        // mIvSetting.setVisibility(View.VISIBLE);
        mViewMsg.setVisibility(View.VISIBLE);
        mScrollView.setPullToRefreshEnabled(true);
        mScrollView.setPullToRefreshOverScrollEnabled(true);
        requestData();
    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
        requestData();
    }

    private void getBankCardInfo()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token",
                UserConfig.getInstance().getLoginToken(mActivity));
        HttpUtil.post(UrlConstants.BANK_CARD_INFO, map,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        showProgressDialog();
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        try {
                            if (CreateCode.AuthentInfo(response)) {
                                JSONObject json = StringUtils
                                        .parseContent(response);
                                if (json.optString("result")
                                        .contains("success")) {

                                    JSONObject jo = new JSONObject(
                                            json.optString("data"));
                                    if ("0".equals(jo.optString("is_bind"))) {
                                        showBindBankDialog();
                                    } else {
                                        mActivity.startActivity(new Intent(
                                                mActivity,
                                                WithdrawActivity.class));
                                    }
                                } else {
                                    ToastUtil.show(
                                            json.getString("description"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish() {
                        hideProgressDialog();
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        ToastUtil.show(R.string.generic_error);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });

    }

    private void showBindBankDialog(){
        ConfirmCancelDialog dialog=new ConfirmCancelDialog(mActivity,"温馨提示","您未绑定银行卡是否前往绑定");
        dialog.setOnClickListener(new ConfirmCancelDialog.onClickListener() {
            @Override
            public void confirmClick() {
                mActivity.startActivity(new Intent(
                        mActivity,
                        BankCardManagerActivity.class));
            }

            @Override
            public void cancelClick() {

            }
        });
        dialog.show();
    }

}
