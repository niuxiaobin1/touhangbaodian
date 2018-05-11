package com.xinyi.touhang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.WebActivity;
import com.xinyi.touhang.constants.AppUrls;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/5/9.
 */

public class IpoListAdapter extends BaseAdapter<IpoListAdapter.InnerHolder> {

    private Context context;

    public IpoListAdapter(Context context) {
        super();
        this.context=context;
    }


    @Override
    public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.ipo_inner_item, parent, false);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(InnerHolder holder, final int position) {
        holder.subTitleTv.setText(datas.get(position).get("name"));
        holder.textview2.setText(datas.get(position).get("date"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, WebActivity.class);
                it.putExtra(WebActivity.TITLESTRING, datas.get(position).get("name"));
                it.putExtra(WebActivity.TITLEURL, AppUrls.IpoDetailUrl + datas.get(position).get("id"));
                context.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.subTitleTv)
        TextView subTitleTv;

        @BindView(R.id.textview1)
        TextView textview1;

        @BindView(R.id.textview2)
        TextView textview2;

        public InnerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
