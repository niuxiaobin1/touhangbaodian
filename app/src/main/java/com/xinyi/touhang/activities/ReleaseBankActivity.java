package com.xinyi.touhang.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
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
 * 银行间发布
 */
public class ReleaseBankActivity extends BaseActivity {

    @BindView(R.id.commit_btn)
    Button commit_btn;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    //短期拆借
    @BindView(R.id.select_layout1)
    LinearLayout select_layout1;

    @BindView(R.id.et11)
    EditText et11;
    @BindView(R.id.et12)
    EditText et12;

    @BindView(R.id.layout11)
    LinearLayout layout11;

    @BindView(R.id.tv13)
    TextView tv13;
    @BindView(R.id.et14)
    EditText et14;
    @BindView(R.id.et15)
    EditText et15;
    @BindView(R.id.et16)
    EditText et16;
    @BindView(R.id.et17)
    EditText et17;

    //银行同存
    @BindView(R.id.select_layout2)
    LinearLayout select_layout2;

    @BindView(R.id.et21)
    EditText et21;
    @BindView(R.id.et22)
    EditText et22;

    @BindView(R.id.layout21)
    LinearLayout layout21;

    @BindView(R.id.tv23)
    TextView tv23;
    @BindView(R.id.et24)
    EditText et24;
    @BindView(R.id.et25)
    EditText et25;
    @BindView(R.id.et26)
    EditText et26;
    @BindView(R.id.et27)
    EditText et27;

    //理财产品
    @BindView(R.id.select_layout3)
    LinearLayout select_layout3;

    @BindView(R.id.et31)
    EditText et31;

    @BindView(R.id.et32)
    EditText et32;

    @BindView(R.id.layout31)
    LinearLayout layout31;
    @BindView(R.id.tv33)
    TextView tv33;

    @BindView(R.id.et34)
    EditText et34;

    @BindView(R.id.et35)
    EditText et35;


    @BindView(R.id.layout32)
    LinearLayout layout32;
    @BindView(R.id.tv36)
    TextView tv36;


    @BindView(R.id.layout33)
    LinearLayout layout33;
    @BindView(R.id.tv37)
    TextView tv37;

    @BindView(R.id.et38)
    EditText et38;


    private List<LinearLayout> selectLayouts;
    private List<TextView> Edittexts1;
    private List<TextView> Edittexts2;
    private List<TextView> Edittexts3;
    private List<String> Vaules1;
    private List<String> Vaules2;
    private List<String> Vaules3;
    private String[] titles = new String[]{"短期拆借", "银行同存", "理财产品"};

