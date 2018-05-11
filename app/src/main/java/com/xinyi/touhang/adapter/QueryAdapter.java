package com.xinyi.touhang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.StudyListActivity;
import com.xinyi.touhang.activities.VideoActivity;
import com.xinyi.touhang.activities.WebActivity;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.fragments.searchInner.QueryFragment;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DividerDecoration;
import com.xinyi.touhang.utils.JsonUtils;
import com.xinyi.touhang.weight.EllipsizingTextView;

import org.json.JSONArray;
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

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.ViewHolder> {

    private String[] titles = new String[]{"热门培训", "热门视频"};
    private Context context;
    private String data;

    public void setData(String data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public QueryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.query_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.title.setText(titles[position]);

        List<Map<String, String>> list = new ArrayList<>();
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject js = new JSONObject(data);
                if (position == 0) {
                    JSONArray ads = js.getJSONArray("ads");
                    list = JsonUtils.ArrayToList(ads, new String[]{
                            "id", "name", "url", "author", "top",
                            "read_num", "created", "modified", "image"
                    });
                } else {
                    JSONArray video = js.getJSONArray("video");
                    list = JsonUtils.ArrayToList(video, new String[]{
                            "id", "name", "price", "author", "good_num",
                            "read_num", "editer", "video_type_id", "video_id",
                            "pay", "top", "apple_pay", "created", "modified", "image"
                    });
                }
            } catch (JSONException e) {
            }
        }
        holder.innerRecylerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        holder.innerRecylerView.addItemDecoration(new DividerDecoration(context, R.color.colorItem,
                DensityUtil.dip2px(context, 0.5f)));
        holder.innerRecylerView.setAdapter(new InnerAdapter(list, position));
        holder.topLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, StudyListActivity.class);
                if (position == 0) {
                    it.putExtra(StudyListActivity.INDEX, 1);
                } else {
                    it.putExtra(StudyListActivity.INDEX, 0);
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

        @BindView(R.id.innerRecylerView)
        RecyclerView innerRecylerView;

        @BindView(R.id.title)
        TextView title;

        @BindView(R.id.topLayout)
        ConstraintLayout topLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    class InnerAdapter extends BaseAdapter<InnerAdapter.InnerHolder> {

        private int type = 0;

        public InnerAdapter(List<Map<String, String>> datas, int type) {
            this.datas = datas;
            this.type = type;
        }

        @Override
        public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.query_inner_item, parent, false);
            return new InnerHolder(v);
        }

        @Override
        public void onBindViewHolder(InnerHolder holder,final int position) {
            if (type == 0) {
                holder.play_icon.setVisibility(View.GONE);
            } else {
                holder.play_icon.setVisibility(View.VISIBLE);
            }
            Glide.with(context).load(datas.get(position).get("image")).placeholder(R.mipmap.loading_image).into(holder.imageView);
            holder.titleTv.setText(datas.get(position).get("name"));
            holder.news_time.setText(datas.get(position).get("author"));
            holder.readNumTv.setText(datas.get(position).get("read_num") + "阅读");

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 0) {
                        String url = datas.get(position).get("url");
                        Intent it = new Intent(context, WebActivity.class);
                        it.putExtra(WebActivity.TITLESTRING,  datas.get(position).get("name"));
                        if (!TextUtils.isEmpty(url)) {
                            it.putExtra(WebActivity.TITLEURL, url);
                        } else {
                            it.putExtra(WebActivity.TITLEURL, AppUrls.AdviseDetailUrl+ datas.get(position).get("id"));
                        }
                        context.startActivity(it);
                    } else {

                        Intent it = new Intent(context, VideoActivity.class);
                        it.putExtra(VideoActivity.VIDEO_ID,datas.get(position).get("id"));
                        context.startActivity(it);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        public class InnerHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.imageView)
            ImageView imageView;

            @BindView(R.id.play_icon)
            ImageView play_icon;

            @BindView(R.id.imageLayout)
            RelativeLayout imageLayout;

            @BindView(R.id.right_layout)
            LinearLayout right_layout;

            @BindView(R.id.titleTv)
            EllipsizingTextView titleTv;

            @BindView(R.id.news_time)
            TextView news_time;

            @BindView(R.id.readNumTv)
            TextView readNumTv;

            public InnerHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                int screenWidth = DensityUtil.getScreenWidth(context);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageLayout.getLayoutParams();
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) right_layout.getLayoutParams();
                params.width = screenWidth / 3;
                params.height = (int) (params.width * 0.6);

                layoutParams.height = (int) (params.width * 0.6);

                imageLayout.setLayoutParams(params);
                right_layout.setLayoutParams(layoutParams);
            }
        }
    }
}
