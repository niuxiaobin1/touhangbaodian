package com.xinyi.touhang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xinyi.touhang.R;

import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/5/7.
 */

public class OrderAdapter extends BaseAdapter<OrderAdapter.ViewHolder> {

    private Context context;

    public OrderAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.my_order_item, parent, false
        );
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return datas.size() == 0 ? 10 : datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}