package com.xinyi.touhang.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.R;
import com.xinyi.touhang.activities.AuthenticationActivity;
import com.xinyi.touhang.activities.HistoryActivity;
import com.xinyi.touhang.activities.LoginActivity;
import com.xinyi.touhang.activities.MainActivity;
import com.xinyi.touhang.activities.MyCommentActivity;
import com.xinyi.touhang.activities.MyFocusActivity;
import com.xinyi.touhang.activities.MyForumActivity;
import com.xinyi.touhang.activities.MyNotificationActivity;
import com.xinyi.touhang.activities.MyOrderActivity;
import com.xinyi.touhang.activities.PersonalSettingsActivity;
import com.xinyi.touhang.activities.SettingsActivity;
import com.xinyi.touhang.activities.UserFeedBackActivity;
import com.xinyi.touhang.activities.VipActivity;
import com.xinyi.touhang.activities.WorkPlaceActivity;
import com.xinyi.touhang.base.BaseFragment;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.constants.Configer;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.GlideCircleTransform;
import com.xinyi.touhang.utils.SpUtils;
import com.xinyi.touhang.utils.StatusBarUtil;
import com.xinyi.touhang.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

/**
 * 我的
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends BaseFragment {

    @BindView(R.id.parentView)
    LinearLayout parentView;
    //关注
    @BindView(R.id.focusLayout)
    RelativeLayout focusLayout;
    //我的职场
    @BindView(R.id.workPlaceLayout)
    RelativeLayout workPlaceLayout;
    //历史
    @BindView(R.id.historyLayout)
    RelativeLayout historyLayout;
    //评论
    @BindView(R.id.commentLayout)
    RelativeLayout commentLayout;
    //消息通知
    @BindView(R.id.notificationLayout)
    RelativeLayout notificationLayout;
    //订单
    @BindView(R.id.orderLayout)
    RelativeLayout orderLayout;
    //我的帖子
    @BindView(R.id.forumLayout)
    RelativeLayout forumLayout;
    //设置
    @BindView(R.id.settingsLayout)
    RelativeLayout settingsLayout;

    private LocalBroadcastManager localBroadcastManager;//本地广播manager
    private LoginBroadcastReceiver mReceiver;

    //未登录
    @BindView(R.id.login_none_layout)
    LinearLayout login_none_layout;

    @BindView(R.id.login_tv)
    TextView login_tv;
    //登录
    @BindView(R.id.login_user_layout)
    LinearLayout login_user_layout;
    @BindView(R.id.userImage)
    ImageView userImage;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.vipFlag)
    TextView vipFlag;
    @BindView(R.id.authenFlag)
    TextView authenFlag;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registBroadCastReceive();//注册
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void initViews() {


        login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    Intent it = new Intent(getActivity(), PersonalSettingsActivity.class);
                    startActivity(it);
                }
            }
        });

        focusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    Intent it = new Intent(getActivity(), MyFocusActivity.class);
                    startActivity(it);
                }
            }
        });
        historyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    Intent it = new Intent(getActivity(), HistoryActivity.class);
                    startActivity(it);
                }
            }
        });
        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    Intent it = new Intent(getActivity(), MyCommentActivity.class);
                    startActivity(it);
                }
            }
        });
        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    Intent it = new Intent(getActivity(), MyNotificationActivity.class);
                    startActivity(it);
                }
            }
        });
        orderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    Intent it = new Intent(getActivity(), MyOrderActivity.class);
                    startActivity(it);
                }
            }
        });
        forumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    Intent it = new Intent(getActivity(), MyForumActivity.class);
                    startActivity(it);
                }
            }
        });

        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), SettingsActivity.class);
                startActivity(it);
            }
        });

        vipFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    Intent it = new Intent(getActivity(), VipActivity.class);
                    startActivity(it);
                }
            }
        });
        authenFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    String confirm = (String) SpUtils.get(getActivity(), SpUtils.USERCONFIRM, "");
                    if (confirm.equals("0") || confirm.equals("3")) {
                        Intent it = new Intent(getActivity(), AuthenticationActivity.class);
                        startActivity(it);
                    }
                }
            }
        });

        workPlaceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    Intent it = new Intent(getActivity(), WorkPlaceActivity.class);
                    startActivity(it);
                }

            }
        });

    }

    private void login() {
        Intent it = new Intent(getActivity(), LoginActivity.class);
        startActivity(it);
    }

    @Override
    public void initDatas() {

        String user_token = (String) SpUtils.get(getActivity(), SpUtils.USERUSER_TOKEN, "");
        if (TextUtils.isEmpty(user_token)) {
            StatusBarUtil.StatusBarLightMode(getActivity());
            login_none_layout.setVisibility(View.VISIBLE);
            login_user_layout.setVisibility(View.GONE);
            parentView.setPadding(0, StatusBarUtil.getStatusBarHeight(getActivity()), 0, 0);
            return;
        } else {
            parentView.setPadding(0, 0, 0, 0);
            StatusBarUtil.StatusBarDarkMode(getActivity());
            restoreUserInfo();
            login_none_layout.setVisibility(View.GONE);
            login_user_layout.setVisibility(View.VISIBLE);
        }
        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        OkGo.<String>post(AppUrls.MineUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(getActivity(), params, user_token))
                .tag(this)
                .execute(new DialogCallBack(getActivity(), false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                savaUserInfo(js.getJSONObject("data"));
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initDatas();
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

    @Override
    public void onResume() {
        super.onResume();
        initDatas();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegistBroadCastReceive();//解除注册
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    /**
     * 注册广播
     */
    private void registBroadCastReceive() {
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        mReceiver = new LoginBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Configer.LOCAL_USERLOGIN_ACTION);
        localBroadcastManager.registerReceiver(mReceiver, intentFilter);
    }

    /**
     * 销毁广播
     */
    private void unRegistBroadCastReceive() {
        localBroadcastManager.unregisterReceiver(mReceiver);
    }

    private class LoginBroadcastReceiver extends BroadcastReceiver {

        //接收到广播后自动调用该方法
        @Override
        public void onReceive(Context context, Intent intent) {
            //写入接收广播后的操作
            if (intent.getAction().equals(Configer.LOCAL_USERLOGIN_ACTION)) {
                //
                initDatas();
            }
        }
    }


    private void savaUserInfo(JSONObject jsonObject) throws JSONException {
        JSONObject user = jsonObject.getJSONObject("user");
        SpUtils.put(getActivity(), SpUtils.USERNAME, user.getString("name"));
        SpUtils.put(getActivity(), SpUtils.USERTELEPHONE, user.getString("telephone"));
        SpUtils.put(getActivity(), SpUtils.USERWX_OPENID, user.getString("wx_openid"));
        SpUtils.put(getActivity(), SpUtils.USERQQ_ACCOUNT, user.getString("qq_account"));
        SpUtils.put(getActivity(), SpUtils.USERVIP, user.getString("vip"));
        SpUtils.put(getActivity(), SpUtils.USERUSER_TOKEN, user.getString("user_token"));
        SpUtils.put(getActivity(), SpUtils.USERUDID, user.getString("udid"));
        SpUtils.put(getActivity(), SpUtils.USERBIRTHDAY, user.getString("birthday"));
        SpUtils.put(getActivity(), SpUtils.USERSEX, user.getString("sex"));
        SpUtils.put(getActivity(), SpUtils.USERCREATED, user.getString("created"));
        SpUtils.put(getActivity(), SpUtils.USERMODIFIED, user.getString("modified"));
        SpUtils.put(getActivity(), SpUtils.USERIMAGE, user.getString("image"));
        SpUtils.put(getActivity(), SpUtils.USERVIP_LIMIT, user.getString("vip_limit"));
        SpUtils.put(getActivity(), SpUtils.USERCONFIRM, user.getString("confirm"));

        userName.setText(user.getString("name"));
        Glide.with(getActivity()).load(user.getString("image")).transform(new GlideCircleTransform(getActivity()))
                .into(userImage);
        if (user.getString("vip").equals("1")) {
            vipFlag.setBackgroundResource(R.drawable.vip_bg);
            vipFlag.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            vipFlag.setBackgroundResource(R.drawable.vip_none_bg);
            vipFlag.setTextColor(getResources().getColor(R.color.colorMain));
        }


        if (user.getString("confirm").equals("0")) {
            authenFlag.setBackgroundResource(R.drawable.vip_none_bg);
            authenFlag.setTextColor(getResources().getColor(R.color.colorMain));
            authenFlag.setText("实名认证");
        } else if (user.getString("confirm").equals("1")) {
            authenFlag.setBackgroundResource(R.drawable.authen_onway_bg);
            authenFlag.setTextColor(getResources().getColor(R.color.colorWhite));
            authenFlag.setText("正在认证");
        } else if (user.getString("confirm").equals("2")) {
            authenFlag.setBackgroundResource(R.drawable.vip_bg);
            authenFlag.setTextColor(getResources().getColor(R.color.colorWhite));
            authenFlag.setText("实名认证");
        } else {
            authenFlag.setBackgroundResource(R.drawable.authen_failed_bg);
            authenFlag.setTextColor(getResources().getColor(R.color.colorWhite));
            authenFlag.setText("认证失败");
        }
    }


    private boolean checkLogin() {
        String user_token = (String) SpUtils.get(getActivity(), SpUtils.USERUSER_TOKEN, "");
        if (TextUtils.isEmpty(user_token)) {
            login();
            return false;
        } else {
            return true;
        }
    }

    private void restoreUserInfo() {

        userName.setText((String) SpUtils.get(getActivity(), SpUtils.USERNAME, ""));
        Glide.with(getActivity()).load((String) SpUtils.get(getActivity(), SpUtils.USERIMAGE, "")).transform(new GlideCircleTransform(getActivity()))
                .into(userImage);
        String vip = (String) SpUtils.get(getActivity(), SpUtils.USERVIP, "");
        String confirm = (String) SpUtils.get(getActivity(), SpUtils.USERCONFIRM, "");
        if (vip.equals("1")) {
            vipFlag.setBackgroundResource(R.drawable.vip_bg);
            vipFlag.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            vipFlag.setBackgroundResource(R.drawable.vip_none_bg);
            vipFlag.setTextColor(getResources().getColor(R.color.colorMain));
        }

        if (confirm.equals("0")) {
            authenFlag.setText("实名认证");
            authenFlag.setBackgroundResource(R.drawable.vip_none_bg);
            authenFlag.setTextColor(getResources().getColor(R.color.colorMain));
        } else if (confirm.equals("1")) {
            authenFlag.setBackgroundResource(R.drawable.authen_onway_bg);
            authenFlag.setTextColor(getResources().getColor(R.color.colorWhite));
            authenFlag.setText("正在认证");
        } else if (confirm.equals("2")) {
            authenFlag.setText("实名认证");
            authenFlag.setBackgroundResource(R.drawable.vip_bg);
            authenFlag.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            authenFlag.setBackgroundResource(R.drawable.authen_failed_bg);
            authenFlag.setTextColor(getResources().getColor(R.color.colorWhite));
            authenFlag.setText("认证失败");
        }
    }

}
