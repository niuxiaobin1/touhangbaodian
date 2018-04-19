package com.xinyi.touhang.base;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xinyi.touhang.R;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.weight.ObservableScrollView;

/**
 * Created by Niu on 2018/4/17.
 */

public abstract class BaseFragment extends Fragment {
    protected TextView titleTv;
    protected TextView subTitleTv;
    protected ObservableScrollView mScrollView;

    private Toast toast;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initDatas();
        if (titleTv != null && mScrollView != null) {

            mScrollView.setOnScrollChangeListener(new ObservableScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChanged(ObservableScrollView scrollView, int l, int t, int oldl, int oldt) {
                    Rect rect = new Rect();
                    boolean cover = subTitleTv.getGlobalVisibleRect(rect);
                    if (!cover) {
                        titleTv.setVisibility(View.VISIBLE);
                    } else {
                        titleTv.setVisibility(View.INVISIBLE);
                    }
                }
            });


        }

    }

    public abstract void initViews();

    public abstract void initDatas();

    protected void showToast(@StringRes int string) {
        if (toast == null) {
            toast = Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT);
        } else {
            toast.setText(string);
        }
        toast.show();
    }



}
