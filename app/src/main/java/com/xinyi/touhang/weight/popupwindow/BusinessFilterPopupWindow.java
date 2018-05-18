package com.xinyi.touhang.weight.popupwindow;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.xinyi.touhang.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Niu on 2018/5/12.
 * 业务机会筛选的popup
 */

public class BusinessFilterPopupWindow extends BackgoudDimPopuwindow {

    //取消筛选 和确认的点击事件
    private OnLeftClickListener onLeftClickListener;
    private OnRightClickListener onRightClickListener;

    private View contentView;
    private Context mContext;

    private LinearLayout layout;

    private GridView gridView1;//利率
    private GridView gridView2;//期限
    private GridView gridView3;//金额
    private GridView gridView4;//债项评级
    private FilterAdapter adapter1;
    private FilterAdapter adapter2;
    private FilterAdapter adapter3;
    private FilterAdapter adapter4;
    //取消筛选 和确认
    private TextView left_tv;
    private TextView right_tv;

    //当前选择项
    private String item1 = "";
    private String item2 = "";
    private String item3 = "";
    private String item4 = "";

    private List<Map<String, String>> list1;
    private List<Map<String, String>> list2;
    private List<Map<String, String>> list3;
    private List<Map<String, String>> list4;

    public BusinessFilterPopupWindow(Context context, List<Map<String, String>> list1, List<Map<String, String>> list2,
                                     List<Map<String, String>> list3, List<Map<String, String>> list4) {
        super(context, list1, list2, list3, list4);
        this.list1 = list1;
        this.list2 = list2;
        this.list3 = list3;
        this.list4 = list4;
        mContext = context;
    }

    @Override
    View getContent(Context context, final List<Map<String, String>> list1, final List<Map<String, String>> list2
    ,final List<Map<String, String>> list3,final List<Map<String, String>> list4) {

        contentView = inflater.inflate(R.layout.business_filter_popup_layout, null);
        layout=contentView.findViewById(R.id.layout);


        left_tv = (TextView) contentView.findViewById(R.id.left_tv);
        right_tv = (TextView) contentView.findViewById(R.id.right_tv);
        gridView1 = contentView.findViewById(R.id.grid1);
        gridView2 = contentView.findViewById(R.id.grid2);
        gridView3 = contentView.findViewById(R.id.grid3);
        gridView4 = contentView.findViewById(R.id.grid4);

        if (list4==null||list4.size()==0){
            layout.setVisibility(View.GONE);
        }else{
            layout.setVisibility(View.VISIBLE);
            adapter4=new FilterAdapter(list4);
            gridView4.setAdapter(adapter4);
        }
        adapter1=new FilterAdapter(list1);
        adapter2=new FilterAdapter(list2);
        adapter3=new FilterAdapter(list3);

        gridView1.setAdapter(adapter1);
        gridView2.setAdapter(adapter2);
        gridView3.setAdapter(adapter3);

        this.setContentView(contentView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //刷新grid
                //赋值
                adapter1.setCurrentSelect(position);
                item1 = list1.get(position).get("id");
            }
        });
        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter2.setCurrentSelect(position);
                item2 = list2.get(position).get("id");
            }
        });
        gridView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //刷新grid
                //赋值
                adapter3.setCurrentSelect(position);
                item3 = list3.get(position).get("id");
            }
        });
        gridView4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter4.setCurrentSelect(position);
                item4 = list4.get(position).get("id");
            }
        });

        left_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消筛选 清除保存的value
                dismiss();
                adapter1.setCurrentSelect(-1);
                adapter2.setCurrentSelect(-1);
                adapter3.setCurrentSelect(-1);
                if (adapter4!=null){
                    adapter4.setCurrentSelect(-1);
                }

                if (onLeftClickListener != null) {
                    onLeftClickListener.onLeftClick();
                }
            }
        });
        right_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onRightClickListener != null) {
                    onRightClickListener.onRightClick(item1, item2,item3,item4);
                }
            }
        });

        return contentView;
    }

    public interface OnLeftClickListener

    {
        void onLeftClick();
    }

    public interface OnRightClickListener

    {
        void onRightClick(String key1, String key2,String key3, String key4);
    }


    public void setOnLeftClickListener(OnLeftClickListener onLeftClickListener) {
        this.onLeftClickListener = onLeftClickListener;
    }

    public void setOnRightClickListener(OnRightClickListener onRightClickListener) {
        this.onRightClickListener = onRightClickListener;
    }

    private class FilterAdapter extends BaseAdapter {

        private List<Map<String, String>> datas;

        private int currentSelect = -1;

        public FilterAdapter(List<Map<String, String>> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.filter_item, null);
                holder = new Holder();
                holder.content_tv = convertView.findViewById(R.id.content_tv);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            if (currentSelect == position) {
                holder.content_tv.setSelected(true);
            } else {
                holder.content_tv.setSelected(false);
            }
            holder.content_tv.setText(datas.get(position).get("name"));
            return convertView;
        }

        private class Holder {
            private TextView content_tv;
        }


        public int getCurrentSelect() {
            return currentSelect;
        }

        public void setCurrentSelect(int currentSelect) {
            this.currentSelect = currentSelect;
            notifyDataSetChanged();
        }
    }

    /**
     * 设置选中项
     *
     * @param
     */
    public void setSelected(String _item1,String _item2,String _item3,String _item4) {
        if (TextUtils.isEmpty(_item1)) {
            //为空则设置为-1
            adapter1.setCurrentSelect(-1);
        } else {
            //否则选择对应位置
            for (int i = 0; i < list1.size(); i++) {
                if (_item1.equals(list1.get(i).get("id"))) {
                    adapter1.setCurrentSelect(i);
                    break;
                }
            }
        }

        if (TextUtils.isEmpty(_item2)) {
            //为空则设置为-1
            adapter2.setCurrentSelect(-1);
        } else {
            //否则选择对应位置
            for (int i = 0; i < list2.size(); i++) {
                if (_item2.equals(list2.get(i).get("id"))) {
                    adapter2.setCurrentSelect(i);
                    break;
                }
            }
        }

        if (TextUtils.isEmpty(_item3)) {
            //为空则设置为-1
            adapter3.setCurrentSelect(-1);
        } else {
            //否则选择对应位置
            for (int i = 0; i < list3.size(); i++) {
                if (_item3.equals(list3.get(i).get("id"))) {
                    adapter3.setCurrentSelect(i);
                    break;
                }
            }
        }

        if (adapter4!=null){
            if (TextUtils.isEmpty(_item4)) {
                //为空则设置为-1
                adapter4.setCurrentSelect(-1);
            } else {
                //否则选择对应位置
                for (int i = 0; i < list4.size(); i++) {
                    if (_item4.equals(list4.get(i).get("id"))) {
                        adapter4.setCurrentSelect(i);
                        break;
                    }
                }
            }
        }

        this.item1 = _item1;
        this.item2 = _item2;
        this.item3 = _item3;
        this.item4 = _item4;
    }





}
