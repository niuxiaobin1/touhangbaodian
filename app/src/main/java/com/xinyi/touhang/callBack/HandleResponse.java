package com.xinyi.touhang.callBack;

import android.app.Activity;

import com.lzy.okgo.exception.HttpException;
import com.xinyi.touhang.utils.UIHelper;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;

/**
 * Created by Niu on 2017/12/22.
 */

public class HandleResponse {

    /**
     * 检验response是否等于200
     * 200：成功返回
     * other:错误的操作
     *
     * @param response
     */
    public static void handleReponse(Response response) {
        int code = response.code();
        if (code == 200) {
            //noinspection unchecked

        } else {
            //直接将服务端的错误信息抛出，onError中可以获取
            throw new IllegalStateException("error code：" + code + ",message：" + response.message());
        }

    }

    /**
     * 处理请求结果为error时候的异常情况
     *
     * @param response
     * @param activity
     */
    public static void handleException(final com.lzy.okgo.model.Response<?> response, final Activity activity) {

        Throwable throwable = response.getException();
        int code = response.code();

        if (throwable instanceof UnknownHostException) {
            UIHelper.toastMsg("UnknownHostException");
        } else if (throwable instanceof SocketTimeoutException) {
            UIHelper.toastMsg("SocketTimeoutException");
        } else if (throwable instanceof HttpException) {
            UIHelper.toastMsg("HttpException：" + code);
        } else if (throwable instanceof IllegalStateException) {

            Observable.create(new ObservableOnSubscribe<com.lzy.okgo.model.Response>() {

                @Override
                public void subscribe(ObservableEmitter<com.lzy.okgo.model.Response> e) throws Exception {
                    e.onNext(response);
                }
            }).map(new Function<com.lzy.okgo.model.Response, String>() {

                @Override
                public String apply(com.lzy.okgo.model.Response response) throws Exception {
                    return response.getRawResponse().body().string();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String result) throws Exception {
                            try {
                                final JSONObject js = new JSONObject(result);
                                UIHelper.toastMsg(js.getString("errorMsg"));
                            } catch (Exception e) {
                                UIHelper.toastMsg("JSON Exception ");
                            }
                        }
                    });

//            new Thread() {
//                @Override
//                public void run() {
//                    super.run();
//                    try {
//                        String result = response.getRawResponse().body().string();
//
//                        final JSONObject js = new JSONObject(result);
//                        activity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    UIHelper.toastMsg(js.getString("errorMsg"));
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//
//                    } catch (Exception e) {
//                        activity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                UIHelper.toastMsg("未知JSON解析错误");
//                            }
//                        });
//
//                    }
//                }
//            }.start();

        }
    }
}