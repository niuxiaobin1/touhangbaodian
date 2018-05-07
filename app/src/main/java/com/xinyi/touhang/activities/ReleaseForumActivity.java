package com.xinyi.touhang.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kongqw.permissionslibrary.PermissionsManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.constants.Configer;
import com.xinyi.touhang.utils.BitmapUtil;
import com.xinyi.touhang.utils.CommonUtils;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.ImageSelectUtil;
import com.xinyi.touhang.utils.JsonUtils;
import com.xinyi.touhang.utils.SpUtils;
import com.xinyi.touhang.utils.StatusBarUtil;
import com.xinyi.touhang.utils.UIHelper;
import com.xinyi.touhang.weight.SelectPhotoPopupwindow;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReleaseForumActivity extends BaseActivity implements View.OnClickListener {

    public static final String IMAGE_NAME = "forumImage.jpg";
    public final static int PHOTO_GRAPH = 1;// 拍照
    public final static int SELECT_PICTURE = 0;// 相册选择
    @BindView(R.id.title_et)
    EditText title_et;

    @BindView(R.id.webView)
    WebView webView;

    @BindView(R.id.bodyLayout)
    LinearLayout bodyLayout;

    @BindView(R.id.bottomLayout)
    LinearLayout bottomLayout;

    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;

    @BindView(R.id.addImage)
    ImageView addImage;

    private List<Map<String, String>> typeLists;

    private String typeId = "";

    private int currentKeyboardH;
    private int keyboardH;
    private boolean isFocus = false;
    private PermissionsManager mPermissionsManager;

    public static String filePath = "";
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_forum);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }


    @Override
    protected void initViews() {
        super.initViews();
        initTitle("发帖");
        initRightTv("发送", R.color.colorTabSelectedIndicator);
        typeLists = new ArrayList<>();
        setViewTreeObserver();
        initListener();
        initWebView();
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
        webSettings.setBuiltInZoomControls(false); //设置内置的缩放控件。若为false，则该WebView不可缩放
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

        // 通过addJavascriptInterface()将Java对象映射到JS对象
        //参数1：Javascript对象名
        //参数2：Java对象名
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "jsInterface");//AndroidtoJS类对象映射到js的test对象


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });

        webView.loadUrl("file:///android_asset/richTextEditor.html");
    }

    private void initListener() {
        title_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.showInputMethod(title_et);
            }
        });
        title_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isFocus = hasFocus;
                if (!isFocus) {
                    bottomLayout.setVisibility(View.VISIBLE);
                } else {
                    bottomLayout.setVisibility(View.GONE);
                }
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideSoftInput(ReleaseForumActivity.this, title_et);
                initPermissiton();
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

    private SelectPhotoPopupwindow photoPopupwindow;

    private void selectPhonto() {
        if (photoPopupwindow == null) {
            photoPopupwindow = new SelectPhotoPopupwindow(this, this);
        }

        photoPopupwindow.showAtLocation(bodyLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

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
                int statusBarH = StatusBarUtil.getStatusBarHeight(ReleaseForumActivity.this);//状态栏高度
                int navigationBarH = StatusBarUtil.getNavigationBarHeight(ReleaseForumActivity.this);//状态栏高度
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
                    bodyLayout.setPadding(0, statusBarH, 0, 0);
                    bottomLayout.setVisibility(View.GONE);
                    return;
                }
                bodyLayout.setPadding(0, statusBarH, 0, keyboardH - statusBarH);
//                bodyLayout.setPadding(0, 0, 0, (screenHeight - DensityUtil.dip2px(ConsulationDetailActivity.this, 60)) - r.bottom);
                if (!isFocus) {
                    bottomLayout.setVisibility(View.VISIBLE);
                }

            }

        });
    }


    @Override
    protected void onRightClick() {
        super.onRightClick();
//        webView.evaluateJavascript("richTextEditor:window.jsInterface.processHTML('<head>'" +
//                "+document.getElementsByTagName('html')[0].innerHTML+'</head>');", new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//                //此处为 js 返回的结果
//                Log.e("nxb",value);
//            }
//        });
        webView.evaluateJavascript("richTextEditor:window.jsInterface.processHTML(" +
                "document.getElementById('content').innerHTML);", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                //此处为 js 返回的结果

            }
        });


    }

    @Override
    protected void initDatas() {
        super.initDatas();
        HttpParams params = new HttpParams();
        OkGo.<String>post(AppUrls.ForumType_listUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(ReleaseForumActivity.this, params, ""))
                .execute(new DialogCallBack(ReleaseForumActivity.this, false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                JSONArray type_list = js.getJSONObject("data")
                                        .getJSONArray("type_list");
                                typeLists.clear();
                                typeLists.addAll(JsonUtils.ArrayToList(type_list, new String[]{
                                        "id", "name", "created", "modified"
                                }));
                                if (typeLists.size() > 0) {
                                    typeId = typeLists.get(0).get("id");
                                }
                                initMagicIndicator();
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
                        HandleResponse.handleException(response, ReleaseForumActivity.this);
                    }
                });
    }

    private void initMagicIndicator() {
        if (typeLists == null) {
            return;
        }
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setScrollPivotX(0.35f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return typeLists == null ? 0 : typeLists.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                final SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(typeLists.get(index).get("name"));
                simplePagerTitleView.setTextSize(13);
                simplePagerTitleView.setNormalColor(Color.parseColor("#4A4A4A"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#000000"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        magicIndicator.onPageSelected(index);
                        magicIndicator.onPageScrolled(index, 0, 0);
                        typeId = typeLists.get(index).get("id");
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                WrapPagerIndicator indicator = new WrapPagerIndicator(context);
                indicator.setFillColor(Color.parseColor("#FFD700"));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
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
                    addToHtml();
                    break;
                case SELECT_PICTURE:
                    if (data == null) {
                        return;
                    }
                    imageUri = data.getData();
                    filePath = ImageSelectUtil.getPath(this, imageUri);
                    addToHtml();

                    break;

            }
        }
    }


    private void addToHtml() {

        String localPath = filePath;
        String ImageName = "android" + System.currentTimeMillis();
        String hostPath = AppUrls.UploadForum_imageUrl + ImageName + ".jpg";
        // Android版本变量
        final int version = Build.VERSION.SDK_INT;
        // 因为该方法在 Android 4.4 版本才可使用，所以使用时需进行版本判断
        if (version < 18) {
            webView.loadUrl("richTextEditor:insertImage('" + hostPath + "','" + localPath + "')");
        } else {
            webView.evaluateJavascript("richTextEditor:insertImage('" + hostPath + "','" + localPath + "')", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //此处为 js 返回的结果
                }
            });
        }
        File upLoadFile = new File(localPath);
        upLoadImage(ImageName, upLoadFile);
    }


    private void upLoadImage(String name, File image) {
        String user_token = (String) SpUtils.get(ReleaseForumActivity.this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        File file = null;
        //如果file大于200kb
        if (image.length() / 1024 > 200) {
            Bitmap bitmap = BitmapUtil.compressBitmap(image.getPath(), 1080, 1080);
            //压缩后的bitmap保存为file
            file = new File(Configer.FileDirStringForUpload, System.currentTimeMillis() + "compress.jpg");
            BitmapUtil.compressBmpToFile(bitmap, Configer.FileDirStringForUpload, file);
        } else {
            file = image;
        }
        params.put("image", file);
        params.put("name", name);
        params.put("user_token", user_token);
        OkGo.<String>post(AppUrls.ForumForum_imageUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(ReleaseForumActivity.this, params, user_token))
                .execute(new DialogCallBack(ReleaseForumActivity.this, false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {

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
                        HandleResponse.handleException(response, ReleaseForumActivity.this);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 用户做出选择以后复查权限，判断是否通过了权限申请
        mPermissionsManager.recheckPermissions(requestCode, permissions, grantResults);
    }

    class MyJavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html) {
            // process the html as needed by the app
            if (TextUtils.isEmpty(html)) {
                showToast(R.string.contentEmptyString);
                return;
            }

            final String titleString = title_et.getText().toString().trim();
            if (TextUtils.isEmpty(titleString)) {
                showToast(R.string.NotitleString);
                return;
            }


            final StringBuffer buffer = new StringBuffer();
            int index = 0;
            int index2 = 0;

            index = html.indexOf("<img src=\"");
            index2 = html.indexOf("id=\"");
            while (index != -1 && index2 != -1) {
                buffer.append(html.substring(0, index));
                buffer.append("<img src=\"");
//                buffer.append(html.substring(index2 + 4, html.length()));
                html = html.substring(index2 + 4, html.length());
                index = html.indexOf("<img src=\"");
                index2 = html.indexOf("id=\"");

            }
            buffer.append(html);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    postHtml(titleString, buffer.toString());
                }
            });

        }
    }


    private void postHtml(String title, String content) {

        String user_token = (String) SpUtils.get(ReleaseForumActivity.this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("name", title);
        params.put("content", content);
        params.put("user_token", user_token);
        params.put("type", typeId);
        OkGo.<String>post(AppUrls.ForumAdd_forumUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(ReleaseForumActivity.this, params, user_token))
                .execute(new DialogCallBack(ReleaseForumActivity.this, true) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                finish();
                            } else {
                            }
                            UIHelper.toastMsg(js.getString("message"));
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
                        HandleResponse.handleException(response, ReleaseForumActivity.this);
                    }
                });
    }
}
