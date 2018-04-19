package com.xinyi.touhang.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xinyi.touhang.R;
import com.xinyi.touhang.fragments.searchInner.QueryFragment;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DividerDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/18.
 */

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.ViewHolder> {

    private Context context;


    public QueryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.query_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.innerRecylerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        holder.innerRecylerView.addItemDecoration(new DividerDecoration(context,R.color.colorItem,
                DensityUtil.dip2px(context,1)));
        holder.innerRecylerView.setAdapter(new InnerAdapter());
    }

    @Override
    public int getItemCount() {
        return 2;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.innerRecylerView)
        RecyclerView innerRecylerView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    class InnerAdapter extends RecyclerView.Adapter<InnerAdapter.InnerHolder> {

        @Override
        public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.query_inner_item, parent, false);
            return new InnerHolder(v);
        }

        @Override
        public void onBindViewHolder(InnerHolder holder, int position) {

            int screenWidth = DensityUtil.getScreenWidth(context);
            int imageWidth = screenWidth / 3;
            int imageHeight = (int) (imageWidth * 0.6);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.imageView.getLayoutParams();
            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) holder.right_layout.getLayoutParams();
            params.height = imageHeight;
            params.width = imageWidth;
            params1.height = imageHeight;
            holder.imageView.setLayoutParams(params);
            holder.right_layout.setLayoutParams(params1);

        }

        @Override
        public int getItemCount() {
            return 3;
        }

        public class InnerHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.imageView)
            ImageView imageView;

            @BindView(R.id.right_layout)
            LinearLayout right_layout;

            public InnerHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
