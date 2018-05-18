package com.xinyi.touhang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.WebActivity;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.utils.CommonUtils;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/18.
 */

public class AccountingAdapter extends BaseAdapter<AccountingAdapter.ViewHolder> {

    private String key="";

    private Context context;
    public AccountingAdapter(Context context){
        super();
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View  view= LayoutInflater.from(parent.getContext()).inflate(R.layout.accounting_item,
                parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map<String,String> map=datas.get(position);

        if (TextUtils.isEmpty(key)){
            holder.titleTv.setText(map.get("name"));
        }else{
            holder.titleTv.setText(CommonUtils.changeKeyColor(context,
                    R.color.colorTabSelectedIndicator,map.get("name"),key));
        }
        holder.textview2.setText(map.get("date"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(context, WebActivity.class);
                it.putExtra(WebActivity.TITLESTRING,map.get("name"));
                it.putExtra(WebActivity.TITLECANDOWNLOAD,true);
                it.putExtra(WebActivity.DOWNLOADURL,map.get("file"));
                it.putExtra(WebActivity.TITLEURL, AppUrls.AccountingDetailUrl+map.get("id"));
                context.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.titleTv)
        TextView titleTv;

        @BindView(R.id.textview2)
        TextView textview2;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
