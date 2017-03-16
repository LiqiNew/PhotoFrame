package com.liql.photograph;

import android.app.Activity;

/**
 * 图片框架静态操作对象
 * Created by LiQi on 2017/3/16.
 */

public class PhotographStaticUtils {
    public static PhotographConfigura.PhotographBuilder getPhotographBuilder(Activity activity) {
        return new PhotographConfigura.PhotographBuilder(activity);
    }
}
