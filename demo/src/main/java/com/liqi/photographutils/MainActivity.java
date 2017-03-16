package com.liqi.photographutils;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.liql.photograph.PhotographStaticUtils;
import com.liql.photograph.interfa.OnDisposeOuterListener;
import com.liql.photograph.interfa.OnPhotographGetDataListener;
import com.liql.photograph.utils.ImageDispose;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnPhotographGetDataListener<File> {
    private final String COMPRESSPATH = "photograph/compresspath";
    private final String IMAGEPATH = "photograph/imagepath";
    private final long IMAGESIZE = 1024 * 1024;
    private final int NEED_CAMERA = 200;
    //图库和照相机处理操作对象暴露接口
    private OnDisposeOuterListener mOnDisposeOuterListener;
    private Button mButtonDepot, mButtonCamera;
    private ImageView mImageView;
    private ProgressDialog mDialog;
    private int mTag = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonDepot = (Button) findViewById(R.id.button_depot);
        mButtonDepot.setOnClickListener(this);
        mButtonCamera = (Button) findViewById(R.id.button_camera);
        mButtonCamera.setOnClickListener(this);
        mImageView = (ImageView) findViewById(R.id.imageView);

        mOnDisposeOuterListener = PhotographStaticUtils.getPhotographBuilder(this)
                //设置图片压缩路径
                .setCompressPath(COMPRESSPATH)
                //设置照片暂时存路径
                .setImagePath(IMAGEPATH)
                //设置是否删除没有压缩的拍照照片（默认是拍一张删一张）
                .setDelePGImage(false)
                //设置处理好的图片路径接口
                .setOnPhotographGetDataListener(this)
                //设置图片压缩大小（默认是1M）
                .setImageSize(IMAGESIZE)
                //构建图库和照相机处理操作对象暴露接口（OnDisposeOuterListener）
                .builder();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showDialog("图片处理中...").show();
        mOnDisposeOuterListener.onActivityResult(requestCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case NEED_CAMERA:
                // 如果权限被拒绝，grantResults 为空
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    switch (mTag){
                        case 1:
                            mOnDisposeOuterListener.startPhoto();
                            break;
                        case 2:
                            mOnDisposeOuterListener.startCamera();
                            break;
                    }
                } else {
                    Toast.makeText(this, "改功能需要相机和读写文件权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //打开图库
            case R.id.button_depot:
                mTag = 1;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //检测是否有相机和读写文件权限
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, NEED_CAMERA);
                    } else {
                        mOnDisposeOuterListener.startPhoto();
                    }
                } else {
                    mOnDisposeOuterListener.startPhoto();
                }
                break;
            //打开照相机
            case R.id.button_camera:
                mTag = 2;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //检测是否有相机和读写文件权限
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        this.requestPermissions(new String[]{Manifest.permission.CAMERA}, NEED_CAMERA);
                    } else {
                        mOnDisposeOuterListener.startCamera();
                    }
                } else {
                    mOnDisposeOuterListener.startCamera();
                }
                break;
        }
    }

    @Override
    public void getPhotographData(File file) {
        closeDialog();
        if (null != file)
            mImageView.setImageBitmap(ImageDispose.acquireBitmap(file.getPath(), 2));
    }

    /**
     * 进度条对话框
     *
     * @param content 提示内容
     * @return
     */
    protected ProgressDialog showDialog(String content) {
        if (null == mDialog) {
            mDialog = new ProgressDialog(this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        mDialog.setMessage(content);
        return mDialog;
    }

    /**
     * 关闭对话框
     */
    protected void closeDialog() {
        if (null != mDialog && mDialog.isShowing())
            mDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //防止内存泄漏,请求在界面kill掉调用
        mOnDisposeOuterListener.clear();
    }
}
