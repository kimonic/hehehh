package com.diyou.activity;

import com.diyou.base.BaseActivity;
import com.diyou.config.Constants;
import com.diyou.config.UserConfig;
import com.diyou.util.ActionSheet;
import com.diyou.util.ActionSheet.MenuItemClickListener;
import com.diyou.util.CustomDialog;
import com.diyou.util.SharedPreUtils;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.util.UpdateAPK;
import com.diyou.util.Utils;
import com.diyou.v5yibao.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 设置
 * 
 * @author Administrator
 * 
 */
public class SettingActivity extends BaseActivity implements OnClickListener
{
    private Button mExitButton;
    private boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        isLogin = !StringUtils
                .isEmpty2(UserConfig.getInstance().getLoginToken(this));
        initActionBar();
        initView();
    }

    private void initView()
    {
        View mCommonSet = findViewById(R.id.rl_setting_common_set);
        findViewById(R.id.NoticeListActivity).setOnClickListener(this);
        mExitButton = (Button) findViewById(R.id.setting_exit_login);
        if (isLogin)
        {
            mExitButton.setText("退出登录");
//            mCommonSet.setVisibility(View.VISIBLE);
        }
        else
        {
            mExitButton.setText("立即登录");
//            mCommonSet.setVisibility(View.GONE);
        }
        //客服电话显示
        TextView tvPhone = (TextView) findViewById(R.id.tv_setting_phone);
        tvPhone.setText(SharedPreUtils.getString(Constants.KEY_SERVICE_TEL, "", this));
        // 版本显示
        TextView tvVersion = (TextView) findViewById(R.id.tv_setting_version);
        tvVersion.setText("当前版本号V" + UpdateAPK.getVersionName(this));

        findViewById(R.id.setting_exit_login).setOnClickListener(this);
        findViewById(R.id.settingactivity_commonproblems)
                .setOnClickListener(this);
        findViewById(R.id.rl_setting_contact_us).setOnClickListener(this);
        findViewById(R.id.rl_setting_version_check).setOnClickListener(this);
    }

    private void initActionBar()
    {
        TextView tvTitle = (TextView) findViewById(R.id.title_name);
        tvTitle.setText(R.string.setting_title);
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
        case R.id.NoticeListActivity:
            startActivity(
                    new Intent(SettingActivity.this, NoticeListActivity.class));
            break;
        case R.id.settingactivity_commonproblems:
            startActivity(new Intent(SettingActivity.this,
                    DetaileProblemActivity.class));
            break;
        case R.id.setting_exit_login:
            exitLogin();
            break;
        case R.id.rl_setting_version_check:
            UpdateAPK.getInstance(this, true).updataVersion();
            break;
        case R.id.rl_setting_contact_us:
            // CustomDialog dialog=new CustomDialog(this, "联系客服",
            // Constants.SERVICE_TEL, Constants.PHONECALL,
            // Constants.SERVICE_TEL);
            // dialog.show();
            setTheme(R.style.ActionSheetStyleIOS7);
            showActionSheet();
            break;
        default:
            break;
        }

    }

    public void showActionSheet()
    {
        final String tel=SharedPreUtils.getString(Constants.KEY_SERVICE_TEL, "", this);
        String hours=SharedPreUtils.getString(Constants.KEY_SERVICE_HOURS, "", this);
        if(StringUtils.isEmpty(tel)){
            ToastUtil.show(R.string.remind_service_info_error);
        }else{
            ActionSheet menuView = new ActionSheet(this);
            if(StringUtils.isEmpty(hours)){
                menuView.addItems(getString(R.string.setting_service_hotline),tel);
            }else{
                menuView.addItems(getString(R.string.setting_service_hotline)+"\n("+getString(R.string.setting_service_hours)+hours+")",tel);
            }
            menuView.setItemClickListener(new MenuItemClickListener() {

                @Override
                public void onItemClick(int itemPosition) {
                    Log.e("", "" + itemPosition);
                    switch (itemPosition) {
                        case 1:
                            Utils.openDial(tel,SettingActivity.this);
                            break;

                        default:
                            break;
                    }
                }
            });
            menuView.showMenu();
        }
    }

    private void exitLogin()
    {
        if (!isLogin)
        {
            startActivity(new Intent(SettingActivity.this,
                    BeforeLoginActivity.class));
            finish();
        }
        else
        {
            CustomDialog dialog = new CustomDialog(SettingActivity.this, "退出登录",
                    "你确定退出登录吗?", Constants.LOGOUT, "");
            dialog.show();
        }
    }

}
