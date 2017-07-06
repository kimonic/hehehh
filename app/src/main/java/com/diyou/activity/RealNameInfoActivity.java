package com.diyou.activity;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.ImageUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.LoadingLayout;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class RealNameInfoActivity extends BaseActivity
        implements OnClickListener
{

    private LoadingLayout mLoadingLayout;
    private TextView mTvName;
    private TextView mTvCardID;
    private ImageView mIvZheng;
    private ImageView mIvFang;
    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private TextView mTvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_name_info);
        initActionBar();
        initView();
        requestData();
    }

    private void requestData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        HttpUtil.post(UrlConstants.APPROVE, map, new JsonHttpResponseHandler()
        {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse)
            {
                mLoadingLayout.setOnLoadingError(RealNameInfoActivity.this,
                        null);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response)
            {
                try
                {
                    if (CreateCode.AuthentInfo(response))
                    {
                        JSONObject data = StringUtils.parseContent(response);
                        if (data.optString("result").equals("success"))
                        {
                            JSONObject info = data.getJSONObject("data");
                            mTvName.setText(info.optString("realname"));
                            mTvCardID.setText(StringUtils
                                    .getHideCardID(info.optString("card_id")));
                            mImageLoader.displayImage(
                                    info.optString("positive_card"), mIvZheng,
                                    ImageUtil.getImageOptions());
                            mImageLoader.displayImage(
                                    info.optString("back_card"), mIvFang,
                                    ImageUtil.getImageOptions());

                            if (info.optString("realname_status").equals("-2"))
                            {
                                mTvStatus.setText(R.string.real_name_wait_auth);
                            }
                            else if (info.optString("realname_status")
                                    .equals("1"))
                            {
                                mTvStatus.setText(R.string.real_name_pass_auth);
                            }
                            mLoadingLayout.setOnStopLoading(
                                    RealNameInfoActivity.this, null);
                        }
                        else
                        {
                            ToastUtil.show(data.optString("description"));
                        }
                    }
                }
                catch (Exception e)
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

    private void initView()
    {
        mTvName = (TextView) findViewById(R.id.tv_realname_info_name);
        mTvStatus = (TextView) findViewById(R.id.tv_real_name_status);
        mTvCardID = (TextView) findViewById(R.id.tv_realname_info_cardid);
        mIvZheng = (ImageView) findViewById(R.id.iv_real_name_photo_zheng);
        mIvFang = (ImageView) findViewById(R.id.iv_real_name_photo_fang);

        mLoadingLayout = (LoadingLayout) findViewById(
                R.id.account_detail_loadlayout);
        mLoadingLayout.getReloadingTextView()
                .setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        requestData();
                    }
                });
        mLoadingLayout.setOnStartLoading(null);
    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.real_name_title);
        findViewById(R.id.title_back).setOnClickListener(this);
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
        mImageLoader.cancelDisplayTask(mIvZheng);
        mImageLoader.cancelDisplayTask(mIvFang);
        super.onDestroy();
    }
}
