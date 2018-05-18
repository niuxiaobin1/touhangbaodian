package com.xinyi.touhang.activities;

import android.os.Bundle;
import android.text.TextUtils;
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

public class WorkPlaceDetailActivity extends BaseActivity {


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

    public static final String WORKPALCEID = "_id";
    private String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_place_detail);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        initTitle("职场详情");
        id=getIntent().getStringExtra(WORKPALCEID);


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

        OkGo.<String>post(AppUrls.OfferDetailUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(WorkPlaceDetailActivity.this, params, user_token))
                .execute(new DialogCallBack(WorkPlaceDetailActivity.this, true) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());
                            if (js.getBoolean("result")) {
                                JSONObject data = js.getJSONObject("data");
                                JSONObject res = data.getJSONObject("res");
                                titleTv.setText(res.getString("name"));
                                tv1.setText(res.getString("num"));
                                tv2.setText(res.getString("price"));
                                tv3.setText(res.getString("address"));
                                tv4.setText(res.getString("company"));
                                tv5.setText(res.getString("description"));
                                tv6.setText(res.getString("tips"));
                                tv7.setText(res.getString("contact"));
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
                        HandleResponse.handleException(response, WorkPlaceDetailActivity.this);
                    }
                });
    }
}
