package com.xinyi.touhang.utils;

import android.content.Context;
import android.text.TextUtils;

import com.lzy.okgo.model.HttpParams;

/**
 * Created by Administrator on 2018/4/24.
 */

public class DoParams {

    public static HttpParams encryptionparams(Context context, HttpParams httpParams, String user_token) {

        String time = String.valueOf(System.currentTimeMillis() / 1000);
        String salt = "fjsadhfkjashfhwruefhijoishfeu";
        httpParams.put("t", time);

        if (TextUtils.isEmpty(user_token)) {
            user_token = salt + time;
        } else {
            user_token = user_token + salt + time;
        }
        httpParams.put("api_token", EncryUtil.MD5(user_token));
        return httpParams;
    }
}
