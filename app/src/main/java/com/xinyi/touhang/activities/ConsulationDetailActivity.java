package com.xinyi.touhang.activities;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xinyi.touhang.R;
import com.xinyi.touhang.adapter.CommentAdapter;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.utils.CommonUtils;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DividerDecoration;
import com.xinyi.touhang.utils.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConsulationDetailActivity extends BaseActivity {

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
        comment_RecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        comment_RecylerView.addItemDecoration(new DividerDecoration(this, R.color.colorItem, DensityUtil.dip2px(
                this, 1)));
        comment_RecylerView.setAdapter(new CommentAdapter(this));

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


    private void changeInputEdittextVisibility(int visibility) {

        if (visibility == View.GONE) {
            input_layout.setVisibility(View.GONE);
            CommonUtils.hideSoftInput(this,input_et);
        } else {
            input_layout.setVisibility(View.VISIBLE);
            CommonUtils.showSoftInput(this,input_et);
        }
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }


    private void setViewTreeObserver() {

        final ViewTreeObserver swipeRefreshLayoutVTO = input_layout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                input_layout.getWindowVisibleDisplayFrame(r);
                int statusBarH = StatusBarUtil.getStatusBarHeight(ConsulationDetailActivity.this);//状态栏高度
                int screenH = input_layout.getRootView().getHeight();
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

                input_layout.setPadding(0, 0, 0, keyboardH-statusBarH);
//                bodyLayout.setPadding(0, 0, 0, (screenHeight - DensityUtil.dip2px(ConsulationDetailActivity.this, 60)) - r.bottom);

            }

        });
    }
}
