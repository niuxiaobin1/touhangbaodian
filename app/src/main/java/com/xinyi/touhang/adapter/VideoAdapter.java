package com.xinyi.touhang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.VideoActivity;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.weight.EllipsizingTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/5/3.
 */

public class VideoAdapter extends BaseAdapter<VideoAdapter.ViewHolder> {

    private Context context;

    public VideoAdapter(Context context) {
        super();
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map<String, String> map = datas.get(position);
        Glide.with(context).load(map.get("image")).placeholder(R.mipmap.loading_image)
                .into(holder.imageView);
        holder.titleTv.setText(map.get("name"));
        holder.commitInfoTv.setText(map.get("author") );
        holder.commitTimeTv.setText(map.get("passed") );

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, VideoActivity.class);
                it.putExtra(VideoActivity.VIDEO_ID,map.get("id"));
                context.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageLayout)
        RelativeLayout imageLayout;

        @BindView(R.id.imageView)
        ImageView imageView;

        @BindView(R.id.rightLayout)
        LinearLayout rightLayout;

        @BindView(R.id.titleTv)
        EllipsizingTextView titleTv;

        @BindView(R.id.commitInfoTv)
        TextView commitInfoTv;

        @BindView(R.id.commitTimeTv)
        TextView commitTimeTv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            //resize view

            int screenWidth = DensityUtil.getScreenWidth(context);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageLayout.getLayoutParams();
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rightLayout.getLayoutParams();
            params.width = screenWidth / 3;
            params.height = (int) (params.width * 0.6);

            layoutParams.height = (int) (params.width * 0.6);

            imageLayout.setLayoutParams(params);
            rightLayout.setLayoutParams(layoutParams);
        }
    }
}
