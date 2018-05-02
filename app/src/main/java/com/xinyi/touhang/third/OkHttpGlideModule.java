package com.xinyi.touhang.third;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2018/5/2.
 */

public class OkHttpGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        OkHttpClient mHttpClient = null;
        try {
            mHttpClient = new OkHttpClient().newBuilder()
                    .sslSocketFactory(new TLSSocketFactory())
                    .build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(mHttpClient));
    }
}