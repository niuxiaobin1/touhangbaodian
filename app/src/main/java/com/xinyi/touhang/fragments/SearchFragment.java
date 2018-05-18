package com.xinyi.touhang.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.WebActivity;
import com.xinyi.touhang.base.BaseFragment;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.fragments.searchInner.AccountingFragment;
import com.xinyi.touhang.fragments.searchInner.IpoFragment;
import com.xinyi.touhang.fragments.searchInner.LawsFragment;
import com.xinyi.touhang.fragments.searchInner.QueryFragment;
import com.xinyi.touhang.fragments.searchInner.RegroupFragment;
import com.xinyi.touhang.utils.CommonUtils;
import com.xinyi.touhang.utils.StatusBarUtil;
import com.xinyi.touhang.utils.TabLayoutUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 搜索
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends BaseFragment {


    @BindView(R.id.parentView)
    LinearLayout parentView;

    @BindView(R.id.rightTv)
    TextView rightTv;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.mViewPager)
    ViewPager mViewPager;

    private String[] searchTitles = new String[]{"法律法规",
            "会计准则", "IPO案例", "重组案例", "工商查询"};

    private List<Fragment> fragments;

    private MyPagerAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initViews() {
        parentView.setPadding(0, StatusBarUtil.getStatusBarHeight(getActivity()), 0, 0);

        initTabs();
        rightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), WebActivity.class);
                it.putExtra(WebActivity.TITLESTRING, "能上市吗");
                it.putExtra(WebActivity.TITLEURL, AppUrls.ForumCanUrl);
                startActivity(it);
            }
        });
    }

    @Override
    public void initDatas() {

    }


    private void initTabs() {
        fragments = new ArrayList<>();

        fragments.add(LawsFragment.newInstance("", ""));
        fragments.add(AccountingFragment.newInstance("", ""));
        fragments.add(IpoFragment.newInstance("", ""));
        fragments.add(RegroupFragment.newInstance("", ""));
        fragments.add(QueryFragment.newInstance("", ""));

        adapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(adapter);
        ViewCompat.setElevation(tabLayout, 10);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                TabLayoutUtils.invalidateTablayout(getActivity(), tabLayout);
            }
        }, 100);
        mViewPager.setOffscreenPageLimit(5);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                CommonUtils.hideSoftInput(getActivity(),mViewPager);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
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
            return searchTitles[position];
        }
    }

}
