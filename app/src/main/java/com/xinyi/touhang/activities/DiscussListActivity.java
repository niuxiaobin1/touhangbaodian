package com.xinyi.touhang.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.PullRefreshLayout.OnRefreshListener;
import com.xinyi.touhang.PullRefreshLayout.PullRefreshLayout;
import com.xinyi.touhang.R;
import com.xinyi.touhang.adapter.DiscussListAdapter;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DividerDecoration;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.JsonUtils;
import com.xinyi.touhang.utils.SpUtils;
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

public class DiscussListActivity extends BaseActivity {

    @BindView(R.id.refresh_layout)
    PullRefreshLayout refresh_layout;

    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;

    @BindView(R.id.recylerView)
    RecyclerView recylerView;

    private String type = "";
    private List<Map<String, String>> typeLists;

    private DiscussListAdapter adapter;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss_list);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }


    @Override
    protected void initViews() {
        super.initViews();
        initTitle("交流");
        initRightTv("发帖", R.color.colorTabSelectedIndicator,getResources().getDrawable(R.mipmap.release_icon_new));
        typeLists = new ArrayList<>();

        recylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recylerView.addItemDecoration(new DividerDecoration(this, R.color.colorLine, DensityUtil.dip2px(
                this, 0.5f
        )));
        adapter = new DiscussListAdapter(this);
        recylerView.setAdapter(adapter);
        refresh_layout.setMode(PullRefreshLayout.PULL_FROM_END);
        refresh_layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onPullDownRefresh() {
                page = 1;
                refreshList();
            }

            @Override
            public void onPullUpRefresh() {
                page++;
                refreshList();

            }
        });

    }

    @Override
    protected void onRightClick() {
        super.onRightClick();
        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");

        if (TextUtils.isEmpty(user_token)) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent it = new Intent(DiscussListActivity.this, ReleaseForumActivity.class);
        startActivity(it);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void initDatas() {
        super.initDatas();
        HttpParams params = new HttpParams();
        OkGo.<String>post(AppUrls.ForumType_listUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(DiscussListActivity.this, params, ""))
                .execute(new DialogCallBack(DiscussListActivity.this, false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                JSONArray type_list = js.getJSONObject("data")
                                        .getJSONArray("type_list");
                                typeLists.clear();
                                typeLists.addAll(JsonUtils.ArrayToList(type_list, new String[]{
                                        "id", "name", "created", "modified"
                                }));
                                if (typeLists.size() > 0) {
                                    type = typeLists.get(0).get("id");
                                    refreshList();
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
                    public String convertResponse(okhttp3.Response response) throws Throwable {
                        HandleResponse.handleReponse(response);
                        return super.convertResponse(response);
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        HandleResponse.handleException(response, DiscussListActivity.this);
                    }
                });
    }

    private void initMagicIndicator() {
        if (typeLists == null) {
            return;
        }
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setScrollPivotX(0.35f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return typeLists == null ? 0 : typeLists.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                final SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(typeLists.get(index).get("name"));
                simplePagerTitleView.setTextSize(13);
                simplePagerTitleView.setNormalColor(Color.parseColor("#4A4A4A"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#000000"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        magicIndicator.onPageSelected(index);
                        magicIndicator.onPageScrolled(index, 0, 0);
                        type = typeLists.get(index).get("id");
                        page = 1;
                        refreshList();

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

    private void refreshList() {
        HttpParams params = new HttpParams();
        params.put("type", type);
        params.put("page", String.valueOf(page));
        if (page == 1) {
            adapter.clearDatas();
        }
        OkGo.<String>post(AppUrls.ForumListsUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(DiscussListActivity.this, params, ""))
                .execute(new DialogCallBack(DiscussListActivity.this, true) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        refresh_layout.onRefreshComplete();
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                adapter.addDatas(JsonUtils.ArrayToList(
                                        js.getJSONObject("data").getJSONArray("forum"),
                                        new String[]{"id", "name", "content", "customer_id", "read_num", "good_num", "top", "vip"
                                                , "type", "created", "modified", "customer_name", "telephone", "wx_openid",
                                                "qq_account", "customer_vip", "user_token", "udid", "customer_created",
                                                "customer_modified", "image", "passed", "author", "comment_cnt"
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
                    public String convertResponse(okhttp3.Response response) throws Throwable {
                        HandleResponse.handleReponse(response);
                        return super.convertResponse(response);
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        refresh_layout.onRefreshComplete();
                        HandleResponse.handleException(response, DiscussListActivity.this);
                    }
                });
    }
}
