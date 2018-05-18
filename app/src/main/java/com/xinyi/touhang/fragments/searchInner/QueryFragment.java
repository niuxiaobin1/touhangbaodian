package com.xinyi.touhang.fragments.searchInner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.nex3z.flowlayout.FlowLayout;
import com.xinyi.touhang.PullRefreshLayout.OnRefreshListener;
import com.xinyi.touhang.PullRefreshLayout.PullRefreshLayout;
import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.WebActivity;
import com.xinyi.touhang.adapter.QueryAdapter;
import com.xinyi.touhang.base.BaseFragment;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.utils.CommonUtils;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DividerDecoration;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.JsonUtils;
import com.xinyi.touhang.utils.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

/**
 * 工商查询
 * Use the {@link QueryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QueryFragment extends BaseFragment {

    @BindView(R.id.select_layout1)
    LinearLayout select_layout1;

    @BindView(R.id.select_layout2)
    LinearLayout select_layout2;

    @BindView(R.id.select_layout3)
    LinearLayout select_layout3;

    @BindView(R.id.search_et)
    EditText search_et;

    @BindView(R.id.search_image)
    ImageView search_image;

    @BindView(R.id.refresh_layout)
    PullRefreshLayout refresh_layout;

    @BindView(R.id.flowLayout)
    FlowLayout flowLayout;

    @BindView(R.id.query_recylerView)
    RecyclerView queryRecylerView;


    private List<LinearLayout> linearLayouts;

    private QueryAdapter adapter;

    private List<Map<String, String>> tags = new ArrayList<>();
    private String name = "";//搜索的关键字


    private String selectUrl = AppUrls.ForumIc_searchUrl;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public QueryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QueryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QueryFragment newInstance(String param1, String param2) {
        QueryFragment fragment = new QueryFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_query, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void initViews() {

        queryRecylerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        queryRecylerView.addItemDecoration(new DividerDecoration(getActivity(), R.color.colorItem,
                DensityUtil.dip2px(getActivity(), 5)));
        adapter = new QueryAdapter(getActivity());
        queryRecylerView.setAdapter(adapter);

        refresh_layout.setMode(PullRefreshLayout.PULL_FROM_START);
        refresh_layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onPullDownRefresh() {
                initDatas();
            }

            @Override
            public void onPullUpRefresh() {


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
                CommonUtils.hideInputMethod(search_et, getActivity());
                name = search_et.getText().toString().trim();
                goQuery(selectUrl);
            }
        });

        linearLayouts = new ArrayList<>();
        linearLayouts.add(select_layout1);
        linearLayouts.add(select_layout2);
        linearLayouts.add(select_layout3);

        for (int i = 0; i < linearLayouts.size(); i++) {
            final int current = i;
            linearLayouts.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelect(current);
                }
            });
        }


        setSelect(0);
    }


    private void setSelect(int position) {
        switch (position) {
            case 0:
                search_et.setHint("请输入企业名称");
                selectUrl = AppUrls.ForumIc_searchUrl;
                break;
            case 1:
                search_et.setHint("请输入企业全称");
                selectUrl = AppUrls.ForumIcon_searchUrl;
                break;
            case 2:
                search_et.setHint("请输入企业全称");
                selectUrl = AppUrls.ForumPatent_searchUrl;
                break;
        }
        for (int i = 0; i < linearLayouts.size(); i++) {
            if (i == position) {
                linearLayouts.get(i).getChildAt(1).setVisibility(View.VISIBLE);
            } else {
                linearLayouts.get(i).getChildAt(1).setVisibility(View.INVISIBLE);
            }
        }
    }


    private void goQuery(String url) {
        Intent it = new Intent(getActivity(), WebActivity.class);
        it.putExtra(WebActivity.TITLESTRING, "工商查询");
        it.putExtra(WebActivity.TITLEURL, url + "?key=" + name);
        startActivity(it);
    }

    @Override
    public void initDatas() {
        HttpParams params = new HttpParams();
        OkGo.<String>post(AppUrls.ForumIc_homeUrl)
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
                                JSONObject data = js.getJSONObject("data");

                                tags.clear();
                                JSONArray hotwords = data.getJSONArray("hotwords");
                                tags = JsonUtils.ArrayToList(hotwords, new String[]{
                                        "id", "name", "sort", "created", "modified"
                                });
                                initFlowLayout();
                                adapter.setData(data.toString());

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


    private void initFlowLayout() {
        flowLayout.removeAllViews();

        for (int i = 0; i < tags.size(); i++) {
            final TextView tv = new TextView(getActivity());
            tv.setTextSize(14);
            tv.setTextColor(getResources().getColor(R.color.colorWhite));
            tv.setText(tags.get(i).get("name"));
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name = tv.getText().toString().trim();
                    goQuery(AppUrls.ForumIc_searchUrl);
                }
            });
            flowLayout.addView(tv);
        }
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
