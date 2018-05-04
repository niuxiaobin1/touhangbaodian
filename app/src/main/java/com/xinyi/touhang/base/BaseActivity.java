package com.xinyi.touhang.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xinyi.touhang.R;
import com.xinyi.touhang.utils.AppManager;
import com.xinyi.touhang.utils.StatusBarUtil;

import butterknife.BindView;

public class BaseActivity extends AppCompatActivity {

    private Toast toast;

    private ImageView back_image;
    private TextView title_tv;
    private TextView right_tv;

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
        back_image = findViewById(R.id.back_image);
        title_tv = findViewById(R.id.title_tv);
        right_tv = findViewById(R.id.right_tv);
        if (back_image != null) {
            back_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

    }

    protected void initDatas() {

    }


    protected void initTitle(@StringRes int resId) {
        if (title_tv != null) {
            title_tv.setText(resId);
        }
    }

    protected void initRightTv(String title, Drawable drawable) {
        if (right_tv != null) {
            right_tv.setText(title);
            right_tv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            right_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRightClick();
                }
            });
        }
    }

    protected void initRightTv(String title, @ColorRes int color) {
        if (right_tv != null) {
            right_tv.setText(title);
            right_tv.setTextColor(getResources().getColor(color));

            right_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRightClick();
                }
            });
        }

    }

    protected void onRightClick() {

    }

    protected void initTitle(String title) {
        if (title_tv != null) {
            title_tv.setText(title);
        }
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
