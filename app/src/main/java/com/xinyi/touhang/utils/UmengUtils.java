package com.xinyi.touhang.utils;

import android.app.Activity;
import android.content.Context;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by Niu on 2018/5/18.
 */

public class UmengUtils {

    public static void UmengLogin(Activity activity, SHARE_MEDIA way, UMAuthListener listener) {
        UMShareAPI.get(activity).getPlatformInfo(activity, way, listener);
    }
}
