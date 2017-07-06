package com.diyou.base;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.diyou.activity.EmailBindActivity;
import com.diyou.activity.PhoneBindActivity;
import com.diyou.activity.RealNameAuthActivity;
import com.diyou.activity.YiBaoRegisterActivity;
import com.diyou.config.Constants;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.ConfirmCancelDialog;
import com.diyou.view.ConfirmCancelDialog.onClickListener;
import com.diyou.view.ProgressDialogBar;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TreeMap;

public class BaseFragment extends Fragment
{

    private ProgressDialogBar progressDialogBar;

    public void showProgressDialog()
    {
        if (progressDialogBar == null)
        {
            progressDialogBar = ProgressDialogBar.createDialog(getActivity());
        }
        progressDialogBar.setProgressMsg("加载中...");
        progressDialogBar.show();
    }

    public void hideProgressDialog()
    {
        if (progressDialogBar != null && progressDialogBar.isShowing())
        {
            progressDialogBar.dismiss();
        }

    }

    public void requestYibaoRegistered(final Runnable runnable)
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token",
                UserConfig.getInstance().getLoginToken(getActivity()));
        HttpUtil.post(UrlConstants.IS_TRUST_REGISTER, map,
                new JsonHttpResponseHandler()
                {

                    @Override
                    public void onStart()
                    {
                        showProgressDialog();
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                            JSONObject response)
                    {
                        try
                        {
                            if (CreateCode.AuthentInfo(response))
                            {
                                JSONObject json = StringUtils
                                        .parseContent(response);
                                if (json.optString("result")
                                        .contains("success"))
                                {

                                    JSONObject jo = new JSONObject(
                                            json.optString("data"));
                                    if (1 == jo.optInt("register"))
                                    {
                                        getApprove1(runnable);
//                                        runnable.run();
                                    }
                                    else
                                    {
                                        showYiBaoRegisterDialog();
                                    }
                                }
                                else
                                {
                                    ToastUtil.show(
                                            json.getString("description"));
                                }
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish()
                    {
                        hideProgressDialog();
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse)
                    {
                        ToastUtil.show(R.string.generic_error);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }
                });

    }

    public void showYiBaoRegisterDialog()
    {
        ConfirmCancelDialog dialog = new ConfirmCancelDialog(getActivity(),
                "托管注册", "请先开通资金托管", "马上开通", "暂不开通");
        dialog.setOnClickListener(new onClickListener() {

            @Override
            public void confirmClick() {
                getApprove();
            }

            @Override
            public void cancelClick() {

            }
        });
        dialog.show();
    }

    private void getApprove()
    {

        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token",
                UserConfig.getInstance().getLoginToken(getActivity()));
        HttpUtil.post(UrlConstants.APPROVE, map, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                ToastUtil.show(R.string.generic_error);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                try {
                    if (CreateCode.AuthentInfo(response)) {
                        JSONObject data = StringUtils.parseContent(response);
                        if (data.optString("result").equals("success")) {
                            JSONObject info = data.getJSONObject("data");
                            // TODO 获取认证信息
                            Intent intent = new Intent();
                            if (StringUtils.isEmpty(info.optString("phone"))) {
                                // 未绑定手机
                                ToastUtil.show("请绑定手机");
                                intent.putExtra("authAction",
                                        Constants.ACTION_PROCESS_YBREG);
                                intent.setClass(getActivity(),
                                        PhoneBindActivity.class);
                                startActivity(intent);
                            }
                            else if (StringUtils
                                    .isEmpty(info.optString("email"))) {
                                // 未认证邮箱
                                ToastUtil.show("请验证邮箱");
                                intent.putExtra("authAction",
                                        Constants.ACTION_PROCESS_YBREG);
                                intent.setClass(getActivity(),
                                        EmailBindActivity.class);
                                startActivity(intent);
                            }
                            else if (StringUtils
                                    .isEmpty(info.optString("realname_status"))
                                    || info.optString("realname_status")
                                    .equals("-1")) {
                                // 未认证实名或认证失败
                                ToastUtil.show("请进行实名认证");
                                intent.putExtra("authAction",
                                        Constants.ACTION_PROCESS_YBREG);
                                intent.setClass(getActivity(),
                                        RealNameAuthActivity.class);
                                startActivity(intent);
                            }
                      /*      else if (info.optString("realname_status")
                                    .equals("-2"))
                            {
                                // 待审核
                                ToastUtil.show("实名认证待审核中");
                                intent.setClass(getActivity(),
                                        RealNameInfoActivity.class);
                                startActivity(intent);
                            }*/
                            else {
                                startActivity(new Intent(getActivity(),
                                        YiBaoRegisterActivity.class));
                            }

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
    /**
     * 先对邮箱和手机进行判断
     */
    private void getApprove1(final Runnable runnable) {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token",
                UserConfig.getInstance().getLoginToken(getActivity()));
        HttpUtil.post(UrlConstants.APPROVE, map, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                ToastUtil.show(R.string.generic_error);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                try {
                    if (CreateCode.AuthentInfo(response)) {
                        JSONObject data = StringUtils.parseContent(response);
                        if (data.optString("result").equals("success")) {
                            JSONObject info = data.getJSONObject("data");
                            Intent intent = new Intent();
                            if (StringUtils.isEmpty(info.optString("phone"))) {
                                // 未绑定手机
                                ToastUtil.show("请绑定手机");
                                intent.putExtra("authAction",
                                        Constants.ACTION_PROCESS_YBREG);
                                intent.setClass(getActivity(),
                                        PhoneBindActivity.class);
                                startActivity(intent);
                            }
                            else if (StringUtils
                                    .isEmpty(info.optString("email"))) {
                                // 未认证邮箱
                                ToastUtil.show("请验证邮箱");
                                intent.putExtra("authAction",
                                        Constants.ACTION_PROCESS_YBREG);
                                intent.setClass(getActivity(),
                                        EmailBindActivity.class);
                                startActivity(intent);
                            }
                            else{
                                runnable.run();
                            }

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
}
