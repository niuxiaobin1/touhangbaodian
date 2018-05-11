package com.xinyi.touhang.activities;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kongqw.permissionslibrary.PermissionsManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;
import com.lzy.okserver.task.XExecutor;
import com.tencent.smtt.sdk.TbsReaderView;
import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.base.ThApplication;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.utils.CommonUtils;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.UIHelper;
import com.xinyi.touhang.weight.SuperFileView2;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class FileDisplayActivity extends BaseActivity implements XExecutor.OnAllTaskEndListener {

    public final static String FILE_URL = "_file_url";
    public final static String FILE_ID = "_file_id";
    public final static String FILE_NAME = "_file_name";
    public final static String FILE_PATH = "/touhang/download";

    private PermissionsManager mPermissionsManager;
    private String TAG = "FileDisplayActivity";
    private SuperFileView2 mSuperFileView;
    private TextView alertTv;
    private ProgressBar progresBar;
    private TextView downLoadTv;

    private String fileUrl = "";//文件的url
    private String mFileName = "";//文件名

    private boolean isLoading = false;

    private OkDownload okDownload;
    private DownloadTask task;
    private DownloadTask userTask;
    private String path;//文件内部本地保存路径
    private String downLoadpath;//文件外部本地保存路径 用户可见
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_display_activity);
        initPermissiton(0);
        upDateReadNums();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (dialog != null && dialog.isShowing()) {

                } else {
                    dialog = new ProgressDialog(FileDisplayActivity.this);
                    dialog.setMessage("x5內核初始化中...");
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        init();
                    }
                },1000);

            }
        }
    };

    private void init() {
        if (!((ThApplication) getApplication()).isLoadX5Sttus()) {
            handler.sendEmptyMessage(0);
        } else {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            okDownload = OkDownload.getInstance();
            path = getFilesDir().getPath();
            downLoadpath = Environment.getExternalStorageDirectory() + FILE_PATH;
            okDownload.setFolder(path);
            okDownload.getThreadPool().setCorePoolSize(3);
            okDownload.addOnAllTaskEndListener(this);

            fileUrl = getIntent().getStringExtra(FILE_URL);
            mFileName = getIntent().getStringExtra(FILE_NAME);
            initViews();
        }


    }

    @Override
    protected void initViews() {
        super.initViews();

        initTitle(mFileName);

        File dir = new File(downLoadpath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final File file = new File(dir, mFileName);

        downLoadTv = findViewById(R.id.downLoadTv);
        alertTv = findViewById(R.id.alertTv);
        progresBar = findViewById(R.id.progresBar);
        mSuperFileView = (SuperFileView2) findViewById(R.id.mSuperFileView);
        mSuperFileView.setOnGetFilePathListener(new SuperFileView2.OnGetFilePathListener() {
            @Override
            public void onGetFilePath(SuperFileView2 mSuperFileView2) {
                if (file.exists()) {
                    mSuperFileView2.displayFile(file);
                } else {
                    getFilePathAndShowFile(mSuperFileView2);
                }
            }
        });

        if (file.exists()) {
            progresBar.setProgress(100);
            downLoadTv.setText("已下载");
            downLoadTv.setEnabled(false);
        } else {
            progresBar.setProgress(0);
            downLoadTv.setText("下载");
            downLoadTv.setEnabled(true);
        }
        mSuperFileView.show();

        downLoadTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoading) {
                    upDateDownLoadNums();
                    userDownLoad();
                }
            }
        });

    }


    private void userDownLoad() {

        GetRequest<File> request = OkGo.<File>get(fileUrl);
        userTask = OkDownload.request(mFileName + "_user", request)
                .folder(downLoadpath)
                .fileName(mFileName)
                .save()
                .register(new DownloadListener(mFileName + "_user") {
                    @Override
                    public void onStart(Progress progress) {
                        isLoading = true;
                    }

                    @Override
                    public void onProgress(Progress progress) {
                        int p = Integer.parseInt(String.format("%.0f", progress.fraction * 100));
                        progresBar.setProgress(p);
                        downLoadTv.setText("下载中(" + String.format("%.2f", progress.fraction * 100) + "%)");

                    }

                    @Override
                    public void onError(Progress progress) {
                    }

                    @Override
                    public void onFinish(File file, Progress progress) {
                        progresBar.setProgress(100);
                        downLoadTv.setText("下载完成");

                    }

                    @Override
                    public void onRemove(Progress progress) {
                    }
                });
        userTask.start();
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        GetRequest<File> request = OkGo.<File>get(fileUrl);
        task = OkDownload.request(mFileName, request)
                .fileName(mFileName)
                .save()
                .register(new DownloadListener(mFileName) {
                    @Override
                    public void onStart(Progress progress) {
                        alertTv.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgress(Progress progress) {
                        alertTv.setText("加载中..." + String.format("%.2f", progress.fraction * 100) + "%");
                    }

                    @Override
                    public void onError(Progress progress) {

                    }

                    @Override
                    public void onFinish(File file, Progress progress) {

                        alertTv.setText("");
                        alertTv.setVisibility(View.GONE);
                        File file1 = new File(path, mFileName);
                        mSuperFileView.displayFile(file1);
                    }

                    @Override
                    public void onRemove(Progress progress) {
                    }
                });
        task.start();

    }

    private void initPermissiton(final int type) {
        // 初始化
        mPermissionsManager = new PermissionsManager(this) {
            @Override
            public void authorized(int requestCode) {
                if (type == 1) {
                    initDatas();
                } else {
                    init();

                }

            }

            @Override
            public void noAuthorization(int requestCode, String[] lacksPermissions) {
            }

            @Override
            public void ignore(int requestCode) {
                // Android 6.0 以下系统不校验
                if (type == 1) {
                    initDatas();
                } else {
                    init();
                }
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
        if (task != null) {
            task.remove(true);
        }
        if (userTask != null) {
            userTask.remove();
        }
        okDownload.removeOnAllTaskEndListener(this);
    }


    private String getFilePath() {
        return fileUrl;
    }

    private void downLoadFromNet(final String url, final SuperFileView2 mSuperFileView2) {

        //1.网络下载、存储路径、

        initPermissiton(1);


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

    private void upDateReadNums() {
        HttpParams params = new HttpParams();
        params.put("id", getIntent().getStringExtra(FILE_ID));

        OkGo.<String>post(AppUrls.FileDetailUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(FileDisplayActivity.this, params, ""))
                .execute(new DialogCallBack(FileDisplayActivity.this, false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());
                            if (js.getBoolean("result")) {

                            } else {
//                                UIHelper.toastMsg(js.getString("message"));
                            }
                        } catch (JSONException e) {
                            UIHelper.toastMsg(e.getMessage());
                        }
                    }


                    @Override
                    public String convertResponse(okhttp3.Response response) throws Throwable {
                        HandleResponse.handleReponse(response);
                        return super.convertResponse(response);
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        HandleResponse.handleException(response, FileDisplayActivity.this);
                    }
                });
    }

    private void upDateDownLoadNums() {
        HttpParams params = new HttpParams();
        params.put("id", getIntent().getStringExtra(FILE_ID));

        OkGo.<String>post(AppUrls.FileDownloadUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(FileDisplayActivity.this, params, ""))
                .execute(new DialogCallBack(FileDisplayActivity.this, false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());
                            if (js.getBoolean("result")) {

                            } else {
//                                UIHelper.toastMsg(js.getString("message"));
                            }
                        } catch (JSONException e) {
                            UIHelper.toastMsg(e.getMessage());
                        }
                    }


                    @Override
                    public String convertResponse(okhttp3.Response response) throws Throwable {
                        HandleResponse.handleReponse(response);
                        return super.convertResponse(response);
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        HandleResponse.handleException(response, FileDisplayActivity.this);
                    }
                });
    }

}
