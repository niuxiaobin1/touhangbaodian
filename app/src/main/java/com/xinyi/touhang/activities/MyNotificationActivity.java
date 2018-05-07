package com.xinyi.touhang.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.fragments.other.MyCommentListFragment;
import com.xinyi.touhang.fragments.other.NotificationFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyNotificationActivity extends BaseActivity {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private List<Fragment> fragments;

    private MyPagerAdapter adapter;

    private String[] titles = new String[]{
            "评论我的", "通知"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notification);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }


    @Override
    protected void initViews() {
        super.initViews();
        initTitle("消息通知");
        initTabs();
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }



    private void initTabs() {
        fragments = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            fragments.add(NotificationFragment.newInstance(""+i, ""));
        }
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);
        ViewCompat.setElevation(tabLayout, 10);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
//                invalidateTablayout();
            }
        }, 100);

    }
}
