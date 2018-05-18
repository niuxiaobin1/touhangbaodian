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


/**
 * 非标资产发布
 */
public class ReleaseNonStandActivity extends BaseActivity {

    @BindView(R.id.commit_btn)
    Button commit_btn;

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

    @BindView(R.id.et6)
    EditText et6;

    @BindView(R.id.et7)
    EditText et7;

    @BindView(R.id.et8)
    EditText et8;

    @BindView(R.id.et9)
    EditText et9;

    @BindView(R.id.tv10)
    TextView tv10;

    @BindView(R.id.tv11)
    TextView tv11;

    @BindView(R.id.tv12)
    TextView tv12;

    @BindView(R.id.tv13)
    TextView tv13;

    @BindView(R.id.et14)
    EditText et14;

    @BindView(R.id.et15)
    EditText et15;

    @BindView(R.id.layout1)
    LinearLayout layout1;
    @BindView(R.id.layout2)
    LinearLayout layout2;
    @BindView(R.id.layout3)
    LinearLayout layout3;
    @BindView(R.id.layout4)
    LinearLayout layout4;

    private String[] types = new String[]{
            "公司债", "ABS", "短期融资券", "专项收益计划", "PPN", "信托计划", "其他"};
    private List<TextView> textViews;
    private List<String> vaules;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_non_stand);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        initTitle("非投标资产发布");

        textViews = new ArrayList<>();
        vaules = new ArrayList<>();
        init();

        for (int i = 0; i < textViews.size(); i++) {
            final TextView et = textViews.get(i);
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
                    vaules.set(position, s.toString());
                }
            });
        }

        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker1(v, 0);
            }
        });

        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker2(v, 0);
            }
        });

        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker3(v, 0);
            }
        });

        layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker4(v, 0);
            }
        });

        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commit();
            }
        });
    }

    private void init() {
        textViews.add(et1);
        textViews.add(et2);
        textViews.add(et3);
        textViews.add(et4);
        textViews.add(et5);
        textViews.add(et6);
        textViews.add(et7);
        textViews.add(et8);
        textViews.add(et9);
        textViews.add(tv10);
        textViews.add(tv11);
        textViews.add(tv12);
        textViews.add(tv13);
        textViews.add(et14);
        textViews.add(et15);

        for (int i = 0; i < textViews.size(); i++) {
            vaules.add("");
        }


        et4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String txt = et4.getText().toString().trim();
                    if (!TextUtils.isEmpty(txt)) {
                        et4.setText(txt + "亿");
                    }
                }
            }
        });
        et5.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String txt = et5.getText().toString().trim();
                    if (!TextUtils.isEmpty(txt)) {
                        et5.setText(txt + "天");
                    }
                }
            }
        });
        et6.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String txt = et6.getText().toString().trim();
                    if (!TextUtils.isEmpty(txt)) {
                        et6.setText(txt + "%");
                    }
                }
            }
        });

    }


    public void onOptionPicker1(View view, final int left) {
        CommonUtils.hideSoftInput(this, view);

        showSelectDialog1("", new String[]{
                "否", "是"
        }, left);
    }


    public void onOptionPicker2(View view, final int left) {
        CommonUtils.hideSoftInput(this, view);

        showSelectDialog2("", new String[]{
                "AAA", "AA+", "AA-", "A+", "其他"
        }, left);
    }


    public void onOptionPicker3(View view, final int left) {
        CommonUtils.hideSoftInput(this, view);

        showSelectDialog3("", new String[]{
                "AAA", "AA+", "AA-", "A+", "其他"
        }, left);
    }


    public void onOptionPicker4(View view, final int left) {
        CommonUtils.hideSoftInput(this, view);

        showSelectDialog4("", types, left);
    }

    private void showSelectDialog1(String title, final String[] lists, final int left) {
        builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv10.setText(lists[which]);

                    }
                }).show();
    }

    private void showSelectDialog2(String title, final String[] lists, final int left) {
        builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv11.setText(lists[which]);


                    }
                }).show();
    }

    private void showSelectDialog3(String title, final String[] lists, final int left) {
        builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv12.setText(lists[which]);


                    }
                }).show();
    }

    private void showSelectDialog4(String title, final String[] lists, final int left) {
        builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv13.setText(lists[which]);
                    }
                }).show();
    }


    private void Commit() {

        for (int i = 0; i < vaules.size(); i++) {
            if (i == 3) {
                continue;
            }
            if (TextUtils.isEmpty(vaules.get(i))) {
                UIHelper.toastMsg("录入信息不完整");
                return;
            }
        }

        if (!CommonUtils.isMobileNO(vaules.get(1))) {
            UIHelper.toastMsg("手机号码不正确");
            return;
        }


        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        params.put("name", vaules.get(0));
        params.put("contact", vaules.get(1));
        params.put("purpose", vaules.get(2));
        params.put("amount", vaules.get(3).contains("亿")?
                vaules.get(3).split("亿")[0]:vaules.get(3)
        );
        params.put("time_limit", vaules.get(4).contains("天")?
                vaules.get(4).split("天")[0]:vaules.get(4));
        params.put("rates", vaules.get(5).contains("%")?
                vaules.get(5).split("%")[0]:vaules.get(5));
        params.put("guarantee_method", vaules.get(6));
        params.put("explain", vaules.get(7));
        params.put("measures", vaules.get(8));
        params.put("is_state", getIs_stateId(vaules.get(9)));
        params.put("bond_rating", vaules.get(10));
        params.put("subject_rating", vaules.get(11));
        params.put("type", getTypeId(vaules.get(12)));
        params.put("description", vaules.get(13));
        params.put("main", vaules.get(14));

        OkGo.<String>post(AppUrls.NonStandardSaveUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(ReleaseNonStandActivity.this, params, user_token))
                .execute(new DialogCallBack(ReleaseNonStandActivity.this, true) {
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
                        HandleResponse.handleException(response, ReleaseNonStandActivity.this);
                    }
                });

    }

    private String getIs_stateId(String value) {
        if (value.equals("否")) {
            return "0";
        } else {
            return "1";
        }
    }

    private String getTypeId(String value) {
        String result = "";
        for (int i = 0; i < types.length; i++) {
            if (value.equals(types[i])) {
                result = String.valueOf(i + 1);
                break;
            }
        }
        return result;
    }

}
