# PhotoFrame是什么?
PhotoFrame是对图库调用和相机调用进行封装的框架，<br>并对用户操作的图片进行指定质量压缩。<br>兼容到android7.0系统
#### PhotographUtils图片压缩机制
当图片第一次被选择时,将进行质量压缩并存进压缩文件夹中.<br>
如果此图片存在压缩文件夹中,那么不进行压缩,将直接从压缩文件中取出

使用方法
-----
#### Gradle
**1：在项目根目录build.gradley**	<br>
allprojects {<br>
　　repositories { <br>
  　　//依赖仓库	<br>		
　　　maven { url 'https://jitpack.io' }<br>
　　}<br>
}<br>
**2：依赖PhotoFrame框架**<br>
compile 'com.github.liqinew:photoframe:v.1.0.0'
## 如何通过PhotoFrame去使用?
**1：通过链式配置对象构建图库和照相机操作对象暴露接口(OnDisposeOuterListener)**<br>
//获取配置对象<br>
PhotographStaticUtils.getPhotographBuilder(this)<br><br>
//设置图片压缩路径<br>
.setCompressPath(COMPRESSPATH)<br><br>
//设置照片暂时存路径<br>
.setImagePath(IMAGEPATH)<br><br>
//设置是否删除没有压缩的拍照照片（默认是拍一张删一张）<br>
.setDelePGImage(false)<br><br>
//设置处理好的图片路径接口<br>
.setOnPhotographGetDataListener(new OnPhotographGetDataListener\<File>())<br><br>
//设置图片压缩大小（默认是1M）<br>
.setImageSize(IMAGESIZE)<br><br>
//构建图库和照相机处理操作对象暴露接口（OnDisposeOuterListener）<br>
.builder();<br><br>
**2：通过OnDisposeOuterListener暴露接口去进行操作**<br>
(！注：此方法一定要放到Activity中onActivityResult(int requestCode, int resultCode, Intent data)回调方法里面)<br>
//处理activity界面中图片系统回调操作<br>
OnDisposeOuterListener.onActivityResult(requestCode(请求编码), Intent);<br><br>
//打开照相机<br>
OnDisposeOuterListener.startCamera();<br><br>
//打开相册<br>
OnDisposeOuterListener.startPhoto();<br><br>
(！注：为了防止内存溢出,请在Activity生命周期onDestroy()调用)<br>
//清理数据<br>
OnDisposeOuterListener.clear()<br><br>
##### 如果觉得不错,请star给我动力.

### 如有疑问请联系我
技术QQ群：46523908<br>
联系QQ：543945827
