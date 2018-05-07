package com.xinyi.touhang.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xinyi.touhang.R;
import com.xinyi.touhang.adapter.MyCommentAdapter;
import com.xinyi.touhang.adapter.MyFourmAdapter;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DividerDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyForumActivity extends BaseActivity {

    @BindView(R.id.recylerView)
    RecyclerView recylerView;

    private MyFourmAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_forum);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();

        initTitle("我的帖子");
        recylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recylerView.addItemDecoration(new DividerDecoration(this, R.color.colorLine, DensityUtil.dip2px(
              this, 0.5f
        )));

        adapter = new MyFourmAdapter(this);
        recylerView.setAdapter(adapter);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }
}
