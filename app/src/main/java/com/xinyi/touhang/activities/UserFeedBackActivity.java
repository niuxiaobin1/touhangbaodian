package com.xinyi.touhang.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserFeedBackActivity extends BaseActivity {

    @BindView(R.id.editText)
    EditText editText;

    @BindView(R.id.radioGroup)
    RadioGroup  radioGroup;

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
        buttons=new ArrayList<>();
        buttons.add(rd1);
        buttons.add(rd2);
        buttons.add(rd3);
        buttons.add(rd4);
        buttons.add(rd5);
        buttons.add(rd6);

        for (int i = 0; i <buttons.size() ; i++) {
            final int curP=i;
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
    }

    private void setChecked(int p){
        for (int i = 0; i <buttons.size() ; i++) {
         if (p==i){
             buttons.get(i).setChecked(true);
         }  else{
             buttons.get(i).setChecked(false);
         }
        }
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }



}
