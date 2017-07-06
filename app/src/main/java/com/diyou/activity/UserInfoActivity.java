package com.diyou.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.diyou.base.BaseActivity;
import com.diyou.bean.EventObject;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.FileUtil;
import com.diyou.util.ImageUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.CircleImageView;
import com.diyou.view.LoadingLayout;
import com.diyou.view.PhotoDialog;
import com.example.encryptionpackages.CreateCode;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.TreeMap;

import de.greenrobot.event.EventBus;

public class UserInfoActivity extends BaseActivity implements OnClickListener
{

    private LoadingLayout mLoadingLayout;
    private TextView mTvCreditPoint;
    private TextView mTvMemberName;
    private ImageView mIvCreditLevel;
    private CircleImageView mCivAvatar;
    private DisplayImageOptions options;
    private File mAvatarFile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initActionBar();
        initView();
        initData();
        requestData();
    }

    private void initData()
    {
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_2)
                .showImageOnFail(R.drawable.default_2)
                .resetViewBeforeLoading(true).cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300)).build();
    }
    private void initView()
    {
        mLoadingLayout = (LoadingLayout) findViewById(
                R.id.user_info_loadlayout);
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

        mTvCreditPoint = (TextView) findViewById(
                R.id.tv_user_info_credit_point);
        mTvMemberName = (TextView) findViewById(R.id.tv_user_info_member_name);
        mIvCreditLevel = (ImageView) findViewById(
                R.id.iv_user_info_credit_level);
        mCivAvatar = (CircleImageView) findViewById(R.id.civ_user_info_avatar);

        findViewById(R.id.rl_user_personal_info).setOnClickListener(this);
        findViewById(R.id.rl_user_company_info).setOnClickListener(this);
        findViewById(R.id.rl_user_funds_info).setOnClickListener(this);
        mCivAvatar.setOnClickListener(this);
    }

    private void requestData()
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        HttpUtil.post(UrlConstants.USER_INFO, map, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                try {
                    if (CreateCode.AuthentInfo(response)) {
                        JSONObject json = StringUtils.parseContent(response);
                        if (json.optString("result").contains("success")) {
                            JSONObject jo = new JSONObject(
                                    json.optString("data"));
                            mTvMemberName.setText(jo.optString("member_name"));
                            mTvCreditPoint
                                    .setText(jo.optString("credit_point"));
                            String creditLevel = jo.optString("credit_name");
                            if ("HR".equals(creditLevel)) {
                                mIvCreditLevel.setImageResource(
                                        R.drawable.user_info_credit_hr);
                            } else if ("AA".equals(creditLevel)) {
                                mIvCreditLevel.setImageResource(
                                        R.drawable.user_info_credit_aa);
                            } else if ("A".equals(creditLevel)) {
                                mIvCreditLevel.setImageResource(
                                        R.drawable.user_info_credit_a);
                            } else if ("B".equals(creditLevel)) {
                                mIvCreditLevel.setImageResource(
                                        R.drawable.user_info_credit_b);
                            } else if ("C".equals(creditLevel)) {
                                mIvCreditLevel.setImageResource(
                                        R.drawable.user_info_credit_c);
                            } else if ("D".equals(creditLevel)) {
                                mIvCreditLevel.setImageResource(
                                        R.drawable.user_info_credit_d);
                            } else if ("E".equals(creditLevel)) {
                                mIvCreditLevel.setImageResource(
                                        R.drawable.user_info_credit_e);
                            }

                            ImageLoader.getInstance().displayImage(
                                    jo.optString("avatar"), mCivAvatar,
                                    options);
                            mLoadingLayout.setOnStopLoading(
                                    UserInfoActivity.this, null);
                        } else {
                            ToastUtil.show(json.optString("description"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                mLoadingLayout.setOnLoadingError(UserInfoActivity.this, null);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.user_info_title);
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == ImageUtil.REQUEST_CODE_PICK_PHOTO
                && resultCode == RESULT_OK)
        {
            mAvatarFile = ImageUtil.getFile(this, data);
            uploadData();
        }
        else if (requestCode == ImageUtil.REQUEST_CODE_TAKE_PHOTO
                && resultCode == RESULT_OK)
        {
            uploadData();
        }
    }

    private void uploadData()
    {
        Bitmap b = ImageUtil.getLocalImage(mAvatarFile, 300, 300);
        ImageUtil.compressImage(b, mAvatarFile, 30);

        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        RequestParams params = new RequestParams();
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        params.addBodyParameter("diyou",
                CreateCode.p2sDiyou(CreateCode.GetJson(map)));
        params.addBodyParameter("xmdy", CreateCode
                .p2sXmdy(CreateCode.p2sDiyou(CreateCode.GetJson(map))));
        params.addBodyParameter("front", mAvatarFile);

        httpUtils.send(HttpRequest.HttpMethod.POST, UrlConstants.YIBAO_AVATAR_SUBMIT, params,
                new RequestCallBack<String>()
                {
                    @Override
                    public void onStart()
                    {
                        showProgressDialog();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1)
                    {
                        ToastUtil.show("数据上传失败，请检查网络");
                        hideProgressDialog();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> response)
                    {
                        LogUtils.e(response.result);
                        try
                        {
                            android.util.Log.e("UserInfoActivity", "[zys-->] response.result="+response.result);
                            JSONObject result = new JSONObject(response.result);
                            if (CreateCode.AuthentInfo(result))
                            {
                                JSONObject json = StringUtils
                                        .parseContent(result);
                                if (json.optString("result")
                                        .contains("success"))
                                {
                                    // JSONObject jo= new
                                    // JSONObject(json.optString("data"));
                                    EventBus.getDefault()
                                            .post(new EventObject("refresh"));
                                    ToastUtil.show("上传成功");

                                    Bitmap b1 = ImageUtil.getLocalImage(mAvatarFile, 300, 200);
                                    mCivAvatar.setImageBitmap(b1);
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
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        hideProgressDialog();
                    }
                });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.title_back:
                finish();
                break;
            case R.id.rl_user_personal_info:
                ToastUtil.show(R.string.common_operate_on_pc);
                break;
            case R.id.rl_user_company_info:
                ToastUtil.show(R.string.common_operate_on_pc);
                break;
            case R.id.rl_user_funds_info:
                ToastUtil.show(R.string.common_operate_on_pc);
                break;
            case R.id.civ_user_info_avatar:
                mAvatarFile = FileUtil.creatImageFile("id_avatar_photo.jpg");
                PhotoDialog dialog = new PhotoDialog(this, mAvatarFile);
                dialog.show();
                break;
            default:
                break;
        }
    }

}
