package com.xinyi.touhang.fragments.business;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.PullRefreshLayout.OnRefreshListener;
import com.xinyi.touhang.PullRefreshLayout.PullRefreshLayout;
import com.xinyi.touhang.R;
import com.xinyi.touhang.adapter.BusinessOpportunitiesAdapter;
import com.xinyi.touhang.base.BaseFragment;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DividerDecoration;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

/**
 * 并购投资
 */
public class BusinessInner1ragment extends BaseFragment {

    @BindView(R.id.magic_indicator)
    MagicIndicator magic_indicator;


    @BindView(R.id.refresh_layout)
    PullRefreshLayout refresh_layout;

    @BindView(R.id.recylerView)
    RecyclerView recylerView;

    private String[] mDataList = new String[]{"全部", "投资方", "融资方"};

    private BusinessOpportunitiesAdapter adapter;

    private String key = "";
    private int page = 1;
    private String type = "0";
    private String url = AppUrls.SupplySearchUrl;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BusinessInner1ragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BusinessInner1ragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BusinessInner1ragment newInstance(String param1, String param2) {
        BusinessInner1ragment fragment = new BusinessInner1ragment();
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
        View rootView = inflater.inflate(R.layout.fragment_business_inner1ragment, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void initViews() {
        try {
            initMagicIndicator();
        } catch (JSONException e) {
        }

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
    }

    private void initMagicIndicator() throws JSONException {
        magic_indicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setScrollPivotX(0.35f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                final SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(mDataList[index]);
                simplePagerTitleView.setTextSize(12);
                simplePagerTitleView.setNormalColor(Color.parseColor("#4A4A4A"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#000000"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        magic_indicator.onPageSelected(index);
                        magic_indicator.onPageScrolled(index, 0, 0);
                        if (index == 0) {
//                            url = AppUrls.SupplySearchUrl;
                            type = "0";
//                            adapter.setType(0);
                        } else if (index == 1) {
//                            url = AppUrls.DemandSearchUrl;
                            type = "1";
//                            adapter.setType(1);
                        } else {
                            type = "2";
                        }
                        page = 1;
                        initDatas();

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

    @Override
    public void initDatas() {

        HttpParams params = new HttpParams();
        params.put("page", String.valueOf(page));
        params.put("type", type);
        params.put("name", key);
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
                                                "created", "modified", "type1", "purpose","yellow"
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

    @Override
    public void onButton1Click(String key) {
        super.onButton1Click(key);
        page = 1;
        this.key = key;
        initDatas();
    }
}
