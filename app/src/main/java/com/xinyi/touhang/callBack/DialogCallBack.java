package com.xinyi.touhang.callBack;

import android.app.Activity;
import android.app.Dialog;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.base.Request;
import com.xinyi.touhang.weight.CustomProgressDialog;

/**
 * Created by Niu on 2017/12/22.
 */

public abstract class DialogCallBack extends StringCallback {
    private Dialog dialog;

    private void initDialog(Activity activity) {
        dialog=CustomProgressDialog.createLoadingDialog(activity,"");
        dialog.setCancelable(false);

    }


    public DialogCallBack(Activity activity, boolean isShowDialog) {
        super();
        if (isShowDialog) {
            initDialog(activity);
        }
    }


    @Override
    public void onStart(Request<String, ? extends Request> request) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onFinish() {
        //网络请求结束后关闭对话框
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
