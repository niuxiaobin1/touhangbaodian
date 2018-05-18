package com.xinyi.touhang.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.PullRefreshLayout.OnRefreshListener;
import com.xinyi.touhang.PullRefreshLayout.PullRefreshLayout;
import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.DiscussListActivity;
import com.xinyi.touhang.activities.StudyListActivity;
import com.xinyi.touhang.activities.VideoActivity;
import com.xinyi.touhang.activities.WebActivity;
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
import com.xinyi.touhang.utils.StatusBarUtil;
import com.xinyi.touhang.utils.UIHelper;
import com.xinyi.touhang.weight.EllipsizingTextView;
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

    @BindView(R.id.refresh_layout)
    PullRefreshLayout refresh_layout;

    @BindView(R.id.parentView)
    LinearLayout parentView;

    @BindView(R.id.toStudyListLayout)
    RelativeLayout toStudyListLayout;

    @BindView(R.id.toDiscussListLayout)
    RelativeLayout toDiscussListLayout;

    @BindView(R.id.gridview)
    MyGridView gridview;

    @BindView(R.id.gridview2)
    MyGridView gridview2;

    @BindView(R.id.center_recylerView)
    RecyclerView center_recylerView;

    @BindView(R.id.discuss_recylerView)
    RecyclerView discuss_recylerView;

    private List<Map<String, String>> adList;
    private List<Map<String, String>> videoList;
    private List<Map<String, String>> fileList;
    private List<Map<String, String>> forumList;
    private CommunicationGridAdapter communicationGridAdapter;
    private CommunicationGridAdapter communicationGridAdapter2;
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
        parentView.setPadding(0, StatusBarUtil.getStatusBarHeight(getActivity()), 0, 0);

        adList = new ArrayList<>();
        videoList = new ArrayList<>();
        fileList = new ArrayList<>();
        forumList = new ArrayList<>();

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

        //学习
        communicationGridAdapter = new CommunicationGridAdapter(0);
        communicationGridAdapter2 = new CommunicationGridAdapter(1);
        gridview.setAdapter(communicationGridAdapter);
        gridview2.setAdapter(communicationGridAdapter2);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String url = adList.get(position).get("url");
                Intent it = new Intent(getActivity(), WebActivity.class);
                it.putExtra(WebActivity.TITLESTRING, adList.get(position).get("name"));
                if (!TextUtils.isEmpty(url)) {
                    it.putExtra(WebActivity.TITLEURL, url);
                } else {
                    it.putExtra(WebActivity.TITLEURL, AppUrls.AdviseDetailUrl + adList.get(position).get("id"));
                }

                startActivity(it);

            }
        });

        gridview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(getActivity(), VideoActivity.class);
                it.putExtra(VideoActivity.VIDEO_ID, videoList.get(position).get("id"));
                startActivity(it);
            }
        });

        //file
        center_recylerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        center_recylerView.addItemDecoration(new

                DividerDecoration(getActivity(), R.color.colorItem, DensityUtil.dip2px(

                getActivity(), 0.5f)));
        communicationAdapter = new

                CommunicationAdapter(getActivity());
        center_recylerView.setAdapter(communicationAdapter);

        //讨论
        discuss_recylerView.setLayoutManager(new

                                                     LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false)

                                                     {
                                                         @Override
                                                         public boolean canScrollVertically() {
                                                             return false;
                                                         }
                                                     });
        discuss_recylerView.addItemDecoration(new

                DividerDecoration(getActivity(), R.color.colorItem, DensityUtil.dip2px(

                getActivity(), 0.5f)));
        discussAdapter = new

                DiscussAdapter(getActivity());
        discuss_recylerView.setAdapter(discussAdapter);


        toStudyListLayout.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), StudyListActivity.class);
                startActivity(it);
            }
        });
        toDiscussListLayout.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), DiscussListActivity.class);
                startActivity(it);
            }
        });
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
                        refresh_layout.onRefreshComplete();
                        try {
                            JSONObject js = new JSONObject(response.body());
                            if (js.getBoolean("result")) {
                                JSONObject data = js.getJSONObject("data");
                                JSONArray ads = data.getJSONArray("ads");
                                adList = JsonUtils.ArrayToList(ads, new String[]{
                                        "id", "name", "url", "author", "top", "read_num", "created", "modified", "image"
                                });
                                JSONArray video = data.getJSONArray("video");
                                videoList = JsonUtils.ArrayToList(video, new String[]{
                                        "id", "name", "price", "author", "good_num", "read_num", "editer", "video_type_id",
                                        "video_id", "pay", "created", "modified", "image"
                                });

                                communicationGridAdapter.notifyDataSetChanged();
                                communicationGridAdapter2.notifyDataSetChanged();

                                JSONArray file = data.getJSONArray("file");
                                fileList = JsonUtils.ArrayToList(file, new String[]{
                                        "top", "id", "name", "read_num", "down_num", "type", "created", "modified", "file", "ext"
                                });
                                communicationAdapter.clearDatas();
                                communicationAdapter.addDatas(fileList);
                                JSONArray forum = data.getJSONArray("forum");
                                forumList = JsonUtils.ArrayToList(forum, new String[]{
                                        "id", "name", "content", "customer_id", "read_num", "good_num", "top", "vip"
                                        , "type", "created", "modified", "customer_name", "telephone", "wx_openid",
                                        "qq_account", "customer_vip", "user_token", "udid", "customer_created",
                                        "customer_modified", "image", "passed", "author"
                                });
                                discussAdapter.clearDatas();
                                discussAdapter.addDatas(forumList);
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


    private class CommunicationGridAdapter extends BaseAdapter {

        private int type;

        public CommunicationGridAdapter(int type) {
            this.type = type;
        }

        @Override
        public int getCount() {
            if (type == 0) {
                return adList.size();
            } else {
                return videoList.size();
            }
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
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.communication_grid_item, null);
                holder = new Holder();
                holder.imageview = convertView.findViewById(R.id.imageview);
                holder.top_layout = convertView.findViewById(R.id.top_layout);
                holder.icon_image = convertView.findViewById(R.id.icon_image);
                holder.play_image = convertView.findViewById(R.id.play_image);
                holder.textview = convertView.findViewById(R.id.textview);
                holder.videoInfo_layout = convertView.findViewById(R.id.videoInfo_layout);
                holder.viewNums_tv = convertView.findViewById(R.id.viewNums_tv);
                holder.rightTv = convertView.findViewById(R.id.rightTv);
                //resize iamge
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.top_layout.getLayoutParams();
                int contentWidth = DensityUtil.getScreenWidth(getActivity()) - DensityUtil.dip2px(getActivity(), 14);
                params.width = contentWidth / 2;
                params.height = (int) (params.width * 0.6);
                holder.top_layout.setLayoutParams(params);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            if (type == 1) {
                //视频
                holder.videoInfo_layout.setVisibility(View.VISIBLE);
                holder.play_image.setVisibility(View.VISIBLE);
                holder.icon_image.setImageResource(R.mipmap.video_icon);
                holder.viewNums_tv.setText(videoList.get(position).get("read_num"));
                double price = Float.parseFloat(videoList.get(position).get("price"));
                if (price > 0) {
                    holder.rightTv.setText("￥" + price + "元");
                    holder.rightTv.setTextColor(Color.parseColor("#DC2828"));
                } else {
                    holder.rightTv.setText("免费");
                    holder.rightTv.setTextColor(Color.parseColor("#5AAA1E"));
                }

                holder.textview.setText(videoList.get(position).get("name"));
                Glide.with(getActivity()).load(videoList.get(position).get("image")).placeholder(R.mipmap.loading_image)
                        .into(holder.imageview);
            } else {
                holder.videoInfo_layout.setVisibility(View.GONE);
                holder.play_image.setVisibility(View.GONE);
                holder.icon_image.setImageResource(R.mipmap.advise_icon);
                holder.textview.setText(adList.get(position).get("name"));
                Glide.with(getActivity()).load(adList.get(position).get("image")).placeholder(R.mipmap.loading_image)
                        .into(holder.imageview);
            }
            return convertView;
        }


        public class Holder {
            ImageView imageview;
            RelativeLayout top_layout;
            ImageView icon_image;
            ImageView play_image;
            TextView textview;
            LinearLayout videoInfo_layout;
            TextView viewNums_tv;
            TextView rightTv;
        }
    }
}
