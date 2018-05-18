package com.xinyi.touhang.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.PullRefreshLayout.OnRefreshListener;
import com.xinyi.touhang.PullRefreshLayout.PullRefreshLayout;
import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.LoginActivity;
import com.xinyi.touhang.activities.MyNotificationActivity;
import com.xinyi.touhang.activities.ReleaseBankActivity;
import com.xinyi.touhang.activities.ReleaseNonStandActivity;
import com.xinyi.touhang.activities.RleaseBusinessActivity;
import com.xinyi.touhang.adapter.BusinessOpportunitiesAdapter;
import com.xinyi.touhang.base.BaseFragment;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.fragments.business.BusinessInner1ragment;
import com.xinyi.touhang.fragments.business.BusinessInner2ragment;
import com.xinyi.touhang.fragments.business.BusinessInner3ragment;
import com.xinyi.touhang.fragments.other.NotificationFragment;
import com.xinyi.touhang.utils.CommonUtils;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DividerDecoration;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.JsonUtils;
import com.xinyi.touhang.utils.SpUtils;
import com.xinyi.touhang.utils.StatusBarUtil;
import com.xinyi.touhang.utils.UIHelper;
import com.xinyi.touhang.weight.ObservableScrollView;
import com.xinyi.touhang.weight.popupwindow.BusinessFilterPopupWindow;

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

