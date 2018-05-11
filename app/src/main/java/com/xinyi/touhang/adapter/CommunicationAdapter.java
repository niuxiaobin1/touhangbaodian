package com.xinyi.touhang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.FileDisplayActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/21.
 */

public class CommunicationAdapter extends BaseAdapter<CommunicationAdapter.ViewHolder> {

    private Context context;

    public CommunicationAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.communication_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map<String, String> map = datas.get(position);
        if (map.get("top").equals("1")) {
            holder.isTop_image.setVisibility(View.VISIBLE);
        } else {
            holder.isTop_image.setVisibility(View.GONE);
        }
        holder.fileName.setText(map.get("name"));
        holder.downNum.setText(map.get("down_num"));
        holder.readNum.setText(map.get("read_num"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FileDisplayActivity.class);
                intent.putExtra(FileDisplayActivity.FILE_URL, map.get("file"));
                intent.putExtra(FileDisplayActivity.FILE_ID, map.get("id"));
                intent.putExtra(FileDisplayActivity.FILE_NAME, map.get("name") + "." +
                        map.get("ext"));
                context.startActivity(intent);
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

        @BindView(R.id.fileName)
        TextView fileName;

        @BindView(R.id.downNum)
        TextView downNum;

        @BindView(R.id.readNum)
        TextView readNum;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
