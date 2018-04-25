package com.xinyi.touhang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xinyi.touhang.R;
import com.xinyi.touhang.utils.GlideCircleTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/23.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context context;
    private List<Map<String,String>> datas;

    public CommentAdapter(Context context){
        this.context=context;
        datas=new ArrayList<>();
    }

    public void clearDatas() {
        datas.clear();
        notifyDataSetChanged();
    }

    public void addDatas(List<Map<String, String>> list) {
        datas.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Map<String,String> map=datas.get(position);
        Glide.with(context).load(map.get("image")).transform(new GlideCircleTransform(context)).into(holder.imageView);
        holder.userNameTv.setText(map.get("name"));
        holder.userThumbNumTv.setText(map.get("good_num"));
        holder.userEditTimeTv.setText(map.get("modified"));
        holder.userCommentTv.setText(map.get("content"));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.userNameTv)
        TextView userNameTv;
        @BindView(R.id.userThumbNumTv)
        TextView userThumbNumTv;
        @BindView(R.id.userEditTimeTv)
        TextView userEditTimeTv;
        @BindView(R.id.userCommentTv)
        TextView userCommentTv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
