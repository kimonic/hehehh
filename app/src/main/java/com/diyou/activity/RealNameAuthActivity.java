package com.diyou.activity;

import java.io.File;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.bean.EventObject;
import com.diyou.config.Constants;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.util.FileUtil;
import com.diyou.util.ImageUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.PhotoDialog;
import com.example.encryptionpackages.CreateCode;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import de.greenrobot.event.EventBus;

/**
 * 实名认证
 *
 * @author Administrator
 *
 */
public class RealNameAuthActivity extends BaseActivity
        implements OnClickListener
{

    private ImageView mIvPhotoZheng;
    private ImageView mIvCurrent;
    private ImageView mIvPhotoFang;
    private File mFileZheng;
    private File mFileFang;
    private EditText mEtName;
    private EditText mEtId;
    private boolean existZheng = false;
    private boolean existFang = false;
    private String mAuthAction;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_name_auth);
        mAuthAction = getIntent().getStringExtra("authAction");
        initActionBar();
        initView();
        initData();
    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.real_name_title);
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    private void initData()
    {
        mFileZheng = FileUtil.creatImageFile("id_card_zheng.jpg");
        mFileFang = FileUtil.creatImageFile("id_card_fang.jpg");
    }

    private void initView()
    {
        mIvPhotoZheng = (ImageView) findViewById(R.id.iv_real_name_photo_zheng);
        mIvPhotoFang = (ImageView) findViewById(R.id.iv_real_name_photo_fang);
        mIvPhotoZheng.setOnClickListener(this);
        mIvPhotoFang.setOnClickListener(this);
        findViewById(R.id.btn_phone_bind_number_next).setOnClickListener(this);

        mEtName = (EditText) findViewById(R.id.et_real_name);
        mEtId = (EditText) findViewById(R.id.et_real_name_id);
    }

    private boolean checkData()
    {
        if (StringUtils.isEmpty(StringUtils.getString(mEtName)))
        {
            ToastUtil.show("请输入姓名");
            return false;
        }
        else if (!StringUtils.checkChinese(StringUtils.getString(mEtName)))
        {
            ToastUtil.show("请输入中文姓名");
            return false;
        }
        else if (StringUtils.isEmpty(StringUtils.getString(mEtId)))
        {
            ToastUtil.show("请输入身份证号");
            return false;
        }
        else if (!existZheng || !existFang)
        {
            ToastUtil.show("请选择身份证正反面照");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v)
    {
        PhotoDialog dialog;
        switch (v.getId())
        {
            case R.id.title_back:
                finish();
                break;
            case R.id.iv_real_name_photo_zheng:
                mIvCurrent = mIvPhotoZheng;
                dialog = new PhotoDialog(this, mFileZheng);
                dialog.show();
                break;
            case R.id.iv_real_name_photo_fang:
                mIvCurrent = mIvPhotoFang;
                dialog = new PhotoDialog(this, mFileFang);
                dialog.show();
                break;
            case R.id.btn_phone_bind_number_next:
                if (checkData())
                {
                    uploadData();
                }
                break;
            default:
                break;
        }

    }

    private void uploadData()
    {
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        RequestParams params = new RequestParams();
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(this));
        map.put("realname", StringUtils.getString(mEtName));
        map.put("card_id", StringUtils.getString(mEtId));
        params.addBodyParameter("diyou",
                CreateCode.p2sDiyou(CreateCode.GetJson(map)));
        params.addBodyParameter("xmdy", CreateCode
                .p2sXmdy(CreateCode.p2sDiyou(CreateCode.GetJson(map))));
        params.addBodyParameter("front", mFileZheng);
        params.addBodyParameter("verso", mFileFang);
        httpUtils.send(HttpMethod.POST, UrlConstants.REAL_NAME_AUTH, params,
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
                                    ToastUtil.show("数据上传成功，等待审核");
                                    if(Constants.ACTION_PROCESS_YBREG.equals(mAuthAction)){
                                        startActivity(new Intent(RealNameAuthActivity.this,YiBaoRegisterActivity.class));
                                    }
                                    finish();
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

                        hideProgressDialog();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageUtil.REQUEST_CODE_PICK_PHOTO
                && resultCode == RESULT_OK)
        {
            Bitmap b = ImageUtil.getLocalImage(ImageUtil.getFile(this, data),
                    300, 200);
            mIvCurrent.setImageBitmap(b);
            compressImage(b);
        }
        else if (requestCode == ImageUtil.REQUEST_CODE_TAKE_PHOTO
                && resultCode == RESULT_OK)
        {
            if (mIvCurrent == mIvPhotoZheng)
            {
                Bitmap b1 = ImageUtil.getLocalImage(mFileZheng, 300, 200);
                mIvCurrent.setImageBitmap(b1);
                compressImage(b1);
            }
            else if (mIvCurrent == mIvPhotoFang)
            {
                Bitmap b2 = ImageUtil.getLocalImage(mFileFang, 300, 200);
                mIvCurrent.setImageBitmap(b2);
                compressImage(b2);
            }
        }
    }

    private void compressImage(Bitmap b)
    {
        if (mIvCurrent == mIvPhotoZheng)
        {
            ImageUtil.compressImage(b, mFileZheng, 30);// 压缩图片
            existZheng = true;
        }
        else if (mIvCurrent == mIvPhotoFang)
        {
            ImageUtil.compressImage(b, mFileFang, 30);
            existFang = true;
        }
    }

}
