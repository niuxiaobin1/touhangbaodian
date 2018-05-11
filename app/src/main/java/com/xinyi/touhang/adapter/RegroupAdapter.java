package com.xinyi.touhang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.IpoListActivity;
import com.xinyi.touhang.activities.RegroupListActivity;
import com.xinyi.touhang.activities.WebActivity;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DividerDecoration;
import com.xinyi.touhang.utils.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/18.
 */

public class RegroupAdapter extends RecyclerView.Adapter<RegroupAdapter.ViewHolder> {

    private String[] titles = new String[]{"借壳", "非借壳"};
    private Context context;

    private String data;

    public void setData(String data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public RegroupAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ipo_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.title.setText(titles[position]);

        holder.innerRecylerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        holder.innerRecylerView.addItemDecoration(new DividerDecoration(context, R.color.colorItem,
                DensityUtil.dip2px(context, 0.5f)));
        List<Map<String, String>> list = new ArrayList<>();
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject js = new JSONObject(data);
                if (position == 0) {
                    list = JsonUtils.ArrayToList(js.getJSONArray("list1"),
                            new String[]{
                                    "id", "name",  "type1",
                                    "type2", "type3", "read_num", "created", "modified", "date"
                            });
                } else {
                    list = JsonUtils.ArrayToList(js.getJSONArray("list2"),
                            new String[]{
                                    "id", "name", "type1",
                                    "type2", "type3", "read_num", "created", "modified", "date"
                            });
                }
            } catch (JSONException e) {

            }
        }
        holder.innerRecylerView.setAdapter(new InnerAdapter(list));
        holder.topLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, RegroupListActivity.class);
                it.putExtra(RegroupListActivity.TITLE, "重组·" + titles[position]);
                if (position == 0) {
                    it.putExtra(RegroupListActivity.TYPE, "1");
                } else {
                    it.putExtra(RegroupListActivity.TYPE, "2");
                }
                context.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.topLayout)
        ConstraintLayout topLayout;
        @BindView(R.id.innerRecylerView)
        RecyclerView innerRecylerView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    class InnerAdapter extends BaseAdapter<InnerAdapter.InnerHolder> {

        public InnerAdapter(List<Map<String, String>> datas) {
            super();
            this.datas = datas;
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
                    it.putExtra(WebActivity.TITLEURL, AppUrls.ReorganizationDetailUrl + datas.get(position).get("id"));
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
}
