package com.xinyi.touhang.weight;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by Niu on 2017/6/28.
 */

public class ObservableScrollView extends ScrollView {

    private OnScrollChangeListener mOnScrollChangeListener;

    /**
     * 设置滚动接口
     * @param
     */

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        mOnScrollChangeListener = onScrollChangeListener;
    }

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    /**
     *
     *定义一个滚动接口
     * */

    public interface OnScrollChangeListener{
        void onScrollChanged(ObservableScrollView scrollView,int l, int t, int oldl, int oldt);
    }

    /**
     * 当scrollView滑动时系统会调用该方法,并将该回调放过中的参数传递到自定义接口的回调方法中,
     * 达到scrollView滑动监听的效果
     *
     * */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mOnScrollChangeListener!=null){
            mOnScrollChangeListener.onScrollChanged(this,l,t,oldl,oldt);

        }
    }

}
