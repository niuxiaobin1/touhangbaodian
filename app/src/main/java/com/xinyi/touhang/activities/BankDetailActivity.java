package com.xinyi.touhang.activities;

import android.support.v7.app.AppCompatActivity;
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

public class BankDetailActivity extends BaseActivity {


    @BindView(R.id.titleTv)
    TextView titleTv;
    private String[] mDataList = new String[]{"全部","短期拆借", "银行同存", "理财产品"};

    @BindView(R.id.layout3)
    LinearLayout layout3;

    @BindView(R.id.layout2)
    LinearLayout layout2;

    @BindView(R.id.layout1)
    LinearLayout layout1;

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

    @BindView(R.id.introduceTv)
    TextView introduceTv;

    @BindView(R.id.contact_btn)
    Button contact_btn;

    @BindView(R.id.favoTv)
    TextView favoTv;

    public static final String BUSINESSID = "_id";
    public static final String BUSINESSTITLE = "_title";

    private String id = "";
    private String title = "";
    private String favorite_flg;//是否收藏


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_detail);
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
        if (title.equals("3")){
            layout1.setVisibility(View.GONE);
            layout2.setVisibility(View.VISIBLE);
            layout3.setVisibility(View.VISIBLE);
        }else{
            layout1.setVisibility(View.VISIBLE);
            layout2.setVisibility(View.GONE);
            layout3.setVisibility(View.GONE);
        }

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

        OkGo.<String>post(AppUrls.BankDetailUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(BankDetailActivity.this, params, user_token))
                .execute(new DialogCallBack(BankDetailActivity.this, true) {
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
                                tv1.setText(res.getString("direction"));
                                tv2.setText(res.getString("rates"));
                                tv3.setText(res.getString("amount"));
                                tv4.setText(res.getString("time_limit"));
                                tv5.setText(res.getString("rish"));
                                tv6.setText(res.getString("baoben"));


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
                        HandleResponse.handleException(response, BankDetailActivity.this);
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
        String favoUrl = AppUrls.BankAdd_favoriteUrl;
        if (!TextUtils.isEmpty(favorite_flg) && !favorite_flg.equals("0")) {
            //取消收藏 同一个接口
            favoUrl = AppUrls.SupplyRemove_favoriteUrl;
            params.put("fid", favorite_flg);
        } else {
            //收藏
            favoUrl = AppUrls.BankAdd_favoriteUrl;
            params.put("id", id);

        }

        params.put("user_token", user_token);
        OkGo.<String>post(favoUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(BankDetailActivity.this, params, user_token))
                .execute(new DialogCallBack(BankDetailActivity.this, false) {
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
                        HandleResponse.handleException(response, BankDetailActivity.this);
                    }
                });
    }

}
