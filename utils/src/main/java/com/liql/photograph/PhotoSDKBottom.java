package com.liql.photograph;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.liql.photograph.interfa.OnPhotoSDKDispose;
import com.liql.photograph.interfa.OnPhotographDispose;

/**
 * SDK低于19相册处理对象
 * 
 * @author Liqi
 * 
 * @param <T>
 */
public class PhotoSDKBottom<T> implements OnPhotoSDKDispose<T> {
	private OnPhotographDispose<T> onPhotographDispose;
	private Activity activity;

	PhotoSDKBottom(Activity activity,
			OnPhotographDispose<T> onPhotographDispose) {
		this.activity = activity;
		this.onPhotographDispose = onPhotographDispose;
	}

	@Override
	public T getPhotoData(Uri uri) {
		if (null != activity) {
			String[] proj = { MediaStore.Images.Media.DATA };

			// 好像是android多媒体数据库的封装接口，具体的看Android文档

			Cursor cursor = activity.getContentResolver().query(uri, proj,
					null, null, null);
			// 按我个人理解 这个是获得用户选择的图片的索引值

			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

			// 将光标移至开头 ，这个很重要，不小心很容易引起越界

			cursor.moveToFirst();

			// 最后根据索引值获取图片路径

			String path = cursor.getString(column_index);
			cursor.close();
			if (null != onPhotographDispose) {
				return onPhotographDispose.getPhotographDisposeData(path);
			}
			return null;
		} else
			return null;
	}

}
