package com.xinyi.touhang.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.fragments.BusinessOpportunitiesFragment;
import com.xinyi.touhang.fragments.CommunicationFragment;
import com.xinyi.touhang.fragments.ConsulationFragment;
import com.xinyi.touhang.fragments.MineFragment;
import com.xinyi.touhang.fragments.SearchFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    //footer
    @BindView(R.id.consulation_layout)
    LinearLayout consulation_layout;

    @BindView(R.id.business_opportunities_layout)
    LinearLayout business_opportunities_layout;

    @BindView(R.id.commucation_layout)
    LinearLayout commucation_layout;

    @BindView(R.id.mine_layout)
    LinearLayout mine_layout;

    @BindView(R.id.search_layout)
    LinearLayout search_layout;
    //footer

    @BindView(R.id.fragment_holder)
    FrameLayout fragment_holder;

    private List<LinearLayout> footers;//footers
    private List<Fragment> fragmentsList;//fragments

    private ConsulationFragment consulationFragment;
    private BusinessOpportunitiesFragment businessOpportunitiesFragment;
    private SearchFragment searchFragment;
    private CommunicationFragment communicationFragment;
    private MineFragment mineFragment;

    private FragmentManager fragmentManager;
    //current show
    private int currentIndex;
    private Fragment currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }


    @Override
    protected void initViews() {
        super.initViews();

        fragmentManager = getSupportFragmentManager();

        fragmentsList = new ArrayList<>();
        footers = new ArrayList<>();
        //init footer
        footers.add(consulation_layout);
        footers.add(business_opportunities_layout);
        footers.add(search_layout);
        footers.add(commucation_layout);
        footers.add(mine_layout);

        //init fragments
        consulationFragment = ConsulationFragment.newInstance("", "");
        businessOpportunitiesFragment = BusinessOpportunitiesFragment.newInstance("", "");
        searchFragment = SearchFragment.newInstance("", "");
        communicationFragment = CommunicationFragment.newInstance("", "");
        mineFragment = MineFragment.newInstance("", "");

        fragmentsList.add(consulationFragment);
        fragmentsList.add(businessOpportunitiesFragment);
        fragmentsList.add(searchFragment);
        fragmentsList.add(communicationFragment);
        fragmentsList.add(mineFragment);

        //init single click

        for (int i = 0; i < footers.size(); i++) {
            final int position = i;
            footers.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchTab(position);
                }
            });
        }
        //first come
        switchTab(0);
    }


    /**
     * 切换Tab
     *
     * @param index
     */
    private void switchTab(int index) {
        for (int i = 0; i < footers.size(); i++) {
            if (i == index) {
                footers.get(i).getChildAt(0).setSelected(true);
                footers.get(i).getChildAt(1).setSelected(true);
            } else {
                footers.get(i).getChildAt(0).setSelected(false);
                footers.get(i).getChildAt(1).setSelected(false);
            }
        }
        swtichFragment(index);
    }

    private void swtichFragment(int index) {

        FragmentTransaction ft = fragmentManager.beginTransaction();

        //if current fragment is exit,hide it.
        if (currentFragment != null) {
            ft.hide(currentFragment);
        }
        //find fragment if exit
        Fragment fragment = fragmentManager.findFragmentByTag(
                fragmentsList.get(index).getClass().getName());
        if (null == fragment) {
            fragment = fragmentsList.get(index);
        }
        currentFragment = fragment;
        //check fragment is Added
        if (!fragment.isAdded()) {
            ft.add(R.id.fragment_holder, fragment, fragment.getClass().getName());
        } else {
            ft.show(fragment);
        }
        ft.commit();
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }
}
