package com.liql.photograph;

import android.app.Activity;
import android.net.Uri;

import com.liql.photograph.interfa.OnPhotoSDKDisposeListener;
import com.liql.photograph.interfa.OnPhotographDisposeListener;

/**
 * SDK高于19相册处理对象
 * 
 * @author Liqi
 * 
 * @param <T>
 */
class PhotoSDKTopListener<T> implements OnPhotoSDKDisposeListener<T> {
	private OnPhotographDisposeListener<T> mOnPhotographDisposeListener;
	private Activity activity;

	PhotoSDKTopListener(Activity activity,
						OnPhotographDisposeListener<T> onPhotographDisposeListener) {
		this.mOnPhotographDisposeListener = onPhotographDisposeListener;
		this.activity = activity;
	}

	@Override
	public T getPhotoData(Uri uri) {
		if (null != activity) {
			String path = GalleryAddressTool.getPath(activity, uri);
			if (null != mOnPhotographDisposeListener) {
				return mOnPhotographDisposeListener.getPhotographDisposeData(path);
			} else
				return null;
		} else
			return null;
	}

}
