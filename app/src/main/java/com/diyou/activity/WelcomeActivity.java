package com.diyou.activity;

import com.diyou.util.SharedPreUtils;
import com.diyou.v5yibao.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 如果app在手机上打开过，直接启动mainactivity
 * 没打开过则启动导航页activity
 */

public class WelcomeActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1000);

                    if (SharedPreUtils.getBoolean("isFirstStart",
                            WelcomeActivity.this))
                    {
                        Intent intent = new Intent(WelcomeActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        startActivity(new Intent(WelcomeActivity.this,
                                GuidePageActivity.class));
                    }
                    finish();

                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
