package com.xinyi.touhang.weight.popupwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;


import com.xinyi.touhang.R;

import java.util.List;
import java.util.Map;


/**
 * 背景变暗的Popuwindow
 */
public abstract class BackgoudDimPopuwindow extends PopupWindow {


    protected LayoutInflater inflater;

    /**
     * popuwindow 的root布局
     */
    private LinearLayout mRootLayout;

    //要显示为透明黑色的布局
    private LinearLayout mDimLayout;


    public BackgoudDimPopuwindow(Context context,List<Map<String, String>> list,List<Map<String, String>> list1,List<Map<String, String>> list2,List<Map<String, String>> list3) {
        super(context);

       /* if (anchor == null) {
            throw new NullPointerException("The anchor is must not null");
        }*/

        inflater = LayoutInflater.from(context);

        View contentView = getContent(context,list,list1,list2,list3);

        if (contentView == null) {
            throw new NullPointerException("The contentView is must not null");
        }

        mRootLayout = new LinearLayout(context);
        ViewGroup.LayoutParams rootParams =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        mRootLayout.setOrientation(LinearLayout.VERTICAL);

        mRootLayout.setLayoutParams(rootParams);

        generalDimLayout(context);

        mRootLayout.addView(contentView);
        mRootLayout.addView(mDimLayout);

        //默认 这个popu是 全屏的
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        setContentView(mRootLayout);
        //点击外部消失
        setOutsideTouchable(true);

        setTouchable(true);

        setFocusable(true);
        //设置一个透明背景，不设这个，不然丑的要死，不信你试试。
        setBackgroundDrawable(new BitmapDrawable());

        //一个渐变动画
        setAnimationStyle(R.style.popuwindow_style);
        //貌似，然并卵。
        update();



    }

    /**
     * 生成dim布局
     * @param context
     */
    private void generalDimLayout(Context context) {
        mDimLayout = new LinearLayout(context);
        LinearLayout.LayoutParams dimParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);

        mDimLayout.setBackgroundColor(context.getResources().getColor(R.color.popu_dim_color));

        mDimLayout.setLayoutParams(dimParams);
        mDimLayout.setFitsSystemWindows(true);

        mDimLayout.setOnClickListener(new View.OnClickListener() {          //点击了dim部分就消失
            @Override
            public void onClick(View v) {

                BackgoudDimPopuwindow.this.dismiss();
            }
        });

    }

    /**
     * 子类不行复写这，在这里可以生成要显示的内容
     * @param context
     * @return
     */
    abstract View getContent(Context context,List<Map<String, String>> list,List<Map<String, String>> list1,List<Map<String, String>> list2,List<Map<String, String>> list3);



}
