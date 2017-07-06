package com.diyou.util;

import java.io.File;
import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.ProgressDialogBar;
import com.example.encryptionpackages.CreateCode;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class UpdateAPK
{
    private ProgressDialogBar progressDialogBar;
    private Context mContext;
    private Dialog noticeDialog;
    private String mVersionCode;
    private String fileurl;
    private int progress;
    private File apkFile;
    /**是否显示请求数据时的dialog???*/
    private boolean isMoreActivity = false;
    private int apkSize;
    private int mCurrVersionCode = 0;
    private HttpHandler<File> httpHandler;
    private static UpdateAPK mInstance;

    /**获取单例实例*/
    public static synchronized UpdateAPK getInstance(Context context,
            boolean isMoreActivity)
    {
        if (mInstance == null)
        {
            return new UpdateAPK(context, isMoreActivity);
        }
        return mInstance;
    }

    // public static synchronized UpdateAPK getInstance(Context context){
    // return getInstance(context, false);
    // }
    /**私有构造方法*/
    private UpdateAPK(Context context, boolean isMoreActivity)
    {
        this.mContext = context;
        this.isMoreActivity = isMoreActivity;
    }
    /**战士进度dialog*/
    public void showProgressDialog()
    {
        if (progressDialogBar == null)
        {
            progressDialogBar = ProgressDialogBar.createDialog(mContext);
        }
        progressDialogBar.setProgressMsg("加载中...");
        progressDialogBar.show();
    }
    /**隐藏进度dialog*/
    public void hideProgressDialog()
    {
        if (progressDialogBar != null && progressDialogBar.isShowing())
        {
            progressDialogBar.dismiss();
        }
    }

    /**检测app是否更新*/
    public void updataVersion()
    {
        mCurrVersionCode = Integer
                .valueOf(getVersionName(mContext).replace(".", ""));
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(mContext));
        map.put("phone_type", "1");// 手机类型1=andriod 2=IOS
        map.put("version", getVersionName(mContext));// 当前版本号
        //登陆状态,手机类型,当前版本号作为参数发起请求
        HttpUtil.post(UrlConstants.UPDATE_VERSION, map,
                new JsonHttpResponseHandler()
                {
                    @Override
                    public void onFinish()
                    {
                        hideProgressDialog();//隐藏加载动画
                        super.onFinish();
                    }

                    @Override
                    public void onStart()
                    {
                        if (isMoreActivity)//默认值为false
                        {
                            showProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        ToastUtil.show(R.string.generic_error);//网络请求失败处理
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                            JSONObject response)
                    {
                        // 请求成功调用，有几个重载方法,解析json数据
                        try
                        {
                            if (CreateCode.AuthentInfo(response))
                            {
                                JSONObject json = StringUtils
                                        .parseContent(response);
                                if ("success".equals(json.optString("result")))
                                {
                                    JSONObject jo = json.getJSONObject("data");
                                    mVersionCode = jo.optString("version");
                                    // mVersionCode="1.2.0";
                                    fileurl = jo.optString("url");//判断版本是否为最新
                                    if (Integer.valueOf(mVersionCode.replace(
                                            ".", "")) > mCurrVersionCode)
                                    {
                                        showNoticeDialog();//不为最新,显示更新通知
                                    }
                                    else
                                    {
                                        if (isMoreActivity)
                                        {
                                            ToastUtil.show("当前已是最新版本！");
                                        }
                                    }

                                }
                                else
                                {
                                    if (isMoreActivity)
                                    {
                                        ToastUtil.show(json.optString("description"));
                                    }
                                }
                            }

                        }
                        catch (JSONException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }
                });
    }

    private RemoteViews mRemoteViews;
    private NotificationManager mNotifyManager;
    private Notification mNotification;
    public static final int ID_DOWNLOAD_NOTIFI = 1201;
    /**下载app时展示状态栏通知*/
    private void showNotify()
    {
        mNotifyManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        mRemoteViews = new RemoteViews(mContext.getPackageName(),
                R.layout.upgrade_notification);
        mNotification = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.v5_logo)
                .setTicker("开始下载")
                .setWhen(System.currentTimeMillis()).setContent(mRemoteViews)
                .build();
        mNotification.flags = Notification.FLAG_ONGOING_EVENT
                | Notification.FLAG_NO_CLEAR;
        mNotifyManager.notify(ID_DOWNLOAD_NOTIFI, mNotification);
    }
    /**检查app的下载地址是否正确,是否由sdcard*/
    private boolean checkEnable()
    {
        // 判断地址是否存在
        if (StringUtils.isEmpty(fileurl))
        {
            Toast.makeText(mContext, "下载地址错误", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 判断SDcard状态
        else if (!Environment.MEDIA_MOUNTED
                .equals(Environment.getExternalStorageState()))
        {
            // 错误提示
            Toast.makeText(mContext, "请检查是否插入SD卡", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 检查SDcard空间
        // else if (Environment.getExternalStorageDirectory().getFreeSpace() <
        // apkSize)
        // {
        // // 弹出对话框提示用户空间不够
        // Toast.makeText(mContext, "SDcard空间不够!", Toast.LENGTH_SHORT)
        // .show();
        // return false;
        // }
        return true;
    }
    /**下载app*/
    private void downLoadFile()
    {
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        RequestParams param = new RequestParams();
        apkFile = new File(Environment.getExternalStorageDirectory()
                + "/diyou_v5_" + mVersionCode.replace(".", "_") + ".apk");
        // if (!apkFile.getParentFile().exists())
        // {
        // apkFile.getParentFile().mkdirs();
        // }
        // try
        // {
        // apkFile.createNewFile();
        // }
        // catch (IOException e)
        // {
        // e.printStackTrace();
        // }
//        String pUrl = "http://dldir1.qq.com/qqfile/qq/QQ7.4/15197/QQ7.4.exe";
        httpHandler = httpUtils.download(HttpMethod.GET, fileurl,
                apkFile.getAbsolutePath(), param, true, true,
                new RequestCallBack<File>()
                {
                    @Override
                    public void onStart()
                    {
                        showNotify();
                    }

                    @Override
                    public void onLoading(long total, long current,
                            boolean isUploading)
                            //更新下载进度
                    {
                        progress = (int) (current * 100 / total);
                        mRemoteViews.setTextViewText(R.id.tv_notify_upgrade_msg,
                                "已下载：" + progress + "%");
                        mRemoteViews.setProgressBar(R.id.pb_notify_upgrade, 100,
                                progress, false);
                        mNotifyManager.notify(ID_DOWNLOAD_NOTIFI,
                                mNotification);
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1)
                    {
                        ToastUtil.show("网络连接失败，请重试");
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> arg0)
                    {
                        // 下载完成通知安装
                        ToastUtil.show("下载完成");
                        finishNotify();//取消状态栏通知
                    }

                });
    }
    /**取消状态栏通知,打开安装apk  activity*/
    public void finishNotify()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri data = Uri.fromFile(apkFile);
        String type = "application/vnd.android.package-archive";
        intent.setDataAndType(data, type);
        mContext.startActivity(intent);
        PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, 0);
        mNotification.contentIntent = pi;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;

        mRemoteViews.setTextViewText(R.id.tv_notify_upgrade_msg, "点击更新");
        mRemoteViews.setViewVisibility(R.id.pb_notify_upgrade, View.GONE);
        mNotifyManager.notify(ID_DOWNLOAD_NOTIFI, mNotification);
    }

    // public int getCurrVersionCode()
    // {
    // PackageManager pm = mContext.getPackageManager();
    // try
    // {
    // PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), 0);
    // return info.versionCode;
    // }
    // catch (NameNotFoundException e)
    // {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // return 0;
    // }

    /**
     * 弹出更新提示对话框
     */
    private void showNoticeDialog()
    {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("版本更新");
        builder.setMessage("发现最新版本：" + mVersionCode);
        builder.setPositiveButton("下载", new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (checkEnable())
                {
                    downLoadFile();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("以后再说", new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        noticeDialog = builder.create();
        noticeDialog.show();
    }
    /**获取app的版本号*/
    public static String getVersionName(Context context)// 获取版本号
    {
        try
        {
            PackageInfo pi = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
            // return "0.0.1";
        }
        catch (NameNotFoundException e)
        {
            e.printStackTrace();
            return "1.0.0";
        }
    }
}
