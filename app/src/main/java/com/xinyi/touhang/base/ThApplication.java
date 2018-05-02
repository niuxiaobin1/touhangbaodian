package com.xinyi.touhang.base;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.tencent.smtt.sdk.QbSdk;
import com.xinyi.touhang.constants.Configer;
import com.xinyi.touhang.third.TLSSocketFactory;
import com.xinyi.touhang.third.WeakHandler;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

/**
 * Created by Niu on 2018/4/16.
 */

public class ThApplication extends Application {


    private Timer timer;//整个app的倒计时
    private boolean isCountDowning;//true：timer运行中，false:timer==null
    private String tel;
    public WeakHandler weakHandler;
    private int countDownNum = 120;//倒计时总数
    private long curTime = 0;//用来标记verifyCodeBean的有效时长
    private LocalBroadcastManager localBroadcastManager;//本地广播manager

    public static final int APP_HANDER_WHAT_FLAG = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            initOkGo();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        weakHandler = new WeakHandler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == APP_HANDER_WHAT_FLAG) {
                    Intent intent = new Intent();
                    intent.setAction(Configer.LOCAL_COUNTDOWN_ACTION);
                    intent.putExtra(Configer.LOCAL_COUNTDOWN_KEY, countDownNum);
                    intent.putExtra(Configer.LOCAL_COUNTDOWN_TEL, tel);
                    localBroadcastManager.sendBroadcast(intent);
                    if (countDownNum == 0) {
                        //倒计时结束
                        stopTimer();
                    } else {
                        countDownNum--;
                    }
                }
                return false;
            }
        });

        QbSdk.initX5Environment(getApplicationContext(), null); QbSdk.setDownloadWithoutWifi(true);
    }


    /**
     * init Okgo
     */
    private void initOkGo() throws NoSuchAlgorithmException, KeyManagementException {

        //配置HttpClient全局参数
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log打印级别，决定了log显示的详细程度
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);
        //使用sp保持cookie，如果cookie不过期，则一直有效
//        builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));
        //使用数据库保持cookie，如果cookie不过期，则一直有效
//        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));
        //使用内存保持cookie，app退出后，cookie消失
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));


        //方法一：信任所有证书,不安全有风险
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
//        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        builder.sslSocketFactory(new TLSSocketFactory(), sslParams1.trustManager);
        //------------------------配置OkGo全局参数-------------------------


        OkGo.getInstance().init(this)                       //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3);                           //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
        //.addCommonHeaders(headers)                      //全局公共头
        //.addCommonParams(params);                       //全局公共参数
    }

    public boolean isCountDowning() {
        return isCountDowning;
    }

    public void setCountDowning(boolean countDowning) {
        isCountDowning = countDowning;
    }

    /**
     * 停止倒计时
     */
    private void stopTimer() {
        isCountDowning = false;
        tel = "";
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 开始倒计时
     */
    public void startTimer(String tel) {
        isCountDowning = true;
        this.tel = tel;
        countDownNum = 120;
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                weakHandler.sendEmptyMessage(APP_HANDER_WHAT_FLAG);
            }
        }, 0, 1000);

    }

}
