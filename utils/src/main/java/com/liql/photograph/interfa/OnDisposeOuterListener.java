package com.liql.photograph.interfa;

import android.content.Intent;

/**
 * 图片处理对象对外暴露方法
 * Created by LiQi on 2017/3/16.
 */

public interface OnDisposeOuterListener {
    /**
     * 处理activity界面中图片回调操作
     *
     * @param requestCode
     * @param data
     */
    void onActivityResult(int requestCode, Intent data);

    /**
     * 清空数据
     */
    void clear();

    /**
     * 打开照相机
     */
    void startCamera();

    /**
     * 打开相册
     */
    void startPhoto();
}
