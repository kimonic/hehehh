package com.diyou.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.diyou.base.BaseActivity;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.LoadingLayout;

/**
 * Created by Administrator on 2015-10-16.
 */
public class CAgreementActivity extends BaseActivity implements View.OnClickListener {
    private String AgreementUrl;
    private String AgreementTitle;
    private LoadingLayout mLoadinglayout;
    private WebView webview;
    private boolean webViewStatus = true;
    private View titlelayout;
    private TextView titleName;
    private ImageView title_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iaggrement);
        AgreementUrl = getIntent().getStringExtra("AgreementUrl");
        AgreementTitle = getIntent().getStringExtra("AgreementTitle");
        intiView();
    }

    private void intiView() {
        titlelayout = findViewById(R.id.activity_investment_title);
        titlelayout.findViewById(R.id.title_back).setOnClickListener(this);
        titleName = (TextView) titlelayout.findViewById(R.id.title_name);
        titleName.setText(AgreementTitle);
        title_share = (ImageView) titlelayout.findViewById(R.id.title_share);
        title_share.setVisibility(View.GONE);
        mLoadinglayout = (LoadingLayout) findViewById(
                R.id.IAgreementActivity_loadinglayout);
        mLoadinglayout.getReloadingTextView()
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        webViewStatus = true;
                        mLoadinglayout.setVisibility(View.VISIBLE);
                        webview.loadUrl(AgreementUrl);
                    }
                });

        webview = (WebView) findViewById(R.id.IAgreementActivity_webview);
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

                mLoadinglayout.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (webViewStatus) {
                    mLoadinglayout.setVisibility(View.GONE);
                    webview.setVisibility(View.VISIBLE);
                    mLoadinglayout.setOnStopLoading(
                            CAgreementActivity.this, webview);
                } else {
                    mLoadinglayout.setVisibility(View.VISIBLE);
                    webview.setVisibility(View.GONE);
                }

            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                webViewStatus = false;
                mLoadinglayout.setOnLoadingError(CAgreementActivity.this,
                        webview);
                ToastUtil.show(R.string.remind_load_fail);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });

        webview.loadUrl(AgreementUrl);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
}
