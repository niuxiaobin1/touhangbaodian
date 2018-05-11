package com.xinyi.touhang.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.PullRefreshLayout.OnRefreshListener;
import com.xinyi.touhang.PullRefreshLayout.PullRefreshLayout;
import com.xinyi.touhang.R;
import com.xinyi.touhang.adapter.MyCommentAdapter;
import com.xinyi.touhang.adapter.MyFourmAdapter;
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

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

public class MyForumActivity extends BaseActivity {

    @BindView(R.id.recylerView)
    RecyclerView recylerView;

    @BindView(R.id.refresh_layout)
    PullRefreshLayout refresh_layout;

    private int page = 1;

    private MyFourmAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_forum);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();

        initTitle("我的帖子");
        recylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recylerView.addItemDecoration(new DividerDecoration(this, R.color.colorLine, DensityUtil.dip2px(
              this, 0.5f
        )));

        adapter = new MyFourmAdapter(this);
        recylerView.setAdapter(adapter);

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
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");

        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        params.put("page", String.valueOf(page));
        if (page==1){
            adapter.clearDatas();
        }
        OkGo.<String>post(AppUrls.ForumMy_listsUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(MyForumActivity.this, params, user_token))
                .tag(this)
                .execute(new DialogCallBack(MyForumActivity.this, false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        refresh_layout.onRefreshComplete();
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                if (adapter != null) {
                                    adapter.addDatas(JsonUtils.ArrayToList(
                                            js.getJSONObject("data").getJSONArray("forum"), new String[]{
                                                    "id", "name", "content", "customer_id",
                                                    "read_num", "good_num", "top", "vip",
                                                    "type", "created", "modified", "customer_name",
                                                    "telephone", "wx_openid", "qq_account", "customer_vip",
                                                    "user_token", "udid", "birthday", "sex",
                                                    "customer_created", "customer_modified", "image", "passed",
                                                    "author", "comment_cnt", "type_name"
                                            }
                                    ));
                                }
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
                        HandleResponse.handleException(response, MyForumActivity.this);
                    }
                });
    }
}
