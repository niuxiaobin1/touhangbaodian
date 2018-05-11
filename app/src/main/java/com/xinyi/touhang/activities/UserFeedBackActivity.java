package com.xinyi.touhang.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.utils.CommonUtils;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.JsonUtils;
import com.xinyi.touhang.utils.SpUtils;
import com.xinyi.touhang.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

public class UserFeedBackActivity extends BaseActivity {

    @BindView(R.id.editText)
    EditText editText;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.rd1)
    RadioButton rd1;
    @BindView(R.id.rd2)
    RadioButton rd2;
    @BindView(R.id.rd3)
    RadioButton rd3;
    @BindView(R.id.rd4)
    RadioButton rd4;
    @BindView(R.id.rd5)
    RadioButton rd5;
    @BindView(R.id.rd6)
    RadioButton rd6;

    @BindView(R.id.commit_btn)
    Button commit_btn;

    private List<RadioButton> buttons;

    private String type = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed_back);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }


    @Override
    protected void initViews() {
        super.initViews();
        initTitle("意见反馈");
        buttons = new ArrayList<>();
        buttons.add(rd1);
        buttons.add(rd2);
        buttons.add(rd3);
        buttons.add(rd4);
        buttons.add(rd5);
        buttons.add(rd6);

        for (int i = 0; i < buttons.size(); i++) {
            final int curP = i;
            buttons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setChecked(curP);
                }
            });
        }

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.showInputMethod(editText);
            }
        });

        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitFeedBack();
            }
        });
    }

    private void setChecked(int p) {
        type = String.valueOf(p + 1);
        for (int i = 0; i < buttons.size(); i++) {
            if (p == i) {
                buttons.get(i).setChecked(true);
            } else {
                buttons.get(i).setChecked(false);
            }
        }
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }

    private void commitFeedBack() {
        String content = editText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            UIHelper.toastMsg("请输入反馈内容");
            return;
        }
        CommonUtils.hideInputMethod(editText,this);

        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");

        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        params.put("content", content);
        params.put("type", type);

        OkGo.<String>post(AppUrls.FrontCommentUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(UserFeedBackActivity.this, params, user_token))
                .tag(this)
                .execute(new DialogCallBack(UserFeedBackActivity.this, true) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                finish();
                            } else {
                            }
                            UIHelper.toastMsg(js.getString("message"));
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
                        HandleResponse.handleException(response, UserFeedBackActivity.this);
                    }
                });
    }


}
