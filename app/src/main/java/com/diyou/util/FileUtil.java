package com.diyou.util;

import java.io.File;
import java.io.IOException;

import com.diyou.application.MyApplication;

import android.content.Context;
import android.os.Environment;

public class FileUtil
{

    /**
     * 判断存储卡是否存在
     */
    public static Boolean hasSDCard()
    {
        return android.os.Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /***
     * 获取项目文件夹
     * 
     * @return
     */
    public static File getDir()
    {
        Context context = MyApplication.getInstance();
        String packname = context.getPackageName();
        String name = packname.substring(packname.lastIndexOf(".") + 1,
                packname.length());
        File dir = null;
        if (hasSDCard())
        {
            dir = new File(
                    Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/snxun/" + name);
        }
        else
        {
            dir = context.getCacheDir();
        }
        dir.mkdirs();
        return dir;
    }

    /**
     * 获取项目缓存文件
     * 
     * @return
     */
    public static File getCacheDir()
    {
        File file = new File(
                getDir().getAbsolutePath() + File.separator + "cache");
        if (!file.exists())
        {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 获取项目使用过程中产生的图片文件夹
     * 
     * @return
     */
    public static File getImageDir()
    {
        File file = new File(
                getDir().getAbsolutePath() + File.separator + "image");
        file.mkdirs();
        return file;
    }

    /**
     * 获取系统异常日志路径
     * 
     * @return
     */
    public static File getCaughtLogDir()
    {
        File file = new File(
                getDir().getAbsolutePath() + File.separator + "log");
        file.mkdirs();
        return file;
    }

    /**
     * 删除文件夹
     * 
     * @param dirf
     */
    public static void deleteDir(File dirf)
    {
        if (dirf.isDirectory())
        {
            File[] childs = dirf.listFiles();
            for (int i = 0; i < childs.length; i++)
            {
                deleteDir(childs[i]);
            }
        }
        dirf.delete();
    }

    public static File creatImageFile(String fileName)
    {
        File file = new File(FileUtil.getImageDir(), fileName);
        if (FileUtil.hasSDCard())
        {
            if (!file.exists())
            {
                try
                {
                    file.createNewFile();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    ToastUtil.show("照片创建失败!");
                    return null;
                }
            }
        }
        return file;
    }
}
