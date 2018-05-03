package com.xinyi.touhang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xinyi.touhang.R;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.weight.EllipsizingTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/5/3.
 */

public class AdviseAdapter extends BaseAdapter<AdviseAdapter.ViewHolder> {


    private Context context;

    public AdviseAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.advise_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageLayout)
        RelativeLayout imageLayout;

        @BindView(R.id.imageView)
        ImageView imageView;

        @BindView(R.id.rightLayout)
        LinearLayout rightLayout;

        @BindView(R.id.titleTv)
        EllipsizingTextView titleTv;

        @BindView(R.id.commitorTv)
        TextView commitorTv;

        @BindView(R.id.readNumTv)
        TextView readNumTv;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //resize view

            int screenWidth = DensityUtil.getScreenWidth(context);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageLayout.getLayoutParams();
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rightLayout.getLayoutParams();
            params.width = screenWidth / 3;
            params.height = (int) (params.width * 0.6);

            layoutParams.height = (int) (params.width * 0.6);

            imageLayout.setLayoutParams(params);
            rightLayout.setLayoutParams(layoutParams);
        }
    }
}
