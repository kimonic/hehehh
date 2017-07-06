package com.diyou.huifu;

import com.diyou.base.BaseActivity;
import com.diyou.v5yibao.R;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HuiFuTopUpActivity extends BaseActivity implements OnClickListener
{

    /**
     * 汇付充值 充值
     * 
     * @author harve
     * 
     */
    private WebView huifupay_wv;
    private ProgressBar huifupay_pb;
    private LinearLayout huifupay_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hui_fu_pay);
        initView();
        initData();
    }

    private void initData()
    {
        WebSettings ws = huifupay_wv.getSettings();
        ws.setAllowFileAccess(true);
        ws.setSupportZoom(true);
        ws.setJavaScriptEnabled(true);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        huifupay_wv.setWebChromeClient(new WebClient());
        huifupay_wv.setWebViewClient(new myWebClient());
        huifupay_wv.loadUrl(getIntent().getStringExtra("url"));
    }

    private void initView()
    {
        huifupay_wv = (WebView) findViewById(R.id.huifupay_wv);
        huifupay_pb = (ProgressBar) findViewById(R.id.huifupay_pb);
        huifupay_rl = (LinearLayout) findViewById(R.id.huifupay_rl);
        View titlelayout = findViewById(R.id.huifupay_titlelayout);
        titlelayout.findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleName = (TextView) titlelayout
                .findViewById(R.id.title_name);
        titleName.setText("汇付充值");
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

    @Override
    protected void onDestroy()
    {
        huifupay_wv.clearCache(true);
        huifupay_wv.clearHistory();
        huifupay_rl.removeView(huifupay_wv);
        huifupay_wv.destroy();
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().removeSessionCookie();
        super.onDestroy();
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
            huifupay_pb.setMax(100);
            if (newProgress < 100)
            {
                huifupay_pb.setVisibility(View.VISIBLE);
                huifupay_pb.setProgress(newProgress);
            }
            else
            {
                huifupay_pb.setProgress(100);
                animation = AnimationUtils.loadAnimation(
                        HuiFuTopUpActivity.this, R.anim.web_animation);
                // 运行动画 animation
                huifupay_pb.startAnimation(animation);
                // 将 spinner 的可见性设置为不可见状态
                huifupay_pb.setVisibility(View.GONE);
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
            if (url.contains("trust/success"))
            {
                huifupay_wv.setVisibility(View.GONE);
                showDialog("恭喜您充值成功！");
                return;
            }
            if (url.contains("trust/error"))
            {
                huifupay_wv.setVisibility(View.GONE);
                showDialog("充值失败！");
                return;
            }
            super.onPageStarted(view, url, favicon);
        }

        private void showDialog(String string)
        {
            Builder builder = new Builder(HuiFuTopUpActivity.this);
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

}
