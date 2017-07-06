package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.config.UrlConstants;
import com.diyou.http.HttpUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.example.encryptionpackages.CreateCode;
import com.lidroid.xutils.util.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class DetaileProblemActivity extends BaseActivity
        implements OnClickListener
{
    private WebView detaileproblem_wv;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detaile_problem);
        initActionBar();
        initView();
        getProblemContent();
    }

    private void initActionBar() {
        tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText("常见问题");
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    private void initView()
    {
        detaileproblem_wv = (WebView) findViewById(R.id.detaileproblem_webView);
        // 打开网页时不调用系统浏览器， 而是在本WebView中显示
        detaileproblem_wv.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.contains("detail")) {
                    tvTitle.setText("问题详情");
                } else {
                    tvTitle.setText("常见问题");
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && detaileproblem_wv.canGoBack())
        {
            detaileproblem_wv.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    private void getProblemContent()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        HttpUtil.post(UrlConstants.ARTICLESLIST, map,
                new JsonHttpResponseHandler()
                {
                    @Override
                    public void onFinish()
                    {
                        hideProgressDialog();
                        super.onFinish();
                    }

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
                                JSONObject json = StringUtils
                                        .parseContent(response);
                                if (json.optString("result")
                                        .contains("success"))
                                {

                                    JSONObject data = json
                                            .getJSONObject("data");
                                    WebSettings ws = detaileproblem_wv
                                            .getSettings();
                                    ws.setJavaScriptEnabled(true);
                                    // ws.setUseWideViewPort(true);
                                    // ws.setLoadWithOverviewMode(true);
                                    detaileproblem_wv
                                            .loadUrl(data.optString("url"));
                                }
                                else
                                {
                                    ToastUtil.show(
                                            json.optString("description"));
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
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        ToastUtil.show("网络请求失败,请稍后在试！");
                    }

                });
    }
}
