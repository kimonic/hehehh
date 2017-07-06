package com.diyou.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.diyou.base.BaseActivity;
import com.diyou.util.StringUtils;
import com.diyou.v5yibao.R;

public class AdvertisingActivity extends BaseActivity implements OnClickListener
{
    private ProgressBar pb;
    private TextView mTvTitle;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertising);
        initActionBar();
        action = getIntent().getStringExtra("action");
        if("newForecast".equals(action)){
            mTvTitle.setText(R.string.ad_title_new_forecast);
        }else if("creditorAgreement".equals(action)){
            mTvTitle.setText(R.string.common_creditor_transfer_agreement_text);
        }else if("borrowAgreement".equals(action)){
            mTvTitle.setText(R.string.common_loan_agreement_text);
        }else if("websiteAgreement".equals(action)){
            mTvTitle.setText(R.string.common_web_agreement_text);
        }
        initView();
    }

    private void initView()
    {
        pb = (ProgressBar) findViewById(R.id.advertsing_pb);
        WebView webView = (WebView) findViewById(R.id.advertsing_wv);
        WebSettings ws = webView.getSettings();
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);
        ws.setUseWideViewPort(true);
        ws.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new MyWebClient());
        webView.setWebViewClient(new WebViewClient()  
        {  
            @Override  
            public boolean shouldOverrideUrlLoading(WebView view, String url) {  
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;  
            }  
        });  
        webView.loadUrl(getIntent().getStringExtra("url"));
    }

    private void initActionBar()
    {
        mTvTitle = (TextView) findViewById(R.id.title_name);
        findViewById(R.id.title_back).setOnClickListener(this);
    }
    class MyWebClient extends WebChromeClient
    {
        private Animation animation;

        @Override
        public void onProgressChanged(WebView view, int newProgress)
        {
            pb.setMax(100);
            if (newProgress < 100)
            {
                pb.setProgress(newProgress);
            }
            else
            {
                pb.setProgress(100);
                animation = AnimationUtils.loadAnimation(
                        AdvertisingActivity.this, R.anim.web_animation);
                // 运行动画 animation
                pb.startAnimation(animation);
                // 将 spinner 的可见性设置为不可见状态
                pb.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (StringUtils.isEmpty(action)) {
                mTvTitle.setText(title);
            }
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
