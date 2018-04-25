package com.xinyi.touhang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.ConsulationDetailActivity;
import com.xinyi.touhang.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Niu on 2018/4/17.
 */

public class ConsulationInnerAdapter extends RecyclerView.Adapter<ConsulationInnerAdapter.ViewHolder> {

    private List<Map<String, String>> datas;
    private Context context;

    public ConsulationInnerAdapter(Context context) {
        this.context = context;
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map<String, String> map = datas.get(position);
        Glide.with(context).load(map.get("image")).into(holder.news_image);
        holder.newsTitleTv.setText(map.get("name"));
        holder.newsEditorTv.setText(map.get("author"));
        holder.news_readNum.setText(map.get("read_num") + "阅读");
        holder.news_time.setText(map.get("passed"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, ConsulationDetailActivity.class);
                it.putExtra(ConsulationDetailActivity.NEWS_ID, map.get("id"));
                context.startActivity(it);
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout left_layout;
        private ImageView news_image;
        private TextView newsTitleTv;
        private TextView newsEditorTv;
        private TextView news_time;
        private TextView news_readNum;

        public ViewHolder(View itemView) {
            super(itemView);
            left_layout = itemView.findViewById(R.id.left_layout);
            news_image = itemView.findViewById(R.id.news_image);
            newsTitleTv = itemView.findViewById(R.id.newsTitleTv);
            newsEditorTv = itemView.findViewById(R.id.newsEditorTv);
            news_time = itemView.findViewById(R.id.news_time);
            news_readNum = itemView.findViewById(R.id.news_readNum);

            //resize view

            int screenWidth = DensityUtil.getScreenWidth(context);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) news_image.getLayoutParams();
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) left_layout.getLayoutParams();
            params.width = screenWidth / 3;
            params.height = (int) (params.width * 0.6);

            layoutParams.height = (int) (params.width * 0.6);

            news_image.setLayoutParams(params);
            left_layout.setLayoutParams(layoutParams);

        }
    }
}