    AlertDialog.Builder builder;
    private int currentSelect = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_bank);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        initTitle("项目供方发布");
        initTabs();

        Edittexts1 = new ArrayList<>();
        Edittexts2 = new ArrayList<>();
        Edittexts3 = new ArrayList<>();
        Vaules1 = new ArrayList<>();
        Vaules2 = new ArrayList<>();
        Vaules3 = new ArrayList<>();
        init();


        layout11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker1(v, 0);
            }
        });
        layout21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker1(v, 1);
            }
        });
        layout31.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker1(v, 2);
            }
        });

        layout32.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker2(v);
            }
        });
        layout33.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker3(v);
            }
        });


        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCommit();

            }
        });
        setSelectLayout(0);
    }

    private void init() {
        selectLayouts = new ArrayList<>();
        selectLayouts.add(select_layout1);
        selectLayouts.add(select_layout2);
        selectLayouts.add(select_layout3);

        Edittexts1.add(et11);
        Edittexts1.add(et12);
        Edittexts1.add(tv13);
        Edittexts1.add(et14);
        Edittexts1.add(et15);
        Edittexts1.add(et16);
        Edittexts1.add(et17);

        for (int i = 0; i < Edittexts1.size(); i++) {
            Vaules1.add("");
        }
        Edittexts2.add(et21);
        Edittexts2.add(et22);
        Edittexts2.add(tv23);
        Edittexts2.add(et24);
        Edittexts2.add(et25);
        Edittexts2.add(et26);
        Edittexts2.add(et27);

        for (int i = 0; i < Edittexts2.size(); i++) {
            Vaules2.add("");
        }
        Edittexts3.add(et31);
        Edittexts3.add(et32);
        Edittexts3.add(tv33);
        Edittexts3.add(et34);
        Edittexts3.add(et35);
        Edittexts3.add(tv36);
        Edittexts3.add(tv37);
        Edittexts3.add(et38);
        for (int i = 0; i < Edittexts3.size(); i++) {
            Vaules3.add("");
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


        for (int i = 0; i < Edittexts2.size(); i++) {
            final TextView et = Edittexts2.get(i);
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
                    Vaules2.set(position, s.toString());
                }
            });
        }

        for (int i = 0; i < Edittexts3.size(); i++) {
            final TextView et = Edittexts3.get(i);
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
                    Vaules3.set(position, s.toString());
                }
            });
        }

        et14.setOnFocusChangeListener(onFocusChangeListener1);
        et24.setOnFocusChangeListener(onFocusChangeListener1);
        et34.setOnFocusChangeListener(onFocusChangeListener1);

        et15.setOnFocusChangeListener(onFocusChangeListener2);
        et25.setOnFocusChangeListener(onFocusChangeListener2);

        et16.setOnFocusChangeListener(onFocusChangeListener3);
        et26.setOnFocusChangeListener(onFocusChangeListener3);
        et35.setOnFocusChangeListener(onFocusChangeListener4);
    }

    private View.OnFocusChangeListener onFocusChangeListener2 = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    ((EditText) v).setText(text + "亿");
                }
            }
        }
    };

    private View.OnFocusChangeListener onFocusChangeListener1 = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    ((EditText) v).setText(text + "%");
                }
            }
        }
    };
    private View.OnFocusChangeListener onFocusChangeListener3 = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    ((EditText) v).setText(text + "天");
                }
            }
        }
    };
    private View.OnFocusChangeListener onFocusChangeListener4 = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    ((EditText) v).setText(text + "月");
                }
            }
        }
    };

    /**
     * 切换布局
     *
     * @param position
     */
    private void setSelectLayout(int position) {
        currentSelect = position;
        for (int i = 0; i < selectLayouts.size(); i++) {
            if (position == i) {
                selectLayouts.get(i).setVisibility(View.VISIBLE);
            } else {
                selectLayouts.get(i).setVisibility(View.GONE);
            }
        }
    }

    private void initTabs() {

        for (int i = 0; i < titles.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(titles[i]));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                initTitle(tab.getText() + "发布");
                if (tab.getText().equals(titles[0])) {
                    setSelectLayout(0);
                } else if (tab.getText().equals(titles[1])) {
                    setSelectLayout(1);
                } else {
                    setSelectLayout(2);
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

    public void onOptionPicker1(View view, int position) {
        CommonUtils.hideSoftInput(this, view);

        showSelectDialog1("", new String[]{
                "入", "出"
        }, position);

    }

    public void onOptionPicker2(View view) {
        CommonUtils.hideSoftInput(this, view);

        showSelectDialog2("", new String[]{
                "R1", "R1", "R3", "R4", "R5"
        });

    }

    public void onOptionPicker3(View view) {
        CommonUtils.hideSoftInput(this, view);

        showSelectDialog3("", new String[]{
                "保本", "非保本"
        });

    }

    private void showSelectDialog1(String title, final String[] lists, final int position) {
        builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (position == 0) {
                            tv13.setText(lists[which]);
                        } else if (position == 1) {
                            tv23.setText(lists[which]);
                        } else {
                            tv33.setText(lists[which]);
                        }


                    }
                }).show();
    }

    private void showSelectDialog2(String title, final String[] lists) {
        builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv36.setText(lists[which]);
                    }
                }).show();
    }

    private void showSelectDialog3(String title, final String[] lists) {
        builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv37.setText(lists[which]);
                    }
                }).show();
    }

    private void doCommit() {
        List<String> commitValues;
        if (currentSelect == 0) {
            commitValues = Vaules1;
        } else if (currentSelect == 1) {
            commitValues = Vaules2;
        } else {
            commitValues = Vaules3;
        }
        for (int i = 0; i < commitValues.size(); i++) {
            if (currentSelect != 2) {
                if (i == 4) {
                    continue;
                }
            }

            if (TextUtils.isEmpty(commitValues.get(i))) {
                UIHelper.toastMsg("录入信息不完整");
                return;
            }
        }

        if (!CommonUtils.isMobileNO(commitValues.get(1))) {
            UIHelper.toastMsg("手机号码不正确");
            return;
        }

        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        params.put("name", commitValues.get(0));
        params.put("contact", commitValues.get(1));
        params.put("direction", commitValues.get(2).equals("入")?"1":"2");
        params.put("rates", commitValues.get(3).contains("%")?
                commitValues.get(3).split("%")[0]:commitValues.get(3));
        if (currentSelect!=2){
            params.put("amount", commitValues.get(4).contains("亿")?
                    commitValues.get(4).split("亿")[0]:commitValues.get(4));
            params.put("time_limit", commitValues.get(5).contains("天")?
                    commitValues.get(5).split("天")[0]:commitValues.get(5));
            params.put("description", commitValues.get(6));
        }else{
            params.put("time_limit", commitValues.get(4).contains("月")?
                    commitValues.get(4).split("月")[0]:commitValues.get(4));
            params.put("rish", getRishId(commitValues.get(5)));
            params.put("baoben", commitValues.get(6).equals("保本")?"1":"2");
            params.put("description", commitValues.get(7));
        }

        params.put("type", String.valueOf(currentSelect+1));
        OkGo.<String>post(AppUrls.BankSaveUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(ReleaseBankActivity.this, params, user_token))
                .execute(new DialogCallBack(ReleaseBankActivity.this, true) {
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
                        HandleResponse.handleException(response, ReleaseBankActivity.this);
                    }
                });
    }


    private String getRishId(String value){
        String id="";
        switch (value){
            case "R1":
                id="1";
                break;
            case "R2":
                id="2";
                break;
            case "R3":
                id="3";
                break;
            case "R4":
                id="4";
                break;
            case "R5":
                id="5";
                break;
        }
        return id;
    }
}
