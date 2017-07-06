package com.diyou.util;

import java.io.File;
import java.io.FileOutputStream;

import com.diyou.v5yibao.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class ShareSDKUtil
{
    private static String TEST_IMAGE;
    private static String FILE_NAME = "/v5_logo.jpg";
    private Context mContext;
    
    private ShareSDKUtil(Context context){
        this.mContext = context;
        initShareLogo(context);
        ShareSDK.initSDK(context);
    }

    private void initShareLogo(Context context)
    {
        try
        {
            if (Environment.MEDIA_MOUNTED
                    .equals(Environment.getExternalStorageState())
                    && Environment.getExternalStorageDirectory().exists())
            {
                TEST_IMAGE = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + FILE_NAME;
            }
            else
            {
                TEST_IMAGE = context.getFilesDir()
                        .getAbsolutePath() + FILE_NAME;
            }
            // 创建图片文件夹
            File file = new File(TEST_IMAGE);
            if (!file.exists())
            {
                file.createNewFile();
                Bitmap pic = BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.v5_logo);
                FileOutputStream fos = new FileOutputStream(file);
                pic.compress(CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            TEST_IMAGE = null;
        }
    }
    private static ShareSDKUtil shareSDKUtil;
    public static synchronized ShareSDKUtil getInstance(Context context){
        if(shareSDKUtil==null){
            shareSDKUtil = new ShareSDKUtil(context);
        }
        return shareSDKUtil;
    }

    public void share(String title,String content,String shareUrl)
    {
        
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
        // oks.setNotification(R.drawable.ic_launcher,
        // context.getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(shareUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(TEST_IMAGE);// 分享本地图片
        // oks.setImageUrl(picture_url);// 分享网络图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(shareUrl);
        // 启动分享GUI
        oks.show(mContext);
    }
}
