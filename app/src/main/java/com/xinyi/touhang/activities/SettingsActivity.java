package com.xinyi.touhang.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.utils.AppManager;
import com.xinyi.touhang.utils.SpUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity {

    //意见反馈
    @BindView(R.id.feedbackLayout)
    RelativeLayout feedbackLayout;

    @BindView(R.id.logout_btn)
    Button logout_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        initTitle("设置");
        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        if (TextUtils.isEmpty(user_token)) {
            logout_btn.setVisibility(View.GONE);
        } else {
            logout_btn.setVisibility(View.VISIBLE);
        }

        feedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    Intent it = new Intent(SettingsActivity.this, UserFeedBackActivity.class);
                    startActivity(it);
                }
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpUtils.clear(SettingsActivity.this);
                AppManager.finishAllActivity();
                Intent it = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(it);
            }
        });
    }

    private boolean checkLogin() {
        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        if (TextUtils.isEmpty(user_token)) {
            login();
            return false;
        } else {
            return true;
        }
    }


    private void login() {
        Intent it = new Intent(this, LoginActivity.class);
        startActivity(it);
    }

}
