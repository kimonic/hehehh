package com.diyou.huifu;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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

public class HuifuBindBankCardActivity extends BaseActivity
        implements OnClickListener
{

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huifu_bind_bank_card);
        initView();
        initData();
    }

    private void initData()
    {
        requestData();
    }

    private void requestData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("module", "trust");
        map.put("q", "bindbank");
        map.put("method", "post");
        map.put("user_id", UserConfig.getInstance().getUserId(this));
        HttpUtil.post(CreateCode.getUrl(map), new JsonHttpResponseHandler()
        {
            @Override
            public void onFailure(int statusCode, Header[] headers,
                    String responseString, Throwable throwable)
            {
                ToastUtil.show(responseString);
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONArray errorResponse)
            {
                ToastUtil.show(errorResponse.toString());
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                ToastUtil.show(errorResponse.toString());
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    if ("success".equals(response.getString("result")))
                    {
                        String url = CreateCode.decrypt2HF(response
                                .getJSONObject("data").getString("form"));
                        webView.setWebViewClient(new myWebClient());
                        WebSettings ws = webView.getSettings();
                        ws.setAllowFileAccess(true);
                        ws.setSupportZoom(true);
                        ws.setJavaScriptEnabled(true);
                        ws.setJavaScriptCanOpenWindowsAutomatically(true);
                        ws.setUseWideViewPort(true);
                        ws.setLoadWithOverviewMode(true);
                        ws.setBuiltInZoomControls(true);
                        webView.setWebChromeClient(new WebClient());
                        webView.loadUrl(url);
                    }
                    else
                    {
                        ToastUtil.show(response.getString("error_remark"));
                        finish();
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
                super.onFinish();
            }

            @Override
            public void onStart()
            {
                super.onStart();
            }
        });
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
                webView.setVisibility(View.GONE);
                showDialog("添加成功");
                return;
            }
            super.onPageStarted(view, url, favicon);
        }

        private void showDialog(String string)
        {
            Builder builder = new Builder(HuifuBindBankCardActivity.this);
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
                        HuifuBindBankCardActivity.this, R.anim.web_animation);
                // 运行动画 animation
                progressBar.startAnimation(animation);
                // 将 spinner 的可见性设置为不可见状态
                progressBar.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    private void initView()
    {
        findViewById(R.id.activity_huifu_bind_bank_card_title_layout)
                .findViewById(R.id.title_back).setOnClickListener(this);
        ((TextView) findViewById(
                R.id.activity_huifu_bind_bank_card_title_layout)
                        .findViewById(R.id.title_name)).setText(
                                R.string.title_activity_huifu_bind_bank_card);
        webView = (WebView) findViewById(R.id.activity_huifu_bind_bank_card_wv);
        progressBar = (ProgressBar) findViewById(
                R.id.activity_huifu_bind_bank_card_pb);
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
