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
import com.xinyi.touhang.R;
import com.xinyi.touhang.adapter.AdviseAdapter;
import com.xinyi.touhang.adapter.BaseAdapter;
import com.xinyi.touhang.adapter.VideoAdapter;
import com.xinyi.touhang.base.BaseFragment;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DividerDecoration;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.JsonUtils;
import com.xinyi.touhang.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudyListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StudyListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudyListFragment extends BaseFragment {

    @BindView(R.id.recylerView)
    RecyclerView recylerView;

    private BaseAdapter adapter;

    private int page = 1;
    private static final String TITLE = "paramTitle";
    private static final String LISTURL = "paramUrl";

    private String titleString;
    private String urlString;

    private OnFragmentInteractionListener mListener;

    public StudyListFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static StudyListFragment newInstance(String param1, String param2) {
        StudyListFragment fragment = new StudyListFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, param1);
        args.putString(LISTURL, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            titleString = getArguments().getString(TITLE);
            urlString = getArguments().getString(LISTURL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_study_list, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void initViews() {
        recylerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recylerView.addItemDecoration(new DividerDecoration(getActivity(), R.color.colorLine, DensityUtil.dip2px(
                getActivity(), 0.5f
        )));
        if (TextUtils.isEmpty(urlString)) {
            return;
        }
        if (urlString.equals(AppUrls.VideoListsUrl)) {
            adapter = new VideoAdapter(getActivity());
        } else if (urlString.equals(AppUrls.AdviseListsUrl)) {
            adapter = new AdviseAdapter(getActivity());
        } else {

        }
        recylerView.setAdapter(adapter);
    }

    @Override
    public void initDatas() {
        if (TextUtils.isEmpty(urlString)) {
            return;
        }
        HttpParams params = new HttpParams();
        params.put("page", String.valueOf(page));
        OkGo.<String>post(urlString)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(getActivity(), params, ""))
                .tag(this)
                .execute(new DialogCallBack(getActivity(), false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                if (adapter != null) {
                                    adapter.addDatas(JsonUtils.ArrayToList(
                                            js.getJSONArray("data"), new String[]{
                                                    "id", "name", "price", "author", "good_num",
                                                    "read_num", "editer", "video_type_id", "video_id"
                                                    , "pay", "created", "modified", "image", "passed"
                                            }
                                    ));
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
