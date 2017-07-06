package com.diyou.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.diyou.v5yibao.R;
import com.lidroid.xutils.util.LogUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

public class ImageUtil
{

    public static final int REQUEST_CODE_TAKE_PHOTO = 4001;
    public static final int REQUEST_CODE_PICK_PHOTO = 4002;

    // 调用相机取得照片
    public static boolean takePhoto(Activity activity, File tempFile)
    {
        if (tempFile != null)
        {
            Intent getImageByCamera = new Intent(
                    "android.media.action.IMAGE_CAPTURE");
            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(tempFile));
            activity.startActivityForResult(getImageByCamera,
                    REQUEST_CODE_TAKE_PHOTO);
            return true;
        }
        return false;
    }

    // 从相册取得照片
    public static boolean pickPhoto(Activity activity)
    {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);

        // Intent getImage = new Intent(
        // Intent.ACTION_GET_CONTENT);
        // getImage.addCategory(Intent.CATEGORY_OPENABLE);
        // getImage.setType("image/jpeg");
        // activity.startActivityForResult(getImage, REQUEST_CODE_PICK_PHOTO);
        return true;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth)
    {
        // 源图片的宽度
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > reqWidth)
        {
            // 计算出实际宽度和目标宽度的比率
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap file2Bitmap(File file)
    {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), option);
        option.inSampleSize = calculateInSampleSize(option, 320);
        option.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), option);
        bitmap = loadBitmap(file.getPath(), bitmap);
        try
        {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 30, out))
            {
                out.flush();
                out.close();
                return bitmap;
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static File getFile(Context context, Intent data)
    {
        if (data == null)
            return null;
        Uri originalUri = data.getData();
        if (originalUri == null)
        {
            originalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        Cursor cursor = context.getContentResolver().query(originalUri,
                new String[]
        { MediaStore.Images.Media.DATA }, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc");
        String img_url = null;
        if (cursor != null)
        {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            img_url = cursor.getString(index);
        }
        else
        {
            img_url = originalUri.getPath();
        }
        return new File(img_url);
    }

    /** 从给定的路径加载图片，并指定是否自动旋转方向 */
    public static Bitmap loadBitmap(String imgpath, Bitmap bm)
    {
        int digree = 0;
        ExifInterface exif = null;
        try
        {
            exif = new ExifInterface(imgpath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            exif = null;
        }
        if (exif != null)
        {
            // 读取图片中相机方向信息
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            // 计算旋转角度
            switch (ori)
            {
            case ExifInterface.ORIENTATION_ROTATE_90:
                digree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                digree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                digree = 270;
                break;
            default:
                digree = 0;
                break;
            }
        }
        if (digree != 0)
        {
            // 旋转图片
            Matrix m = new Matrix();
            m.postRotate(digree);
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m,
                    true);
        }
        return bm;
    }

    /**
     * 由本地文件获取希望大小的文件(并识别旋转)
     * 
     * @param f
     * @return
     */
    public static Bitmap getLocalImage(File f, int swidth, int sheight)
    {
        File file = f;
        if (file.exists())
        {
            try
            {
                int degree = readPictureDegree(f.getAbsolutePath());
                file.setLastModified(System.currentTimeMillis());
                FileInputStream in = new FileInputStream(file);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
                int sWidth = swidth;
                int sHeight = sheight;
                int mWidth = options.outWidth;
                int mHeight = options.outHeight;
                LogUtils.e("图片-w:" + mWidth + ",h:" + mHeight);
                int s = 1;
                while ((mWidth / s > sWidth * 2) || (mHeight / s > sHeight * 2))
                {
                    s *= 2;
                }
                options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inSampleSize = s;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inPurgeable = true;

                options.inInputShareable = true;
                try
                {
                    // 4. inNativeAlloc 属性设置为true，可以不把使用的内存算到VM里
                    BitmapFactory.Options.class.getField("inNativeAlloc")
                            .setBoolean(options, true);
                }
                catch (Exception e)
                {
                }
                in.close();
                // 再次获取
                in = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
                LogUtils.e("图片压缩后-w:" + options.outWidth + ",h:"
                        + options.outHeight);
                in.close();
                if (degree > 0)
                {
                    return postRotateBitamp(bitmap, degree);
                }
                else
                {
                    return bitmap;
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 读取图片属性：旋转的角度
     * 
     * @param path
     *            图片绝对路径
     * @return degree旋转的角度 目前AndroidSDK定义的Tag有: TAG_DATETIME 时间日期 TAG_FLASH 闪光灯
     *         TAG_GPS_LATITUDE 纬度 TAG_GPS_LATITUDE_REF 纬度参考 TAG_GPS_LONGITUDE
     *         经度 TAG_GPS_LONGITUDE_REF 经度参考 TAG_IMAGE_LENGTH 图片长
     *         TAG_IMAGE_WIDTH 图片宽 TAG_MAKE 设备制造商 TAG_MODEL 设备型号 TAG_ORIENTATION
     *         方向 TAG_WHITE_BALANCE 白平衡
     */
    public static int readPictureDegree(String path)
    {
        int degree = 0;
        try
        {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation)
            {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 图片旋转
     * 
     * @param bmp
     * @param degree
     * @return
     */
    public static Bitmap postRotateBitamp(Bitmap bmp, float degree)
    {
        // 获得Bitmap的高和宽
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        // 产生resize后的Bitmap对象
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight,
                matrix, true);
        return resizeBmp;
    }

    /**
     * 图片压缩 上传图片时建议compress为30
     * 
     * @param bm
     *            需要压缩的图片
     * @param f
     *            压缩后保存的文件
     * @param compress
     *            压缩率，30表示压缩掉70%; 如果不压缩是100，表示压缩率为0
     */
    public static void compressImage(Bitmap bm, File f, int compress)
    {
        if (bm == null)
            return;
        File file = f;
        if (file.exists())
        {
            file.delete();
        }
        OutputStream os = null;
        try
        {
            file.createNewFile();
            os = new FileOutputStream(file);
            bm.compress(android.graphics.Bitmap.CompressFormat.JPEG, compress,
                    os);
            LogUtils.e("照片大小：" + file.length() / 1024);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (os != null)
                try
                {
                    os.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
        }
    }

    public static DisplayImageOptions getImageOptions()
    {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_1)
                .showImageOnFail(R.drawable.default_1)
                .resetViewBeforeLoading(true).cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        return options;
    }
}
