package com.xinyi.touhang.base;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.xinyi.touhang.R;
import com.xinyi.touhang.utils.AppManager;
import com.xinyi.touhang.utils.StatusBarUtil;

public class BaseActivity extends AppCompatActivity {

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        StatusBarUtil.transparencyBar(BaseActivity.this);
        AppManager.addActivity(BaseActivity.this);
        StatusBarUtil.StatusBarLightMode(BaseActivity.this);
        //把所有的activity全部添加在栈里，在需要清除所有页面的地方调用AppExit()方法即可。
    }


    protected void initViews() {

    }

    protected void initDatas() {

    }

    protected void showToast(@StringRes int string) {
        if (toast == null) {
            toast = Toast.makeText(this, string, Toast.LENGTH_SHORT);
        } else {
            toast.setText(string);
        }
        toast.show();
    }

}