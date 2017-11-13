[![](https://jitpack.io/v/liqinew/photoframe.svg)](https://jitpack.io/#liqinew/photoframe)
[![](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-%E6%9D%8E%E5%A5%87-orange.svg)](https://github.com/LiqiNew)
# PhotoFrame是什么?
PhotoFrame是对图库调用和相机调用进行封装的框架，<br>并对用户操作的图片进行指定质量压缩。<br>兼容到android7.0系统
#### PhotographUtils图片压缩机制
当图片第一次被选择时,将进行质量压缩并存进压缩文件夹中.<br>
如果此图片存在压缩文件夹中,那么不进行压缩,将直接从压缩文件中取出

使用方法
-----
#### Gradle
**1：在项目根目录build.gradley**	<br>

```gradle
allprojects {
　　repositories {
  　　//依赖仓库
　　　maven { url 'https://jitpack.io' }
　　}
}
```

**2：依赖PhotoFrame框架**<br>

```gradle
compile 'com.github.liqinew:photoframe:v.1.0.2'
```

## 如何通过PhotoFrame去使用?
**1：通过链式配置对象构建图库和照相机操作对象暴露接口(OnDisposeOuterListener)**<br>
```java
//获取配置对象
PhotographStaticUtils.getPhotographBuilder(this)

//设置图片压缩路径
.setCompressPath(COMPRESS_PATH)

//设置照片暂时存路径
.setImagePath(IMAGE_PATH)

//设置是否删除没有压缩的拍照照片，默认是拍一张删一张。
.setDelePGImage(false)

//设置处理好的图片路径接口
.setOnPhotographGetDataListener(new OnPhotographGetDataListener<File>())

//设置图片压缩大小，默认是1M。
.setImageSize(IMAGE_SIZE)

//构建图库和照相机处理操作对象暴露接口（OnDisposeOuterListener）
.builder();
```
**2：通过OnDisposeOuterListener暴露接口去进行操作**<br>
```java
//处理activity界面中图片系统回调操作
//注：此方法一定要放到Activity中onActivityResult(...)回调方法里面。
OnDisposeOuterListener.onActivityResult(requestCode(请求编码), Intent);

//打开照相机
OnDisposeOuterListener.startCamera();

//打开相册
OnDisposeOuterListener.startPhoto();

//清理数据
//注：为了防止内存泄漏,请在Activity生命周期onDestroy()调用。
OnDisposeOuterListener.clear()
```
**3：ImageDispose图片操作工具对象，提供一系列静态方法对图片或者图片路径进行操作。**<br>
```java
//通过输入流转换成字节数组
ImageDispose.readStream(InputStream inStream);

//通过字节数组和BitmapFactory转换成Bitmap
ImageDispose.getPicFromBytes(byte[] bytes,BitmapFactory.Options opts);

//转换成指定大小的Bitmap
ImageDispose.zoomBitmap(Bitmap bitmap, int w, int h);

//把bitmap转换成字节数组
ImageDispose.bitmap2Bytes(Bitmap bm, int quality);

//切割指定大小的Bitmap对象
ImageDispose.cutterBitmap(Bitmap srcBitmap, int limitWidth,int limitHeight);

//图片压缩，返回bitmap字节数组
ImageDispose.compressBmpFromByte(Bitmap bitmap, long maxsize);

//图片压缩，返回压缩的bitmap
ImageDispose.compressBmpGetBmp(Bitmap bitmap, long maxsize);

//通过地址获取指定大小的Bitmap(质量压缩)
ImageDispose.acquireBitmap(String path, int compressSize);

//Drawable转Bitmap
ImageDispose.drawableToBitmap(Drawable drawable);

//Drawable 指定大小的缩放
ImageDispose.zoomDrawable(Drawable drawable, int w, int h);

//字节数组转换File对象
ImageDispose.acquireByteFile(byte[] byteOne, String filePath);

//获取图片路径切割出来的图片名字，路径需要带扩展名
ImageDispose.getImageName(String path);

//产生随机唯一图片名称，路径需要带扩展名
ImageDispose.getImageRandomName(String path);
```

6.0系统以上需要自己动态添加权限
-----
##### 如果觉得不错,请star给我动力.

### 如有疑问请联系我
技术QQ群：46523908<br>
联系QQ：543945827

# License

    Copyright 2017 Liqi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
