[中文版文档](https://github.com/LiqiNew/PhotoFrame/blob/master/README_CHINESE.md) <br><br>
[![](https://jitpack.io/v/liqinew/photoframe.svg)](https://jitpack.io/#liqinew/photoframe)
[![](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-%E6%9D%8E%E5%A5%87-orange.svg)](https://github.com/LiqiNew)
# What is the PhotoFrame framework?
PhotoFrame is a framework for calling libraries and calling camera packages,<br>
And the user to operate the image specified quality compression.<br>
Compatible with android7.0 system.
#### PhotographUtils picture compression mechanism.
When the picture is selected for the first time, it will be mass compressed and stored in the compressed folder. <br>
If this picture is in a compressed folder, it will not be compressed and taken directly from the compressed file.

Instructions
-----
#### Gradle
**1：In the project root directory build.gradle**	<br>

```gradle
allprojects {
　　repositories {
  　　//Rely on the warehouse
　　　maven { url 'https://jitpack.io' }
　　}
}
```

**2：Depends on the PhotoFrame framework**<br>

```gradle
compile 'com.github.liqinew:photoframe:v.1.0.1'
```

## How do I use it with PhotoFrame?
**1：Building a library and a camera operation object through a chain configuration object Exposure interface (OnDisposeOuterListener)**<br>
```java
//Get the configuration object
PhotographStaticUtils.getPhotographBuilder(this)

//Set the image compression path
.setCompressPath(COMPRESS_PATH)

//Set the photo temporary save path
.setImagePath(IMAGE_PATH)

//Set whether to delete a photo without compression.if true yes,else false. default true.
.setDelePGImage(false)

//Set the interface to get the processed image path
.setOnPhotographGetDataListener(new OnPhotographGetDataListener<File>())

//Set the image compression size, the default is 1M.
.setImageSize(IMAGE_SIZE)

//Building the Gallery and Camera Handling the Object Exposure Interface (OnDisposeOuterListener)
.builder();
```
**2：Through the "OnDisposeOuterListener" interface to operate**<br>
```java
//Processing the interface in the picture system callback operation
//Note: This method must be placed in the Activity "onActivityResult (...)" callback method inside.
OnDisposeOuterListener.onActivityResult(requestCode,Intent);

//Turn on the camera
OnDisposeOuterListener.startCamera();

//Open the album
OnDisposeOuterListener.startPhoto();

//Clean up the data
//Note: To prevent memory leaks, call "onDestroy()" in the Activity lifecycle.
OnDisposeOuterListener.clear()
```

**3：ImageDispose image manipulation tool object, providing a series of static methods to manipulate the image or image path.**<br>

```java
//Converts the input stream into a byte array
ImageDispose.readStream(InputStream inStream);

//Converted to Bitmap by byte array and BitmapFactory
ImageDispose.getPicFromBytes(byte[] bytes,BitmapFactory.Options opts);

//Convert to a specified size Bitmap
ImageDispose.zoomBitmap(Bitmap bitmap, int w, int h);

//Convert bitmap to byte array
ImageDispose.bitmap2Bytes(Bitmap bm, int quality);

//Cut a specified size Bitmap object
ImageDispose.cutterBitmap(Bitmap srcBitmap, int limitWidth,int limitHeight);

//Image compression, return bitmap byte array
ImageDispose.compressBmpFromByte(Bitmap bitmap, long maxsize);

//Image compression, return compressed bitmap
ImageDispose.compressBmpGetBmp(Bitmap bitmap, long maxsize);

//Get the specified size of Bitmap by address, the quality of compression
ImageDispose.acquireBitmap(String path, int compressSize);

//Drawable to Bitmap
ImageDispose.drawableToBitmap(Drawable drawable);

//Drawable The specified size of the zoom
ImageDispose.zoomDrawable(Drawable drawable, int w, int h);

//Byte array conversion File object
ImageDispose.acquireByteFile(byte[] byteOne, String filePath);

//Get the image path to cut out the name of the picture, the path needs to be extended
ImageDispose.getImageName(String path);

//Generate a random unique picture name, the path needs to be extended
ImageDispose.getImageRandomName(String path);
```

6.0 system above need to add their own dynamic permissions
-----
##### If you feel good, please star give me motivation.<br><br>thank you very much.

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
