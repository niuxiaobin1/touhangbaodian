package com.xinyi.touhang.fragments.other;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.PullRefreshLayout.PullRefreshLayout;
import com.xinyi.touhang.R;
import com.xinyi.touhang.adapter.BaseAdapter;
import com.xinyi.touhang.adapter.focus.BusinessAdapter;
import com.xinyi.touhang.adapter.focus.ConsulationAdapter;
import com.xinyi.touhang.adapter.focus.DiscussAdapter;
import com.xinyi.touhang.adapter.focus.StudyAdapter;
import com.xinyi.touhang.base.BaseFragment;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DividerDecoration;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.JsonUtils;
import com.xinyi.touhang.utils.SpUtils;
import com.xinyi.touhang.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyHistoryListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyHistoryListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyHistoryListFragment extends BaseFragment {

    @BindView(R.id.refresh_layout)
    PullRefreshLayout refresh_layout;

    @BindView(R.id.recylerView)
    RecyclerView recylerView;

    private BaseAdapter adapter;

    private String urlString;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TYPE = "type";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String type;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyHistoryListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyHistoryListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyHistoryListFragment newInstance(String param1, String param2) {
        MyHistoryListFragment fragment = new MyHistoryListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (type.equals("0")) {
            urlString = AppUrls.NewsFootpathUrl;
        }else if(type.equals("1")){
            urlString = AppUrls.SupplyFootpathUrl;
        }else if(type.equals("2")){
            urlString = AppUrls.VideoFootpathUrl;
        }else{
            urlString = AppUrls.ForumFootpathUrl;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_list, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void initViews() {
        refresh_layout.setMode(PullRefreshLayout.DISABLED);
        recylerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recylerView.addItemDecoration(new DividerDecoration(getActivity(), R.color.colorLine, DensityUtil.dip2px(
                getActivity(), 0.5f
        )));
//        if (TextUtils.isEmpty(urlString)) {
//            return;
//        }
//        if (urlString.equals(AppUrls.VideoListsUrl)) {
//            adapter = new VideoAdapter(getActivity());
//        } else if (urlString.equals(AppUrls.AdviseListsUrl)) {
//            adapter = new AdviseAdapter(getActivity());
//        } else {
//
//        }
        if (type.equals("0")) {
            //资讯
            adapter = new ConsulationAdapter(getActivity());
        }else if(type.equals("1")){
            adapter=new BusinessAdapter(getActivity());
        }else if(type.equals("2")){
            adapter=new StudyAdapter(getActivity());
        }else{
            adapter=new DiscussAdapter(getActivity());
        }
        recylerView.setAdapter(adapter);
    }

    @Override
    public void initDatas() {

        if (TextUtils.isEmpty(urlString)) {
            return;
        }
        String user_token = (String) SpUtils.get(getActivity(), SpUtils.USERUSER_TOKEN, "");

        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        OkGo.<String>post(urlString)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(getActivity(), params, user_token))
                .tag(this)
                .execute(new DialogCallBack(getActivity(), false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                adapter.clearDatas();
                                if (adapter != null) {
                                    if (type.equals("1")) {
                                        adapter.addDatas(JsonUtils.ArrayToList(
                                                js.getJSONObject("data").getJSONArray("history"), new String[]{
                                                        "id", "name", "type","sub_type_name"
                                                }
                                        ));
                                    }else{
                                        adapter.addDatas(JsonUtils.ArrayToList(
                                                js.getJSONObject("data").getJSONArray("history"), new String[]{
                                                        "id", "name", "image"
                                                }
                                        ));
                                    }

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
}
