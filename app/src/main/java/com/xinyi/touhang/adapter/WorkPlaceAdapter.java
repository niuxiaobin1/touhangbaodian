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

import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.BusinessDetailActivity;
import com.xinyi.touhang.activities.WorkPlaceActivity;
import com.xinyi.touhang.activities.WorkPlaceDetailActivity;
import com.xinyi.touhang.constants.AppUrls;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/18.
 */

public class WorkPlaceAdapter extends BaseAdapter<WorkPlaceAdapter.ViewHolder> {

    private Context context;


    public WorkPlaceAdapter(Context context) {
        super();
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workplace_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.isTop_image.setVisibility(View.GONE);
        holder.titleTv.setText(datas.get(position).get("name"));
        holder.Tv1.setText(datas.get(position).get("address"));
        holder.Tv2.setText(datas.get(position).get("price"));
        holder.Tv3.setText(datas.get(position).get("company"));
        holder.Tv4.setText(datas.get(position).get("modified"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, WorkPlaceDetailActivity.class);
                it.putExtra(WorkPlaceDetailActivity.WORKPALCEID,datas.get(position).get("id"));
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

        @BindView(R.id.Tv1)
        TextView Tv1;
        @BindView(R.id.Tv2)
        TextView Tv2;
        @BindView(R.id.Tv3)
        TextView Tv3;
        @BindView(R.id.Tv4)
        TextView Tv4;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

