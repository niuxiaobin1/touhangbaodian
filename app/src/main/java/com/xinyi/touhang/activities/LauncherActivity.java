package com.xinyi.touhang.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LauncherActivity extends BaseActivity {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private List<View> imageViews;

    private int currentItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        ButterKnife.bind(this);

        init();
        viewPager.setAdapter(new MyPagerAdapgter());

        viewPager.setOnTouchListener(new View.OnTouchListener() {

            float startX;
            float startY;//没有用到
            float endX;
            float endY;//没有用到

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        endX = event.getX();
                        endY = event.getY();
                        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

                        //获取屏幕的宽度
                        Point size = new Point();
                        windowManager.getDefaultDisplay().getSize(size);
                        int width = size.x;

                        //首先要确定的是，是否到了最后一页，然后判断是否向左滑动，并且滑动距离是否符合，我这里的判断距离是屏幕宽度的4分之一（这里可以适当控制）
                        if (currentItem == (imageViews.size() - 1) && startX - endX >= (width / 4)) {
                            goToMainActivity();//进入主页
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);//这部分代码是切换Activity时的动画，看起来就不会很生硬
                        }
                        break;
                }
                return false;
            }
        });


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean result = (boolean) SpUtils.get(LauncherActivity.this, "isFirst", true);
        if (!result) {
            Intent it = new Intent(LauncherActivity.this, MainActivity.class);
            startActivity(it);
            finish();
        }

    }

    private void goToMainActivity() {
        SpUtils.put(LauncherActivity.this, "isFirst", false);
        Intent it = new Intent(LauncherActivity.this, MainActivity.class);
        startActivity(it);
        finish();
    }

    private void init() {
        imageViews = new ArrayList<>();

        final ImageView imageView = new ImageView(LauncherActivity.this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.page_first);
        imageViews.add(imageView);

        ImageView imageView1 = new ImageView(LauncherActivity.this);
        imageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView1.setImageResource(R.mipmap.page_two);
        imageViews.add(imageView1);

        ImageView imageView2 = new ImageView(LauncherActivity.this);
        imageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView2.setImageResource(R.mipmap.page_three);
        imageViews.add(imageView2);

        ImageView imageView3 = new ImageView(LauncherActivity.this);
        imageView3.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView3.setImageResource(R.mipmap.page_four);
        imageViews.add(imageView3);

        ImageView imageView4 = new ImageView(LauncherActivity.this);
        imageView4.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView4.setImageResource(R.mipmap.page_five);
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });
        imageViews.add(imageView4);


    }

    class MyPagerAdapgter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {


            container.addView(imageViews.get(position));

            return imageViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews.get(position));
        }
    }


}
