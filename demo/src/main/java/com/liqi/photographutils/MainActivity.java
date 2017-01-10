package com.liqi.photographutils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.liql.photograph.PhotographDispose;
import com.liql.photograph.interfa.OnPhotographGetData;
import com.liql.photograph.utils.ImageDispose;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnPhotographGetData<File> {
    private final String FILE_PATH = "photograph/demo";
    private Button mButtonDepot, mButtonCamera;
    private ImageView mImageView;
    //图库和照相机处理对象
    private PhotographDispose mPhotographDispose;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonDepot = (Button) findViewById(R.id.button_depot);
        mButtonDepot.setOnClickListener(this);
        mButtonCamera = (Button) findViewById(R.id.button_camera);
        mButtonCamera.setOnClickListener(this);
        mImageView = (ImageView) findViewById(R.id.imageView);
        //获取照片处理对象
        mPhotographDispose = new PhotographDispose(this, this);
        //设置照片处理后存储在本地的路径
        mPhotographDispose.setPath(FILE_PATH);
        //设置照片压缩之后的大小，默认是1024*1024
        mPhotographDispose.setImageSize(1024 * 1024);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showDialog("图片处理中...").show();
        mPhotographDispose.onActivityResult(requestCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //打开图库
            case R.id.button_depot:
                mPhotographDispose.startPhoto();
                break;
            //打开照相机
            case R.id.button_camera:
                mPhotographDispose.startCamera();
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
}
