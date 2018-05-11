package com.xinyi.touhang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.ConsulationDetailActivity;
import com.xinyi.touhang.activities.ForumDetailActivity;
import com.xinyi.touhang.activities.VideoActivity;
import com.xinyi.touhang.adapter.focus.StudyAdapter;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.weight.EllipsizingTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/5/7.
 */

public class MyCommentAdapter extends BaseAdapter<MyCommentAdapter.ViewHolder> {

    private Context context;
    private String type;

    public MyCommentAdapter(Context context, String type) {
        super();
        this.context = context;
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.my_comment_item, parent, false
        );
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.commentNameTv.setText(datas.get(position).get("title"));
        holder.commentContentTv.setText(datas.get(position).get("content"));
        holder.commentTimeTv.setText(datas.get(position).get("passed"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("0")) {
                    Intent it = new Intent(context, ConsulationDetailActivity.class);
                    it.putExtra(ConsulationDetailActivity.NEWS_ID, datas.get(position).get("id"));
                    context.startActivity(it);
                } else if (type.equals("1")) {
                    Intent it = new Intent(context, VideoActivity.class);
                    it.putExtra(VideoActivity.VIDEO_ID,datas.get(position).get("id"));
                    context.startActivity(it);
                } else if (type.equals("2")) {
                    Intent it = new Intent(context,ForumDetailActivity.class);
                    it.putExtra(ForumDetailActivity.FORUM_ID,datas.get(position).get("id"));
                    context.startActivity(it);
                } else {
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.commentContentTv)
        EllipsizingTextView commentContentTv;
        @BindView(R.id.commentNameTv)
        EllipsizingTextView commentNameTv;
        @BindView(R.id.commentTimeTv)
        TextView commentTimeTv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}