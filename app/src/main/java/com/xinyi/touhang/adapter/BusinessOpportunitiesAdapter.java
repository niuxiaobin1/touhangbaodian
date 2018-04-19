package com.xinyi.touhang.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xinyi.touhang.R;

/**
 * Created by Niu on 2018/4/18.
 */

public class BusinessOpportunitiesAdapter extends RecyclerView.Adapter<BusinessOpportunitiesAdapter.ViewHolder> {


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.business_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (position == 0) {
            holder.isTop_image.setVisibility(View.VISIBLE);
        } else {
            holder.isTop_image.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView isTop_image;

        public ViewHolder(View itemView) {
            super(itemView);
            isTop_image = itemView.findViewById(R.id.isTop_image);
        }
    }
}

