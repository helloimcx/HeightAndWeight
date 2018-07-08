package com.example.HomeworkOne;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.HomeworkOne.BaseActivity.BaseActivity;
import com.example.HomeworkOne.globalConfig.MyApplication;
import com.gc.materialdesign.views.ButtonRectangle;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static Utils.Md5.md5;

public class AcResetPwd extends BaseActivity{

    @Bind(R.id.edit_pwd)
    EditText edit_pwd;
    @Bind(R.id.edit_repeat_pwd)
    EditText edit_repeat_pwd;
    @Bind(R.id.submit_btn)
    ButtonRectangle btn_submit;
    @Bind(R.id.ivToolbarNavigation)
    ImageView goback;

    private int id;
    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_reset_password);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        MyApplication myApplication = (MyApplication) getApplication();
        SharedPreferences sharedPreferences = myApplication.getShare();
        id = sharedPreferences.getInt("user_id", 0);
        token = sharedPreferences.getString("token", "null");
    }

    @Override
    public void initListener() {
        // 返回
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcResetPwd.this.finish();
            }
        });

        // 提交修改密码
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length1 = edit_pwd.getText().length();
                int length2 = edit_repeat_pwd.getText().length();
                if (length1 * length2 > 0){
                    String pwd1 = edit_pwd.getText().toString();
                    String pwd2 = edit_repeat_pwd.getText().toString();
                    if (pwd1.equals(pwd2)){
                        pwd1 = md5(pwd1);
                        resetPwd(pwd1);
                    }
                    else {
                        showWarningToast("密码不一致");
                    }
                }
                else {
                    showWarningToast("请输入密码");
                }
            }
        });
    }

    // 修改密码
    private void resetPwd(String pwd){
        showLoading();
        MyApplication myApplication = (MyApplication) getApplication();
        JSONObject param = new JSONObject();
        try {
            param.put("password", pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<String>put(myApplication.getHost() + "/android_account/profile/" + id + "/")
                .headers("Authorization", "token " + token)
                .upJson(param)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response.code() == 200) {
                            showSuccessToast("修改成功");
                            Intent intent = new Intent(AcResetPwd.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else{
                            hideLoading();
                            showErrorToast("修改失败");
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        hideLoading();
                        showErrorToast("请求失败");
                    }
                });
    }
}
