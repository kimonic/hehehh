package com.diyou.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.diyou.application.MyApplication;
import com.diyou.config.Constants;
import com.diyou.util.CustomDialog;
import com.diyou.util.LockPatternUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.LockPatternView;
import com.diyou.view.LockPatternView.Cell;

import java.util.List;

public class LockActivity extends Activity
        implements LockPatternView.OnPatternListener, OnClickListener {
    /**
     * 解锁
     */
    private int mFailedPatternAttemptsSinceLastTimeout = 0;
    private CountDownTimer mCountdownTimer = null;
    private Handler mHandler = new Handler();
    private Boolean updatePassword;
    private List<Cell> lockPattern;
    private LockPatternView lockPatternView;
    private TextView mHeadTextView;
    private Animation mShakeAnim;
    private int mClicks = 0;
    private boolean mIsEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.lockActivityIstThere = true;
        SharedPreferences preferences = getSharedPreferences(Constants.LOCK,
                MODE_PRIVATE);
        String patternString = preferences.getString(Constants.LOCK_KEY, null);
        // loader = new ImageLoader(LockActivity.this);
        if (patternString == null) {//不存在锁屏key则关闭
            finish();
            return;
        }
        updatePassword = getIntent().getBooleanExtra("updatePassword", false);
        deletePassword = getIntent().getBooleanExtra("deletePassword", false);
        mIsEditMode = getIntent().getBooleanExtra("ISEDITMODE", false);
        android.util.Log.e("LockActivity", "[zys-->] mIsEditMode="+mIsEditMode);

        mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
        lockPattern = LockPatternUtils.stringToPattern(patternString);
        setContentView(R.layout.activity_lock);
        mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
        lockPatternView = (LockPatternView) findViewById(R.id.lock_pattern);
        lockPatternView.setOnPatternListener(this);
        findViewById(R.id.gesturepwd_forgotPassword).setOnClickListener(this);
        TextView username = (TextView) findViewById(R.id.gesturepwd_username);
        ImageView usericon = (ImageView) findViewById(R.id.usericon);
        // String name = (String) AESencrypt.decrypt2PHP(AESencrypt.PASSWORD,
        // SharedPreUtils.getString(Constants.SHARE_USERNAME,
        // LockActivity.this));
        // if (!StringUtils.isEmpty2(name)) {
        // username.setText("13850075472");
        // }
        // String url = (String) AESencrypt.decrypt2PHP(AESencrypt.PASSWORD,
        // SharedPreUtils.getString(Constants.SHARE_USERIMG,
        // LockActivity.this));
        // if (!StringUtils.isEmpty2(url))
        // {
        // loader.DisplayImage(url, usericon, false);
        // }
    }

    @Override
    public void onPatternStart() {
    }

    @Override
    public void onPatternCleared() {
    }

    @Override
    public void onPatternCellAdded(List<Cell> pattern) {
    }

    @Override
    public void onPatternDetected(List<Cell> pattern) {
        if (pattern.equals(lockPattern)) {

            if (updatePassword) {
                getSharedPreferences(Constants.LOCK, MODE_PRIVATE).edit()
                        .clear().commit();
                startActivity(
                        new Intent(LockActivity.this, LockSetupActivity.class));
            } else if (deletePassword) {
                getSharedPreferences(Constants.LOCK, MODE_PRIVATE).edit()
                        .clear().commit();
            }

            setResult(Constants.SETGESTUREPASSWORD);
            MyApplication.getInstance().RemovalGestures();
            finish();
        } else {
            lockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
            mFailedPatternAttemptsSinceLastTimeout++;
            int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT
                    - mFailedPatternAttemptsSinceLastTimeout;
            if (retry >= 0) {
                if (retry == 0)
                    ToastUtil.show("您已5次输错密码，请30秒后再试");
                mHeadTextView.setText("密码错误，还可以再输入" + retry + "次");
                mHeadTextView.setTextColor(Color.RED);
                mHeadTextView.startAnimation(mShakeAnim);
            }

            if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
                lockPatternView.setEnabled(false);
                mHandler.postDelayed(attemptLockout, 2000);
            } else {
                lockPatternView.postDelayed(mClearPatternRunnable, 2000);
            }
        }

    }

    Runnable attemptLockout = new Runnable() {

        @Override
        public void run() {
            lockPatternView.clearPattern();
            lockPatternView.setEnabled(false);
            mCountdownTimer = new CountDownTimer(
                    LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS + 1, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    int secondsRemaining = (int) (millisUntilFinished / 1000)
                            - 1;
                    if (secondsRemaining > 0) {
                        mHeadTextView.setText(secondsRemaining + " 秒后重试");
                    } else {
                        mHeadTextView.setText("请绘制手势密码");
                        mHeadTextView.setTextColor(Color.WHITE);
                    }

                }

                @Override
                public void onFinish() {
                    lockPatternView.setEnabled(true);
                    mFailedPatternAttemptsSinceLastTimeout = 0;
                }
            }.start();
        }
    };
    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            lockPatternView.clearPattern();
        }
    };
    private boolean deletePassword;

    @Override
    public void onBackPressed() {
        if (mIsEditMode) {
            super.onBackPressed();
        }else{
            exitApp();
        }
    }

    // 在2秒内 重复点击2次back键 就退出
    private void exitApp() {
        mClicks++;
        if (mClicks == 1) {
            ToastUtil.show("再按一次退出");
            mHeadTextView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mClicks = 0;
                }
            }, 2000);
        } else if (mClicks == 2) {

            Intent intent = new Intent("com.diyou.v5.activity.BaseActivity");
            sendBroadcast(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gesturepwd_forgotPassword:

                CustomDialog dialog = new CustomDialog(LockActivity.this, "忘记手势密码！",
                        "需要重新登录账号?", Constants.FORGOTPASSWORD, "");
                dialog.show();

                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.lockActivityIstThere = false;

    }
}