/**
 * 商业机会
 * Use the {@link BusinessOpportunitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusinessOpportunitiesFragment extends BaseFragment {

    @BindView(R.id.businessviewPager)
    ViewPager businessviewPager;

    @BindView(R.id.select_tv)
    TextView select_tv;

    @BindView(R.id.search_image)
    ImageView search_image;

    @BindView(R.id.search_et)
    EditText search_et;

    @BindView(R.id.parentView)
    LinearLayout parentView;

    @BindView(R.id.release_tv)
    TextView release_tv;


    @BindView(R.id.tabLayout)
    TabLayout tabLayouts;

    @BindView(R.id.titleLayout)
    RelativeLayout titleLayout;

    private BusinessFilterPopupWindow popupWindow;
    private String[] selects1 = new String[]{"", "", "", ""};
    private String[] selects2 = new String[]{"", "", ""};

    private String[] titles = new String[]{"并购投资", "非标资产", "银行间"};
    private List<Map<String, String>> list1;
    private List<Map<String, String>> list2;
    private List<Map<String, String>> list3;
    private List<Map<String, String>> list4;

    private List<BaseFragment> fragments;
    private MyPagerAdapter adapter;

    private int currentIndex = 0;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BusinessOpportunitiesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BusinessOpportunitiesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BusinessOpportunitiesFragment newInstance(String param1, String param2) {
        BusinessOpportunitiesFragment fragment = new BusinessOpportunitiesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_business_opportunities, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void initViews() {
        initTabs();
        initSelect();
        parentView.setPadding(0, StatusBarUtil.getStatusBarHeight(getActivity()), 0, 0);

        release_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String confirm = (String) SpUtils.get(getActivity(), SpUtils.USERCONFIRM, "");
                if (!TextUtils.isEmpty(confirm) && confirm.equals("2")) {
                    showSelectDialog("请选择发布类型", titles);
                } else {
                    UIHelper.toastMsg("请您先进行实名认证");
                }


            }
        });

        businessviewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                if (position == 0) {
                    select_tv.setVisibility(View.GONE);
                } else {
                    select_tv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        select_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });

        search_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.showInputMethod(search_et);
            }
        });

        search_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideSoftInput(getActivity(), search_et);
                fragments.get(currentIndex).onButton1Click(search_et.getText().toString().trim());
            }
        });
    }

    private void initTabs() {
        fragments = new ArrayList<>();
        fragments.add(BusinessInner1ragment.newInstance("", ""));
        fragments.add(BusinessInner2ragment.newInstance("", ""));
        fragments.add(BusinessInner3ragment.newInstance("", ""));
        adapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
        businessviewPager.setOffscreenPageLimit(3);
        businessviewPager.setAdapter(adapter);
        ViewCompat.setElevation(tabLayouts, 10);
        tabLayouts.setupWithViewPager(businessviewPager);
        select_tv.setVisibility(View.GONE);
    }

    private void showPopup() {
        if (popupWindow != null) {
            popupWindow = null;
        }
        popupWindow = new BusinessFilterPopupWindow(getActivity(), list1, list2, list3, currentIndex == 2 ? null : list4);
        popupWindow.setOnLeftClickListener(new BusinessFilterPopupWindow.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                if (currentIndex == 1) {
                    selects1[0] = "";
                    selects1[1] = "";
                    selects1[2] = "";
                    selects1[3] = "";
                } else if (currentIndex == 2) {
                    selects2[0] = "";
                    selects2[1] = "";
                    selects2[2] = "";
                }
                fragments.get(currentIndex).onButton2Click("");


            }
        });
        popupWindow.setOnRightClickListener(new BusinessFilterPopupWindow.OnRightClickListener() {
            @Override
            public void onRightClick(String key1, String key2, String key3, String key4) {
                if (currentIndex == 1) {
                    selects1[0] = key1;
                    selects1[1] = key2;
                    selects1[2] = key3;
                    selects1[3] = key4;
                    fragments.get(currentIndex).onButton2Click(key1 + "_" + key2 + "_" + key3 + "_" + key4);
                } else if (currentIndex == 2) {
                    selects2[0] = key1;
                    selects2[1] = key2;
                    selects2[2] = key3;
                    fragments.get(currentIndex).onButton2Click(key1 + "_" + key2 + "_" + key3 + "_" + "");

                }

            }
        });
        if (currentIndex == 1) {
            popupWindow.setSelected(selects1[0], selects1[1], selects1[2], selects1[3]);
        } else if (currentIndex == 2) {
            popupWindow.setSelected(selects2[0], selects2[1], selects2[2], "");
        }


        if (Build.VERSION.SDK_INT >= 24) {
            Rect visibleFrame = new Rect();
            titleLayout.getGlobalVisibleRect(visibleFrame);
            int height = titleLayout.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            popupWindow.setHeight(height);
            popupWindow.showAsDropDown(titleLayout, 0, 0);
        } else {
            popupWindow.showAsDropDown(titleLayout, 0, 0);
        }
    }

    @Override
    public void initDatas() {
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

    AlertDialog.Builder builder;

    private void showSelectDialog(String title, final String[] lists) {
        builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title)
                .setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String user_token = (String) SpUtils.get(getActivity(), SpUtils.USERUSER_TOKEN, "");
                        if (TextUtils.isEmpty(user_token)) {
                            Intent it = new Intent(getActivity(), LoginActivity.class);
                            startActivity(it);
                        } else {
                            Intent it = null;
                            if (which == 0) {
                                it = new Intent(getActivity(), RleaseBusinessActivity.class);
                            } else if (which == 1) {
                                it = new Intent(getActivity(), ReleaseNonStandActivity.class);
                            } else {
                                it = new Intent(getActivity(), ReleaseBankActivity.class);
                            }
                            startActivity(it);
                        }

                    }
                }).show();
    }


    private void initSelect() {
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();
        list4 = new ArrayList<>();
        initList(new String[]{"0", "1", "2", "3", "4"}, new String[]{"不限", "3%以下",
                "3%-5%", "5%-8%", "8%以上"}, list1);
        initList(new String[]{"0", "1", "2", "3", "4", "5"}, new String[]{"不限", "8个月以下",
                "6-12个月", "12-36个月", "36-60个月", "60个月以上"}, list2);
        initList(new String[]{"0", "1", "2", "3", "4", "5"}, new String[]{"不限", "5千万以下",
                "5千万至一亿", "1亿至3亿", "3亿至5亿", "5亿以上"}, list3);
        initList(new String[]{"0", "1", "2", "3", "4"}, new String[]{"不限", "AAA",
                "AAA-AA", "AA-A+", "A+以下"}, list4);

    }

    private void initList(String[] ids, String[] names, List<Map<String, String>> mList) {
        mList.clear();
        for (int i = 0; i < ids.length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("id", ids[i]);
            map.put("name", names[i]);
            mList.add(map);
        }
    }
}
