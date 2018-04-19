package com.xinyi.touhang.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Niu on 2018/4/17.
 */

public class DividerDecoration extends RecyclerView.ItemDecoration  {


    private int dividerHeight;
    private Paint dividerPaint;

    public DividerDecoration(Context context, @ColorRes int color,int dividerHeight) {
        dividerPaint = new Paint();
        dividerPaint.setColor(context.getResources().getColor(color));
        this.dividerHeight = dividerHeight;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = dividerHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < childCount - 1; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + dividerHeight;
            c.drawRect(left, top, right, bottom, dividerPaint);
        }
    }

}
