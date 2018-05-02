package com.xinyi.touhang.activities;

import android.graphics.Rect;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.R;
import com.xinyi.touhang.adapter.CommentAdapter;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.utils.CommonUtils;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DividerDecoration;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.GlideCircleTransform;
import com.xinyi.touhang.utils.JsonUtils;
import com.xinyi.touhang.utils.SpUtils;
import com.xinyi.touhang.utils.StatusBarUtil;
import com.xinyi.touhang.utils.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConsulationDetailActivity extends BaseActivity {

    public static final String NEWS_ID = "news_id";
    public String id = "";

    @BindView(R.id.webView)
    WebView webView;
    //评论列表
    @BindView(R.id.comment_RecylerView)
    RecyclerView comment_RecylerView;
    //发表按钮
    @BindView(R.id.inputTv)
    TextView inputTv;
    //输入部分
    @BindView(R.id.input_layout)
    LinearLayout input_layout;

    @BindView(R.id.rootView)
    RelativeLayout rootView;
    //编辑框
    @BindView(R.id.input_et)
    EditText input_et;
    //编辑框字数
    @BindView(R.id.et_content_length)
    TextView et_content_length;
    //发表
    @BindView(R.id.commit_tv)
    TextView commit_tv;
    //收藏
    @BindView(R.id.favo_tv)
    TextView favo_tv;
    //分享
    @BindView(R.id.share_tv)
    TextView share_tv;

    @BindView(R.id.empty_view)
    View empty_view;

    @BindView(R.id.bodyLayout)
    LinearLayout bodyLayout;

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    private int currentKeyboardH;
    private int editTextBodyHeight;
    private int screenHeight;
    private int keyboardH;

    private JSONObject data;
    private CommentAdapter commentAdapter;
    private String favorite_flg;//是否收藏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_consulation_detail);
        ButterKnife.bind(this);
        initViews();
        initDatas();

    }


    @Override
    protected void initViews() {
        super.initViews();
        id = getIntent().getStringExtra(NEWS_ID);
        setViewTreeObserver();
        initWebView();
        initListener();

        comment_RecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        comment_RecylerView.addItemDecoration(new DividerDecoration(this, R.color.colorItem, DensityUtil.dip2px(
                this, 0.5f)));
        commentAdapter = new CommentAdapter(this);
        comment_RecylerView.setAdapter(commentAdapter);


    }

    private void initListener() {
        inputTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changeInputEdittextVisibility(View.VISIBLE);
                    }
                }, 500);


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
                            commentAdapter.addDatas(JsonUtils.ArrayToList(comments, new String[]{
                                    "id", "name", "customer_id", "news_id", "content", "good_num", "created", "modified",
                                    "passed", "customer_name", "image", "checked"
                            }));
                        } catch (JSONException e) {

                        }
                    }

                }
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

    @Override
    protected void initDatas() {
        super.initDatas();
        //获取数据
        if (TextUtils.isEmpty(id)) {
            return;
        }
        String user_token = (String) SpUtils.get(ConsulationDetailActivity.this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("id", id);
        params.put("user_token", user_token);
        OkGo.<String>post(AppUrls.NewsDetailUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(ConsulationDetailActivity.this, params, user_token))
                .execute(new DialogCallBack(ConsulationDetailActivity.this, true) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());
                            UIHelper.showLogCompletion(response.body(), 1000);
                            if (js.getBoolean("result")) {
                                data = js.getJSONObject("data");
                                //init webView
                                String newsContent = data.getJSONObject("news").getString("content");
                                String name = data.getJSONObject("news").getString("name");
                                String author = data.getJSONObject("news").getString("author");
                                String read_num = data.getJSONObject("news").getString("read_num");
                                String editer = data.getJSONObject("news").getString("editer");
                                String author_img = data.getJSONObject("news").getString("author_img");
                                String passed = data.getJSONObject("news").getString("passed");
//                                webView.loadDataWithBaseURL(null, CommonUtils.addHeadToHtml(newsContent,
//                                        name, author, passed, author_img, read_num, editer), "text/html", "UTF-8", null);
                                webView.loadDataWithBaseURL(null, CommonUtils.resolveHtml(newsContent), "text/html", "UTF-8", null);
                                favorite_flg = data.getString("favorite_flg");
                                if (favorite_flg.equals("0")) {
                                    favo_tv.setSelected(false);
                                } else {
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
                        HandleResponse.handleException(response, ConsulationDetailActivity.this);
                    }
                });
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
        params.put("news_id", id);
        OkGo.<String>post(AppUrls.NewsAddCommentUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(ConsulationDetailActivity.this, params, user_token))
                .execute(new DialogCallBack(ConsulationDetailActivity.this, true) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                changeInputEdittextVisibility(View.GONE);
                                List<Map<String, String>> list = new ArrayList<>();
                                Map map = new HashMap();
                                map.put("image", SpUtils.get(ConsulationDetailActivity.this, SpUtils.USERIMAGE, ""));
                                map.put("name", SpUtils.get(ConsulationDetailActivity.this, SpUtils.USERNAME, ""));
                                map.put("good_num", "0");
                                map.put("modified", "刚刚");
                                map.put("content", comment);
                                map.put("checked", "0");
                                list.add(map);
                                commentAdapter.addDatas(list);
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
                        HandleResponse.handleException(response, ConsulationDetailActivity.this);
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
                int statusBarH = StatusBarUtil.getStatusBarHeight(ConsulationDetailActivity.this);//状态栏高度
                int navigationBarH = StatusBarUtil.getNavigationBarHeight(ConsulationDetailActivity.this);//状态栏高度
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
                screenHeight = screenH;//应用屏幕的高度
                editTextBodyHeight = input_layout.getHeight();
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
     * 关注与取消关注
     */
    private void doFavo() {

        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        if (TextUtils.isEmpty(user_token)) {
            showToast(R.string.commentNeedloginString);
            return;
        }
        String favoUrl = AppUrls.NewsAddFavoUrl;
        HttpParams params = new HttpParams();
        if (!TextUtils.isEmpty(favorite_flg) && !favorite_flg.equals("0")) {
            //取消收藏
            favoUrl = AppUrls.NewsRemoveFavoUrl;
            params.put("fid", favorite_flg);
        } else {
            //收藏
            params.put("news_id", id);
        }
        params.put("user_token", user_token);
        OkGo.<String>post(favoUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(ConsulationDetailActivity.this, params, user_token))
                .execute(new DialogCallBack(ConsulationDetailActivity.this, false) {
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
                        HandleResponse.handleException(response, ConsulationDetailActivity.this);
                    }
                });
    }
}
