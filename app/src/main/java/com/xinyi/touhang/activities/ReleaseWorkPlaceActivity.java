package com.xinyi.touhang.activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.xinyi.touhang.utils.CommonUtils;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.SpUtils;
import com.xinyi.touhang.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReleaseWorkPlaceActivity extends BaseActivity {


    @BindView(R.id.commit_btn)
    Button commit_btn;

    @BindView(R.id.layout1)
    LinearLayout layout1;

    @BindView(R.id.et1)
    EditText et1;

    @BindView(R.id.et2)
    EditText et2;

    @BindView(R.id.et3)
    EditText et3;

    @BindView(R.id.et4)
    EditText et4;

    @BindView(R.id.et5)
    EditText et5;

    @BindView(R.id.tv6)
    TextView tv6;

    @BindView(R.id.et7)
    EditText et7;

    @BindView(R.id.et8)
    EditText et8;

    @BindView(R.id.et9)
    EditText et9;

    private List<TextView> Edittexts1;
    private List<String> Vaules1;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_work_place);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        initTitle("职场详情发布");

        Edittexts1 = new ArrayList<>();
        Vaules1 = new ArrayList<>();
        init();


        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker1(v);
            }
        });



        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCommit();

            }
        });
    }

    private void init() {

        Edittexts1.add(et1);
        Edittexts1.add(et2);
        Edittexts1.add(et3);
        Edittexts1.add(et4);
        Edittexts1.add(et5);
        Edittexts1.add(tv6);
        Edittexts1.add(et7);
        Edittexts1.add(et8);
        Edittexts1.add(et9);

        for (int i = 0; i < Edittexts1.size(); i++) {
            Vaules1.add("");
        }

        for (int i = 0; i < Edittexts1.size(); i++) {
            final TextView et = Edittexts1.get(i);
            final int position = i;
            if (et instanceof EditText) {
                et.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtils.showInputMethod((EditText) et);
                    }
                });
            }


            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Vaules1.set(position, s.toString());
                }
            });
        }

    }


    public void onOptionPicker1(View view) {
        CommonUtils.hideSoftInput(this, view);

        showSelectDialog1("", new String[]{
                "券商", "创投", "银行", "其他中介"
        });

    }
    private void showSelectDialog1(String title, final String[] lists) {
        builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       tv6.setText(lists[which]);

                    }
                }).show();
    }

    private void doCommit() {

        CommonUtils.hideSoftInput(this,et1);

        for (int i = 0; i < Vaules1.size(); i++) {

            if (TextUtils.isEmpty(Vaules1.get(i))) {
                UIHelper.toastMsg("录入信息不完整");
                return;
            }
        }


        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        params.put("name", Vaules1.get(0));
        params.put("num", Vaules1.get(1));
        params.put("price", Vaules1.get(2));
        params.put("address", Vaules1.get(3));
        params.put("company", Vaules1.get(4));
        params.put("type", getTypeId(Vaules1.get(5)));
        params.put("description", Vaules1.get(6));
        params.put("tips", Vaules1.get(7));
        params.put("contact", Vaules1.get(8));

        OkGo.<String>post(AppUrls.OfferSaveUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(ReleaseWorkPlaceActivity.this, params, user_token))
                .execute(new DialogCallBack(ReleaseWorkPlaceActivity.this, true) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());
                            if (js.getBoolean("result")) {
                            } else {
                            }
                            UIHelper.toastMsg(js.getString("message"));
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
                        HandleResponse.handleException(response, ReleaseWorkPlaceActivity.this);
                    }
                });
    }

    private String getTypeId(String value){
        String id="";
        switch (value){
            case "券商":
                id="1";
                break;
            case "创投":
                id="2";
                break;
            case "银行":
                id="3";
                break;
            case "其他中介":
                id="4";
                break;
        }
        return id;
    }

}
