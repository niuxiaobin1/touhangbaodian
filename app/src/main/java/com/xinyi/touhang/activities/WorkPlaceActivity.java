package com.xinyi.touhang.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.PullRefreshLayout.OnRefreshListener;
import com.xinyi.touhang.PullRefreshLayout.PullRefreshLayout;
import com.xinyi.touhang.R;
import com.xinyi.touhang.adapter.NonStandardAdapter;
import com.xinyi.touhang.adapter.WorkPlaceAdapter;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.utils.CommonUtils;
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

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

public class WorkPlaceActivity extends BaseActivity {

    @BindView(R.id.search_et)
    EditText search_et;

    @BindView(R.id.search_image)
    ImageView search_image;


    @BindView(R.id.magic_indicator)
    MagicIndicator magic_indicator;


    @BindView(R.id.refresh_layout)
    PullRefreshLayout refresh_layout;

    @BindView(R.id.recylerView)
    RecyclerView recylerView;

    private String[] mDataList = new String[]{"全部", "券商", "创投",
            "银行", "其他中介"};

    private int page = 1;
    private String type = "0";
    private String key = "";

    private WorkPlaceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_place);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();

        initTitle("我的职场");
        initRightTv("发布",R.color.colorTabSelectedIndicator, getResources().getDrawable(R.mipmap.release_icon_new));
        try {
            initMagicIndicator();
        } catch (JSONException e) {
        }

        refresh_layout.setMode(PullRefreshLayout.BOTH);
        refresh_layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onPullDownRefresh() {
                page = 1;
                initDatas();
            }

            @Override
            public void onPullUpRefresh() {
                page++;
                initDatas();

            }
        });

        adapter = new WorkPlaceAdapter(this);
        recylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recylerView.addItemDecoration(new DividerDecoration(this, R.color.colorItem,
                DensityUtil.dip2px(this, 0.5f)));
        recylerView.setAdapter(adapter);

        search_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.showInputMethod(search_et);
            }
        });

        search_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideSoftInput(WorkPlaceActivity.this, search_et);
                key = search_et.getText().toString().trim();
                page = 1;
                initDatas();
            }
        });
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        HttpParams params = new HttpParams();
        params.put("page", String.valueOf(page));
        params.put("type", type);
        params.put("name", key);
        if (page == 1) {
            adapter.clearDatas();
        }
        OkGo.<String>post(AppUrls.OfferSearchUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(WorkPlaceActivity.this, params, ""))
                .tag(this)
                .execute(new DialogCallBack(WorkPlaceActivity.this, false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        refresh_layout.onRefreshComplete();
                        try {
                            JSONObject js = new JSONObject(response.body());
                            if (js.getBoolean("result")) {

                                adapter.addDatas(JsonUtils.ArrayToList(js.getJSONArray("data"),
                                        new String[]{
                                                "id", "name", "num", "price", "address", "company",
                                                "type", "description", "tips", "contact", "modified"
                                        }));
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
                        HandleResponse.handleException(response, WorkPlaceActivity.this);
                    }
                });

    }

    private void initMagicIndicator() throws JSONException {
        magic_indicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setScrollPivotX(0.35f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                final SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(mDataList[index]);
                simplePagerTitleView.setTextSize(12);
                simplePagerTitleView.setNormalColor(Color.parseColor("#4A4A4A"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#000000"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        magic_indicator.onPageSelected(index);
                        magic_indicator.onPageScrolled(index, 0, 0);
                        type = String.valueOf(index + 1);
                        page = 1;
                        initDatas();

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
        magic_indicator.setNavigator(commonNavigator);

    }

    @Override
    protected void onRightClick() {
        super.onRightClick();
        String confirm = (String) SpUtils.get(this, SpUtils.USERCONFIRM, "");
        if (!TextUtils.isEmpty(confirm) && confirm.equals("2")) {
            Intent it = new Intent(this, ReleaseWorkPlaceActivity.class);
            startActivity(it);
        } else {
            UIHelper.toastMsg("请您先进行实名认证");
        }


    }
}
