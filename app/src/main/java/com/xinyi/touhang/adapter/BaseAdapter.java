package com.xinyi.touhang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Niu on 2018/4/27.
 */

public abstract class BaseAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {


    protected List<Map<String, String>> datas;

    public BaseAdapter() {
        datas = new ArrayList<>();
    }

    public void clearDatas() {
        datas.clear();
        notifyDataSetChanged();
    }

    public void addDatas(List<Map<String, String>> list) {
        datas.addAll(list);
        notifyDataSetChanged();
    }

}
