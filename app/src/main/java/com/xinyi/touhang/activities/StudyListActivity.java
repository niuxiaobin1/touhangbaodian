package com.xinyi.touhang.activities;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.base.BaseFragment;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.fragments.ConsulationFragment;
import com.xinyi.touhang.fragments.ConsulationInnerFragment;
import com.xinyi.touhang.fragments.other.StudyListFragment;
import com.xinyi.touhang.utils.CommonUtils;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.JsonUtils;
import com.xinyi.touhang.utils.UIHelper;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

public class StudyListActivity extends BaseActivity {
    public static final String INDEX = "_index";
    private int index = 0;

    @BindView(R.id.magic_indicator)
    MagicIndicator magic_indicator;

    @BindView(R.id.search_image)
    ImageView search_image;

    @BindView(R.id.search_et)
    EditText search_et;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private List<BaseFragment> fragments;

    private List<Integer> pageSelect = new ArrayList<>();//保存每个列表当前的筛选项
    private List<String> pageQuery = new ArrayList<>();//保存每个列表当前的搜索项
    private int currentIndex = 0;
    private List<Map<String, String>> mDataList = new ArrayList<>();
    private String[] titles = new String[]{"学习视频", "培训信息", "学习资料"};
    private String[] Urls = new String[]{AppUrls.VideoListsUrl, AppUrls.AdviseListsUrl, AppUrls.FileListsUrl};
    private MyPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_list);
        ButterKnife.bind(this);
        index = getIntent().getIntExtra(INDEX, 0);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        initTitle("学习");
        //默认全部选择：全部
        pageSelect.add(0);
        pageSelect.add(0);
        pageSelect.add(0);
        pageQuery.add("");
        pageQuery.add("");
        pageQuery.add("");
        initTabs();

        search_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.showInputMethod(search_et);
            }
        });

        search_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageQuery.set(currentIndex,search_et.getText().toString().trim());
                CommonUtils.hideSoftInput(StudyListActivity.this, search_et);
                fragments.get(currentIndex).onButton2Click(search_et.getText().toString().trim());
            }
        });


    }

    @Override
    protected void initDatas() {
        super.initDatas();

        HttpParams params = new HttpParams();

        OkGo.<String>post(AppUrls.VideoType_listUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(StudyListActivity.this, params, ""))
                .tag(this)
                .execute(new DialogCallBack(StudyListActivity.this, true) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());
                            if (js.getBoolean("result")) {
                                mDataList.clear();
                                Map<String, String> fMap = new HashMap<>();
                                fMap.put("id", "");
                                fMap.put("name", "全部");
                                fMap.put("created", "");
                                fMap.put("modified", "");
                                mDataList.add(fMap);

                                mDataList.addAll(JsonUtils.ArrayToList(
                                        js.getJSONArray("data"), new String[]{
                                                "id", "name", "created", "modified"
                                        }
                                ));
                                try {
                                    initMagicIndicator();
                                } catch (JSONException e) {
                                }

                            } else {
                                UIHelper.toastMsg(js.getString("message"));
                            }
                        } catch (JSONException e) {
                            UIHelper.toastMsg(e.getMessage());
                        }

                    }

                    @Override
                    public String convertResponse(Response response) throws Throwable {
                        HandleResponse.handleReponse(response);
                        return super.convertResponse(response);
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        HandleResponse.handleException(response, StudyListActivity.this);
                    }
                });

    }

    private void initTabs() {
        fragments = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            fragments.add(StudyListFragment.newInstance(titles[i], Urls[i]));
        }
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        ViewCompat.setElevation(tabLayout, 10);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index != 0) {
                    viewPager.setCurrentItem(index);
                }
            }
        }, 100);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                //还原筛选项
                magic_indicator.onPageSelected(pageSelect.get(position));
                magic_indicator.onPageScrolled(pageSelect.get(position), 0, 0);
                search_et.setText(pageQuery.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private void initMagicIndicator() throws JSONException {
        magic_indicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(StudyListActivity.this);
        commonNavigator.setScrollPivotX(0.35f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                final SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index).get("name"));
                simplePagerTitleView.setTextSize(12);
                simplePagerTitleView.setNormalColor(Color.parseColor("#4A4A4A"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#000000"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        magic_indicator.onPageSelected(index);
                        magic_indicator.onPageScrolled(index, 0, 0);
                        fragments.get(currentIndex).onButton1Click(mDataList.get(index).get("id"));
                        pageSelect.set(currentIndex, index);

                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                WrapPagerIndicator indicator = new WrapPagerIndicator(context);
                indicator.setFillColor(Color.parseColor("#FFD700"));
                return indicator;
            }
        });
        magic_indicator.setNavigator(commonNavigator);

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
}
