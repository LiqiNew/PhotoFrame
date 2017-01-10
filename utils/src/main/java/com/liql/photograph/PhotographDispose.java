package com.liql.photograph;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.liql.photograph.interfa.OnPhotoSDKDispose;
import com.liql.photograph.interfa.OnPhotographDispose;
import com.liql.photograph.interfa.OnPhotographGetData;
import com.liql.photograph.utils.ImageDispose;

import java.io.File;
import java.util.Date;

/**
 * Photograph操作处理对象
 * （打开图库和打开照相机兼容到android 6.0。
 * 自动压缩到指定大小。
 * ）
 * 如有疑问请联系我
 * 联系QQ：543945827
 *
 * @author Liqi
 */
public class PhotographDispose implements OnPhotographDispose<File> {
    /**
     * 图库的标记19版本以下
     */
    private final int SDK_19_BOTTOM = -0x1;
    /**
     * 图库的标记19版本以上
     */
    private final int SDK_19_TOP = 0x1;
    /**
     * 相机的标记
     */
    private final int SDK_PHOTOGRAPH = 0x2;
    /**
     * 默认系统存储路径
     */
    private String systemPath;
    /**
     * 默认压缩图片存储文件夹路径
     */
    private String compressPath = "LiQi/compress/compressImage";
    /**
     * 拍照暂时存储默认路径
     */
    private String imagePath = "LiQi/compress/image";
    /**
     * 拍照成功，存储文件对象
     */
    private File imageFile;
    /**
     * 默认图片压缩大小
     */
    private long imageSize = 1024 * 1024;
    private Activity activity;
    private OnPhotographGetData<File> onPhotographGetData;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (null != onPhotographGetData)
                onPhotographGetData.getPhotographData(msg.obj == null ? null
                        : (File) msg.obj);
        }

        ;
    };

    public PhotographDispose(Activity activity,
                             OnPhotographGetData<File> onPhotographGetData) {
        this.activity = activity;
        this.onPhotographGetData = onPhotographGetData;
        systemPath = getSystemPath();
    }

    /**
     * 设置拍照图片暂时存储地址
     *
     * @param path 图片暂时存储文件夹路径
     * @return
     */
    public PhotographDispose setImagePath(String path) {
        this.imagePath = path;
        return this;
    }

    /**
     * 设置压缩图片存储文件夹路径
     *
     * @param path 文件夹名字
     * @return
     */
    public PhotographDispose setPath(String path) {
        this.compressPath = path;
        return this;
    }

    /**
     * 设置图片压缩大小
     *
     * @param imageSize
     * @return
     */
    public PhotographDispose setImageSize(long imageSize) {
        this.imageSize = imageSize;
        return this;
    }

    /**
     * 打开照相机
     */
    public void startCamera() {
        camera();
    }

    /**
     * 打开相册
     */
    public void startPhoto() {
        photo();
    }

    /**
     * 获取调用处理返回结果
     *
     * @param requestCode
     * @param data
     */
    public void onActivityResult(final int requestCode, final Intent data) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                File file = null;
                switch (requestCode) {
                    case SDK_19_BOTTOM:
                        if (null != data)
                            file = getFile(new PhotoSDKBottom<File>(activity,
                                    PhotographDispose.this), data.getData());

                        break;
                    case SDK_19_TOP:
                        if (null != data)
                            file = getFile(new PhotoSDKTop<File>(activity,
                                    PhotographDispose.this), data.getData());
                        break;
                    case SDK_PHOTOGRAPH:
                        if (null != imageFile)
                            // 压缩拍照照片
                            file = getPhotographDisposeData(imageFile.getPath());
                        if (null != file) {
                            // 删掉没有压缩的照片
                            imageFile.delete();
                            imageFile = null;
                        }
                        break;
                }
                Message message = handler.obtainMessage();
                message.obj = file;
                handler.sendMessage(message);
            }
        }).start();
    }

    /**
     * 采用策略模式去获取File
     *
     * @param onPhotoSDKDispose 获取File的接口
     * @param uri               uri地址
     * @return
     */
    private File getFile(OnPhotoSDKDispose<File> onPhotoSDKDispose, Uri uri) {
        return onPhotoSDKDispose.getPhotoData(uri);
    }

    /**
     * 相册调用
     */
    private void photo() {
        if (null != activity) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                activity.startActivityForResult(intent, SDK_19_TOP);
            } else {
                activity.startActivityForResult(intent, SDK_19_BOTTOM);
            }
        }
    }

    /**
     * 相机调用
     */
    private void camera() {
        if (null != activity) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageFile = getPath("", imagePath);
            Uri mOutPutFileUri = Uri.fromFile(imageFile);
            // 拍照图片地址存储地址写入
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);
            activity.startActivityForResult(intent, SDK_PHOTOGRAPH);
        }
    }

    @Override
    public File getPhotographDisposeData(String path) {
        File file = getPath(getImageName(path), compressPath);
        // 判断用户选择的图片是否已经压缩过
        if (!file.exists()) {
            Bitmap bitmap = ImageDispose.acquireBitmap(path, 0, 0);
            // 压缩图片
            file = bitmapToFile(bitmap, file.getPath());
            if (null != bitmap) {
                // 把获取的bitmap对象回收掉。防止内存溢出
                bitmap.recycle();
                System.gc();
            }
        }
        return file;
    }

    /**
     * 把bitmap转换成File对象
     *
     * @param bitmap
     * @param path   压缩图片存储路径
     * @return
     */
    private File bitmapToFile(Bitmap bitmap, String path) {
        if (null != bitmap) {
            byte[] bytes = ImageDispose.compressBmpFromByte(bitmap, imageSize);
            return ImageDispose.acquireByteFile(bytes, path);
        }
        return null;
    }

    /**
     * 获取图片压缩存储之后存储路径
     *
     * @param imageName 图片名字
     * @param path      存储图片的文件地址
     * @return
     */
    private File getPath(String imageName, String path) {
        if ("".equals(imageName)) {
            Date date = new Date();
            long time = date.getTime();
            imageName = String.valueOf(time) + ".jpg";
        }
        File file = new File(systemPath + File.separator + path);
        // 判断存储图片的文件夹是否存在
        if (!file.exists())
            file.mkdirs();

        return new File(file.getPath(), imageName);
    }

    /**
     * 获取图片路径切割出来的图片名字
     *
     * @param path 图片路径
     * @return
     */
    public String getImageName(String path) {
        String imageName = "";
        String[] split = path.split("/");
        if (null != split)
            imageName = split[split.length - 1];
        return imageName;
    }

    /**
     * 获取手机存储图片保存路径
     *
     * @return
     */
    private String getSystemPath() {
        String path = "";
        // 判断是否安装有SD卡
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().toString();
        } else {
            path = activity.getCacheDir().toString();
        }
        return path;
    }
}
