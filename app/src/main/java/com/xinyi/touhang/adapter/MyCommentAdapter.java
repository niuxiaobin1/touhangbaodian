package com.xinyi.touhang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.VideoActivity;
import com.xinyi.touhang.adapter.focus.StudyAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/5/7.
 */

public class MyCommentAdapter extends BaseAdapter<MyCommentAdapter.ViewHolder> {

    private Context context;

    public MyCommentAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(
                R.layout.my_comment_item,parent,false
        );
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {



        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}