package com.liql.photograph.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 图片一系列操作处理对象
 */
public class ImageDispose {
    /**
     * 通过输入流转换成字节数组
     *
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        System.out.println("data转换" + data == null);
        return data;

    }

    /**
     * 通过字节数组和BitmapFactory转换成Bitmap
     *
     * @param bytes
     * @param opts
     * @return
     */
    public static Bitmap getPicFromBytes(byte[] bytes,
                                         BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null) {
                opts.inJustDecodeBounds = false;
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                        opts);
            } else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    /**
     * 转换成指定大小的Bitmap
     *
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return newBmp;
    }

    /**
     * 把bitmap转换成字节数组
     *
     * @param bm      bitmap
     * @param quality 压缩质量百分比
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bm, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
        return baos.toByteArray();
    }

    public static void getFileFromBytes(byte[] b, File file) {
        BufferedOutputStream stream = null;
        try {
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 切割指定大小的Bitmap对象
     *
     * @param srcBitmap
     * @param limitWidth
     * @param limitHeight
     * @return
     */
    public static Bitmap cutterBitmap(Bitmap srcBitmap, int limitWidth,
                                      int limitHeight) {
        float width = srcBitmap.getWidth();
        float height = srcBitmap.getHeight();

        float limitScale = limitWidth / limitHeight;
        float srcScale = width / height;

        if (limitScale > srcScale) {
            // 高小了，所以要去掉多余的高度
            height = limitHeight / ((float) limitWidth / width);
        } else {
            // 宽度小了，所以要去掉多余的宽度
            width = limitWidth / ((float) limitHeight / height);
        }

        Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height,
                Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(srcBitmap, 0, 0, new Paint());
        return bitmap;
    }

    /**
     * 图片压缩->返回bitmap字节数组
     *
     * @param bitmap
     * @param maxsize 指定多大
     * @return
     */
    public static byte[] compressBmpFromByte(Bitmap bitmap, long maxsize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int compress = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        System.out.println("没有压缩前>>>>" + baos.toByteArray().length);
        while (baos.toByteArray().length > maxsize) {
            baos.reset();
            compress -= 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, compress, baos);
        }
        System.out.println("压缩之后>>>>" + baos.toByteArray().length);
        return baos.toByteArray();
    }

    /**
     * 图片压缩->返回压缩的bitmap
     *
     * @param bitmap
     * @param maxsize 指定多大
     * @return
     */
    public static Bitmap compressBmpGetBmp(Bitmap bitmap, long maxsize) {
        if (null != bitmap) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int compress = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            while (baos.toByteArray().length > maxsize) {
                baos.reset();
                compress -= 10;
                bitmap.compress(Bitmap.CompressFormat.JPEG, compress, baos);
            }
        }
        return bitmap;
    }

    /**
     * 通过地址获取指定大小的Bitmap(没有压缩)
     *
     * @param path      地址
     * @param reqWidth  指定宽(无效果)
     * @param reqHeight 指定高(无效果)
     * @return
     */
    public static Bitmap acquireBitmap(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false; // 不进行图片抖动处理
        options.inPreferredConfig = null; // 设置让解码器以最佳方式解码
        if (reqWidth > 0 || reqHeight > 0) {
            options.inJustDecodeBounds = true;
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);
        }
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 通过地址获取指定大小的Bitmap(质量压缩)
     *
     * @param path         本地路径
     * @param compressSize 设置位图缩放比例 width，hight设为原来的四分一（该参数请使用2的整数倍）,这也减小了位图占用的内存大小；
     *                     例如，一张
     *                     分辨率为2048*1536px的图像使用inSampleSize值为4的设置来解码，产生的Bitmap大小约为512
     *                     *384px。 相较于完整图片占用12M的内存，这种方式只需0.75M内存(假设Bitmap配置为//ARGB_8888)。
     * @return
     */
    public static Bitmap acquireBitmap(String path, int compressSize) {
        FileInputStream is = null;
        try {
            is = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 2.为位图设置100K的缓存
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTempStorage = new byte[100 * 1024];
        // 3.设置位图颜色显示优化方式
        // ALPHA_8：每个像素占用1byte内存（8位）
        // ARGB_4444:每个像素占用2byte内存（16位）
        // ARGB_8888:每个像素占用4byte内存（32位）
        // RGB_565:每个像素占用2byte内存（16位）
        // Android默认的颜色模式为ARGB_8888，这个颜色模式色彩最细腻，显示质量最高。但同样的，占用的内存//也最大。也就意味着一个像素点占用4个字节的内存。我们来做一个简单的计算题：3200*2400*4
        // bytes //=30M。如此惊人的数字！哪怕生命周期超不过10s，Android也不会答应的。
        opts.inPreferredConfig = Config.RGB_565;
        // 4.设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
        opts.inPurgeable = true;
        // 5.设置位图缩放比例
        // width，hight设为原来的四分一（该参数请使用2的整数倍）,这也减小了位图占用的内存大小；例如，一张//分辨率为2048*1536px的图像使用inSampleSize值为4的设置来解码，产生的Bitmap大小约为//512*384px。相较于完整图片占用12M的内存，这种方式只需0.75M内存(假设Bitmap配置为//ARGB_8888)。
        opts.inSampleSize = compressSize;
        // 6.设置解码位图的尺寸信息
        opts.inInputShareable = true;
        // 7.解码位图
        Bitmap btp = BitmapFactory.decodeStream(is, null, opts);
        try {
            if (is != null) {
                is.close();
                is = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return btp;
    }

    // 计算图片的缩放值
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final double heightRatio = Math.ceil((float) height
                    / (float) reqHeight);
            final double widthRatio = Math.ceil((float) width
                    / (float) reqWidth);
            inSampleSize = (int) Math.ceil(Math.max(heightRatio, widthRatio));
        }
        return inSampleSize;
    }

    /**
     * Drawable转Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                : Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Drawable 指定大小的缩放
     *
     * @param drawable
     * @param w
     * @param h
     * @return
     */
    public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // drawable转换成bitmap
        Bitmap oldbmp = drawableToBitmap(drawable);
        // 创建操作图片用的Matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放比例
        float sx = ((float) w / width);
        float sy = ((float) h / height);
        // 设置缩放比例
        matrix.postScale(sx, sy);
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true);
        return new BitmapDrawable(newbmp);
    }

    /**
     * 字节数组转换File对象
     *
     * @param byteOne
     * @param filePath
     * @return
     * @throws Exception
     */
    public static File acquireByteFile(byte[] byteOne, String filePath) {
        File file = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            file = new File(filePath);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(byteOne);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file;
    }

    /**
     * 获取图片路径切割出来的图片名字
     *
     * @param path 图片路径-->需要带扩展名
     * @return
     */
    public static String getImageName(String path) {
        String imageName = "";
        String[] split = path.split("/");
        if (split.length > 0) {
            imageName = split[split.length - 1];
        }
        return imageName;
    }

    /**
     * 产生随机唯一图片名称
     *
     * @param path 图片路径-->需要带扩展名
     * @return
     */
    public static String getImageRandomName(String path) {
        String randomName = System.currentTimeMillis() + "";
        String imageName = getImageName(path);
        if (!"".equals(imageName.trim())) {
            String[] split = imageName.split("\\.");
            if (split.length > 1) {
                String extendName = split[split.length - 1];
                if (!"".equals(split[split.length - 1].trim())) {
                    randomName += "." + extendName;
                }
            }
        } else {
            randomName += ".jpg";
        }
        return randomName;
    }
}
