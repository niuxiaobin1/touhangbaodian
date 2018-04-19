package com.xinyi.touhang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xinyi.touhang.R;
import com.xinyi.touhang.utils.DensityUtil;

/**
 * Created by Niu on 2018/4/17.
 */

public class ConsulationInnerAdapter extends RecyclerView.Adapter<ConsulationInnerAdapter.ViewHolder> {

    private Context context;

    public ConsulationInnerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout left_layout;
        private ImageView news_image;

        public ViewHolder(View itemView) {
            super(itemView);
            left_layout = itemView.findViewById(R.id.left_layout);
            news_image = itemView.findViewById(R.id.news_image);

            //resize view

            int screenWidth = DensityUtil.getScreenWidth(context);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) news_image.getLayoutParams();
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) left_layout.getLayoutParams();
            params.width = screenWidth / 3;
            params.height =(int)( params.width * 0.6);

            layoutParams.height = (int)( params.width * 0.6);

            news_image.setLayoutParams(params);
            left_layout.setLayoutParams(layoutParams);

        }
    }
}
