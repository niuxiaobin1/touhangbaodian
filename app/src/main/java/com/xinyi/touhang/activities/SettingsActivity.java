package com.xinyi.touhang.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kongqw.permissionslibrary.PermissionsManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Progress;
import com.xinyi.touhang.R;
import com.xinyi.touhang.base.BaseActivity;
import com.xinyi.touhang.callBack.DialogCallBack;
import com.xinyi.touhang.callBack.HandleResponse;
import com.xinyi.touhang.constants.AppUrls;
import com.xinyi.touhang.constants.Configer;
import com.xinyi.touhang.utils.AppManager;
import com.xinyi.touhang.utils.BitmapUtil;
import com.xinyi.touhang.utils.CommonUtils;
import com.xinyi.touhang.utils.DensityUtil;
import com.xinyi.touhang.utils.DoParams;
import com.xinyi.touhang.utils.GlideCircleTransform;
import com.xinyi.touhang.utils.ImageSelectUtil;
import com.xinyi.touhang.utils.SpUtils;
import com.xinyi.touhang.utils.UIHelper;
import com.xinyi.touhang.weight.SelectPhotoPopupwindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;

public class SettingsActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.rootView)
    LinearLayout rootView;

    @BindView(R.id.account_tv)
    TextView account_tv;
    @BindView(R.id.tel_tv)
    TextView tel_tv;

    @BindView(R.id.header_image_layout)
    LinearLayout header_image_layout;
    @BindView(R.id.user_image)
    ImageView user_image;

    @BindView(R.id.userName_layout)
    LinearLayout userName_layout;
    @BindView(R.id.userName_tv)
    TextView userName_tv;

    @BindView(R.id.birth_layout)
    LinearLayout birth_layout;
    @BindView(R.id.birth_tv)
    TextView birth_tv;

    @BindView(R.id.sex_layout)
    LinearLayout sex_layout;
    @BindView(R.id.sex_tv)
    TextView sex_tv;

    @BindView(R.id.logout_btn)
    Button logout_btn;

    private PermissionsManager mPermissionsManager;
    public static String filePath = "";
    private Uri imageUri;
    public static final String IMAGE_NAME = "header.jpg";
    public final static int PHOTO_GRAPH = 1;// 拍照
    public final static int SELECT_PICTURE = 0;// 相册选择

    private String nickName="";
    private String birthString="";
    private String sexString="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        initTitle("个人设置");
        header_image_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPermissiton();
            }
        });
        userName_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(SettingsActivity.this);
                View view= LayoutInflater.from(SettingsActivity.this).inflate(R.layout.edit_item,null);
                final EditText  content_et=view.findViewById(R.id.content_et);
                content_et.setText(userName_tv.getText().toString());
                builder.setView(view);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content=content_et.getText().toString().trim();
                        if (TextUtils.isEmpty(content)){
                            UIHelper.toastMsg("昵称不能为空");
                            return;
                        }
                        nickName=content;
                        userName_tv.setText(content);

                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
        });

        birth_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onYearMonthDayPicker(v);
            }
        });
        sex_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionPicker(v);
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpUtils.clear(SettingsActivity.this);
                AppManager.finishAllActivity();
                Intent it=new Intent(SettingsActivity.this,MainActivity.class);
                startActivity(it);
            }
        });
    }

    @Override
    protected boolean onBackImageClick() {
        upDateUserInfo();
        return false;
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

    private SelectPhotoPopupwindow photoPopupwindow;

    private void selectPhonto() {
        if (photoPopupwindow == null) {
            photoPopupwindow = new SelectPhotoPopupwindow(this, this);
        }

        photoPopupwindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }



    private ProgressDialog dialog;
    private void upDateUserInfo(){
        dialog=new ProgressDialog(this);
        dialog.setMessage("正在保存...");
        dialog.show();

        String user_token = (String) SpUtils.get(SettingsActivity.this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        params.put("name", TextUtils.isEmpty(nickName)?userName_tv.getText().toString():nickName);
        params.put("birthday", birthString);
        params.put("sex", sexString);
        //压缩
        if (!TextUtils.isEmpty(filePath)) {
            File uploadTemp = new File(filePath);
            File file = null;
            //如果file大于200kb
            if (uploadTemp.length() / 1024 > 200) {

                Bitmap bitmap = BitmapUtil.compressBitmap(filePath, 1080, 1080);
                //压缩后的bitmap保存为file
                file = new File(Configer.FileDirStringForUpload, "compressHeader.jpg");
                BitmapUtil.compressBmpToFile(bitmap, Configer.FileDirStringForUpload, file);
            } else {
                file = new File(filePath);
            }
            params.put("image", file);
        }

        OkGo.<String>post(AppUrls.CustomerUpdateUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(SettingsActivity.this, params, user_token))
                .execute(new DialogCallBack(SettingsActivity.this, false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        dialog.dismiss();
                        try {
                            JSONObject js = new JSONObject(response.body());
                            if (js.getBoolean("result")) {
                                finish();
                            } else {
                                UIHelper.toastMsg(js.getString("message"));
                            }
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
                        dialog.dismiss();
                        HandleResponse.handleException(response, SettingsActivity.this);
                    }
                });
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        account_tv.setText((String)SpUtils.get(this,SpUtils.USERQQ_ACCOUNT,""));
        tel_tv.setText((String)SpUtils.get(this,SpUtils.USERTELEPHONE,""));
        String imageUrl=(String)SpUtils.get(this,SpUtils.USERIMAGE,"");
        Glide.with(this).load(imageUrl).transform(new GlideCircleTransform(this))
                .into(user_image);
        userName_tv.setText((String)SpUtils.get(this,SpUtils.USERNAME,""));
        String sex=(String)SpUtils.get(this,SpUtils.USERSEX,"");
        if (sex.equals("0")){
            sexString="0";
            sex_tv.setText("男");
        }else{
            sex_tv.setText("女");
            sexString="1";
        }
        String birString=(String)SpUtils.get(this,SpUtils.USERBIRTHDAY,"");
        birthString=birString;
        if (TextUtils.isEmpty(birString)){
            birth_tv.setText("点击添加");
        }else{
            birth_tv.setText(birString);
        }

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
                   Glide.with(SettingsActivity.this).load(new File(filePath)).transform(
                           new GlideCircleTransform(SettingsActivity.this)).into(user_image);
                    break;
                case SELECT_PICTURE:
                    if (data == null) {
                        return;
                    }

                    imageUri = data.getData();
                    filePath = ImageSelectUtil.getPath(this, imageUri);
                    Glide.with(SettingsActivity.this).load(new File(filePath)).transform(
                            new GlideCircleTransform(SettingsActivity.this)).into(user_image);
                    break;

            }
        }
    }

    public void onYearMonthDayPicker(View view) {
        final DatePicker picker = new DatePicker(this);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(DensityUtil.dip2px(this, 10));
        picker.setRangeEnd(2018, 12, 12);
        picker.setRangeStart(1900, 1, 1);
        picker.setSelectedItem(2018, 5, 1);
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                birth_tv.setText(year+"-"+month+"-"+day);
                birthString=year+"-"+month+"-"+day;
            }
        });
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
    }


    public void onOptionPicker(View view) {
        OptionPicker picker = new OptionPicker(this, new String[]{
                "男", "女"
        });
        picker.setTextSize(14);
        picker.setCanceledOnTouchOutside(false);
        picker.setDividerRatio(WheelView.DividerConfig.FILL);
        picker.setShadowColor(Color.RED, 40);
        picker.setSelectedIndex(1);
        picker.setCycleDisable(true);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                sex_tv.setText(item);
                sexString=String.valueOf(index);
            }
        });
        picker.show();
    }
}
