package com.xinyi.touhang.adapter.focus;

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
import com.xinyi.touhang.adapter.BaseAdapter;
import com.xinyi.touhang.utils.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/5/7.
 */

public class ConsulationAdapter extends BaseAdapter<ConsulationAdapter.ViewHolder> {

    private Context context;

    public ConsulationAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(
                R.layout.focus_consulation_item,parent,false
        );
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textView.setText(datas.get(position).get("name"));
        Glide.with(context).load(datas.get(position).get("image"))
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, ConsulationDetailActivity.class);
                it.putExtra(ConsulationDetailActivity.NEWS_ID, datas.get(position).get("id"));
                context.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textView)
        TextView textView;
        @BindView(R.id.imageView)
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            int screenWidth = DensityUtil.getScreenWidth(context);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            params.width = screenWidth / 3;
            params.height = (int) (params.width * 0.6);
            imageView.setLayoutParams(params);
        }
    }
}
