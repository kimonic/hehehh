package com.diyou.huifu;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.application.MyApplication;
import com.diyou.base.BaseActivity;
import com.diyou.config.Constants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.StringUtils;
import com.diyou.v5yibao.R;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.annotation.SuppressLint;
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

public class HuiFuPayActivity extends BaseActivity implements OnClickListener
{
    /**
     * 汇付支付
     * 
     * @author harve
     * 
     */
    private WebView huifupay_wv;
    private ProgressBar huifupay_pb;
    private MyApplication application;
    private LinearLayout huifupay_rl;
    private int mFragmentID;
    private String mTender_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        application = new MyApplication();
        String borrow_nid = getIntent().getStringExtra("borrow_nid");
        mTender_id = getIntent().getStringExtra("tender_id");
        String password = getIntent().getStringExtra("password");

        String tender_account = getIntent().getStringExtra("tender_account");
        // String borrow_password =
        // getIntent().getStringExtra("borrow_password");
        setContentView(R.layout.activity_hui_fu_pay);
        mFragmentID = getIntent().getIntExtra("fragmentID", 0);
        initView();
        getPayUrl(borrow_nid, tender_account, password);
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
        titleName.setText("汇付支付");
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

    private void getPayUrl(String borrow_nid, String tender_account,
            String borrow_password)
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("module", "trust");
        map.put("method", "post");
        map.put("borrow_nid", borrow_nid);
        if (mFragmentID == Constants.CREDITORRIGHTSDETAILSACTIVITY)
        {
            map.put("buy_userid", UserConfig.getInstance().getUserId(this));

            map.put("tender_id", mTender_id);
            map.put("q", "change_buy");
        }
        else
        {
            map.put("user_id", UserConfig.getInstance().getUserId(this));

            map.put("q", "tender");
            map.put("tender_account", tender_account);
        }

        if (!StringUtils.isEmpty(borrow_password))
        {
            map.put("borrow_password", borrow_password);
        }

        HttpUtil.get(CreateCode.getUrl(map), new JsonHttpResponseHandler()
        {

            @Override
            public void onStart()
            {

                super.onStart();
            }

            @Override
            public void onFinish()
            {
                super.onFinish();
            }

            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    if (response.getString("result").equals("error"))
                    {
                        showDialog(response.getString("error_remark"));
                    }
                    else
                    {
                        JSONObject data = response.getJSONObject("data");
                        String url = CreateCode
                                .decrypt2HF(data.getString("url"));
                        WebSettings ws = huifupay_wv.getSettings();
                        ws.setAllowFileAccess(true);
                        ws.setSupportZoom(true);
                        ws.setJavaScriptEnabled(true);
                        ws.setUseWideViewPort(true);
                        ws.setLoadWithOverviewMode(true);
                        huifupay_wv.setWebChromeClient(new WebClient());
                        huifupay_wv.setWebViewClient(new myWebClient());

                        huifupay_wv.loadUrl(url);
                    }

                }
                catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                showDialog("网络请求失败,请稍后在试！");
            }

        });

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
                animation = AnimationUtils.loadAnimation(HuiFuPayActivity.this,
                        R.anim.web_animation);
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
                if (mFragmentID == Constants.CREDITORRIGHTSDETAILSACTIVITY)
                {
                    if (application.getTenderActivity() != null)
                    {
                        application.getTenderActivity().finishThis();
                    }
                    if (application.getCreditorRightsDetailsActivity() != null)
                    {
                        application.getCreditorRightsDetailsActivity()
                                .finishThis();
                    }
                    if (application
                            .getListCreditorRightsTransferFragment() != null)
                    {
                        application.getListCreditorRightsTransferFragment()
                                .updateCreditorRightsTransferDate();
                    }

                }
                else if (mFragmentID == Constants.INVESTMENTDETAILSACTIVITY)
                {
                    setResult(Constants.SUCCESS);
                    huifupay_wv.setVisibility(View.GONE);
                }
                showDialog("恭喜您投资成功！");

                return;
            }
            if (url.contains("trust/error"))
            {
                setResult(Constants.ERROR);

                huifupay_wv.setVisibility(View.GONE);
                showDialog("投资失败！");
                return;
            }
            super.onPageStarted(view, url, favicon);
        }

        private void showDialog(String string)
        {
            Builder builder = new Builder(HuiFuPayActivity.this);
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

    private void showDialog(String string)
    {
        Builder builder = new Builder(HuiFuPayActivity.this);
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

}
