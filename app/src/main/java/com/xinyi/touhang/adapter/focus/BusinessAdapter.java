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

public class BusinessAdapter extends BaseAdapter<BusinessAdapter.ViewHolder> {

    private Context context;

    public BusinessAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(
                R.layout.focus_business_item,parent,false
        );
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
//        holder.textView.setText(datas.get(position).get("name"));
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent it = new Intent(context, ConsulationDetailActivity.class);
//                it.putExtra(ConsulationDetailActivity.NEWS_ID, datas.get(position).get("id"));
//                context.startActivity(it);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textView)
        TextView textView;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
