package com.xinyi.touhang.activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
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

import com.kongqw.permissionslibrary.PermissionsManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.task.XExecutor;
import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.utils.CommonUtils;
import com.xinyi.touhang.weight.SuperFileView2;

import java.io.File;

public class FileDisplayActivityActivity extends BaseActivity implements XExecutor.OnAllTaskEndListener {

    private PermissionsManager mPermissionsManager;

    private String TAG = "FileDisplayActivity";
    SuperFileView2 mSuperFileView;

    private String filePath = "http://39.108.183.178/upload/file/023145353.docx";
    private String mFileName = "doc.docx";

    private boolean isShowing = false;

    private OkDownload okDownload;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_display_activity);
        okDownload = OkDownload.getInstance();
        path = Environment.getExternalStorageDirectory().getPath();
        okDownload.setFolder(path);
        okDownload.getThreadPool().setCorePoolSize(2);
        okDownload.addOnAllTaskEndListener(this);
        init();
    }


    @Override
    protected void initDatas() {
        super.initDatas();

        GetRequest<File> request = OkGo.<File>get(filePath);
        OkDownload.request("", request)
                .fileName(mFileName)
                .save()
                .register(new DownloadListener("") {
                    @Override
                    public void onStart(Progress progress) {
                    }

                    @Override
                    public void onProgress(Progress progress) {
                    }

                    @Override
                    public void onError(Progress progress) {

                    }

                    @Override
                    public void onFinish(File file, Progress progress) {
                        mSuperFileView.displayFile(new File(path, mFileName));
                    }

                    @Override
                    public void onRemove(Progress progress) {

                    }
                }).start();


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

    public void init() {

        mSuperFileView = (SuperFileView2) findViewById(R.id.mSuperFileView);
        mSuperFileView.setOnGetFilePathListener(new SuperFileView2.OnGetFilePathListener() {
            @Override
            public void onGetFilePath(SuperFileView2 mSuperFileView2) {
                getFilePathAndShowFile(mSuperFileView2);
            }
        });

        mSuperFileView.show();

    }

    private void getFilePathAndShowFile(SuperFileView2 mSuperFileView2) {


        if (getFilePath().contains("http")) {//网络地址要先下载

            downLoadFromNet(getFilePath(), mSuperFileView2);

        } else {
            mSuperFileView2.displayFile(new File(getFilePath()));
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSuperFileView != null) {
            mSuperFileView.onStopDisplay();
        }
        okDownload.removeOnAllTaskEndListener(this);
    }


    private String getFilePath() {
        return filePath;
    }

    private void downLoadFromNet(final String url, final SuperFileView2 mSuperFileView2) {

        //1.网络下载、存储路径、

        initPermissiton();


    }


    /***
     * 根据链接获取文件名（带类型的），具有唯一性
     *
     * @param url
     * @return
     */
    private String getFileName(String url) {
        String fileName = CommonUtils.hashKey(url) + "." + getFileType(url);
        return fileName;
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
    public void onAllTaskEnd() {

    }


}
