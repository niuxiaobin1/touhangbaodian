package com.xinyi.touhang.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.xinyi.touhang.activities.RleaseBusinessActivity;
import com.xinyi.touhang.adapter.BusinessOpportunitiesAdapter;
import com.xinyi.touhang.base.BaseFragment;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.fragments.other.NotificationFragment;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DividerDecoration;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.JsonUtils;
import com.xinyi.touhang.utils.SpUtils;
import com.xinyi.touhang.utils.StatusBarUtil;
import com.xinyi.touhang.utils.UIHelper;
import com.xinyi.touhang.weight.ObservableScrollView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

/**
 * 商业机会
 * Use the {@link BusinessOpportunitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusinessOpportunitiesFragment extends BaseFragment {


    @BindView(R.id.parentView)
    LinearLayout parentView;

    @BindView(R.id.release_tv)
    TextView release_tv;

    @BindView(R.id.refresh_layout)
    PullRefreshLayout refresh_layout;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.recylerView)
    RecyclerView recylerView;

    private String[] titles = new String[]{"并购投资", "非标资产","银行间"};

    private BusinessOpportunitiesAdapter adapter;

    private int page = 1;
    private String url = AppUrls.SupplySearchUrl;

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
//        titleTv = rootView.findViewById(R.id.titleTv);
//        subTitleTv = rootView.findViewById(R.id.subTitleTv);
//        mScrollView = rootView.findViewById(R.id.mScrollView);
        return rootView;
    }

    @Override
    public void initViews() {
        initTabs();
        parentView.setPadding(0, StatusBarUtil.getStatusBarHeight(getActivity()),0,0);

        refresh_layout.setMode(PullRefreshLayout.BOTH);
        refresh_layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onPullDownRefresh() {
                page = 1;
                initDatas();
            }

            @Override
            public void onPullUpRefresh() {
                page++;
                initDatas();

            }
        });

        adapter = new BusinessOpportunitiesAdapter(getActivity());
        recylerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recylerView.addItemDecoration(new DividerDecoration(getActivity(), R.color.colorItem,
                DensityUtil.dip2px(getActivity(), 0.5f)));
        recylerView.setAdapter(adapter);

        release_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_token = (String) SpUtils.get(getActivity(), SpUtils.USERUSER_TOKEN, "");
                if (TextUtils.isEmpty(user_token)) {
                    Intent it = new Intent(getActivity(), LoginActivity.class);
                    startActivity(it);
                } else {
                    Intent it = new Intent(getActivity(), RleaseBusinessActivity.class);
                    startActivity(it);
                }

            }
        });

    }

    private void initTabs() {

        for (int i = 0; i < titles.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(titles[i]));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals(titles[0])) {
                    url = AppUrls.SupplySearchUrl;
                    adapter.setType(0);
                } else {
                    url = AppUrls.DemandSearchUrl;
                    adapter.setType(1);
                }
                page = 1;
                initDatas();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void initDatas() {
        HttpParams params = new HttpParams();
        params.put("page", String.valueOf(page));
        if (page == 1) {
            adapter.clearDatas();
        }
        OkGo.<String>post(url)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(getActivity(), params, ""))
                .tag(this)
                .execute(new DialogCallBack(getActivity(), false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        refresh_layout.onRefreshComplete();
                        try {
                            JSONObject js = new JSONObject(response.body());
                            if (js.getBoolean("result")) {
                                adapter.addDatas(JsonUtils.ArrayToList(
                                        js.getJSONArray("data"), new String[]{
                                                "id", "name", "industry", "customer_id", "address",
                                                "price", "points", "stage", "type", "special",
                                                "out", "content", "telephone", "confirm", "top",
                                                "created", "modified"
                                        }
                                ));
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
                        refresh_layout.onRefreshComplete();
                        HandleResponse.handleException(response, getActivity());
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
}
