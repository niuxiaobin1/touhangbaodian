package com.xinyi.touhang.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kongqw.permissionslibrary.PermissionsManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.R;
import com.xinyi.touhang.adapter.ForumCommentAdapter;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.constants.Configer;
import com.xinyi.touhang.utils.BitmapUtil;
import com.xinyi.touhang.utils.CommonUtils;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DividerDecoration;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.ImageSelectUtil;
import com.xinyi.touhang.utils.JsonUtils;
import com.xinyi.touhang.utils.SpUtils;
import com.xinyi.touhang.utils.StatusBarUtil;
import com.xinyi.touhang.utils.UIHelper;
import com.xinyi.touhang.weight.SelectPhotoPopupwindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForumDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String FORUM_ID = "_forum_id";
    public static final String IMAGE_NAME = "comment_image.jpg";
    private String id = "";
    public final static int PHOTO_GRAPH = 1;// 拍照
    public final static int SELECT_PICTURE = 0;// 相册选择
    @BindView(R.id.rootView)
    RelativeLayout rootView;
    //帖子标题
    @BindView(R.id.forumTv)
    TextView forumTv;
    @BindView(R.id.commitorTv)
    TextView commitorTv;
    @BindView(R.id.commitTime)
    TextView commitTime;
    @BindView(R.id.readNumTv)
    TextView readNumTv;

    @BindView(R.id.bodyLayout)
    LinearLayout bodyLayout;

    //帖子内容
    @BindView(R.id.webView)
    WebView webView;
    //评论
    @BindView(R.id.comment_layout)
    LinearLayout comment_layout;
    @BindView(R.id.comment_RecylerView)
    RecyclerView comment_RecylerView;
    @BindView(R.id.inputTv)
    TextView inputTv;
    @BindView(R.id.share_tv)
    TextView share_tv;
    @BindView(R.id.favo_tv)
    TextView favo_tv;

    //输入部分
    @BindView(R.id.input_layout)
    LinearLayout input_layout;
    @BindView(R.id.empty_view)
    View empty_view;
    //编辑框
    @BindView(R.id.input_et)
    EditText input_et;
    //编辑框字数
    @BindView(R.id.et_content_length)
    TextView et_content_length;
    //发表
    @BindView(R.id.commit_tv)
    TextView commit_tv;

    @BindView(R.id.delete_image)
    ImageView delete_image;

    @BindView(R.id.comment_image)
    ImageView comment_image;

    private JSONObject data;
    private ForumCommentAdapter adapter;

    private int currentKeyboardH;
    private int keyboardH;

    private String favorite_flg;//是否收藏
    public static String filePath = "";
    private Uri imageUri;
    private PermissionsManager mPermissionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_discuss_detail);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        id = getIntent().getStringExtra(FORUM_ID);
        initTitle("讨论交流");
        setViewTreeObserver();
        initListener();
        initWebView();
        adapter = new ForumCommentAdapter(this);
        comment_RecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        comment_RecylerView.addItemDecoration(new DividerDecoration(this, R.color.colorLine, DensityUtil.dip2px(
                this, 0.5f
        )));
        comment_RecylerView.setAdapter(adapter);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        //获取数据
        if (TextUtils.isEmpty(id)) {
            return;
        }
        String user_token = (String) SpUtils.get(ForumDetailActivity.this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("id", id);
        params.put("user_token", user_token);
        OkGo.<String>post(AppUrls.ForumDetailUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(ForumDetailActivity.this, params, user_token))
                .execute(new DialogCallBack(ForumDetailActivity.this, true) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());
                            if (js.getBoolean("result")) {
                                data = js.getJSONObject("data");
                                JSONObject forum = data.getJSONObject("forum");
                                forumTv.setText(forum.getString("name"));
                                commitorTv.setText(forum.getString("author"));
                                commitTime.setText(forum.getString("created"));
                                readNumTv.setText(forum.getString("read_num"));
                                webView.loadDataWithBaseURL(AppUrls.HostAddress, CommonUtils.resolveHtml(forum.getString("content")), "text/html", "UTF-8", null);
                                favorite_flg = data.getString("favorite_flg");
                                if (favorite_flg.equals("0")) {
                                    favo_tv.setSelected(false);
                                } else {
                                    favo_tv.setSelected(true);
                                }
                                JSONArray comments = data.getJSONArray("comments");
                                adapter.addDatas(JsonUtils.ArrayToList(comments, new String[]{
                                        "id", "created", "modified", "content", "good_num", "customer_id",
                                        "forum_id", "image",
                                        "passed", "comment_image", "customer_name", "checked"
                                }));
                            } else {
                                UIHelper.toastMsg(js.getString("message"));
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
                        HandleResponse.handleException(response, ForumDetailActivity.this);
                    }
                });
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
                if (newProgress == 100) {
                    //comment
                    if (bodyLayout.getVisibility() == View.INVISIBLE) {
                        bodyLayout.setVisibility(View.VISIBLE);
                        try {
                            JSONArray comments = data.getJSONArray("comments");
                            adapter.addDatas(JsonUtils.ArrayToList(comments, new String[]{
                                    "id", "created", "modified", "content", "good_num", "customer_id",
                                    "forum_id", "image",
                                    "passed", "comment_image", "customer_name", "checked"
                            }));
                        } catch (JSONException e) {

                        }
                    }

                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // 接受网站证书
            }
        });


    }

    /**
     * 监听软键盘弹出后布局变化
     */
    private void setViewTreeObserver() {

        final ViewTreeObserver swipeRefreshLayoutVTO = bodyLayout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                bodyLayout.getWindowVisibleDisplayFrame(r);
                int statusBarH = StatusBarUtil.getStatusBarHeight(ForumDetailActivity.this);//状态栏高度
                int navigationBarH = StatusBarUtil.getNavigationBarHeight(ForumDetailActivity.this);//状态栏高度
                int screenH = bodyLayout.getRootView().getHeight();

                if (r.top != statusBarH) {
                    //r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，通过getStatusBarHeight获取状态栏高度
                    r.top = statusBarH;
                }

                if (navigationBarH != 0) {
                    //有navigationBar的影响
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        screenH -= navigationBarH;
                    }
                }
                keyboardH = screenH - (r.bottom - r.top);

                if (keyboardH == currentKeyboardH) {//有变化时才处理，否则会陷入死循环
                    return;
                }


                currentKeyboardH = keyboardH;
                if (keyboardH < 150) {//说明是隐藏键盘的情况
                    changeInputEdittextVisibility(View.GONE);
                    return;
                }

                input_layout.setPadding(0, 0, 0, keyboardH - statusBarH);
                input_layout.setVisibility(View.VISIBLE);
