package com.xinyi.touhang.activities;

import android.content.Intent;
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

public class NonStandardDetailActivity extends BaseActivity {

    @BindView(R.id.titleTv)
    TextView titleTv;


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
    @BindView(R.id.tv10)
    TextView tv10;
    @BindView(R.id.tv11)
    TextView tv11;

    @BindView(R.id.introduceTv)
    TextView introduceTv;

    @BindView(R.id.contact_btn)
    Button contact_btn;

    @BindView(R.id.favoTv)
    TextView favoTv;
    private String[] mDataList = new String[]{"全部", "公司债", "ABS",
            "短期融资券", "专项收益计划", "PPN", "信托计划", "不动产", "证券类", "其他"};

    public static final String BUSINESSID = "_id";
    public static final String BUSINESSTITLE = "_title";

    private String id = "";
    private String title = "";
    private String favorite_flg;//是否收藏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_standard_detail);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        id = getIntent().getStringExtra(BUSINESSID);
        title = getIntent().getStringExtra(BUSINESSTITLE);
        initTitle(mDataList[Integer.parseInt(title)]);
        checkIsVip();
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

        if (TextUtils.isEmpty(id)) {
            return;
        }
        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("id", id);
        params.put("user_token", user_token);

        OkGo.<String>post(AppUrls.NonStandardDetailUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(NonStandardDetailActivity.this, params, user_token))
                .execute(new DialogCallBack(NonStandardDetailActivity.this, true) {
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
                                introduceTv.setText(res.getString("description"));
                                tv1.setText(res.getString("purpose"));
                                tv2.setText(res.getString("amount"));
                                tv3.setText(res.getString("time_limit"));
                                tv4.setText(res.getString("rates"));
                                tv5.setText(res.getString("guarantee_method"));
                                tv6.setText(res.getString("explain"));
                                tv7.setText(res.getString("measures"));
                                tv8.setText(res.getString("is_state").equals("0") ? "否" : "是");
                                tv9.setText(res.getString("bond_rating"));
                                tv10.setText(res.getString("subject_rating"));
                                tv11.setText(res.getString("main"));

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
                        HandleResponse.handleException(response, NonStandardDetailActivity.this);
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
        HttpParams params = new HttpParams();
        String favoUrl = AppUrls.NonStandardAdd_favoriteUrl;
        if (!TextUtils.isEmpty(favorite_flg) && !favorite_flg.equals("0")) {
            //取消收藏 同一个接口
            favoUrl = AppUrls.SupplyRemove_favoriteUrl;
            params.put("fid", favorite_flg);
        } else {
            //收藏
            favoUrl = AppUrls.NonStandardAdd_favoriteUrl;
            params.put("id", id);

        }

        params.put("user_token", user_token);
        OkGo.<String>post(favoUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(NonStandardDetailActivity.this, params, user_token))
                .execute(new DialogCallBack(NonStandardDetailActivity.this, false) {
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
                        HandleResponse.handleException(response, NonStandardDetailActivity.this);
                    }
                });
    }

}
