package com.liql.photograph;

import android.app.Activity;
import android.net.Uri;

import com.liql.photograph.interfa.OnPhotoSDKDispose;
import com.liql.photograph.interfa.OnPhotographDispose;

/**
 * SDK高于19相册处理对象
 * 
 * @author Liqi
 * 
 * @param <T>
 */
public class PhotoSDKTop<T> implements OnPhotoSDKDispose<T> {
	private OnPhotographDispose<T> onPhotographDispose;
	private Activity activity;

	PhotoSDKTop(Activity activity,
			OnPhotographDispose<T> onPhotographDispose) {
		this.onPhotographDispose = onPhotographDispose;
		this.activity = activity;
	}

	@Override
	public T getPhotoData(Uri uri) {
		if (null != activity) {
			String path = GalleryAddressTool.getPath(activity, uri);
			if (null != onPhotographDispose) {
				return onPhotographDispose.getPhotographDisposeData(path);
			} else
				return null;
		} else
			return null;
	}

}
