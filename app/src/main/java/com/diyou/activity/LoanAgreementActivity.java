package com.diyou.activity;

import com.diyou.base.BaseActivity;
import com.diyou.config.Constants;
import com.diyou.v5yibao.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoanAgreementActivity extends BaseActivity
        implements OnClickListener
{

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_agreement);
        initView();
    }

    private void initView()
    {
        progressBar = (ProgressBar) findViewById(R.id.loanagreementactivity_pb);

        View titlelayout = findViewById(R.id.loanagreementactivity_titlelayout);
        titlelayout.findViewById(R.id.title_back).setOnClickListener(this);
        TextView title_name = (TextView) titlelayout
                .findViewById(R.id.title_name);
        title_name.setText("个人借款协议");
        WebView webView = (WebView) findViewById(
                R.id.loanagreementactivity_webView);
        webView.setWebChromeClient(new WebClient());
        webView.loadUrl(Constants.LOANAGREEMENTURL);
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
            progressBar.setMax(100);
            if (newProgress < 100)
            {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
            else
            {
                progressBar.setProgress(100);
                animation = AnimationUtils.loadAnimation(
                        LoanAgreementActivity.this, R.anim.web_animation);
                // 运行动画 animation
                progressBar.startAnimation(animation);
                // 将 spinner 的可见性设置为不可见状态
                progressBar.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

}
