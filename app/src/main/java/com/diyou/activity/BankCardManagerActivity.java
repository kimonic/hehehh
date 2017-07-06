package com.diyou.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.adapter.BankCardAdapter;
import com.diyou.application.MyApplication;
import com.diyou.base.BaseActivity;
import com.diyou.bean.BankCardInfo;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.pulltorefresh.PullToRefreshBase;
import com.diyou.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.diyou.pulltorefresh.PullToRefreshListView;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.util.Utils;
import com.diyou.v5yibao.R;
import com.diyou.view.ConfirmCancelDialog;
import com.diyou.view.ConfirmCancelDialog.onClickListener;
import com.diyou.view.LoadingLayout;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class BankCardManagerActivity extends BaseActivity
        implements OnClickListener, OnRefreshListener<ListView>, AdapterView.OnItemClickListener {

    public static final int REQUEST_CODE_BANK = 1;
    private boolean isFirst = true;
    private PullToRefreshListView mListView;
    private LoadingLayout mLoadingLayout;
    private List<BankCardInfo> mCardList = new ArrayList <BankCardInfo>();
    private BankCardAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().setBankCardManagerActivity(this);
        setContentView(R.layout.activity_bank_card_manager);
        initActionBar();
        initView();
        requestData();
    }

    private void initActionBar() {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.bank_manager_title);
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    private void initView() {
        mListView = (PullToRefreshListView) findViewById(
                R.id.lv_bank_card);
        mListView.setOnRefreshListener(this);
        mListView.setOnItemClickListener(this);

        mAdapter = new BankCardAdapter(this, mCardList);
        mListView.setAdapter(mAdapter);
        mLoadingLayout = (LoadingLayout) findViewById(
                R.id.fund_detail_loadlayout);
        mLoadingLayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestData();
                    }
                });
        mLoadingLayout.setOnStartLoading(mListView);
    }

