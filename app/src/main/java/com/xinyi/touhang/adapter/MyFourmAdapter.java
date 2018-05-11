package com.xinyi.touhang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.ForumDetailActivity;
import com.xinyi.touhang.activities.VideoActivity;
import com.xinyi.touhang.weight.EllipsizingTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/5/7.
 */

public class MyFourmAdapter extends BaseAdapter<MyFourmAdapter.ViewHolder> {

    private Context context;

    public MyFourmAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.my_forum_item, parent, false
        );
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.forumName.setText(datas.get(position).get("name"));
        holder.forumAauthor.setText(datas.get(position).get("author"));
        holder.forumTime.setText(datas.get(position).get("passed"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context,ForumDetailActivity.class);
                it.putExtra(ForumDetailActivity.FORUM_ID,datas.get(position).get("id"));
                context.startActivity(it);
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.forumName)
        EllipsizingTextView forumName;

        @BindView(R.id.forumAauthor)
        TextView forumAauthor;
        @BindView(R.id.forumTime)
        TextView forumTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
