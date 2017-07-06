package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONObject;

import com.diyou.application.MyApplication;
import com.diyou.base.BaseActivity;
import com.diyou.config.Constants;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.ShareSDKUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.CalculatorFinancialDialog;
import com.diyou.view.LoadingLayout;
import com.example.encryptionpackages.CreateCode;
import com.lidroid.xutils.util.LogUtils;
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

public class InvestmentDetailsActivity extends BaseActivity
        implements OnClickListener {

    /**
     * 投资详情
     *
     * @author harve
     */
    private LoadingLayout mLoadinglayout;
    private ImageView calculator;// 计算器
    private View titlelayout;
    private Button investment_button;
    private WebView webview;
    private Intent intent = new Intent();
    private String url;
    private String loan_id;// 借款标id
    private String apr;// 利率
    private String period;// 期限
    private boolean webViewStatus = true;
    private String status;//标的状态
    private boolean isFirst = true;
    private String share_url;
    private String share_title;
    private String share_content;
    private String AgreementTitle;
    private String AgreementUrl;
    private int FirstCode = 1;
    private String category_id;
    private String category_type;
    private String status_name;
    private String additional_status;
    private String additional_apr;
    private String progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication.getInstance().setInvestmentDetailsActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investment_details);
        initData();
        initView();
    }

    private void initData() {
        loan_id = getIntent().getStringExtra("loan_id");
        url = getIntent().getStringExtra("loan_url");
        apr = getIntent().getStringExtra("apr");
        category_id = getIntent().getStringExtra("category_id");
        category_type = getIntent().getStringExtra("category_type");
        period = getIntent().getStringExtra("period");
        status = getIntent().getStringExtra("status");
        status_name = getIntent().getStringExtra("status_name");
        progress = getIntent().getStringExtra("progress");
        additional_status = getIntent().getStringExtra("additional_status");
        additional_apr = getIntent().getStringExtra("additional_apr");
        share_url = getIntent().getStringExtra("share_url");
        share_title = getIntent().getStringExtra("share_title");
        share_content = getIntent().getStringExtra("share_content");
    }


    private void initView() {
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
        investment_button.setOnClickListener(this);
        if (isFlow()) {
            investment_button.setText(R.string.investment_flow_buy);
        } else if ("4".equals(status) || "6".equals(status) || "7".equals(status)) {
            investment_button.setText(status_name);
        }

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
                    intent.setClass(InvestmentDetailsActivity.this, IAgreementActivity.class);
                    startActivityForResult(intent, Constants.I_AAGGREMENT_CODE);
                }
                if (FirstCode == Constants.I_AAGGREMENT_CODE) {
                    mLoadinglayout.setVisibility(View.GONE);
                } else {
                    mLoadinglayout.setVisibility(View.VISIBLE);
                }
                investment_button.setEnabled(false);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (webViewStatus) {
                    mLoadinglayout.setVisibility(View.GONE);
                    webview.setVisibility(View.VISIBLE);
                    mLoadinglayout.setOnStopLoading(
                            InvestmentDetailsActivity.this, webview);
                } else {
                    mLoadinglayout.setVisibility(View.VISIBLE);
                    webview.setVisibility(View.GONE);
                }
                //流转标 status=3、4、5 进度是100 按钮不可用
                if (("3".equals(status)||"4".equals(status)||"5".equals(status)) && Double.valueOf(progress) >= 100) {
                    investment_button.setEnabled(false);
                } else {
                    investment_button.setEnabled("3".equals(status));//只有借款中的标才可以投
                }

            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                webViewStatus = false;
                mLoadinglayout.setOnLoadingError(InvestmentDetailsActivity.this,
                        webview);
                ToastUtil.show("加载失败");
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });

        webview.loadUrl(url);

    }

    @Override
    protected void onDestroy() {
        MyApplication.getInstance().setInvestmentDetailsActivity(null);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_share:
                if (!StringUtils.isEmpty(url)) {
                    ShareSDKUtil.getInstance(this).share(share_title, share_content, url);
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
        CalculatorFinancialDialog mdialog = new CalculatorFinancialDialog(this,
                apr, period);
        mdialog.show();
    }

    private void switchAuthentication() {

        if (StringUtils.isEmpty2(UserConfig.getInstance()
                .getLoginToken(InvestmentDetailsActivity.this))) {
            intent.setClass(InvestmentDetailsActivity.this,
                    BeforeLoginActivity.class);
            startActivity(intent);
            ToastUtil.show(R.string.remind_login);
        } else {
//            getApprove();
            requestYibaoRegistered(new Runnable() {

                @Override
                public void run() {
                    IsMyLoan();
                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
        if (resultCode == Constants.LOGINSUCCESS) {

        }
        if (requestCode == Constants.I_AAGGREMENT_CODE && resultCode == RESULT_OK) {
            FirstCode = Constants.I_AAGGREMENT_CODE;
            webview.loadUrl(url);
        }
        super.onActivityResult(requestCode, resultCode, arg2);
    }

    /**
     * 是否流转标
     *
     * @return
     */
    private boolean isFlow() {
        return "3".equals(category_type);
    }

    private void IsMyLoan() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance()
                .getLoginToken(InvestmentDetailsActivity.this));
        map.put("loan_id", loan_id);
        HttpUtil.post(UrlConstants.IS_MY_LOAN, map,
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
                                intent.putExtra("loan_id", loan_id);
                                intent.putExtra("additional_status", additional_status);
                                intent.putExtra("additional_apr", additional_apr);
                                if (data.get("result").equals("success")) {

                                    if (data.optJSONObject("data")
                                            .optString("is_myloan").equals("1")) {
                                        ToastUtil.show("不能投自己的标");
                                        return;
                                    } else if (isFlow()) {//流转标
                                        intent.setClass(
                                                InvestmentDetailsActivity.this,
                                                FlowBidActivity.class);
                                        startActivity(intent);

                                    } else {
                                        intent.setClass(
                                                InvestmentDetailsActivity.this,
                                                BidActivity.class);
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


}
