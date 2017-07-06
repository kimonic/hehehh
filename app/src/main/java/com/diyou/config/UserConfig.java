package com.diyou.config;

import com.diyou.util.SharedPreUtils;
import com.diyou.util.StringUtils;
import com.example.encryptionpackages.AESencrypt;
import com.example.encryptionpackages.CreateCode;

import android.content.Context;

/**
 * 用户登陆等相关信息
 */
public class UserConfig
{

    private static UserConfig mUserConfig;
    private String userId;
    private String userKey;
    private String userName;
    private String isLogin;
    private String loginToken;

    /**
     * 
     */
    private UserConfig()
    {

    }

    /**
     * userkey
     * 
     * @return
     */
    public synchronized String getUserKey(Context context)
    {
        if (null == userKey)
        {
            try
            {
                userKey = AESencrypt.decrypt2PHP(CreateCode.getSEND_AES_KEY(),
                        SharedPreUtils
                                .getString(Constants.SHARE_USERKEY, context));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return userKey;
    }

    public void setUserKey(String userKey)
    {
        this.userKey = userKey;
    }

    /**
     * isLogin
     * 
     * @return
     */
    public synchronized String getLogin(Context context)
    {
        if (null == isLogin)//未登录时为null
        {
            try
            {//AES解密?????
                isLogin = AESencrypt.decrypt2PHP(CreateCode.getSEND_AES_KEY(),
                        SharedPreUtils
                                .getString(Constants.SHARE_ISLOGIN, context));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return isLogin;
    }

    public synchronized boolean isLogin(Context context)
    {
        if (StringUtils.isEmpty2(getLogin(context)))
        {
            return false;
        }
        else
        {
            return getLogin(context).equals("isLogin");
        }
    }

    /**
     * userid
     * 
     * @return
     */
    public synchronized String getUserId(Context context)
    {
        if (null == userId)
        {
            try
            {
                userId = AESencrypt.decrypt2PHP(CreateCode.getSEND_AES_KEY(),
                        SharedPreUtils
                                .getString(Constants.SHARE_USERID, context));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    /**
     * loginToken
     * 
     * @return
     */
    public synchronized String getLoginToken(Context context)
    {
        if (null == loginToken)
        {
            try
            {
                loginToken = AESencrypt.decrypt2PHP(
                        CreateCode.getSEND_AES_KEY(),
                        SharedPreUtils.getString(
                                Constants.SHARE_LOGINTOKEN, context));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return loginToken;
    }

    public void setLoginToken(String loginToken)
    {
        this.loginToken = loginToken;
    }

    /**
     * username
     * 
     * @return
     */
    public synchronized String getUserName(Context context)
    {
        if (null == userName)
        {
            try
            {
                userName = AESencrypt.decrypt2PHP(CreateCode.getSEND_AES_KEY(),
                        SharedPreUtils
                                .getString(Constants.SHARE_USERNAME, context));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * 
     * @return
     */
    public synchronized static UserConfig getInstance()
    {
        if (null == mUserConfig)
        {
            mUserConfig = new UserConfig();
        }
        return mUserConfig;
    }

    /**
     * 清除
     */
    public synchronized void clear()
    {
        this.userKey = null;
        this.userId = null;
        this.userName = null;
        this.isLogin = null;
    }
}
