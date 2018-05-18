package com.xinyi.touhang.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.versionTv)
    TextView versionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        initViews();
    }

    @Override
    protected void initViews() {
        super.initViews();
        initTitle("关于我们");
        versionTv.setText("V "+CommonUtils.getAppVersionName(this)[1]);
    }
}
