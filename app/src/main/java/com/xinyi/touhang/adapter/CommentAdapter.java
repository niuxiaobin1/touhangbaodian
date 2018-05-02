package com.xinyi.touhang.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.ConsulationDetailActivity;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.GlideCircleTransform;
import com.xinyi.touhang.utils.SpUtils;
import com.xinyi.touhang.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/23.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context context;
    private List<Map<String, String>> datas;

    public CommentAdapter(Context context) {
        this.context = context;
        datas = new ArrayList<>();
    }

    public void clearDatas() {
        datas.clear();
        notifyDataSetChanged();
    }

    public void addDatas(List<Map<String, String>> list) {
        Log.e("nxb", list.toString());
        datas.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Map<String, String> map = datas.get(position);
        Glide.with(context).load(map.get("image")).transform(new GlideCircleTransform(context)).into(holder.imageView);
        holder.userNameTv.setText(map.get("customer_name"));
        holder.userThumbNumTv.setText(map.get("good_num"));
        holder.userEditTimeTv.setText(map.get("modified"));
        holder.userCommentTv.setText(map.get("content"));
        if (map.get("checked").equals("1")) {
            holder.userThumbNumTv.setSelected(true);
        } else {
            holder.userThumbNumTv.setSelected(false);
        }

        holder.userThumbNumTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doThumbsUp(position, map.get("id"), holder.userThumbNumTv);
            }
        });

    }


    private void doThumbsUp(final int motifyPosition, String comment_id, TextView userThumbNumTv) {

        String user_token = (String) SpUtils.get(context, SpUtils.USERUSER_TOKEN, "");

        if (TextUtils.isEmpty(user_token)) {
            Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

//        if (userThumbNumTv.isSelected()) {
//            userThumbNumTv.setSelected(false);
//        } else {
//            userThumbNumTv.setSelected(true);
//        }
        HttpParams params = new HttpParams();
        params.put("comment_id", comment_id);
        params.put("user_token", user_token);
        OkGo.<String>post(AppUrls.NewsGood_clickUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(context, params, user_token))
                .execute(new DialogCallBack((Activity) context, false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                String flg = js.getJSONObject("data").getString("flg");
                                datas.get(motifyPosition).put("checked", flg);
                                int num = Integer.parseInt(datas.get(motifyPosition).get("good_num"));
                                if (flg.equals("1")) {
                                    datas.get(motifyPosition).put("good_num", String.valueOf(num + 1));
                                } else {
                                    datas.get(motifyPosition).put("good_num", String.valueOf(num - 1));

                                }
                                notifyDataSetChanged();
                            } else {
                                UIHelper.toastMsg(js.getString("message"));
                            }
                        } catch (JSONException e) {
                            UIHelper.toastMsg(e.getMessage());
                        }
                    }


                    @Override
                    public String convertResponse(okhttp3.Response response) throws Throwable {
                        HandleResponse.handleReponse(response);
                        return super.convertResponse(response);
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        HandleResponse.handleException(response, (Activity) context);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.userNameTv)
        TextView userNameTv;
        @BindView(R.id.userThumbNumTv)
        TextView userThumbNumTv;
        @BindView(R.id.userEditTimeTv)
        TextView userEditTimeTv;
        @BindView(R.id.userCommentTv)
        TextView userCommentTv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
