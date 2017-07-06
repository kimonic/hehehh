package com.diyou.huifu;

import com.diyou.base.BaseActivity;
import com.diyou.v5yibao.R;
import com.diyou.view.ProgressDialogBar;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class HuifuAuthenticationActivity extends BaseActivity
        implements OnClickListener
{
    private ProgressDialogBar progressBar;
    private WebView authentication_wv;
    private ProgressBar authentication_pb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huifu_authentication);
        initView();
        initData();
    }

    private void initData()
    {
        String url = getIntent().getStringExtra("url");
        authentication_wv.setWebViewClient(new myWebClient());
        WebSettings ws = authentication_wv.getSettings();
        ws.setAllowFileAccess(true);
        ws.setSupportZoom(true);
        ws.setJavaScriptEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setBuiltInZoomControls(true);
        authentication_wv.setWebChromeClient(new WebClient());
        authentication_wv.loadUrl(url);

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
            if (url.contains("success"))
            {
                authentication_wv.setVisibility(View.GONE);
                showDialog("汇付注册成功");
                // startActivity(new
                // Intent(HuifuAuthenticationActivity.this,RegisterSuccessActivity.class));
                // finish();
                return;
            }
            if (url.contains("error"))
            {
                authentication_wv.setVisibility(View.GONE);
                showDialog("汇付注册失败");
                return;
            }
            super.onPageStarted(view, url, favicon);
        }

        private void showDialog(String string)
        {
            Builder builder = new Builder(HuifuAuthenticationActivity.this);
            builder.setTitle("");
            builder.setMessage(string);
            builder.setPositiveButton(android.R.string.ok,
                    new AlertDialog.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                            finish();
                        }
                    });
            builder.setCancelable(false);
            builder.create();
            builder.show();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            return super.shouldOverrideUrlLoading(view, url);
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
            authentication_pb.setMax(100);
            if (newProgress < 100)
            {
                authentication_pb.setProgress(newProgress);
            }
            else
            {
                authentication_pb.setProgress(100);
                animation = AnimationUtils.loadAnimation(
                        HuifuAuthenticationActivity.this, R.anim.web_animation);
                // 运行动画 animation
                authentication_pb.startAnimation(animation);
                // 将 spinner 的可见性设置为不可见状态
                authentication_pb.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    @Override
    protected void onDestroy()
    {
        authentication_wv.clearCache(true);
        authentication_wv.clearHistory();
        authentication_wv.destroy();
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().removeSessionCookie();
        super.onDestroy();
    }

    private void initView()
    {
        authentication_wv = (WebView) findViewById(R.id.authentication_wv);
        authentication_pb = (ProgressBar) findViewById(R.id.authentication_pb);
        findViewById(R.id.authentication_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.authentication_back:
            finish();
            break;

        default:
            break;
        }
    }

}
