package com.xinyi.touhang.activities;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.VcPlayerLog;
import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.aliyun.vodplayerview.utils.ScreenUtils;
import com.aliyun.vodplayerview.widget.AliyunScreenMode;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
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
import com.xinyi.touhang.utils.SpUtils;
import com.xinyi.touhang.utils.StatusBarUtil;
import com.xinyi.touhang.utils.UIHelper;
import com.xinyi.touhang.weight.EllipsizingTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoActivity extends BaseActivity {

    public final static String VIDEO_ID = "_video_id";
    private String id = "";
    //视频播放器
    @BindView(R.id.video_view)
    AliyunVodPlayerView mAliyunVodPlayerView;
    //付费布局
    @BindView(R.id.payLayout)
    LinearLayout payLayout;
    //cost
    @BindView(R.id.payCostTv)
    TextView payCostTv;
    //说明
    @BindView(R.id.explainTv)
    TextView explainTv;
    //标题
    @BindView(R.id.videoTv)
    TextView videoTv;
    //发布人
    @BindView(R.id.commitorTv)
    TextView commitorTv;
    //发布时间
    @BindView(R.id.commitTime)
    TextView commitTime;
    //观看人数
    @BindView(R.id.readNumTv)
    TextView readNumTv;
    //观看人数
    @BindView(R.id.contentTv)
    EllipsizingTextView contentTv;
    //编辑
    @BindView(R.id.editorTv)
    TextView editorTv;
    //编辑
    @BindView(R.id.comment_RecylerView)
    RecyclerView comment_RecylerView;

    //发表按钮
    @BindView(R.id.inputTv)
    TextView inputTv;
    //输入部分
    @BindView(R.id.input_layout)
    LinearLayout input_layout;
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

    private String favorite_flg;//是否收藏
    private CommentAdapter commentAdapter;
    private int currentKeyboardH;
    private int keyboardH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        id = getIntent().getStringExtra(VIDEO_ID);
        initTitle(R.string.studyVideoString);
        setViewTreeObserver();
        upDateLayout(1);
        initVideo();
        comment_RecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        comment_RecylerView.addItemDecoration(new DividerDecoration(this, R.color.colorLine, DensityUtil.dip2px(
                this, 0.5f
        )));
        commentAdapter = new CommentAdapter(this);
        comment_RecylerView.setAdapter(commentAdapter);
        //收藏
        favo_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFavo();
            }
        });

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
                int statusBarH = StatusBarUtil.getStatusBarHeight(VideoActivity.this);//状态栏高度
                int navigationBarH = StatusBarUtil.getNavigationBarHeight(VideoActivity.this);//状态栏高度
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

    @Override
    protected void initDatas() {
        super.initDatas();
        if (TextUtils.isEmpty(id)) {
            UIHelper.toastMsg("id empty");
            return;
        }
        String user_token = (String) SpUtils.get(VideoActivity.this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("id", id);
        params.put("user_token", user_token);
        OkGo.<String>post(AppUrls.VideoDetailUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(VideoActivity.this, params, user_token))
                .execute(new DialogCallBack(VideoActivity.this, true) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());
                            if (js.getBoolean("result")) {
                                JSONObject video = js.getJSONObject("data").getJSONObject("video");
                                videoTv.setText(video.getString("name"));
                                commitorTv.setText(video.getString("author"));
                                commitTime.setText(video.getString("created"));
                                readNumTv.setText(video.getString("read_num"));
                                contentTv.setText(video.getString("content"));
                                editorTv.setText("责任编辑：" + video.getString("editer"));
                                commentAdapter.addDatas(JsonUtils.ArrayToList(
                                        js.getJSONObject("data").getJSONArray("comments"), new String[]{
                                                "id", "name", "customer_id", "video_id", "content", "good_num", "created", "modified",
                                                "passed", "comment_image", "customer_name", "image", "checked"
                                        }
                                ));

                                favorite_flg = js.getJSONObject("data").getString("favorite_flg");
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
                        HandleResponse.handleException(response, VideoActivity.this);
                    }
                });
    }

    /**
     * 1:视频可播放0：收费
     *
     * @param which
     */
    private void upDateLayout(int which) {
        if (which == 0) {
            payLayout.setVisibility(View.VISIBLE);
            mAliyunVodPlayerView.setVisibility(View.GONE);
            explainTv.setText(UIHelper.changeKeyColor(this, R.color.colorTabSelectedIndicator,
                    getResources().getString(R.string.videoCostString), "收费视频"));
        } else {
            payLayout.setVisibility(View.GONE);
            mAliyunVodPlayerView.setVisibility(View.VISIBLE);
        }

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
        String favoUrl = AppUrls.VideoAdd_favoriteUrl;
        HttpParams params = new HttpParams();
        if (!TextUtils.isEmpty(favorite_flg) && !favorite_flg.equals("0")) {
            //取消收藏
            favoUrl = AppUrls.VideoRemove_favoriteUrl;
            params.put("fid", favorite_flg);
        } else {
            //收藏
            params.put("video_id", id);
        }
        params.put("user_token", user_token);
        OkGo.<String>post(favoUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(VideoActivity.this, params, user_token))
                .execute(new DialogCallBack(VideoActivity.this, false) {
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
                        HandleResponse.handleException(response, VideoActivity.this);
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
        params.put("video_id", id);
        OkGo.<String>post(AppUrls.VideoAdd_commentUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(VideoActivity.this, params, user_token))
                .execute(new DialogCallBack(VideoActivity.this, true) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                changeInputEdittextVisibility(View.GONE);
                                List<Map<String, String>> list = new ArrayList<>();
                                Map map = new HashMap();
                                map.put("image", SpUtils.get(VideoActivity.this, SpUtils.USERIMAGE, ""));
                                map.put("name", SpUtils.get(VideoActivity.this, SpUtils.USERNAME, ""));
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
                        HandleResponse.handleException(response, VideoActivity.this);
                    }
                });
    }

    /**
     * init video
     */
    private void initVideo() {
        setPlaySource();
        //设置播放器监听
        mAliyunVodPlayerView.setTitleBarCanShow(false);
        mAliyunVodPlayerView.setOnPreparedListener(new IAliyunVodPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                //准备完成时触发
            }
        });
        mAliyunVodPlayerView.setOnCompletionListener(new IAliyunVodPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                //播放正常完成时触发
            }
        });
        mAliyunVodPlayerView.setOnFirstFrameStartListener(new IAliyunVodPlayer.OnFirstFrameStartListener() {
            @Override
            public void onFirstFrameStart() {
                //首帧显示时触发
            }
        });
        mAliyunVodPlayerView.setOnChangeQualityListener(new IAliyunVodPlayer.OnChangeQualityListener() {
            @Override
            public void onChangeQualitySuccess(String finalQuality) {
                //清晰度切换成功时触发
            }

            @Override
            public void onChangeQualityFail(int code, String msg) {
                //清晰度切换失败时触发
            }
        });
        mAliyunVodPlayerView.setOnStoppedListener(new IAliyunVodPlayer.OnStoppedListener() {
            @Override
            public void onStopped() {
                //使用stop接口时触发
            }
        });
        mAliyunVodPlayerView.setOnCircleStartListener(new IAliyunVodPlayer.OnCircleStartListener() {
            @Override
            public void onCircleStart() {
                //循环播放开始
            }
        });
    }

    private void setPlaySource() {


//        if (type.equals("localSource")) {

        AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
        alsb.setSource("http://video.xingfulaile.net/5f9ee99cf3c54e93a1048ee50bee7f78/4d3bfdf721bc40ea811198677e4571d5-5287d2089db37e62345123a1be272f8b.mp4?from=groupmessage&isappinstalled=0");
        AliyunLocalSource localSource = alsb.build();
        mAliyunVodPlayerView.setLocalSource(localSource);

//        } else if (type.equals("vidsts")) {
//
//            String vid = getIntent().getStringExtra("vid");
//            String akId = getIntent().getStringExtra("akId");
//            String akSecret = getIntent().getStringExtra("akSecret");
//            String scuToken = getIntent().getStringExtra("scuToken");
//
//            AliyunVidSts vidSts = new AliyunVidSts();
//            vidSts.setVid(vid);
//            vidSts.setAcId(akId);
//            vidSts.setAkSceret(akSecret);
//            vidSts.setSecurityToken(scuToken);
//            mAliyunVodPlayerView.setVidSts(vidSts);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayerViewMode();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onResume();
        }
    }

    private void updatePlayerViewMode() {
        if (mAliyunVodPlayerView != null) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {                //转为竖屏了。
                //显示状态栏
//                if (!isStrangePhone()) {
//                    getSupportActionBar().show();
//                }

                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                //设置view的布局，宽高之类
                RelativeLayout.LayoutParams aliVcVideoViewLayoutParams = (RelativeLayout.LayoutParams) mAliyunVodPlayerView.getLayoutParams();
                aliVcVideoViewLayoutParams.height = (int) (ScreenUtils.getWidth(this) * 9.0f / 16);
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                if (!isStrangePhone()) {
//                    aliVcVideoViewLayoutParams.topMargin = getSupportActionBar().getHeight();
//                }
                mAliyunVodPlayerView.setLayoutParams(aliVcVideoViewLayoutParams);
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {                //转到横屏了。
                //隐藏状态栏
                if (!isStrangePhone()) {
                    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }

                //设置view的布局，宽高
                RelativeLayout.LayoutParams aliVcVideoViewLayoutParams = (RelativeLayout.LayoutParams) mAliyunVodPlayerView.getLayoutParams();
                aliVcVideoViewLayoutParams.height = ScreenUtils.getHeight(this);
                aliVcVideoViewLayoutParams.width = ScreenUtils.getWidth(this);
//                if (!isStrangePhone()) {
//                    aliVcVideoViewLayoutParams.topMargin = 0;
//                }
                mAliyunVodPlayerView.setLayoutParams(aliVcVideoViewLayoutParams);

            }
        }
    }


    @Override
    protected void onDestroy() {
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onDestroy();
            mAliyunVodPlayerView = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAliyunVodPlayerView != null) {
            boolean handler = mAliyunVodPlayerView.onKeyDown(keyCode, event);
            if (!handler) {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //解决某些手机上锁屏之后会出现标题栏的问题。
        updatePlayerViewMode();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updatePlayerViewMode();
    }

    private boolean isStrangePhone() {
        boolean strangePhone = Build.DEVICE.equalsIgnoreCase("mx5")
                || Build.DEVICE.equalsIgnoreCase("Redmi Note2")
                || Build.DEVICE.equalsIgnoreCase("Z00A_1")
                || Build.DEVICE.equalsIgnoreCase("hwH60-L02")
                || Build.DEVICE.equalsIgnoreCase("hermes")
                || (Build.DEVICE.equalsIgnoreCase("V4") && Build.MANUFACTURER.equalsIgnoreCase("Meitu"))
                || (Build.DEVICE.equalsIgnoreCase("m1metal") && Build.MANUFACTURER.equalsIgnoreCase("Meizu"));

        VcPlayerLog.e("lfj1115 ", " Build.Device = " + Build.DEVICE + " , isStrange = " + strangePhone);
        return strangePhone;

    }
}
