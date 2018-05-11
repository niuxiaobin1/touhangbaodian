package com.xinyi.touhang.activities;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.xinyi.touhang.constants.Configer;
import com.xinyi.touhang.utils.BitmapUtil;
import com.xinyi.touhang.utils.CommonUtils;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.SpUtils;
import com.xinyi.touhang.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;

public class RleaseBusinessActivity extends BaseActivity {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.suply_layout)
    LinearLayout suply_layout;

    @BindView(R.id.demand_layout)
    LinearLayout demand_layout;

    @BindView(R.id.commit_btn)
    Button commit_btn;
    //供方
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

    @BindView(R.id.tv8)
    TextView tv8;

    @BindView(R.id.tv9)
    TextView tv9;

    @BindView(R.id.tv10)
    TextView tv10;
    @BindView(R.id.tv11)
    TextView tv11;
    @BindView(R.id.et12)
    EditText et12;

    @BindView(R.id.layout1)
    LinearLayout layout1;
    @BindView(R.id.layout2)
    LinearLayout layout2;
    @BindView(R.id.layout3)
    LinearLayout layout3;
    @BindView(R.id.layout4)
    LinearLayout layout4;

    //需方

    @BindView(R.id.det1)
    EditText det1;

    @BindView(R.id.det2)
    EditText det2;

    @BindView(R.id.det3)
    EditText det3;

    @BindView(R.id.det4)
    EditText det4;

    @BindView(R.id.det5)
    EditText det5;

    @BindView(R.id.det6)
    EditText det6;

    @BindView(R.id.dtv7)
    TextView dtv7;

    @BindView(R.id.dtv8)
    TextView dtv8;

    @BindView(R.id.dtv9)
    TextView dtv9;

    @BindView(R.id.dtv10)
    TextView dtv10;
    @BindView(R.id.det11)
    EditText det11;

    @BindView(R.id.dlayout1)
    LinearLayout dlayout1;
    @BindView(R.id.dlayout2)
    LinearLayout dlayout2;
    @BindView(R.id.dlayout3)
    LinearLayout dlayout3;
    @BindView(R.id.dlayout4)
    LinearLayout dlayout4;

    private List<TextView> sEdittexts;
    private List<TextView> dEdittexts;
    private List<String> sVaules;
    private List<String> dVaules;
    OptionPicker picker;
    AlertDialog.Builder builder;
    private String[] titles = new String[]{"项目供方", "项目需方"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rlease_business);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        initTitle("项目供方发布");
        initTabs();

        sEdittexts = new ArrayList<>();
        dEdittexts = new ArrayList<>();
        sVaules = new ArrayList<>();
        dVaules = new ArrayList<>();
        init();

        for (int i = 0; i < sEdittexts.size(); i++) {
            final TextView et = sEdittexts.get(i);
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
                    sVaules.set(position, s.toString());
                }
            });
        }


        for (int i = 0; i < dEdittexts.size(); i++) {
            final TextView et = dEdittexts.get(i);
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
                    dVaules.set(position, s.toString());
                }
            });
        }


        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker1(v, 0);
            }
        });
        dlayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker1(v, 1);
            }
        });

        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker2(v, 0);
            }
        });
        dlayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker2(v, 1);
            }
        });

        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker3(v, 0);
            }
        });
        dlayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker3(v, 1);
            }
        });

        layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker4(v, 0);
            }
        });
        dlayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker4(v, 1);
            }
        });

        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tabLayout.getSelectedTabPosition() == 0) {
                    sCommit();
                } else {
                    dCommit();
                }
            }
        });

    }

    private void init() {
        sEdittexts.add(et1);
        sEdittexts.add(et2);
        sEdittexts.add(et3);
        sEdittexts.add(et4);
        sEdittexts.add(et5);
        sEdittexts.add(et6);
        sEdittexts.add(et7);
        sEdittexts.add(tv8);
        sEdittexts.add(tv9);
        sEdittexts.add(tv10);
        sEdittexts.add(tv11);
        sEdittexts.add(et12);
        for (int i = 0; i < sEdittexts.size(); i++) {
            sVaules.add("");
        }

        dEdittexts.add(det1);
        dEdittexts.add(det2);
        dEdittexts.add(det3);
        dEdittexts.add(det4);
        dEdittexts.add(det5);
        dEdittexts.add(det6);
        dEdittexts.add(dtv7);
        dEdittexts.add(dtv8);
        dEdittexts.add(dtv9);
        dEdittexts.add(dtv10);
        dEdittexts.add(det11);
        for (int i = 0; i < dEdittexts.size(); i++) {
            dVaules.add("");
        }

    }

    private void initTabs() {

        for (int i = 0; i < titles.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(titles[i]));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals(titles[0])) {
                    initTitle("项目供方发布");
                    suply_layout.setVisibility(View.VISIBLE);
                    demand_layout.setVisibility(View.GONE);
                } else {
                    initTitle("项目需方发布");
                    suply_layout.setVisibility(View.GONE);
                    demand_layout.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }

    public void onOptionPicker1(View view, final int left) {
        CommonUtils.hideSoftInput(this, view);

        showSelectDialog1("", new String[]{
                "天使轮", "A轮", "B轮", "C轮", "上市前", "无限制"
        }, left);

//        if (picker != null && picker.isShowing()) {
//            picker.dismiss();
//            picker = null;
//        }
//        picker = new OptionPicker(this, new String[]{
//                "天使轮", "A轮", "B轮", "C轮", "上市前", "无限制"
//        });
//        picker.setTextSize(14);
//        picker.setCanceledOnTouchOutside(false);
//        picker.setDividerRatio(WheelView.DividerConfig.FILL);
//        picker.setShadowColor(Color.RED, 40);
//        picker.setSelectedIndex(0);
//        picker.setCycleDisable(true);
//        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
//            @Override
//            public void onOptionPicked(int index, String item) {
//                if (left == 0) {
//                    tv8.setText(item);
//                } else {
//                    dtv7.setText(item);
//                }
//
//            }
//        });
//        picker.show();
    }


    public void onOptionPicker2(View view, final int left) {
        CommonUtils.hideSoftInput(this, view);
        showSelectDialog2("", new String[]{
                "股权转让", "增资", "先债后股", "纯债", "无限制"
        }, left);
//        if (picker != null && picker.isShowing()) {
//            picker.dismiss();
//            picker = null;
//        }
//        picker = new OptionPicker(this, new String[]{
//                "股权转让", "增资", "先债后股", "纯债", "无限制"
//        });
//        picker.setTextSize(14);
//        picker.setCanceledOnTouchOutside(false);
//        picker.setDividerRatio(WheelView.DividerConfig.FILL);
//        picker.setShadowColor(Color.RED, 40);
//        picker.setSelectedIndex(0);
//        picker.setCycleDisable(true);
//        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
//            @Override
//            public void onOptionPicked(int index, String item) {
//                if (left == 0) {
//                    tv9.setText(item);
//                } else {
//                    dtv8.setText(item);
//                }
//
//            }
//        });
//        picker.show();
    }


    public void onOptionPicker3(View view, final int left) {
        CommonUtils.hideSoftInput(this, view);

        showSelectDialog3("", new String[]{
                "否", "是"
        }, left);
//        if (picker != null && picker.isShowing()) {
//            picker.dismiss();
//            picker = null;
//        }
//        picker = new OptionPicker(this, new String[]{
//                "否", "是"
//        });
//        picker.setTextSize(14);
//        picker.setCanceledOnTouchOutside(false);
//        picker.setDividerRatio(WheelView.DividerConfig.FILL);
//        picker.setShadowColor(Color.RED, 40);
//        picker.setSelectedIndex(0);
//        picker.setCycleDisable(true);
//        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
//            @Override
//            public void onOptionPicked(int index, String item) {
//                if (left == 0) {
//                    tv10.setText(item);
//                } else {
//                    dtv9.setText(item);
//                }
//
//            }
//        });
//        picker.show();
    }

    public void onOptionPicker4(View view, final int left) {
        CommonUtils.hideSoftInput(this, view);

        showSelectDialog4("", new String[]{
                "转让", "原股东回购", "上市", "不限"
        }, left);
//        if (picker != null && picker.isShowing()) {
//            picker.dismiss();
//            picker = null;
//        }
//        picker = new OptionPicker(this, new String[]{
//                "转让", "原股东回购", "上市", "不限"
//        });
//        picker.setTextSize(14);
//        picker.setCanceledOnTouchOutside(false);
//        picker.setDividerRatio(WheelView.DividerConfig.FILL);
//        picker.setShadowColor(Color.RED, 40);
//        picker.setSelectedIndex(0);
//        picker.setCycleDisable(true);
//        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
//            @Override
//            public void onOptionPicked(int index, String item) {
//                if (left == 0) {
//                    tv11.setText(item);
//                } else {
//                    dtv10.setText(item);
//                }
//
//            }
//        });
//        picker.show();
    }

    private void sCommit() {

        for (int i = 0; i < sVaules.size(); i++) {
            if (TextUtils.isEmpty(sVaules.get(i))) {
                UIHelper.toastMsg("录入信息不完整");
                return;
            }
        }


        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        params.put("name", sVaules.get(0));
        params.put("telephone", sVaules.get(1));
        params.put("address", sVaules.get(2));
        params.put("industry", sVaules.get(3));
        params.put("purpose", sVaules.get(4));
        params.put("price", sVaules.get(5));
        params.put("points", sVaules.get(6));
        params.put("stage", getStageId(sVaules.get(7)));
        params.put("type", getTypeId(sVaules.get(8)));
        params.put("special", getSpecialId(sVaules.get(9)));
        params.put("out", getOutId(sVaules.get(10)));
        params.put("content", sVaules.get(11));

        OkGo.<String>post(AppUrls.SupplySaveUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(RleaseBusinessActivity.this, params, user_token))
                .execute(new DialogCallBack(RleaseBusinessActivity.this, true) {
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
                        HandleResponse.handleException(response, RleaseBusinessActivity.this);
                    }
                });


    }

    private void dCommit() {
        for (int i = 0; i < dVaules.size(); i++) {
            if (TextUtils.isEmpty(dVaules.get(i))) {
                UIHelper.toastMsg("录入信息不完整");
                return;
            }
        }

        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        params.put("name", sVaules.get(0));
        params.put("telephone", sVaules.get(1));
        params.put("address", sVaules.get(2));
        params.put("industry", sVaules.get(3));
        params.put("price", sVaules.get(4));
        params.put("points", sVaules.get(5));
        params.put("stage", getStageId(sVaules.get(6)));
        params.put("type", getTypeId(sVaules.get(7)));
        params.put("special", getSpecialId(sVaules.get(8)));
        params.put("out", getOutId(sVaules.get(9)));
        params.put("content", sVaules.get(10));

        OkGo.<String>post(AppUrls.DemandSaveUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(RleaseBusinessActivity.this, params, user_token))
                .execute(new DialogCallBack(RleaseBusinessActivity.this, true) {
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
                        HandleResponse.handleException(response, RleaseBusinessActivity.this);
                    }
                });
    }


    private String getStageId(String value) {
        if (value.equals("天使轮")) {
            return "1";
        } else if (value.equals("A轮")) {
            return "2";
        } else if (value.equals("B轮")) {
            return "3";
        } else if (value.equals("C轮")) {
            return "4";
        } else if (value.equals("上市前")) {
            return "5";
        } else {
            return "6";
        }
    }

    private String getTypeId(String value) {
        if (value.equals("股权转让")) {
            return "1";
        } else if (value.equals("增资")) {
            return "2";
        } else if (value.equals("先债后股")) {
            return "3";
        } else if (value.equals("纯债")) {
            return "4";
        } else if (value.equals("无限制")) {
            return "5";
        } else {
            return "6";
        }
    }

    private String getOutId(String value) {
        if (value.equals("转让")) {
            return "1";
        } else if (value.equals("原股东回收")) {
            return "2";
        } else if (value.equals("上市")) {
            return "3";
        } else if (value.equals("不限")) {
            return "4";
        } else {
            return "";
        }
    }

    private String getSpecialId(String value) {
        if (value.equals("否")) {
            return "0";
        } else {
            return "1";
        }
    }


    private void showSelectDialog1(String title, final String[] lists, final int left) {
        builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (left == 0) {
                            tv8.setText(lists[which]);
                        } else {
                            dtv7.setText(lists[which]);
                        }


                    }
                }).show();
    }

    private void showSelectDialog2(String title, final String[] lists, final int left) {
        builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (left == 0) {
                            tv9.setText(lists[which]);
                        } else {
                            dtv8.setText(lists[which]);
                        }


                    }
                }).show();
    }

    private void showSelectDialog3(String title, final String[] lists, final int left) {
        builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (left == 0) {
                            tv10.setText(lists[which]);
                        } else {
                            dtv9.setText(lists[which]);
                        }

                    }
                }).show();
    }

    private void showSelectDialog4(String title, final String[] lists, final int left) {
         builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (left == 0) {
                            tv11.setText(lists[which]);
                        } else {
                            dtv10.setText(lists[which]);
                        }

                    }
                }).show();
    }
}