//                bodyLayout.setPadding(0, 0, 0, (screenHeight - DensityUtil.dip2px(ConsulationDetailActivity.this, 60)) - r.bottom);

            }

        });
    }

    /**
     * toggle 软键盘显示与否
     *
     * @param visibility
     */
    private void changeInputEdittextVisibility(int visibility) {

        if (visibility == View.GONE) {
            input_layout.setVisibility(View.GONE);
            CommonUtils.hideSoftInput(this, input_et);
        } else {

            input_et.setFocusable(true);
            input_et.setFocusableInTouchMode(true);
            input_et.requestFocus();
            CommonUtils.showSoftInput(this, input_et);

        }
    }

    private void initListener() {
        inputTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeInputEdittextVisibility(View.VISIBLE);

            }
        });
        empty_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeInputEdittextVisibility(View.GONE);
            }
        });
        commit_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发表
                commitComment();
            }
        });

        input_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                if (TextUtils.isEmpty(content)) {
                    et_content_length.setText("0/800");
                } else {
                    et_content_length.setText(content.length() + "/800");
                }
            }
        });

        favo_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFavo();
            }
        });

        comment_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPermissiton();

            }
        });

        delete_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUri = null;
                filePath = "";
                delete_image.setVisibility(View.GONE);
                comment_image.setImageResource(R.mipmap.default_icon);
            }
        });

    }

    /**
     * 关注与取消关注
     */
    private void doFavo() {

        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        if (TextUtils.isEmpty(user_token)) {
            showToast(R.string.commentNeedloginString);
            return;
        }
        String favoUrl = AppUrls.ForumAdd_favoriteUrl;
        HttpParams params = new HttpParams();
        if (!TextUtils.isEmpty(favorite_flg) && !favorite_flg.equals("0")) {
            //取消收藏
            favoUrl = AppUrls.ForumRemove_favoriteUrl;
            params.put("fid", favorite_flg);
        } else {
            //收藏
            params.put("forum_id", id);
        }
        params.put("user_token", user_token);
        OkGo.<String>post(favoUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(ForumDetailActivity.this, params, user_token))
                .execute(new DialogCallBack(ForumDetailActivity.this, false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                JSONObject data = js.getJSONObject("data");
                                if (!TextUtils.isEmpty(favorite_flg) &&
                                        !favorite_flg.equals("0")) {
                                    favorite_flg = "0";
                                    favo_tv.setSelected(false);
                                } else {
                                    favorite_flg = data.getString("favorite");
                                    favo_tv.setSelected(true);
                                }

                            } else {
                                UIHelper.toastMsg(js.getString("message"));
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
                        HandleResponse.handleException(response, ForumDetailActivity.this);
                    }
                });
    }

    private void initPermissiton() {
        // 初始化
        mPermissionsManager = new PermissionsManager(this) {
            @Override
            public void authorized(int requestCode) {
                selectPhonto();
            }

            @Override
            public void noAuthorization(int requestCode, String[] lacksPermissions) {
            }

            @Override
            public void ignore(int requestCode) {
                // Android 6.0 以下系统不校验
                selectPhonto();
            }
        };

        // 要校验的权限
        String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        // 检查权限
        mPermissionsManager.checkPermissions(0, PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 用户做出选择以后复查权限，判断是否通过了权限申请
        mPermissionsManager.recheckPermissions(requestCode, permissions, grantResults);
    }


    private SelectPhotoPopupwindow photoPopupwindow;

    private void selectPhonto() {
        changeInputEdittextVisibility(View.GONE);
        if (photoPopupwindow == null) {
            photoPopupwindow = new SelectPhotoPopupwindow(this, this);
        }

        photoPopupwindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            //拍照
            case R.id.btn_take_photo:
                photoPopupwindow.dismiss();
                filePath = CommonUtils.takepictures(this, Configer.FileDirString, IMAGE_NAME,
                        imageUri, PHOTO_GRAPH);
                break;
            //相册
            case R.id.btn_pick_photo:
                photoPopupwindow.dismiss();
                //打开选择,本次允许选择的数量
                CommonUtils.cardSelect(this, SELECT_PICTURE);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bitmap bm;
            switch (requestCode) {
                case PHOTO_GRAPH:
                    comment_image.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            changeInputEdittextVisibility(View.VISIBLE);
                        }
                    }, 500);

                    bm = BitmapUtil.compressBitmap(filePath, 500, 500);
                    comment_image.setImageBitmap(bm);
                    delete_image.setVisibility(View.VISIBLE);
                    break;
                case SELECT_PICTURE:
                    if (data == null) {
                        return;
                    }
                    comment_image.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            changeInputEdittextVisibility(View.VISIBLE);
                        }
                    }, 500);
                    imageUri = data.getData();
                    filePath = ImageSelectUtil.getPath(this, imageUri);
                    bm = BitmapUtil.compressBitmap(filePath, 500, 500);
                    comment_image.setImageBitmap(bm);
                    delete_image.setVisibility(View.VISIBLE);
                    break;

            }
        }
    }

    /**
     * 发表评论
     */
    private void commitComment() {

        final String comment = input_et.getText().toString().trim();
        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        if (TextUtils.isEmpty(comment)) {
            showToast(R.string.commentEmptyString);
            return;
        }
        if (TextUtils.isEmpty(user_token)) {
            showToast(R.string.commentNeedloginString);
            return;
        }

        HttpParams params = new HttpParams();
        params.put("content", comment);
        params.put("user_token", user_token);
        params.put("forum_id", id);

        //压缩
        if (!TextUtils.isEmpty(filePath)) {
            File uploadTemp = new File(filePath);
            File file = null;
            //如果file大于200kb
            if (uploadTemp.length() / 1024 > 200) {

                Bitmap bitmap = BitmapUtil.compressBitmap(filePath, 1080, 1080);
                //压缩后的bitmap保存为file
                file = new File(Configer.FileDirStringForUpload, "compress.jpg");
                BitmapUtil.compressBmpToFile(bitmap, Configer.FileDirStringForUpload, file);
            } else {
                file = new File(filePath);
            }
            params.put("image", file);
        }

        OkGo.<String>post(AppUrls.ForumAdd_commentUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(ForumDetailActivity.this, params, user_token))
                .execute(new DialogCallBack(ForumDetailActivity.this, true) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                changeInputEdittextVisibility(View.GONE);
                                List<Map<String, String>> list = new ArrayList<>();
                                Map map = new HashMap();
                                map.put("image", SpUtils.get(ForumDetailActivity.this, SpUtils.USERIMAGE, ""));
                                map.put("customer_name", SpUtils.get(ForumDetailActivity.this, SpUtils.USERNAME, ""));
                                map.put("good_num", "0");
                                map.put("modified", "刚刚");
                                map.put("content", comment);
                                map.put("checked", "0");
                                map.put("id", js.getJSONObject("data").getString("cid"));
                                map.put("comment_image", js.getJSONObject("data").getString("url"));
                                list.add(map);
                                adapter.addDatas(list);
                            } else {
                                UIHelper.toastMsg(js.getString("message"));
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
                        HandleResponse.handleException(response, ForumDetailActivity.this);
                    }
                });

    }
}