//    private void setView() {
//        View cardExistView = findViewById(R.id.rl_bank_card_exist);
//        View cardAddView = findViewById(R.id.rl_bank_card_add);
//        TextView tvTip = (TextView) findViewById(R.id.tv_bank_manager_tip);
//        if (mExistCard) {
//
//            cardExistView.setVisibility(View.VISIBLE);
//            cardAddView.setVisibility(View.GONE);
//            cardExistView.setOnClickListener(this);
//            tvTip.setText(R.string.bank_manager_change_tip);
//        } else {
//            cardExistView.setVisibility(View.GONE);
//            cardAddView.setVisibility(View.VISIBLE);
//            tvTip.setVisibility(View.GONE);
//            cardAddView.setOnClickListener(this);
//        }
//    }

    private void requestData() {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        HttpUtil.post(UrlConstants.BANK_CARD_INFO, map,
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
                                    /*
                                     * {"data":{"is_bind":1,"bank_info":
                                     * {"status":"1","bank_nid":"ABC","bankName"
                                     * :"农业银行","city":"103","member_id":"56",
                                     * "bank_id":"0","id":"5",
                                     * "update_time":null,"name":"厦门开户行",
                                     * "province":"102","account":
                                     * "6222***********8668","add_time":
                                     * "1438229300",
                                     * "realname":"李白","member_name":
                                     * "gk18030041411"}},
                                     * "result":"success","description":"",
                                     * "code":200}
                                     */
                                    mCardList.clear();
                                    JSONObject jo = json.optJSONObject("data");
                                    if ("1".equals(jo.optString("is_bind"))) {
                                        JSONArray ja = jo
                                                .optJSONArray("bank_info");
                                        for (int i = 0; i < ja.length(); i++) {
                                            JSONObject j = ja.optJSONObject(i);
                                            BankCardInfo cardInfo=new BankCardInfo(true);
                                            cardInfo.setAccount(j.optString("account"));
                                            cardInfo.setBank_name(j.optString("bank_name"));
                                            cardInfo.setRealname(j.optString("realname"));
                                            cardInfo.setId(j.optString("id"));
                                            mCardList.add(cardInfo);
                                        }
                                    }
                                    //最多绑定2张银行卡
                                    if(mCardList.size()<2) {
                                        BankCardInfo cardInfo = new BankCardInfo(false);
                                        mCardList.add(cardInfo);
                                    }
                                    mAdapter.refreshData();

                                    if (isFirst) {
                                        isFirst = false;
                                        mLoadingLayout.setOnStopLoading(
                                                BankCardManagerActivity.this,
                                                mListView);
                                    }
                                } else {
                                    ToastUtil.show(
                                            json.optString("description"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish() {
                        mListView.onRefreshComplete();
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        if (isFirst) {
                            mLoadingLayout.setOnLoadingError(
                                    BankCardManagerActivity.this, mListView);
                        }
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
//            case R.id.rl_bank_card_exist:
//                ToastUtil.show("托管不提供换卡");
////
//                break;
//            case R.id.rl_bank_card_add:
////            requestRealName();
//
//
//                break;

            default:
                break;
        }
    }



    private void requestRealName() {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        HttpUtil.post(UrlConstants.APPROVE, map, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                ToastUtil.show(R.string.generic_error);
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
                            if (StringUtils.isEmpty(info.optString("phone"))) {
                                showPhoneDialog();
                            } else if (info.optString("realname_status").equals("-2")) // 待审核
                            {
                                startActivity(
                                        new Intent(BankCardManagerActivity.this,
                                                RealNameInfoActivity.class));
                            } else if (info.optString("realname_status")
                                    .equals("1")) // 已认证
                            {
                                if(info.optString("paypassword")
                                        .equals("")){
                                    //跳到设置支付密码页面
                                    startActivity(new Intent(BankCardManagerActivity.this,SettingPaymentActivity.class));
                                }else {
                                    Intent intent = new Intent();
                                    intent.setClass(BankCardManagerActivity.this,
                                            AddBankCardActivity.class);
                                    intent.putExtra("realname",
                                            info.optString("realname"));
                                    intent.putExtra("action",
                                            WithdrawActivity.ACTION_WITHDRAW);
                                    startActivityForResult(intent,
                                            REQUEST_CODE_BANK);
                                }
                            } else {
                                showRealNameDialog();
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
                hideProgressDialog();
                super.onFinish();
            }

            @Override
            public void onStart() {
                showProgressDialog();
                super.onStart();
            }

        });

    }

    private void showPhoneDialog() {
        ConfirmCancelDialog dialog = new ConfirmCancelDialog(this, R.string.dialog_title_bind_phone,
                R.string.dialog_subtitle_bind_phone);
        dialog.setOnClickListener(new onClickListener() {

            @Override
            public void confirmClick() {
                startActivity(new Intent(BankCardManagerActivity.this,
                        PhoneBindActivity.class));
                finish();
            }

            @Override
            public void cancelClick() {

            }
        });
        dialog.show();
    }

    private void showRealNameDialog() {
        ConfirmCancelDialog dialog = new ConfirmCancelDialog(this, "实名认证",
                "您还未实名认证，是否现在认证?");
        dialog.setOnClickListener(new onClickListener() {

            @Override
            public void confirmClick() {
                startActivity(new Intent(BankCardManagerActivity.this,
                        RealNameAuthActivity.class));
                finish();
            }

            @Override
            public void cancelClick() {

            }
        });
        dialog.show();
    }

    private void showPayPasswordDialog() {
        ConfirmCancelDialog dialog = new ConfirmCancelDialog(this, "支付密码",
                "您还未设置支付密码，是否现在设置?");
        dialog.setOnClickListener(new onClickListener() {

            @Override
            public void confirmClick() {
                startActivity(new Intent(BankCardManagerActivity.this,
                        SettingPaymentActivity.class));
                finish();
            }

            @Override
            public void cancelClick() {

            }
        });
        dialog.show();
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        requestData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BANK && resultCode == RESULT_OK) {
            finish();
        }
    }
  public void closeBankCardManagerActivity(){
      finish();
  }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final int location = position - 1;
        if (mCardList.get(location).isExist()) {
            ConfirmCancelDialog dialog = new ConfirmCancelDialog(BankCardManagerActivity.this, "解绑银行卡", "是否确认解绑该银行卡?");
            dialog.setOnClickListener(new onClickListener() {
                @Override
                public void confirmClick() {
                    unbindBankCard(location);
                }

                @Override
                public void cancelClick() {

                }
            });
            dialog.show();
        }else{
            requestYibaoRegistered(new Runnable() {

                @Override
                public void run() {
                    startActivity(new Intent(BankCardManagerActivity.this, YiBaoBindBankCardActivity.class));
                }
            });
        }
    }

    private void unbindBankCard(final int position)
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("bank_id", mCardList.get(position).getId());
        map.put("operateType", "unBindBankCard");
        HttpUtil.post(UrlConstants.YIBAO_UNBIND_CARD, map,
                new JsonHttpResponseHandler()
                {
                    @Override
                    public void onStart()
                    {
                        showProgressDialog();
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response)
                    {
                        try
                        {
                            if (CreateCode.AuthentInfo(response))
                            {
                                JSONObject json = StringUtils
                                        .parseContent(response);
                                if (json.optString("result")
                                        .contains("success")) {
                                    ToastUtil.show(
                                            json.optString("description"));
//                                            mCardList.remove(position);
//                                    mAdapter.refreshData();
                                    requestData();
                                }
                                else
                                {
                                    ToastUtil.show(
                                            json.optString("description"));
                                }
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish()
                    {
                        hideProgressDialog();
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse)
                    {
                        ToastUtil.show(R.string.generic_error);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });

    }
}
