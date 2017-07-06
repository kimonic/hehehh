package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.bean.EventObject;
import com.diyou.config.Constants;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.util.Utils;
import com.diyou.v5yibao.R;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

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

import de.greenrobot.event.EventBus;

public class YiBaoBuyActivity extends BaseActivity implements OnClickListener
{

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private String mTransferId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yi_bao_register);
        initView();
        initActionBar();
        mTransferId = getIntent().getStringExtra("transfer_id");
        initData();

    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.yibao_bug_title);
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    private void initView()
    {
        mWebView = (WebView) findViewById(R.id.wb_yibao_register);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_yibao_register);
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
                animation = AnimationUtils.loadAnimation(YiBaoBuyActivity.this,
                        R.anim.web_animation);
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
            if(url.contains(Constants.STATUS_BUY_SUCCESS)){
                ToastUtil.show(R.string.remind_buy_succeed);
                startActivity(new Intent(YiBaoBuyActivity.this, MainActivity.class));
                EventBus.getDefault().post(new EventObject("creditorSuccess"));
            }else if(url.contains(Constants.STATUS_BUY_FAIL)){
                ToastUtil.show(R.string.remind_buy_fail);
                finish();
            }else if(url.contains(Constants.STATUS_CLOSE)){
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

    private void initData()
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("transfer_id", mTransferId);
        HttpUtil.post(UrlConstants.YIBAO_BUY_JAVA, map, new JsonHttpResponseHandler()
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
                        JSONObject json = StringUtils.parseContent(response);
                        if (json.optString("result").contains("success"))
                        {
                            JSONObject jo = new JSONObject(
                                    json.optString("data"));
                            String url = jo.optString("submit_url");
                            String body= Utils.handleJson(jo);
                            mWebView.setWebViewClient(new myWebClient());
                            WebSettings ws = mWebView.getSettings();
                            ws.setAllowFileAccess(true);
                            ws.setSupportZoom(true);
                            ws.setJavaScriptEnabled(true);
                            ws.setJavaScriptCanOpenWindowsAutomatically(true);
                            ws.setUseWideViewPort(true);
                            ws.setLoadWithOverviewMode(true);
                            ws.setBuiltInZoomControls(true);
                            mWebView.setWebChromeClient(new WebClient());
                            mWebView.postUrl(url, body.getBytes());
                        }
                        else
                        {
                            ToastUtil.show(json.optString("description"));
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
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

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
