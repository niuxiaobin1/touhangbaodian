package com.xinyi.touhang.adapter.focus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.VideoActivity;
import com.xinyi.touhang.adapter.BaseAdapter;
import com.xinyi.touhang.utils.GlideCircleTransform;
import com.xinyi.touhang.weight.EllipsizingTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/5/7.
 */

public class NotificationAdapter extends BaseAdapter<NotificationAdapter.ViewHolder> {

    private Context context;

    public NotificationAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(
                R.layout.notification_item,parent,false
        );
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        if (datas.get(position).containsKey("customer_name")){
            Glide.with(context).load(datas.get(position).get("customer_image")).transform(new GlideCircleTransform(context))
                    .into(holder.imageView);
            holder.nameTv.setText(datas.get(position).get("customer_name"));
            holder.timeTv.setText(datas.get(position).get("passed"));
        }else{
            Glide.with(context).load(datas.get(position).get("image")).transform(new GlideCircleTransform(context))
                    .into(holder.imageView);
            holder.timeTv.setText(datas.get(position).get("created"));
            holder.nameTv.setText(datas.get(position).get("name"));
        }

        holder.contentTv.setText(datas.get(position).get("content"));


    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageView)
        ImageView imageView;

        @BindView(R.id.nameTv)
        TextView nameTv;

        @BindView(R.id.timeTv)
        TextView timeTv;

        @BindView(R.id.contentTv)
        EllipsizingTextView contentTv;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
