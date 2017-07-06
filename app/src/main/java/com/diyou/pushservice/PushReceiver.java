package com.diyou.pushservice;

import com.diyou.config.UserConfig;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PushReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            return;
        }
        else if (Intent.ACTION_USER_PRESENT.equals(intent.getAction()))
        {
            Intent i = new Intent();
            i.setClass(context, MyService.class);
            context.startService(i);
            return;
        }
        else if ("com.example.service_destory".equals(intent.getAction()))
        {
            Intent i = new Intent();
            i.setClass(context, MyService.class);
            context.startService(i);
            return;
        }
        // else if ("com.example.clock".equals(intent.getAction()))
        // {
        // Intent i = new Intent();
        // i.setClass(context, MyService.class);
        // context.startService(i);
        // return;
        // }

        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION))
        {

        case PushConsts.GET_MSG_DATA:
            // 获取透传数据
            // String appid = bundle.getString("appid");
            byte[] payload = bundle.getByteArray("payload");

            String taskid = bundle.getString("taskid");
            String messageid = bundle.getString("messageid");

            // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
            boolean result = PushManager.getInstance()
                    .sendFeedbackMessage(context, taskid, messageid, 90001);
            System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));

            if (payload != null)
            {
                String data = new String(payload);
                ToastUtil.show(data);
                Log.d("GetuiSdkDemo", "receiver payload : " + data);
            }
            break;
        case PushConsts.GET_CLIENTID:
            // 获取ClientID(CID)
            // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
            String cid = bundle.getString("clientid");
            // Log.e("cid", cid);
            if (!StringUtils
                    .isEmpty2(UserConfig.getInstance().getLoginToken(context)))
            {
                Intent login = new Intent(
                        "com.diyou.oa.activity.LoginActivity");
                login.putExtra("clientId", cid);
                context.sendBroadcast(login);
            }
            break;
        case PushConsts.THIRDPART_FEEDBACK:
            break;
        default:
            break;
        }
    }
}
