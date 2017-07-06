package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.config.Constants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.SharedPreUtils;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.CircleImageView;
import com.example.encryptionpackages.AESencrypt;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ExitLoginActivity extends BaseActivity implements OnClickListener
{

    private TextView activity_exit_login_name_tv;
    private TextView activity_exit_login_accoun_name_tv;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    private CircleImageView usericon;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit_login);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_2)
                .showImageOnFail(R.drawable.default_2)
                .resetViewBeforeLoading(true).cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        initView();
        initData();
    }

    private void initData()
    {
        requestData();
        Intent intent = getIntent();
        activity_exit_login_name_tv.setText(intent.getStringExtra("name"));
        activity_exit_login_accoun_name_tv
                .setText(intent.getStringExtra("account_name"));
    }

    private void initView()
    {
        usericon = (CircleImageView) findViewById(
                R.id.activity_exit_login_usericon);
        findViewById(R.id.activity_exit_login_sumit_btn)
                .setOnClickListener(this);
        findViewById(R.id.activity_exit_login_title_layout)
                .findViewById(R.id.title_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.activity_exit_login_title_layout)
                .findViewById(R.id.title_name)).setText("");

        activity_exit_login_name_tv = (TextView) findViewById(
                R.id.activity_exit_login_name_tv);
        activity_exit_login_accoun_name_tv = (TextView) findViewById(
                R.id.activity_exit_login_accoun_name_tv);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.title_back:
            setResult(Constants.EXIT_LOGIN_CODE_BACK);
            finish();
            break;
        case R.id.activity_exit_login_sumit_btn:
            SharedPreUtils.putString(Constants.SHARE_USERKEY,
                    AESencrypt.encrypt2PHP(CreateCode.getSEND_AES_KEY(), ""),
                    this);
            SharedPreUtils.putString(Constants.SHARE_USERID,
                    AESencrypt.encrypt2PHP(CreateCode.getSEND_AES_KEY(), ""),
                    this);
            UserConfig.getInstance().clear();
            setResult(Constants.EXIT_LOGIN_CODE);

            finish();
            break;
        }
    }

    private void requestData()
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("module", "dyp2p");
        map.put("q", "get_users");
        map.put("method", "get");
        map.put("user_id", UserConfig.getInstance().getUserId(this));
        HttpUtil.get(CreateCode.getUrl(map), new JsonHttpResponseHandler()
        {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONArray errorResponse)
            {
                ToastUtil.show(R.string.generic_error);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    JSONObject user_result = response
                            .getJSONObject("user_result");

                    if (!StringUtils
                            .isEmpty(user_result.getString("avatar_url")))
                    {
                        String url = user_result.getString("avatar_url");
                        imageLoader.displayImage(Constants.IMAGEURL + url,
                                usericon, options);
                    }

                }
                catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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

}
