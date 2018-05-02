package com.xinyi.touhang.fragments;

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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.VideoActivity;
import com.xinyi.touhang.adapter.CommunicationAdapter;
import com.xinyi.touhang.adapter.DiscussAdapter;
import com.xinyi.touhang.base.BaseFragment;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DividerDecoration;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.JsonUtils;
import com.xinyi.touhang.utils.UIHelper;
import com.xinyi.touhang.weight.MyGridView;

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
 * 学习交流
 * Use the {@link CommunicationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunicationFragment extends BaseFragment {

    @BindView(R.id.gridview)
    MyGridView gridview;

    @BindView(R.id.center_recylerView)
    RecyclerView center_recylerView;

    @BindView(R.id.discuss_recylerView)
    RecyclerView discuss_recylerView;

    private List<Map<String, String>> topList;
    private List<Map<String, String>> fileList;
    private List<Map<String, String>> forumList;
    private CommunicationGridAdapter communicationGridAdapter;
    private CommunicationAdapter communicationAdapter;
    private DiscussAdapter discussAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CommunicationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommunicationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommunicationFragment newInstance(String param1, String param2) {
        CommunicationFragment fragment = new CommunicationFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_communication, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void initViews() {

        topList = new ArrayList<>();
        fileList = new ArrayList<>();
        forumList = new ArrayList<>();
        //学习
        communicationGridAdapter = new CommunicationGridAdapter();
        gridview.setAdapter(communicationGridAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent it = null;
                if (topList.get(position).containsKey("video_id")) {
                    //视频
                    it = new Intent(getActivity(), VideoActivity.class);

                } else {

                }
                if (it != null) {

                    startActivity(it);
                }
            }
        });

        //file
        center_recylerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        center_recylerView.addItemDecoration(new DividerDecoration(getActivity(), R.color.colorItem, DensityUtil.dip2px(
                getActivity(), 0.5f)));
        communicationAdapter = new CommunicationAdapter(getActivity());
        center_recylerView.setAdapter(communicationAdapter);

        //讨论
        discuss_recylerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        discuss_recylerView.addItemDecoration(new DividerDecoration(getActivity(), R.color.colorItem, DensityUtil.dip2px(
                getActivity(), 0.5f)));
        discussAdapter = new DiscussAdapter(getActivity());
        discuss_recylerView.setAdapter(discussAdapter);


    }

    @Override
    public void initDatas() {
        HttpParams params = new HttpParams();
        OkGo.<String>post(AppUrls.ForumIndexUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(getActivity(), params, ""))
                .tag(this)
                .execute(new DialogCallBack(getActivity(), false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());
                            if (js.getBoolean("result")) {
                                JSONObject data = js.getJSONObject("data");
                                JSONArray ads = data.getJSONArray("ads");
                                List<Map<String, String>> adList = JsonUtils.ArrayToList(ads, new String[]{
                                        "id", "name", "url", "created", "modified", "image"
                                });
                                JSONArray video = data.getJSONArray("video");
                                List<Map<String, String>> videoList = JsonUtils.ArrayToList(video, new String[]{
                                        "id", "name", "price", "author", "good_num", "read_num", "editer", "video_type_id",
                                        "video_id", "pay", "created", "modified", "image"
                                });
                                topList.addAll(adList);
                                topList.addAll(videoList);
                                communicationGridAdapter.notifyDataSetChanged();

                                JSONArray file = data.getJSONArray("file");
                                fileList = JsonUtils.ArrayToList(file, new String[]{
                                        "id", "name", "read_num", "down_num", "type", "created", "modified", "file", "ext"
                                });
                                communicationAdapter.setData(fileList);
                                JSONArray forum = data.getJSONArray("forum");
                                forumList = JsonUtils.ArrayToList(forum, new String[]{
                                        "id", "name", "content", "customer_id", "read_num", "good_num", "top", "vip"
                                        , "type", "created", "modified", "customer_name", "telephone", "wx_openid",
                                        "qq_account", "customer_vip", "user_token", "udid", "customer_created",
                                        "customer_modified", "image", "passed", "author"
                                });
                                discussAdapter.setData(forumList);
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


    private class CommunicationGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return topList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.communication_grid_item, null);
            ImageView imageview = v.findViewById(R.id.imageview);
            TextView textview = v.findViewById(R.id.textview);
            View line = v.findViewById(R.id.line);

            //分割线
            int lineNum = (getCount() + 1) / 2;
            if (position % 2 == 0) {
                //左边的有右边距
                imageview.setPadding(0, 0, DensityUtil.dip2px(getActivity(), 5), 0);
                textview.setPadding(0, 0, DensityUtil.dip2px(getActivity(), 5), 0);
            } else {
                imageview.setPadding(DensityUtil.dip2px(getActivity(), 5), 0, 0, 0);
                textview.setPadding(DensityUtil.dip2px(getActivity(), 5), 0, 0, 0);
            }

            if ((position + 2) / 2 == lineNum) {
                //最后一行
                line.setVisibility(View.GONE);
            } else {
                line.setVisibility(View.VISIBLE);
            }

            //resize iamge
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageview.getLayoutParams();
            int contentWidth = DensityUtil.getScreenWidth(getActivity()) - DensityUtil.dip2px(getActivity(), 14);
            params.width = contentWidth / 2;
            params.height = (int) (params.width * 0.6);
            imageview.setLayoutParams(params);

            textview.setText(topList.get(position).get("name"));
            Glide.with(getActivity()).load(topList.get(position).get("image")).placeholder(R.mipmap.banner_parker_replace)
                    .into(imageview);

            return v;
        }
    }
}
