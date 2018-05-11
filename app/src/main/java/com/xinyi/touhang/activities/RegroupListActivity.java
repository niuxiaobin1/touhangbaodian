package com.xinyi.touhang.activities;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.PullRefreshLayout.OnRefreshListener;
import com.xinyi.touhang.PullRefreshLayout.PullRefreshLayout;
import com.xinyi.touhang.R;
import com.xinyi.touhang.adapter.IpoListAdapter;
import com.xinyi.touhang.adapter.RegroupListAdapter;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.utils.CommonUtils;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DividerDecoration;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.JsonUtils;
import com.xinyi.touhang.utils.UIHelper;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

public class RegroupListActivity extends BaseActivity {

    public static final String TITLE="_title";
    public static final String TYPE="_type";

    private String type="";

    @BindView(R.id.search_et)
    EditText search_et;

    @BindView(R.id.search_image)
    ImageView search_image;

    @BindView(R.id.refresh_layout)
    PullRefreshLayout refresh_layout;

    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;

    @BindView(R.id.sub_magic_indicator)
    MagicIndicator sub_magic_indicator;

    @BindView(R.id.regroup_recylerView)
    RecyclerView regroupRecylerView;

    private String cid1;
    private String cid2;
    private int page=1;
    private String name="";//搜索的关键字


    private List<Map<String, String>> mDataList = new ArrayList<>();
    private List<Map<String, String>> subList = new ArrayList<>();

    private RegroupListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regroup_list);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }


    @Override
    protected void initViews() {
        super.initViews();
        type=getIntent().getStringExtra(TYPE);
        initTitle(getIntent().getStringExtra(TITLE));

        refresh_layout.setMode(PullRefreshLayout.PULL_FROM_END);
        refresh_layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onPullDownRefresh() {
                page = 1;
                getData();
            }

            @Override
            public void onPullUpRefresh() {
                page++;
                getData();

            }
        });
        regroupRecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        regroupRecylerView.addItemDecoration(new DividerDecoration(this, R.color.colorItem,
                DensityUtil.dip2px(this, 0.5f)));
        adapter=new RegroupListAdapter(this);
        regroupRecylerView.setAdapter(adapter);

        search_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.showInputMethod(search_et);
            }
        });

        search_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideInputMethod(search_et,RegroupListActivity.this);
                name=search_et.getText().toString().trim();
                page=1;
                try {
                    initMagicIndicator();
                } catch (JSONException e) {
                }
                getData();

            }
        });
    }


    @Override
    protected void initDatas() {
        super.initDatas();
        HttpParams params = new HttpParams();
        params.put("type",type);
        OkGo.<String>post(AppUrls.ReorganizationGet_typeUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(RegroupListActivity.this, params, ""))
                .tag(this)
                .execute(new DialogCallBack(RegroupListActivity.this, false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                mDataList.clear();

                                Map<String, String> fMap = new HashMap<>();
                                fMap.put("id", "");
                                fMap.put("name", "全部");
                                fMap.put("list", new JSONArray().toString());
                                mDataList.add(fMap);
                                JSONArray data = js.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject obj = data.getJSONArray(i).getJSONObject(0);
                                    Map<String, String> map = new HashMap<>();
                                    map.put("id", obj.getString("id"));
                                    map.put("name", obj.getString("name"));
                                    map.put("list", obj.getString("list"));
                                    mDataList.add(map);
                                }

                                initMagicIndicator();
                            } else {
                                UIHelper.toastMsg(js.getString("message"));
                            }
                        } catch (JSONException e) {
                            UIHelper.toastMsg(e.getMessage());
                        }

                    }

                    @Override
                    public String convertResponse(Response response) throws Throwable {
                        HandleResponse.handleReponse(response);
                        return super.convertResponse(response);
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);

                        HandleResponse.handleException(response, RegroupListActivity.this);
                    }
                });
    }

    private void initMagicIndicator() throws JSONException {
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setScrollPivotX(0.35f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                final SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index).get("name"));
                simplePagerTitleView.setTextSize(12);
                simplePagerTitleView.setNormalColor(Color.parseColor("#4A4A4A"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#000000"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        magicIndicator.onPageSelected(index);
                        magicIndicator.onPageScrolled(index, 0, 0);
                        cid1 = mDataList.get(index).get("id");
                        try {
                            initSubMagicIndicator(mDataList.get(index).get("list"));
                        } catch (JSONException e) {

                        }
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
        cid1 = mDataList.get(0).get("id");
        initSubMagicIndicator(mDataList.get(0).get("list"));
    }

    //二级分类
    private void initSubMagicIndicator(String data) throws JSONException {
        subList.clear();
        subList = JsonUtils.ArrayToList(new JSONArray(data)
                , new String[]{"id", "name"});
        if (subList.size() == 0) {
            sub_magic_indicator.setVisibility(View.GONE);
            cid2 = "";
            page=1;
            getData();
            return;
        } else {
            sub_magic_indicator.setVisibility(View.VISIBLE);
        }
        sub_magic_indicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setScrollPivotX(0.35f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return subList == null ? 0 : subList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                final SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(subList.get(index).get("name"));
                simplePagerTitleView.setTextSize(12);
                simplePagerTitleView.setNormalColor(Color.parseColor("#4A4A4A"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#000000"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sub_magic_indicator.onPageSelected(index);
                        sub_magic_indicator.onPageScrolled(index, 0, 0);
                        cid2 = subList.get(index).get("id");
                        page=1;
                        getData();
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
        sub_magic_indicator.setNavigator(commonNavigator);
        cid2 = subList.get(0).get("id");
        page=1;
        getData();
    }


    private void getData() {
        HttpParams params = new HttpParams();
        params.put("page",String.valueOf(page));
        params.put("name",name);
        params.put("type1",cid1);
        params.put("type2",cid2);
        params.put("type",type);
        if (page==1){
            adapter.clearDatas();
        }
        OkGo.<String>post(AppUrls.ReorganizationSearchUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(RegroupListActivity.this, params, ""))
                .tag(this)
                .execute(new DialogCallBack(RegroupListActivity.this, false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        refresh_layout.onRefreshComplete();
                        try {
                            JSONObject js = new JSONObject(response.body());
                            if (js.getBoolean("result")) {
                                adapter.addDatas(JsonUtils.ArrayToList(
                                        js.getJSONArray("data"),new String[]{
                                                "id","name","type1","type2",
                                                "type3","read_num","created","modified","date"
                                        }
                                ));
                            } else {
                                UIHelper.toastMsg(js.getString("message"));
                            }
                        } catch (JSONException e) {
                            UIHelper.toastMsg(e.getMessage());
                        }

                    }

                    @Override
                    public String convertResponse(Response response) throws Throwable {
                        HandleResponse.handleReponse(response);
                        return super.convertResponse(response);
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        refresh_layout.onRefreshComplete();
                        HandleResponse.handleException(response, RegroupListActivity.this);
                    }
                });

    }
}
