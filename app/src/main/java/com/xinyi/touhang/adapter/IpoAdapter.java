package com.xinyi.touhang.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xinyi.touhang.R;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DividerDecoration;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/18.
 */

public class IpoAdapter extends RecyclerView.Adapter<IpoAdapter.ViewHolder> {


    private static final String[] CHANNELS = new String[]{"全部", "IPO", "重组", "外资", "国资", "上市公司治理", "其他"};
    private List<String> mDataList1 = Arrays.asList(CHANNELS);

    private static final String[] CHANNELS1 = new String[]{"全部", "有效", "失效", "修订", "未生效", "部分失效", "其他"};
    private List<String> mDataList2 = Arrays.asList(CHANNELS1);

    private Context context;

    public IpoAdapter(Context context){
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ipo_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        initMagicIndicator(holder.magic_indicator1,mDataList1);
        initMagicIndicator(holder.magic_indicator2,mDataList2);
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

        @BindView(R.id.magic_indicator1)
        MagicIndicator magic_indicator1;

        @BindView(R.id.magic_indicator2)
        MagicIndicator magic_indicator2;

        @BindView(R.id.innerRecylerView)
        RecyclerView innerRecylerView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    private void initMagicIndicator(final MagicIndicator magicIndicator,final List<String> mDataList ) {
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(context);
        commonNavigator.setScrollPivotX(0.35f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                final SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setTextSize(12);
                simplePagerTitleView.setNormalColor(Color.parseColor("#4A4A4A"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#000000"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        magicIndicator.onPageSelected(index);
                        magicIndicator.onPageScrolled(index, 0, 0);

                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                WrapPagerIndicator indicator = new WrapPagerIndicator(context);
                indicator.setFillColor(Color.parseColor("#FFD700"));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
    }


    class InnerAdapter extends RecyclerView.Adapter<InnerAdapter.InnerHolder>{


        @Override
        public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.ipo_inner_item,parent,false);
            return new InnerHolder(view);
        }

        @Override
        public void onBindViewHolder(InnerHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 3;
        }

        public  class InnerHolder extends RecyclerView.ViewHolder{

            public InnerHolder(View itemView) {
                super(itemView);
            }
        }

    }
}
