package com.liql.photograph;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import com.liql.photograph.interfa.OnDisposeOuterListener;
import com.liql.photograph.interfa.OnPhotoSDKDisposeListener;
import com.liql.photograph.interfa.OnPhotographDisposeListener;
import com.liql.photograph.interfa.OnPhotographGetDataListener;
import com.liql.photograph.utils.ImageDispose;

import java.io.File;
import java.util.Date;

/**
 * Photograph操作处理对象
 * （打开图库和打开照相机兼容到android 7.0。自动压缩到指定大小。）
 * 如有疑问请联系我
 * 联系QQ：543945827
 *
 * @author Liqi
 */
class PhotographDispose implements OnPhotographDisposeListener<File>, OnDisposeOuterListener {
    private static PhotographDispose mPhotographDispose;
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
     * 拍照成功，存储文件对象
     */
    private File mImageFile;
    /**
     * 默认系统存储路径
     */
    private String mSystemPath;
    /**
     * 配置对象
     */
    private PhotographConfigura mPhotographConfigura;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (mPhotographConfigura != null) {
                OnPhotographGetDataListener<File> onPhotographGetDataListener = mPhotographConfigura.getOnPhotographGetDataListener();
                if (null != onPhotographGetDataListener)
                    onPhotographGetDataListener.getPhotographData(msg.obj == null ? null
                            : (File) msg.obj);
            }
        }
    };

    private PhotographDispose() {

    }

    static PhotographDispose getPhotographDispose() {
        return mPhotographDispose = null == mPhotographDispose ? new PhotographDispose() : mPhotographDispose;
    }

    OnDisposeOuterListener init(@NonNull PhotographConfigura photographConfigura) {
        this.mPhotographConfigura = photographConfigura;
        mSystemPath = getSystemPath();
        return mPhotographDispose;
    }

    /**
     * 打开照相机
     */
    @Override
    public void startCamera() {
        camera();
    }

    /**
     * 打开相册
     */
    @Override
    public void startPhoto() {
        photo();
    }

    /**
     * 处理activity界面中图片回调操作
     *
     * @param requestCode
     * @param data
     */
    @Override
    public void onActivityResult(final int requestCode, final Intent data) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (null != mPhotographConfigura) {
                    Activity activity = mPhotographConfigura.getActivity();
                    File file = null;
                    switch (requestCode) {
                        case SDK_19_BOTTOM:
                            if (null != data)
                                file = getFile(new PhotoSDKBottomListener<File>(activity,
                                        PhotographDispose.this), data.getData());

                            break;
                        case SDK_19_TOP:
                            if (null != data)
                                file = getFile(new PhotoSDKTopListener<File>(activity,
                                        PhotographDispose.this), data.getData());
                            break;
                        case SDK_PHOTOGRAPH:
                            if (null != mImageFile)
                                // 压缩拍照照片
                                file = getPhotographDisposeData(mImageFile.getPath());
                            if (mPhotographConfigura.isDelePGImage()) {
                                if (null != file) {
                                    // 删掉没有压缩的照片
                                    mImageFile.delete();
                                    mImageFile = null;
                                }
                            }
                            break;
                    }
                    Message message = mHandler.obtainMessage();
                    message.obj = file;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    @Override
    public void clear() {
        mPhotographConfigura = null;
    }

    /**
     * 采用策略模式去获取File
     *
     * @param onPhotoSDKDisposeListener 获取File的接口
     * @param uri                       uri地址
     * @return
     */
    private File getFile(OnPhotoSDKDisposeListener<File> onPhotoSDKDisposeListener, Uri uri) {
        return onPhotoSDKDisposeListener.getPhotoData(uri);
    }

    /**
     * 相册调用
     */
    private void photo() {
        Activity activity = mPhotographConfigura.getActivity();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setType("image/*");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            activity.startActivityForResult(intent, SDK_19_TOP);
        } else {
            activity.startActivityForResult(intent, SDK_19_BOTTOM);
        }
    }

    /**
     * 相机调用
     */
    private void camera() {
        Activity activity = mPhotographConfigura.getActivity();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mImageFile = getPath("", mPhotographConfigura.getImagePath());
        Uri mOutPutFileUri;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            //7.0需要
            mOutPutFileUri = FileProvider.getUriForFile(activity, "com.liql.photograph.utils", mImageFile);
        } else {
            mOutPutFileUri = Uri.fromFile(mImageFile);
        }
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // 拍照图片地址存储地址写入
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);
        activity.startActivityForResult(intent, SDK_PHOTOGRAPH);
    }

    @Override
    public File getPhotographDisposeData(String path) {
        if (mPhotographConfigura == null)
            return null;

        File file = getPath(getImageName(path), mPhotographConfigura.getCompressPath());
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
        if (mPhotographConfigura == null)
            return null;

        if (null != bitmap) {
            byte[] bytes = ImageDispose.compressBmpFromByte(bitmap, mPhotographConfigura.getImageSize());
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
        File file = new File(mSystemPath + File.separator + path);
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
    private String getImageName(String path) {
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
            path = mPhotographConfigura.getActivity().getFilesDir().toString();
        }
        return path;
    }
}
