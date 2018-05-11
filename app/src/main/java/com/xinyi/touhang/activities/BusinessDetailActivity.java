package com.xinyi.touhang.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.SpUtils;
import com.xinyi.touhang.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BusinessDetailActivity extends BaseActivity {

    @BindView(R.id.titleTv)
    TextView titleTv;

    @BindView(R.id.sLayout)
    LinearLayout sLayout;

    @BindView(R.id.tv1)
    TextView tv1;

    @BindView(R.id.tv2)
    TextView tv2;

    @BindView(R.id.tv3)
    TextView tv3;

    @BindView(R.id.tv4)
    TextView tv4;

    @BindView(R.id.tv5)
    TextView tv5;

    @BindView(R.id.tv6)
    TextView tv6;

    @BindView(R.id.tv7)
    TextView tv7;

    @BindView(R.id.tv8)
    TextView tv8;

    @BindView(R.id.tv9)
    TextView tv9;

    @BindView(R.id.dtv1)
    TextView dtv1;

    @BindView(R.id.dtv2)
    TextView dtv2;

    @BindView(R.id.dtv3)
    TextView dtv3;

    @BindView(R.id.dtv4)
    TextView dtv4;

    @BindView(R.id.dtv5)
    TextView dtv5;

    @BindView(R.id.dtv6)
    TextView dtv6;

    @BindView(R.id.dtv7)
    TextView dtv7;

    @BindView(R.id.dtv8)
    TextView dtv8;

    @BindView(R.id.dLayout)
    LinearLayout dLayout;

    @BindView(R.id.introduceTv)
    TextView introduceTv;

    @BindView(R.id.contact_btn)
    Button contact_btn;

    @BindView(R.id.favoTv)
    TextView favoTv;


    public static final String DATAURL = "_url";
    public static final String BUSINESSID = "_id";

    private String url = "";
    private String id = "";
    private String favorite_flg;//是否收藏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_detail);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        url = getIntent().getStringExtra(DATAURL);
        id = getIntent().getStringExtra(BUSINESSID);
        checkIsVip();
        if (url.equals(AppUrls.SupplyDetailUrl)) {
            initTitle("供方详情");
            sLayout.setVisibility(View.VISIBLE);
            dLayout.setVisibility(View.GONE);
        } else if (url.equals(AppUrls.DemandDetailUrl)) {
            initTitle("需方详情");
            sLayout.setVisibility(View.GONE);
            dLayout.setVisibility(View.VISIBLE);
        }

        favoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFavo();
            }
        });
    }


    private void checkIsVip() {
        String vip = (String) SpUtils.get(this, SpUtils.USERVIP, "");
        if (TextUtils.isEmpty(vip) || vip.equals("0")) {
            contact_btn.setText("成为VIP，获取对方联系方式");
        } else {
            contact_btn.setText("立即联系");
        }
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(id)) {
            return;
        }
        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("id", id);
        params.put("user_token", user_token);

        OkGo.<String>post(url)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(BusinessDetailActivity.this, params, user_token))
                .execute(new DialogCallBack(BusinessDetailActivity.this, true) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());
                            if (js.getBoolean("result")) {
                                JSONObject data = js.getJSONObject("data");
                                JSONObject res = data.getJSONObject("res");
                                favorite_flg = data.getString("favorite_flg");
                                if (favorite_flg.equals("0")) {
                                    favoTv.setSelected(false);
                                } else {
                                    favoTv.setSelected(true);
                                }

                                titleTv.setText(res.getString("name"));
                                introduceTv.setText(res.getString("content"));
                                if (url.equals(AppUrls.SupplyDetailUrl)) {
                                    tv1.setText(res.getString("address"));
                                    tv2.setText(res.getString("industry"));
                                    tv3.setText(res.getString("purpose"));
                                    tv4.setText(res.getString("price"));
                                    tv5.setText(res.getString("points"));
                                    tv6.setText(res.getString("stage"));
                                    tv7.setText(res.getString("type"));
                                    tv8.setText(res.getString("special"));
                                    tv9.setText(res.getString("out"));
                                } else if (url.equals(AppUrls.DemandDetailUrl)) {
                                    dtv1.setText(res.getString("address"));
                                    dtv2.setText(res.getString("industry"));
                                    dtv3.setText(res.getString("price"));
                                    dtv4.setText(res.getString("points"));
                                    dtv5.setText(res.getString("stage"));
                                    dtv6.setText(res.getString("type"));
                                    dtv7.setText(res.getString("special"));
                                    dtv8.setText(res.getString("out"));
                                }

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
                        HandleResponse.handleException(response, BusinessDetailActivity.this);
                    }
                });

    }


    /**
     * 关注与取消关注
     */
    private void doFavo() {

        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        if (TextUtils.isEmpty(user_token)) {
            showToast(R.string.commentNeedloginString);
            return;
        }
        String favoUrl = AppUrls.SupplyAdd_favoriteUrl;
        HttpParams params = new HttpParams();
        if (!TextUtils.isEmpty(favorite_flg) && !favorite_flg.equals("0")) {
            //取消收藏 同一个接口
            favoUrl = AppUrls.SupplyRemove_favoriteUrl;
            params.put("fid", favorite_flg);
        } else {
            //收藏
            if (url.equals(AppUrls.SupplyDetailUrl)) {
                favoUrl = AppUrls.SupplyAdd_favoriteUrl;
                params.put("supply_id", id);
            } else if (url.equals(AppUrls.DemandDetailUrl)) {
                favoUrl = AppUrls.DemandAdd_favoriteUrl;
                params.put("demand_id", id);
            }


        }
        params.put("user_token", user_token);
        OkGo.<String>post(favoUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(BusinessDetailActivity.this, params, user_token))
                .execute(new DialogCallBack(BusinessDetailActivity.this, false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                JSONObject data = js.getJSONObject("data");
                                if (!TextUtils.isEmpty(favorite_flg) &&
                                        !favorite_flg.equals("0")) {
                                    favorite_flg = "0";
                                    favoTv.setSelected(false);
                                } else {
                                    favorite_flg = data.getString("favorite");
                                    favoTv.setSelected(true);
                                }

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
                        HandleResponse.handleException(response, BusinessDetailActivity.this);
                    }
                });
    }
}
