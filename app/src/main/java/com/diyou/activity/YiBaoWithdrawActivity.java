package com.diyou.activity;

import com.diyou.base.BaseActivity;
import com.diyou.config.Constants;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class YiBaoWithdrawActivity extends BaseActivity
        implements OnClickListener
{

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private String url;
    private String body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yi_bao_register);
        initActionBar();
        url = getIntent().getStringExtra("url");
        body = getIntent().getStringExtra("body");
        initView();

    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.yibao_withdraw_title);
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    private void initView()
    {
        mWebView = (WebView) findViewById(R.id.wb_yibao_register);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_yibao_register);
        mWebView.setWebViewClient(
                new myWebClient());
        WebSettings ws = mWebView.getSettings();
        ws.setAllowFileAccess(true);
        ws.setSupportZoom(true);
        ws.setJavaScriptEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(
                true);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setBuiltInZoomControls(true);
        mWebView.setWebChromeClient(
                new WebClient());
        mWebView.postUrl(url, body.getBytes());
    }

    private class WebClient extends WebChromeClient
    {

        private Animation animation;

        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                JsResult result)
        {
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress)
        {
            mProgressBar.setMax(100);
            if (newProgress < 100)
            {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(newProgress);
            }
            else
            {
                mProgressBar.setProgress(100);
                animation = AnimationUtils.loadAnimation(
                        YiBaoWithdrawActivity.this, R.anim.web_animation);
                // 运行动画 animation
                mProgressBar.startAnimation(animation);
                // 将 spinner 的可见性设置为不可见状态
                mProgressBar.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    private class myWebClient extends WebViewClient
    {
        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            if(url.contains(Constants.STATUS_WITHDRAW_SUCCESS)){
                ToastUtil.show(R.string.remind_withdraw_succeed);
                startActivity(new Intent(YiBaoWithdrawActivity.this, WithdrawRecordActivity.class));
                finish();
            } else if (url.contains(Constants.STATUS_WITHDRAW_FAIL)) {
                ToastUtil.show(R.string.remind_withdraw_fail);
                finish();
            } else if(url.contains(Constants.STATUS_CLOSE)){
                finish();
            }

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            return super.shouldOverrideUrlLoading(view, url);
        }

    }

    @Override
    public void onBackPressed() {

        if(mWebView.canGoBack()){
            mWebView.goBack();
        }else{
            super.onBackPressed();
        }

    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.title_back:
            finish();
            break;

        default:
            break;
        }
    }

}
