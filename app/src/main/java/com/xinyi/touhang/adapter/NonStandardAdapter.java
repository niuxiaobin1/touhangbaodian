package com.xinyi.touhang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.BusinessDetailActivity;
import com.xinyi.touhang.activities.NonStandardDetailActivity;
import com.xinyi.touhang.constants.AppUrls;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/18.
 */

public class NonStandardAdapter extends BaseAdapter<NonStandardAdapter.ViewHolder> {

    private Context context;


    public NonStandardAdapter(Context context) {
        super();
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.business_nonstandrad_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map<String, String> map = datas.get(position);
        holder.sTv1.setText(map.get("bond_rating"));
        holder.sTv2.setText(map.get("purpose"));
        holder.sTv3.setText(map.get("guarantee_method"));
        holder.sTv4.setText(map.get("subject_rating"));
        holder.sTv5.setText(map.get("measures"));
        holder.sTv6.setText(map.get("explain"));


        if (map.get("top").equals("1")) {
            holder.isTop_image.setVisibility(View.VISIBLE);
        } else {
            holder.isTop_image.setVisibility(View.GONE);
        }
        if (map.get("yellow").equals("1")){
            holder.titleTv.setTextColor(ContextCompat.getColor(context,R.color.colorTabSelectedIndicator));
        }else{
            holder.titleTv.setTextColor(ContextCompat.getColor(context,R.color.colorMain));
        }
        holder.titleTv.setText(map.get("name"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, NonStandardDetailActivity.class);
                it.putExtra(NonStandardDetailActivity.BUSINESSID, map.get("id"));
                it.putExtra(NonStandardDetailActivity.BUSINESSTITLE, map.get("type"));
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

        @BindView(R.id.titleTv)
        TextView titleTv;

        @BindView(R.id.sTv1)
        TextView sTv1;
        @BindView(R.id.sTv2)
        TextView sTv2;
        @BindView(R.id.sTv3)
        TextView sTv3;
        @BindView(R.id.sTv4)
        TextView sTv4;
        @BindView(R.id.sTv5)
        TextView sTv5;
        @BindView(R.id.sTv6)
        TextView sTv6;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

