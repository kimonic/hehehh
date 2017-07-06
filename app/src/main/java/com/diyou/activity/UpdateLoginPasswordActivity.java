package com.diyou.activity;

import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONObject;

import com.diyou.base.BaseActivity;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.ProgressDialogBar;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author 登录密码管理
 */
public class UpdateLoginPasswordActivity extends BaseActivity
        implements OnClickListener {

    private EditText activity_update_login_password_old_password;
    private EditText activity_update_login_password_new_password;
    private EditText activity_update_login_password_check_new_password;
    private ImageView new_password_imageview;
    private ImageView again_password_imageview;
    protected ProgressDialogBar progressDialogBar;
    private boolean showAgainPayment = true;
    private boolean showPayment = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_login_password);
        initView();
    }

    private void initView() {
        findViewById(R.id.activity_update_login_password_title_layout)
                .findViewById(R.id.title_bar_back).setOnClickListener(this);
        ((TextView) findViewById(
                R.id.activity_update_login_password_title_layout)
                .findViewById(R.id.title_name)).setText(
                R.string.title_activity_update_login_password);
        findViewById(R.id.updataemailcctivity_next_btn)
                .setOnClickListener(this);
        findViewById(R.id.rl_show_new_password)
                .setOnClickListener(this);
        new_password_imageview = (ImageView) findViewById(
                R.id.show_new_password);
        findViewById(R.id.rl_show_login_password_again)
                .setOnClickListener(this);
        again_password_imageview = (ImageView) findViewById(
                R.id.show_login_password_again);
        activity_update_login_password_old_password = (EditText) findViewById(
                R.id.activity_update_login_password_old_password);
        activity_update_login_password_new_password = (EditText) findViewById(
                R.id.activity_update_login_password_new_password);
        activity_update_login_password_check_new_password = (EditText) findViewById(
                R.id.activity_update_login_password_check_new_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_show_new_password:
                if (showPayment) {
                    showPayment = false;
                    activity_update_login_password_new_password
                            .setInputType(InputType.TYPE_CLASS_TEXT);
                    new_password_imageview
                            .setBackgroundResource(R.drawable.show_password_press);
                } else {
                    showPayment = true;
                    activity_update_login_password_new_password.setInputType(0x81);
                    new_password_imageview
                            .setBackgroundResource(R.drawable.show_passwprd);
                }
                break;
            case R.id.rl_show_login_password_again:
                if (showAgainPayment) {
                    showAgainPayment = false;
                    activity_update_login_password_check_new_password
                            .setInputType(InputType.TYPE_CLASS_TEXT);
                    again_password_imageview
                            .setBackgroundResource(R.drawable.show_password_press);

                } else {
                    showAgainPayment = true;
                    activity_update_login_password_check_new_password
                            .setInputType(0x81);
                    again_password_imageview
                            .setBackgroundResource(R.drawable.show_passwprd);
                }

                break;
            case R.id.title_bar_back:
                finish();
                break;
            case R.id.updataemailcctivity_next_btn:
                if (checkData()) {
                    if (!activity_update_login_password_new_password.getText()
                            .toString()
                            .equals(activity_update_login_password_check_new_password
                                    .getText().toString())) {
                        ToastUtil.show(
                                R.string.activity_update_login_password_error_password);
                        break;
                    } else if (activity_update_login_password_new_password.getText()
                            .toString().length() < 6
                            || activity_update_login_password_check_new_password
                            .getText().toString().length() < 6) {
                        ToastUtil.show("新密码长度不能少于6位");
                        break;
                    } else if (checkUserName(activity_update_login_password_new_password
                            .getText().toString())) {
                        requestData();
                    }
                }
                break;
        }
    }

    private void requestData() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance()
                .getLoginToken(UpdateLoginPasswordActivity.this));
        map.put("password", activity_update_login_password_old_password.getText()
                .toString());
        map.put("new_password", activity_update_login_password_new_password
                .getText().toString());
        map.put("confirm_password",
                activity_update_login_password_check_new_password.getText()
                        .toString());
        HttpUtil.post(UrlConstants.EDITPWD, map, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                ToastUtil.show("设置失败");
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                try {
                    if (CreateCode.AuthentInfo(response)) {
                        JSONObject data = StringUtils.parseContent(response);

                        if (data.optString("result").equals("success")) {
                            ToastUtil.show("设置成功");
                            finish();
                        } else {
                            ToastUtil.show(data.optString("description"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFinish() {
                hideProgressDialog();
                super.onFinish();
            }

            @Override
            public void onStart() {
                showProgressDialog();
                super.onStart();
            }
        });
    }

    private boolean checkData() {
        if (StringUtils.isEmpty2(StringUtils.getString(activity_update_login_password_old_password))) {
            ToastUtil.show("请输入原密码");
            return false;
        } else if (StringUtils.isEmpty2(StringUtils.getString(activity_update_login_password_new_password))) {
            ToastUtil.show("请输入新密码");
            return false;
        } else if (StringUtils.isEmpty2(StringUtils.getString(activity_update_login_password_check_new_password))) {
            ToastUtil.show("请确认新密码");
            return false;
        }

        return true;
    }

    private boolean checkUserName(String string) {
        if (isDec(string)) {
            ToastUtil.show("新密码不能含有特殊字符！");
            return false;
        }
        if (string.contains(" ") || string.contains(" ")) {
            ToastUtil.show("新密码不能有空字符！");
            return false;
        }
        if (Pattern.compile("[-.!@#$%^&*()+?><]").matcher(string).find()) {
            ToastUtil.show("请不要输入非法字符！");
            return false;
        }
        // if (Pattern.compile("[0-9]*").matcher(string).matches())
        // {
        // ToastUtil.show("新密码不能为全数字！");
        // return false;
        // }
        if (!checkInput(string)) {
            ToastUtil.show("请输入6-15个由字母、数字组成的密码");
            return false;
        }
        // for (char c : string.toCharArray())
        // {
        // if (!isDbcCase(c))
        // {
        // return false;
        // }
        // }

        return true;
    }

    /**
     * 是否存在特殊字符
     *
     * @param string
     * @return
     */
    public boolean isDec(String string) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(string);
        return m.find();
    }

    public boolean checkInput(String str) {
        int num = 0;
        num = Pattern.compile("\\d").matcher(str).find() ? num + 1 : num;
        num = Pattern.compile("[a-z]").matcher(str).find() ? num + 1 : num;
        num = Pattern.compile("[A-Z]").matcher(str).find() ? num + 1 : num;

        return num >= 2;
    }
}
