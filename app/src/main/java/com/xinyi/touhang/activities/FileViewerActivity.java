package com.xinyi.touhang.activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.kongqw.permissionslibrary.PermissionsManager;
import com.tencent.smtt.sdk.TbsReaderView;
import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.utils.JsonUtils;
import com.xinyi.touhang.weight.SuperFileView2;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileViewerActivity extends BaseActivity implements TbsReaderView.ReaderCallback {

//    @BindView(R.id.webView)
//    WebView webView;

    @BindView(R.id.mSuperFileView)
    SuperFileView2 mSuperFileView;

    private String mFileUrl = "http://offer.fancyqi.com/xinwen.pdf";
    private String mFileName = "xinwen.pdf";
    private String filePath = "";

    private boolean isShowing = false;

    private DownloadManager mDownloadManager;
    private long mRequestId;
    private DownloadObserver mDownloadObserver;

    private TbsReaderView mTbsReaderView;

    private PermissionsManager mPermissionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_viewer);
        ButterKnife.bind(this);
        initViews();


    }

    @Override
    protected void initViews() {
        super.initViews();
//        initWebView();
        mSuperFileView.setOnGetFilePathListener(new SuperFileView2.OnGetFilePathListener() {
            @Override
            public void onGetFilePath(SuperFileView2 mSuperFileView2) {
                getFilePathAndShowFile(mSuperFileView2);
            }
        });
        filePath = new File(Environment.DIRECTORY_DOWNLOADS, mFileName).getAbsolutePath();
        initPermissiton();
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mFileUrl));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mFileName);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        mRequestId = mDownloadManager.enqueue(request);
        mDownloadObserver = new DownloadObserver(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, mDownloadObserver);

    }

    private void loadFile() {

        mSuperFileView.show();
    }

    private void getFilePathAndShowFile(SuperFileView2 mSuperFileView2) {
        isShowing = true;
        mSuperFileView2.displayFile(new File(filePath));

    }


    private void initPermissiton() {
        // 初始化
        mPermissionsManager = new PermissionsManager(this) {
            @Override
            public void authorized(int requestCode) {
                initDatas();
            }

            @Override
            public void noAuthorization(int requestCode, String[] lacksPermissions) {
            }

            @Override
            public void ignore(int requestCode) {
                // Android 6.0 以下系统不校验
                initDatas();
            }
        };

        // 要校验的权限
        String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        // 检查权限
        mPermissionsManager.checkPermissions(0, PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 用户做出选择以后复查权限，判断是否通过了权限申请
        mPermissionsManager.recheckPermissions(requestCode, permissions, grantResults);
    }

    private void queryDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(mRequestId);
        Cursor cursor = null;
        try {
            cursor = mDownloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                //已经下载的字节数
                int currentBytes = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //总需下载的字节数
                int totalBytes = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                //状态所在的列索引
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                Log.e("nxb---downloadUpdate: ", currentBytes + " " + totalBytes + " " + status);
//                mDownloadBtn.setText("正在下载：" + currentBytes + "/" + totalBytes);
                if ( DownloadManager.STATUS_SUCCESSFUL == status && !isShowing){
                    loadFile();
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    private class DownloadObserver extends ContentObserver {

        private DownloadObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            queryDownloadStatus();
        }
    }

//    /**
//     * 初始化webview
//     */
//    private void initWebView() {
//        //声明WebSettings子类
//        WebSettings webSettings = webView.getSettings();
//
//        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
//        webSettings.setJavaScriptEnabled(true);
//
//
//        //设置自适应屏幕，两者合用
//        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
//        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//
//        //缩放操作
//        webSettings.setSupportZoom(false); //支持缩放，默认为true。是下面那个的前提。
//        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
//        webSettings.setDisplayZoomControls(true); //隐藏原生的缩放控件
//
//        //其他细节操作
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
//        webSettings.setAllowFileAccess(true); //设置可以访问文件
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
//        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
//        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        //优先使用缓存:
//        // webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        //缓存模式如下：
//        //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
//        //LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
//        //LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
//        //LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
//
//        //不使用缓存:
//        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//
//
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return false;
//            }
//        });
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDownloadObserver != null) {
            getContentResolver().unregisterContentObserver(mDownloadObserver);
        }
    }
}
