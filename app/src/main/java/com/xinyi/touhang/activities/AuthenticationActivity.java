package com.xinyi.touhang.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.kongqw.permissionslibrary.PermissionsManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.constants.Configer;
import com.xinyi.touhang.utils.BitmapUtil;
import com.xinyi.touhang.utils.CommonUtils;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.GlideCircleTransform;
import com.xinyi.touhang.utils.ImageSelectUtil;
import com.xinyi.touhang.utils.SpUtils;
import com.xinyi.touhang.utils.UIHelper;
import com.xinyi.touhang.weight.SelectPhotoPopupwindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthenticationActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.rootView)
    LinearLayout rootView;

    @BindView(R.id.name_et)
    EditText name_et;

    @BindView(R.id.tel_et)
    EditText tel_et;

    @BindView(R.id.company_et)
    EditText company_et;

    @BindView(R.id.duty_et)
    EditText duty_et;

    @BindView(R.id.mail_et)
    EditText mail_et;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.commit_btn)
    Button commit_btn;

    private List<EditText> editTexts;
    private List<String> values;

    private PermissionsManager mPermissionsManager;
    public static String filePath = "";
    private Uri imageUri;
    public static final String IMAGE_NAME = "authen.jpg";
    public final static int PHOTO_GRAPH = 1;// 拍照
    public final static int SELECT_PICTURE = 0;// 相册选择

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        initTitle("实名认证");
        editTexts = new ArrayList<>();
        values = new ArrayList<>();
        init();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideSoftInput(AuthenticationActivity.this, name_et);
                initPermissiton();
            }
        });
        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCommit();
            }
        });
    }

    private void initPermissiton() {
        // 初始化
        mPermissionsManager = new PermissionsManager(this) {
            @Override
            public void authorized(int requestCode) {
                selectPhonto();
            }

            @Override
            public void noAuthorization(int requestCode, String[] lacksPermissions) {
            }

            @Override
            public void ignore(int requestCode) {
                // Android 6.0 以下系统不校验
                selectPhonto();
            }
        };

        // 要校验的权限
        String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        // 检查权限
        mPermissionsManager.checkPermissions(0, PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 用户做出选择以后复查权限，判断是否通过了权限申请
        mPermissionsManager.recheckPermissions(requestCode, permissions, grantResults);
    }

    private void init() {
        editTexts.add(name_et);
        editTexts.add(tel_et);
        editTexts.add(company_et);
        editTexts.add(duty_et);
        editTexts.add(mail_et);

        for (int i = 0; i < editTexts.size(); i++) {
            values.add("");
        }

        for (int i = 0; i < editTexts.size(); i++) {
            final EditText et = editTexts.get(i);
            et.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtils.showInputMethod(et);
                }
            });
            final int position = i;
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    values.set(position, s.toString());
                }
            });
        }
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }

    private SelectPhotoPopupwindow photoPopupwindow;

    private void selectPhonto() {
        if (photoPopupwindow == null) {
            photoPopupwindow = new SelectPhotoPopupwindow(this, this);
        }

        photoPopupwindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //拍照
            case R.id.btn_take_photo:
                photoPopupwindow.dismiss();
                filePath = CommonUtils.takepictures(this, Configer.FileDirString, IMAGE_NAME,
                        imageUri, PHOTO_GRAPH);
                break;
            //相册
            case R.id.btn_pick_photo:
                photoPopupwindow.dismiss();
                //打开选择,本次允许选择的数量
                CommonUtils.cardSelect(this, SELECT_PICTURE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PHOTO_GRAPH:
                    Glide.with(AuthenticationActivity.this).load(new File(filePath)).into(imageView);
                    break;
                case SELECT_PICTURE:
                    if (data == null) {
                        return;
                    }

                    imageUri = data.getData();
                    filePath = ImageSelectUtil.getPath(this, imageUri);
                    Glide.with(AuthenticationActivity.this).load(new File(filePath)).into(imageView);
                    break;

            }
        }
    }

    private void doCommit() {

        for (int i = 0; i < values.size(); i++) {
            if (TextUtils.isEmpty(values.get(i))) {
                UIHelper.toastMsg("录入信息不完整");
                return;
            }
        }
        if (!CommonUtils.isMobileNO(values.get(1))) {
            UIHelper.toastMsg("手机号码不正确");
            return;
        }
        if (TextUtils.isEmpty(filePath)) {
            UIHelper.toastMsg("请先上传名片");
            return;
        }

        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        params.put("true_name", values.get(0));
        params.put("true_telephone", values.get(1));
        params.put("company", values.get(2));
        params.put("duty", values.get(3));
        params.put("email", values.get(4));
        //压缩
        if (!TextUtils.isEmpty(filePath)) {
            File uploadTemp = new File(filePath);
            File file = null;
            //如果file大于200kb
            if (uploadTemp.length() / 1024 > 200) {

                Bitmap bitmap = BitmapUtil.compressBitmap(filePath, 1080, 1080);
                //压缩后的bitmap保存为file
                file = new File(Configer.FileDirStringForUpload, "compressAuthen.jpg");
                BitmapUtil.compressBmpToFile(bitmap, Configer.FileDirStringForUpload, file);
            } else {
                file = new File(filePath);
            }
            params.put("image", file);
        }
        OkGo.<String>post(AppUrls.CustomerConfirmUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(AuthenticationActivity.this, params, user_token))
                .execute(new DialogCallBack(AuthenticationActivity.this, true) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());
                            if (js.getBoolean("result")) {
                                finish();
                            } else {
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
                        HandleResponse.handleException(response, AuthenticationActivity.this);
                    }
                });

    }
}
