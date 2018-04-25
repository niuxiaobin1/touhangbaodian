package com.xinyi.touhang.activities;

import android.graphics.Rect;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import android.widget.TextView;

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
import com.xinyi.touhang.utils.JsonUtils;
import com.xinyi.touhang.utils.StatusBarUtil;
import com.xinyi.touhang.utils.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConsulationDetailActivity extends BaseActivity {

    public static final String NEWS_ID = "news_id";
    public String id = "";

    @BindView(R.id.webView)
    WebView webView;

    @BindView(R.id.comment_RecylerView)
    RecyclerView comment_RecylerView;

    @BindView(R.id.inputTv)
    TextView inputTv;

    @BindView(R.id.input_layout)
    LinearLayout input_layout;

    @BindView(R.id.rootView)
    RelativeLayout rootView;

    @BindView(R.id.input_et)
    EditText input_et;

    @BindView(R.id.empty_view)
    View empty_view;

    @BindView(R.id.bodyLayout)
    LinearLayout bodyLayout;

    private int currentKeyboardH;
    private int editTextBodyHeight;
    private int screenHeight;
    private int keyboardH;

    private JSONObject data;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_consulation_detail);
        ButterKnife.bind(this);
        initViews();
        initDatas();
        initListener();
    }


    @Override
    protected void initViews() {
        super.initViews();
        id = getIntent().getStringExtra(NEWS_ID);

        initWebView();

        comment_RecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        comment_RecylerView.addItemDecoration(new DividerDecoration(this, R.color.colorItem, DensityUtil.dip2px(
                this, 1)));
        commentAdapter=new CommentAdapter(this);
        comment_RecylerView.setAdapter(commentAdapter);

        setViewTreeObserver();
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
    }


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
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress==100){
                    //comment
                    try {
                        JSONArray comments=data.getJSONArray("comments");
                        commentAdapter.addDatas(JsonUtils.ArrayToList(comments,new String[]{
                                "id","name","customer_id","news_id","content","good_num","created","modified",
                                "passed","customer_name","image"
                        }));
                    } catch (JSONException e) {

                    }
                }
            }
        });

    }


    private void changeInputEdittextVisibility(int visibility) {

        if (visibility == View.GONE) {
            input_layout.setVisibility(View.GONE);
            CommonUtils.hideSoftInput(this, input_et);
        } else {
            input_layout.setVisibility(View.VISIBLE);
            CommonUtils.showSoftInput(this, input_et);
        }
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        if (TextUtils.isEmpty(id)) {
            return;
        }
        HttpParams params = new HttpParams();
        params.put("id", id);
        OkGo.<String>post(AppUrls.NewsDetailUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(ConsulationDetailActivity.this, params, ""))
                .execute(new DialogCallBack(ConsulationDetailActivity.this, true) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

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
                                webView.loadDataWithBaseURL(null, CommonUtils.addHeadToHtml(newsContent,
                                        name, author, passed, author_img, read_num, editer), "text/html", "UTF-8", null);



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


    private void setViewTreeObserver() {

        final ViewTreeObserver swipeRefreshLayoutVTO = input_layout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                input_layout.getWindowVisibleDisplayFrame(r);
                int statusBarH = StatusBarUtil.getStatusBarHeight(ConsulationDetailActivity.this);//状态栏高度
                int navigationBarH = StatusBarUtil.getNavigationBarHeight(ConsulationDetailActivity.this);//状态栏高度
                int screenH = input_layout.getRootView().getHeight();

                if (navigationBarH!=0){
                    //有navigationBar的影响
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        r.bottom-=navigationBarH;
                    }

                }
                if (r.top != statusBarH) {
                    //r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，通过getStatusBarHeight获取状态栏高度
                    r.top = statusBarH;
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
//                bodyLayout.setPadding(0, 0, 0, (screenHeight - DensityUtil.dip2px(ConsulationDetailActivity.this, 60)) - r.bottom);

            }

        });
    }
}
