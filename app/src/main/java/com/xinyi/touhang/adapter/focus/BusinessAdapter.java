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
import com.xinyi.touhang.activities.BankDetailActivity;
import com.xinyi.touhang.activities.BusinessDetailActivity;
import com.xinyi.touhang.activities.ConsulationDetailActivity;
import com.xinyi.touhang.activities.NonStandardDetailActivity;
import com.xinyi.touhang.adapter.BaseAdapter;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.utils.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/5/7.
 */

public class BusinessAdapter extends BaseAdapter<BusinessAdapter.ViewHolder> {

    private Context context;
    private String[] mDataList = new String[]{"全部", "公司债", "ABS",
            "短期融资券", "专项收益计划", "PPN", "信托计划", "不动产", "证券类", "其他"};
    private String[] mDataList1 = new String[]{"全部","短期拆借", "银行同存", "理财产品"};

    public BusinessAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.focus_business_item, parent, false
        );
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textView.setText(datas.get(position).get("name"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = null;
                if (datas.get(position).get("type").equals("supply")) {
                    it = new Intent(context, BusinessDetailActivity.class);
                    it.putExtra(BusinessDetailActivity.DATAURL, AppUrls.SupplyDetailUrl);
                    it.putExtra(BusinessDetailActivity.BUSINESSTYPE,
                            datas.get(position).get("sub_type_name").equals("投资方") ? "1" : "2");
                    it.putExtra(BusinessDetailActivity.BUSINESSID, datas.get(position).get("id"));
                } else if(datas.get(position).get("type").equals("non")){
                    it = new Intent(context, NonStandardDetailActivity.class);
                    it.putExtra(NonStandardDetailActivity.BUSINESSID, datas.get(position).get("id"));
                    it.putExtra(NonStandardDetailActivity.BUSINESSTITLE, getNonId(datas.get(position).get("sub_type_name")));
                }else{
                    it = new Intent(context, BankDetailActivity.class);
                    it.putExtra(BankDetailActivity.BUSINESSID, datas.get(position).get("id"));
                    it.putExtra(BankDetailActivity.BUSINESSTITLE, getBankId(datas.get(position).get("sub_type_name")));
                }

                context.startActivity(it);
            }
        });
    }

    private String getNonId(String nonString){
        for (int i = 0; i <mDataList.length ; i++) {
            if (mDataList[i].equals(nonString)){
                return String.valueOf(i);
            }
        }
        return "0";
    }

    private String getBankId(String bankString){
        for (int i = 0; i <mDataList1.length ; i++) {
            if (mDataList1[i].equals(bankString)){
                return String.valueOf(i);
            }
        }
        return "0";
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
