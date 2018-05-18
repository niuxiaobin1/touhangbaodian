package com.xinyi.touhang.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.base.ThApplication;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.constants.Configer;
import com.xinyi.touhang.utils.CommonUtils;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.SpUtils;
import com.xinyi.touhang.utils.UIHelper;
import com.xinyi.touhang.utils.UmengUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {


    @BindView(R.id.login_byQQ)
    TextView login_byQQ;

    @BindView(R.id.login_byWechat)
    TextView login_byWechat;

    //获取验证码
    @BindView(R.id.getCodeTv)
    TextView getCodeTv;


    @BindView(R.id.account_et)
    EditText account_et;

    @BindView(R.id.password_et)
    EditText password_et;

    @BindView(R.id.login_btn)
    Button login_btn;

    private boolean flag = true;
    private ThApplication app;
    private LocalBroadcastManager localBroadcastManager;//本地广播manager
    private mBroadcastReceiver mReceiver;//接受倒计时

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();

        app = (ThApplication) getApplication();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        mReceiver = new mBroadcastReceiver();

        getCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (app.isCountDowning()) {
                    return;
                }
                try {
                    getVerifyCode();
                } catch (JSONException e) {
                    UIHelper.toastMsg(e.getMessage());
                }
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        login_byQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengUtils.UmengLogin(LoginActivity.this, SHARE_MEDIA.QQ, new UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        Log.e("nxb","onStart");
                    }

                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                        Log.e("nxb","onComplete");
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                        Log.e("nxb","onError");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int i) {
                        Log.e("nxb","onCancel");
                    }
                });
            }
        });


        login_byWechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengUtils.UmengLogin(LoginActivity.this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        Log.e("nxb","onStart");
                    }

                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                        Log.e("nxb","onComplete");
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                        Log.e("nxb","onError");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int i) {
                        Log.e("nxb","onCancel");
                    }
                });
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registBroadCastReceive();//注册
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegistBroadCastReceive();//解除注册
    }

    /**
     * 注册广播
     */
    private void registBroadCastReceive() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Configer.LOCAL_COUNTDOWN_ACTION);
        localBroadcastManager.registerReceiver(mReceiver, intentFilter);
    }

    /**
     * 销毁广播
     */
    private void unRegistBroadCastReceive() {
        localBroadcastManager.unregisterReceiver(mReceiver);
    }


    /**
     * 请求获取验证码
     *
     * @throws JSONException
     */
    private void getVerifyCode() throws JSONException {

        final String telString = account_et.getText().toString().trim();

        if (TextUtils.isEmpty(telString)) {
            showToast(R.string.telIsEmptyString);
            return;
        }
        if (!UIHelper.isMobileNO(telString)) {
            showToast(R.string.telIsNotCorrectString);
            return;
        }
        getCodeTv.setEnabled(false);
        HttpParams params = new HttpParams();
        params.put("phone", telString);
        OkGo.<String>post(AppUrls.GetidentifyingUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(LoginActivity.this, params, ""))
                .tag(this)
                .execute(new DialogCallBack(LoginActivity.this, false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        getCodeTv.setEnabled(true);
                        try {
                            JSONObject js = new JSONObject(response.body());
                            UIHelper.toastMsg(js.getString("message"));
                            if (js.getBoolean("result")) {
                                app.startTimer(telString);
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
                        getCodeTv.setEnabled(true);
                        HandleResponse.handleException(response, LoginActivity.this);
                    }
                });

    }


    private class mBroadcastReceiver extends BroadcastReceiver {

        //接收到广播后自动调用该方法
        @Override
        public void onReceive(Context context, Intent intent) {
            //写入接收广播后的操作
            if (intent.getAction().equals(Configer.LOCAL_COUNTDOWN_ACTION)) {
                int countDownNum = intent.getIntExtra(Configer.LOCAL_COUNTDOWN_KEY, 0);
                String telString = intent.getStringExtra(Configer.LOCAL_COUNTDOWN_TEL);

                if (TextUtils.isEmpty(account_et.getText().toString()) && flag) {
                    account_et.setText(telString);
                    account_et.setSelection(telString.length());
                    flag = false;
                }

                if (countDownNum == 0) {
                    getCodeTv.setTextColor(getResources().getColor(R.color.colorMain));
                    getCodeTv.setText(R.string.getCodeString);
                } else {
                    getCodeTv.setTextColor(getResources().getColor(R.color.colorPink));
                    getCodeTv.setText(countDownNum + "s");
                }
            }
        }
    }


    /**
     *
     */
    private void login() {
        String account = account_et.getText().toString().trim();
        String psw = password_et.getText().toString().trim();
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(psw)) {
            showToast(R.string.loginNoCompeleteString);
            return;
        }
        HttpParams params = new HttpParams();
        params.put("telephone", account);
        params.put("code", psw);
        OkGo.<String>post(AppUrls.LoginUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(LoginActivity.this, params, ""))
                .execute(new DialogCallBack(LoginActivity.this, true) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                savaUserInfo(js.getJSONObject("data"));
                                senLocalBroadCast();
                                finish();
                            }
                            UIHelper.toastMsg(js.getString("message"));
                        } catch (JSONException e) {
                            UIHelper.toastMsg(e.getMessage());
                        }
                    }


                    @Override
                    public String convertResponse(okhttp3.Response response) throws Throwable {
                        HandleResponse.handleReponse(response);
                        return super.convertResponse(response);
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        HandleResponse.handleException(response, LoginActivity.this);
                    }
                });
    }


    private void savaUserInfo(JSONObject jsonObject) throws JSONException {
        JSONObject user = jsonObject.getJSONObject("user");
        SpUtils.put(this, SpUtils.USERNAME, user.getString("name"));
        SpUtils.put(this, SpUtils.USERTELEPHONE, user.getString("telephone"));
        SpUtils.put(this, SpUtils.USERWX_OPENID, user.getString("wx_openid"));
        SpUtils.put(this, SpUtils.USERQQ_ACCOUNT, user.getString("qq_account"));
        SpUtils.put(this, SpUtils.USERVIP, user.getString("vip"));
        SpUtils.put(this, SpUtils.USERUSER_TOKEN, user.getString("user_token"));
        SpUtils.put(this, SpUtils.USERUDID, user.getString("udid"));
        SpUtils.put(this, SpUtils.USERBIRTHDAY, user.getString("birthday"));
        SpUtils.put(this, SpUtils.USERSEX, user.getString("sex"));
        SpUtils.put(this, SpUtils.USERCREATED, user.getString("created"));
        SpUtils.put(this, SpUtils.USERMODIFIED, user.getString("modified"));
        SpUtils.put(this, SpUtils.USERIMAGE, user.getString("image"));
        SpUtils.put(this, SpUtils.USERCONFIRM, user.getString("confirm"));
    }


    private void senLocalBroadCast() {
        Intent intent = new Intent();
        intent.setAction(Configer.LOCAL_USERLOGIN_ACTION);
        localBroadcastManager.sendBroadcast(intent);
    }
}
