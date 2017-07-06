package com.diyou.application;

import com.diyou.activity.BankCardManagerActivity;
import com.diyou.activity.BeforeLoginActivity;
import com.diyou.activity.CreditorRightsDetailsActivity;
import com.diyou.activity.InvestmentDetailsActivity;
import com.diyou.activity.LoginActivity;
import com.diyou.activity.MainActivity;
import com.diyou.activity.RegisteredActivity;
import com.diyou.activity.RetrievePasswordActivity;
import com.diyou.activity.TheTenderActivity;
import com.diyou.bean.MyPushMessage;
import com.diyou.config.UserConfig;
import com.diyou.fragment.HomeFragment;
import com.diyou.fragment.InvestmentFragment;
import com.diyou.fragment.ListCreditorRightsTransferFragment;
import com.diyou.fragment.MoreFragment;
import com.diyou.fragment.PersonalCenterFragment;
import com.diyou.pushservice.MyService;
import com.diyou.util.Global;
import com.diyou.v5yibao.R;
import com.example.encryptionpackages.CreateCode;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyApplication extends Application
{

    public static boolean lockActivityIstThere = false;
    public static MyApplication app = null;
    private ListCreditorRightsTransferFragment listCreditorRightsTransferFragment;
    private HomeFragment FirstFragment;
    private InvestmentFragment SecondFragment;
    private PersonalCenterFragment ThirdFragment;
    private MoreFragment FourthFragment;
    private InvestmentDetailsActivity investmentDetailsActivity;
    private CreditorRightsDetailsActivity creditorRightsDetailsActivity;
    private TheTenderActivity tenderActivity;
    private BeforeLoginActivity beforeLoginActivity;
    private LoginActivity loginActivity;
    private RegisteredActivity registeredActivity;
    private RetrievePasswordActivity retrievePasswordActivity;
    private BankCardManagerActivity bankCardManagerActivity;

    public static boolean isLogin = false;
    public static boolean isLock = false;
    public static boolean isCountDownTimerStart = false;
    public static boolean isOnClickLockKey = false;
    public static boolean isActive = true;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private int time = 30000;

    public CountDownTimer timer = new CountDownTimer(30000, 1000)
    {

        @Override
        public void onTick(long millisUntilFinished)
        {
            time = (int) millisUntilFinished;
            Log.e("onTick", time / 1000 + "");
        }

        @Override
        public void onFinish()
        {
            time = 0;
        }

    };

    public void RemovalGestures()
    {
        isLogin = false;
        isLock = false;
        isCountDownTimerStart = false;
        isOnClickLockKey = false;
        timer.cancel();
        time = 100;
    }

    @Override
    public void onCreate()
    {
        Global.setApplicationContext(this);
        CreateCode.initCreatCode(this);
        isLogin = UserConfig.getInstance().isLogin(this);
        app = this;
        initImageLoader(this);
        initService();
        initNotifyService();
        initNotify();
        super.onCreate();
    }

    /**
     * 初始化用到的通知服务
     */
    private void initNotifyService()
    {
        mNotificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
    }

    private void initService()
    {
        Intent localIntent = new Intent();
        localIntent.setClass(this, MyService.class); // 启动SERVICE
        startService(localIntent);
    }

    public synchronized static MyApplication getInstance()
    {
        return app;
    }

    public ListCreditorRightsTransferFragment getListCreditorRightsTransferFragment()
    {
        return listCreditorRightsTransferFragment;
    }

    public void setListCreditorRightsTransferFragment(
            ListCreditorRightsTransferFragment listCreditorRightsTransferFragment)
    {
        this.listCreditorRightsTransferFragment = listCreditorRightsTransferFragment;
    }

    public CreditorRightsDetailsActivity getCreditorRightsDetailsActivity()
    {
        return creditorRightsDetailsActivity;
    }

    public void setCreditorRightsDetailsActivity(
            CreditorRightsDetailsActivity creditorRightsDetailsActivity)
    {
        this.creditorRightsDetailsActivity = creditorRightsDetailsActivity;
    }

    public TheTenderActivity getTenderActivity()
    {
        return tenderActivity;
    }

    public void setTenderActivity(TheTenderActivity tenderActivity)
    {
        this.tenderActivity = tenderActivity;
    }

    public InvestmentDetailsActivity getInvestmentDetailsActivity()
    {
        return investmentDetailsActivity;
    }

    public void setInvestmentDetailsActivity(
            InvestmentDetailsActivity investmentDetailsActivity)
    {
        this.investmentDetailsActivity = investmentDetailsActivity;
    }

    public HomeFragment getFirstFragment()
    {
        return FirstFragment;
    }

    public void setFirstFragment(HomeFragment firstFragment)
    {
        FirstFragment = firstFragment;
    }

    public InvestmentFragment getSecondFragment()
    {
        return SecondFragment;
    }

    public void setSecondFragment(InvestmentFragment mInvestmentFragment)
    {
        SecondFragment = mInvestmentFragment;
    }

    public PersonalCenterFragment getThirdFragment()
    {
        return ThirdFragment;
    }

    public void setThirdFragment(PersonalCenterFragment thirdFragment)
    {
        ThirdFragment = thirdFragment;
    }

    public MoreFragment getFourthFragment()
    {
        return FourthFragment;
    }

    public void setFourthFragment(MoreFragment fourthFragment)
    {
        FourthFragment = fourthFragment;
    }

    public BeforeLoginActivity getBeforeLoginActivity()
    {
        return beforeLoginActivity;
    }
    /**设置未登录时的activity
     * 作用:后面使用时如果未销毁可直接从application中获取???
     * */
    public void setBeforeLoginActivity(BeforeLoginActivity beforeLoginActivity)
    {
        this.beforeLoginActivity = beforeLoginActivity;
    }

    public BankCardManagerActivity getBankCardManagerActivity() {
        return bankCardManagerActivity;
    }

    public void setBankCardManagerActivity(BankCardManagerActivity bankCardManagerActivity) {
        this.bankCardManagerActivity = bankCardManagerActivity;
    }

    public LoginActivity getLoginActivity()
    {
        return loginActivity;
    }

    public void setLoginActivity(LoginActivity loginActivity)
    {
        this.loginActivity = loginActivity;
    }

    public RetrievePasswordActivity getRetrievePasswordActivity()
    {
        return retrievePasswordActivity;
    }

    public RegisteredActivity getRegisteredActivity()
    {
        return registeredActivity;
    }

    public void setRegisteredActivity(RegisteredActivity registeredActivity)
    {
        this.registeredActivity = registeredActivity;
    }

    public void setRetrievePasswordActivity(
            RetrievePasswordActivity retrievePasswordActivity)
    {
        this.retrievePasswordActivity = retrievePasswordActivity;
    }

    public static void initImageLoader(Context context)
    {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                        .denyCacheImageMultipleSizesInMemory()
                        .discCacheFileNameGenerator(new Md5FileNameGenerator())
                        .tasksProcessingOrder(QueueProcessingType.LIFO)
                        .writeDebugLogs() // Remove for release app
                        .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    /**
     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
     * @flags属性: 在顶部常驻:Notification.FLAG_ONGOING_EVENT 点击去除：
     *           Notification.FLAG_AUTO_CANCEL
     */
    public PendingIntent getDefalutIntent(int flags)
    {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,
                new Intent(), flags);
        return pendingIntent;
    }

    /** 初始化通知栏 */
    private void initNotify()
    {
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("").setContentText("")
                .setContentIntent(
                        getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                // .setNumber(number)//显示数量
                .setTicker("")// 通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                // .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_LIGHTS
                        | Notification.DEFAULT_VIBRATE)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                // Notification.DEFAULT_ALL Notification.DEFAULT_SOUND 添加声音 //
                // requires VIBRATE permission
                .setSmallIcon(R.drawable.v5_logo);
    }

    /** 显示通知栏点击跳转到指定Activity */
    public void showIntentActivityNotify(MyPushMessage msg, int notifyId)
    {
        mBuilder.setAutoCancel(true)
                // 点击后让通知将消失
                .setContentTitle(msg.getTitle()).setContentText(msg.getBody())
                .setTicker("");
        // 点击的意图ACTION是跳转到Intent
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("isNotifyOpen", "1");
        // resultIntent.putExtra("openUserId", imMessage.getTalkToId());
        // resultIntent.putExtra("openUserName", imMessage.getUserName());
        // resultIntent.putExtra("openUserTag", imMessage.getTag());
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        // Random random = new Random(100);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notifyId,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(notifyId, mBuilder.build());
    }

    public int getTime()
    {
        return time;
    }

    public void setTime(int time)
    {
        this.time = time;
    }

    public CountDownTimer getTimer()
    {
        return timer;
    }

}
