package com.xinyi.touhang.activities;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.weight.EllipsizingTextView;

import butterknife.BindView;

public class VideoActivity extends BaseActivity {
    //视频播放器
    @BindView(R.id.video_view)
    AliyunVodPlayerView vodPlayerView;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        initTitle(R.string.studyVideoString);

    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }
}
