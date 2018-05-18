package com.xinyi.touhang.activities;

import android.Manifest;
import android.app.Dialog;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kongqw.permissionslibrary.PermissionsManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;
import com.lzy.okserver.task.XExecutor;
import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.utils.JsonUtils;
import com.xinyi.touhang.utils.UIHelper;
import com.xinyi.touhang.weight.CustomProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebActivity extends BaseActivity implements XExecutor.OnAllTaskEndListener {

    @BindView(R.id.webView)
    WebView webView;

    private Dialog dialog;

    public static final String TITLESTRING = "web_title";
    public static final String TITLEURL = "web_url";
    public static final String TITLECANDOWNLOAD = "web_canDownLoad";
    public static final String DOWNLOADURL = "web_download_url";

    private String name = "";
    private boolean isDownLoad;
    private String downLoadUrl;

    public final static String FILE_PATH = "/touhang/download";
    private OkDownload okDownload;
    private DownloadTask userTask;
    private String downLoadpath;//文件外部本地保存路径 用户可见

    private PermissionsManager mPermissionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        downLoadUrl = getIntent().getStringExtra(DOWNLOADURL);
        name = getIntent().getStringExtra(TITLESTRING);
        isDownLoad = getIntent().getBooleanExtra(TITLECANDOWNLOAD, false);
        initTitle(getIntent().getStringExtra(TITLESTRING));
        if (isDownLoad) {
            initRightTv("", getResources().getDrawable(R.mipmap.download_icon));
        }
        initWebView();
        dialog = CustomProgressDialog.createLoadingDialog(this, "");
        dialog.setCancelable(false);
        dialog.show();
    }



    private void initPermissiton() {
        // 初始化
        mPermissionsManager = new PermissionsManager(this) {
            @Override
            public void authorized(int requestCode) {
                userDownLoad();
            }

            @Override
            public void noAuthorization(int requestCode, String[] lacksPermissions) {
            }

            @Override
            public void ignore(int requestCode) {
                userDownLoad();
            }
        };

        // 要校验的权限
        String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE};
        // 检查权限
        mPermissionsManager.checkPermissions(0, PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 用户做出选择以后复查权限，判断是否通过了权限申请
        mPermissionsManager.recheckPermissions(requestCode, permissions, grantResults);
    }


    @Override
    protected void onRightClick() {
        super.onRightClick();
        initPermissiton();

    }

    private void userDownLoad() {

        okDownload = OkDownload.getInstance();
        downLoadpath = Environment.getExternalStorageDirectory() + FILE_PATH;
        okDownload.getThreadPool().setCorePoolSize(3);
        okDownload.addOnAllTaskEndListener(this);

        GetRequest<File> request = OkGo.<File>get(downLoadUrl);
        userTask = OkDownload.request(name + "_user", request)
                .folder(downLoadpath)
                .fileName(name+"."+getFileType(downLoadUrl))
                .save()
                .register(new DownloadListener(name + "_user") {
                    @Override
                    public void onStart(Progress progress) {
                        dialog = CustomProgressDialog.createLoadingDialog(WebActivity.this,
                                "下载中...");
                        dialog.setCancelable(false);
                        dialog.show();
                    }

                    @Override
                    public void onProgress(Progress progress) {

                    }

                    @Override
                    public void onError(Progress progress) {
                        dialog.dismiss();
                        UIHelper.toastMsg("下载失败");
                    }

                    @Override
                    public void onFinish(File file, Progress progress) {
                        dialog.dismiss();
                        UIHelper.toastMsg("下载成功");
                    }

                    @Override
                    public void onRemove(Progress progress) {
                    }
                });
        userTask.start();
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            return str;
        }
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            return str;
        }


        str = paramString.substring(i + 1);
        return str;
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }

    /**
     * 初始化webview
     */
    private void initWebView() {
        //声明WebSettings子类
        WebSettings webSettings = webView.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);


        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(false); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(true); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //优先使用缓存:
        // webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //缓存模式如下：
        //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        //LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
        //LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        //LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。

        //不使用缓存:
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

        });
        //webview加载完成再显示评论
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100 && dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        webView.loadUrl(getIntent().getStringExtra(TITLEURL));

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // 接受网站证书
            }

        });

    }

    @Override
    public void onAllTaskEnd() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (userTask != null) {
            userTask.remove();
        }
        if (okDownload!=null){

            okDownload.removeOnAllTaskEndListener(this);
        }
    }
}
