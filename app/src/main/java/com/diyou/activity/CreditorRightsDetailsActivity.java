package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONObject;

import com.diyou.application.MyApplication;
import com.diyou.base.BaseActivity;
import com.diyou.bean.CreditorRightsTransferBean;
import com.diyou.config.Constants;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.ShareSDKUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.CalculatorIncomeDialog;
import com.diyou.view.LoadingLayout;
import com.diyou.view.ProgressDialogBar;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CreditorRightsDetailsActivity extends BaseActivity
        implements OnClickListener {

    /**
     * 债权转让详情
     *
     * @author 林佳荣
     */
    private LoadingLayout mLoadinglayout;
    private ImageView calculator;// 计算器
    private View titlelayout;
    private Button investment_button;
    private WebView webview;
    private Intent intent = new Intent();
    private String url;
    private String loan_id;// 借款标id
    private boolean webViewStatus = true;
    private String transfer_id;// 转让id
    private int status;
    protected ProgressDialogBar progressDialogBar;
    private CreditorRightsTransferBean mCreditorInfo;
    private String AgreementTitle;
    private String AgreementUrl;
    private int FirstCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication.getInstance().setCreditorRightsDetailsActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investment_details);
        mCreditorInfo = (CreditorRightsTransferBean) getIntent().getSerializableExtra("creditorInfo");
        transfer_id = mCreditorInfo.getTransfer_id();
        loan_id = mCreditorInfo.getBorrow_nid();
        status = mCreditorInfo.getStatus_name();
        url = mCreditorInfo.getTransfer_info_url();
        initView();
    }


    private void initView() {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.creditor_dateils_title);
        mLoadinglayout = (LoadingLayout) findViewById(
                R.id.investmentdetailsactivity_loadinglayout);
        mLoadinglayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        webViewStatus = true;
                        mLoadinglayout.setVisibility(View.VISIBLE);
                        webview.loadUrl(url);
                    }
                });
        findViewById(R.id.title_share).setOnClickListener(this);
        titlelayout = findViewById(R.id.activity_investment_title);
        titlelayout.findViewById(R.id.title_back).setOnClickListener(this);
        calculator = (ImageView) findViewById(
                R.id.investment_datails_calculator);
        investment_button = (Button) findViewById(R.id.investment_button);
        if (status == 2) {//转让成功
            investment_button.setText(R.string.creditor_transfer_success);
        } else {
            investment_button.setText(R.string.creditor_bug_now);
        }
        investment_button.setOnClickListener(this);

        webview = (WebView) findViewById(R.id.investment_webview);
        calculator.setOnClickListener(this);
        WebSettings ws = webview.getSettings();
        ws.setAllowFileAccess(true);
        ws.setSupportZoom(true);
        ws.setJavaScriptEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setBuiltInZoomControls(true);
        // 从自己的浏览器打开网页
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.equals(AgreementUrl)) {
                    Intent intent = new Intent();
                    intent.putExtra("AgreementUrl", AgreementUrl);
                    intent.putExtra("AgreementTitle", AgreementTitle);
                    intent.setClass(CreditorRightsDetailsActivity.this, CAgreementActivity.class);
                    startActivityForResult(intent, Constants.I_CAGGREMENT_CODE);
                    return;
                }
                if (FirstCode == Constants.I_AAGGREMENT_CODE) {
                    mLoadinglayout.setVisibility(View.GONE);
                } else {
                    mLoadinglayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (webViewStatus) {
                    mLoadinglayout.setVisibility(View.GONE);
                    webview.setVisibility(View.VISIBLE);
                    mLoadinglayout.setOnStopLoading(
                            CreditorRightsDetailsActivity.this, webview);
                } else {
                    mLoadinglayout.setVisibility(View.VISIBLE);
                    webview.setVisibility(View.GONE);
                }
                if (status == 1) {
                    investment_button.setEnabled(true);
                } else {
                    investment_button.setEnabled(false);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                webViewStatus = false;
                mLoadinglayout.setOnLoadingError(
                        CreditorRightsDetailsActivity.this, webview);
                ToastUtil.show("加载失败");
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });

        webview.loadUrl(url);

    }

    @Override
    protected void onDestroy() {
        MyApplication.getInstance().setCreditorRightsDetailsActivity(null);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_share:
                if (!StringUtils.isEmpty(mCreditorInfo.getShare_url())) {
                    ShareSDKUtil.getInstance(this).share(mCreditorInfo.getShare_title(),
                            mCreditorInfo.getShare_content(), mCreditorInfo.getShare_url());
                } else {
                    ToastUtil.show(R.string.remind_loading);
                }
                break;
            case R.id.investment_datails_calculator:
                showDialog();
                break;
            case R.id.investment_button:
                switchAuthentication();
                break;
        }
    }



    private void showDialog() {
        CalculatorIncomeDialog mdialog = new CalculatorIncomeDialog(this, "",
                "");
        mdialog.show();
    }

    private void switchAuthentication() {

        if (StringUtils.isEmpty2(UserConfig.getInstance()
                .getLoginToken(CreditorRightsDetailsActivity.this))) {
            intent.setClass(CreditorRightsDetailsActivity.this,
                    BeforeLoginActivity.class);
            startActivity(intent);
            ToastUtil.show("请先登录");
        } else {
            requestYibaoRegistered(new Runnable() {

                @Override
                public void run() {
                    isMyTransfer();
                }
            });

        }
    }

    private void isMyTransfer() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance()
                .getLoginToken(this));
        map.put("transfer_id", transfer_id);
        HttpUtil.post(UrlConstants.IS_MY_TRANSFER, map,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        ToastUtil.show("访问失败！");
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        try {
                            if (CreateCode.AuthentInfo(response)) {
                                JSONObject data = StringUtils
                                        .parseContent(response);
                                if (data.get("result").equals("success")) {

                                    if (data.optJSONObject("data")
                                            .optString("is_myloan").equals("1")) {
                                        ToastUtil.show("不能购买自己的债权");
                                        return;
                                    }else {
                                        intent.setClass(CreditorRightsDetailsActivity.this,
                                                BuyCreditorActivity.class);
                                        intent.putExtra("transfer_id", transfer_id);
                                        intent.putExtra("loan_id", loan_id);
                                        startActivity(intent);
                                    }

                                } else {
                                    ToastUtil.show(
                                            data.optString("description"));
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
                        showProgressDialog(getString(R.string.remind_dialog_connect));
                        super.onStart();
                    }

                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
        if (requestCode == Constants.LOGINSUCCESS) {

        }
        if (requestCode == Constants.I_CAGGREMENT_CODE && resultCode == RESULT_OK) {
            FirstCode = Constants.I_AAGGREMENT_CODE;
            webview.loadUrl(url);
        }
        super.onActivityResult(requestCode, resultCode, arg2);
    }

    public void finishThis() {
        this.finish();
    }


}
