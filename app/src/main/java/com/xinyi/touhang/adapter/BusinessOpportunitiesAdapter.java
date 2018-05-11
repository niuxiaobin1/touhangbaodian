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
import com.xinyi.touhang.constants.AppUrls;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/18.
 */

public class BusinessOpportunitiesAdapter extends BaseAdapter<BusinessOpportunitiesAdapter.ViewHolder> {

    private Context context;
    private int type = 0;

    public BusinessOpportunitiesAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.business_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map<String, String> map = datas.get(position);
        if (type == 0) {
            holder.supply_layout.setVisibility(View.VISIBLE);
            holder.demand_layout.setVisibility(View.GONE);
            holder.sTv1.setText(map.get("address"));
            holder.sTv2.setText(map.get("purpose"));
            holder.sTv3.setText(map.get("stage"));
            holder.sTv4.setText(map.get("industry"));
            holder.sTv5.setText(map.get("price"));
            holder.sTv6.setText(map.get("points"));

        } else {
            holder.supply_layout.setVisibility(View.GONE);
            holder.demand_layout.setVisibility(View.VISIBLE);
            holder.dTv1.setText(map.get("address"));
            holder.dTv2.setText(map.get("price"));
            holder.dTv3.setText(map.get("stage"));
            holder.dTv4.setText(map.get("industry"));
            holder.dTv5.setText(map.get("points"));
        }

        if (map.get("top").equals("1")) {
            holder.isTop_image.setVisibility(View.VISIBLE);
        } else {
            holder.isTop_image.setVisibility(View.GONE);
        }

        holder.titleTv.setText(map.get("name"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(context, BusinessDetailActivity.class);
                if (type == 0) {
                    it.putExtra(BusinessDetailActivity.DATAURL, AppUrls.SupplyDetailUrl);
                }else{
                    it.putExtra(BusinessDetailActivity.DATAURL, AppUrls.DemandDetailUrl);
                }

                it.putExtra(BusinessDetailActivity.BUSINESSID,map.get("id"));
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
        @BindView(R.id.supply_layout)
        LinearLayout supply_layout;

        @BindView(R.id.demand_layout)
        LinearLayout demand_layout;

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


        @BindView(R.id.dTv1)
        TextView dTv1;
        @BindView(R.id.dTv2)
        TextView dTv2;
        @BindView(R.id.dTv3)
        TextView dTv3;
        @BindView(R.id.dTv4)
        TextView dTv4;
        @BindView(R.id.dTv5)
        TextView dTv5;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

