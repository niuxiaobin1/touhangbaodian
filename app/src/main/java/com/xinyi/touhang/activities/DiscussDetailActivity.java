package com.xinyi.touhang.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;

public class DiscussDetailActivity extends BaseActivity {

    public static final String FORUM_ID = "_forum_id";
    private String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss_detail);
        initDatas();
        initViews();
    }

    @Override
    protected void initViews() {
        super.initViews();
        id = getIntent().getStringExtra(FORUM_ID);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }
}
