package com.xinyi.touhang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.ForumDetailActivity;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/21.
 */

public class DiscussAdapter extends BaseAdapter<DiscussAdapter.ViewHolder> {

    private Context context;

    public DiscussAdapter(Context context) {
        super();
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.discuss_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map<String, String> map = datas.get(position);
        if (map.get("top").equals("1")) {
            holder.isTop_image.setVisibility(View.VISIBLE);
        } else {
            holder.isTop_image.setVisibility(View.GONE);
        }
        holder.forumName.setText(map.get("name"));
        holder.forumContent.setText(map.get("content"));
        holder.forumAauthor.setText(map.get("author"));
        holder.forumTime.setText(map.get("passed"));
        holder.forumReadNum.setText(map.get("read_num"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context,ForumDetailActivity.class);
                it.putExtra(ForumDetailActivity.FORUM_ID,map.get("id"));
                context.startActivity(it);

            }
        });

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.isTop_image)
        ImageView isTop_image;

        @BindView(R.id.forumReadNum)
        TextView forumReadNum;

        @BindView(R.id.forumTime)
        TextView forumTime;

        @BindView(R.id.forumAauthor)
        TextView forumAauthor;

        @BindView(R.id.forumContent)
        TextView forumContent;

        @BindView(R.id.forumName)
        TextView forumName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
