package com.example.HomeworkOne;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import MyInterface.InitView;
import Utils.JsonUserBean;
import butterknife.Bind;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.HomeworkOne.globalConfig.MyApplication;
import com.gc.materialdesign.views.ButtonRectangle;
import com.google.gson.*;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;


/**
 * Created by kafca on 17-9-28.
 */

public class AcLogin extends Activity implements InitView{
    static AcLogin instance;
    private String accountStr;
    private String passwordStr;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    @Bind(R.id.edit_account)
    EditText account;
    @Bind(R.id.edit_password)
    EditText password;
    @Bind(R.id.tv_no_account)
    TextView no_account;
    @Bind(R.id.btn_login)
    ButtonRectangle LoginButton;
    @Bind(R.id.ivToolbarNavigation)
    ImageView goback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.ac_login);
        initView();
        initListener();
    }


    @Override
    public void initView() {
        ButterKnife.bind(this);
        goback.setVisibility(View.GONE);
        instance = this;
    }

    @Override
    public void initListener() {

        //没有账号，转到注册
        no_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AcLogin.this, AcRegister.class);
                startActivity(intent);
            }
        });

        //登录
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //获取登录表单
                accountStr = account.getText().toString();
                passwordStr = password.getText().toString();
                if (accountStr.length() * password.length() == 0){
                    Toasty.warning(AcLogin.this, "请输入账号和密码!",
                            Toast.LENGTH_SHORT, true).show();
                    return;
                }

                //登录请求
                JSONObject param = new JSONObject();
                try {
                    if (accountStr.contains("@"))
                        param.put("email", accountStr);
                    else
                        param.put("phone", accountStr);
                    param.put("password", passwordStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MyApplication myApplication = (MyApplication) getApplication();
                OkGo.<String>post(myApplication.getHost() + "/android_account/token/")
                        .upJson(param)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                                //登陆成功
                                if (response.code() == 200) {
                                    //解析用户数据并保存
                                    Gson gson = new Gson();
                                    try {
                                        JsonUserBean jsonUserBean = gson.fromJson(response.body(),
                                                JsonUserBean.class);
                                        int user_id = jsonUserBean.getUser_id();
                                        String email = jsonUserBean.getEmail();
                                        String username = jsonUserBean.getUsername();
                                        String sex = jsonUserBean.getSex();
                                        String header = jsonUserBean.getHeader();
                                        String phone = jsonUserBean.getPhone();
                                        String token = jsonUserBean.getToken();
                                        sex = (sex.equals("M")) ? "男" : "女";
                                        SharedPreferences share = getSharedPreferences("Session",
                                                MODE_PRIVATE);
                                        SharedPreferences.Editor edit = share.edit();
                                        edit.putInt("user_id", user_id);
                                        edit.putString("phone", phone);
                                        edit.putString("email", email);
                                        edit.putString("username", username);
                                        edit.putString("sex", sex);
                                        edit.putString("header", header);
                                        edit.putString("token", token);
                                        edit.apply();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //登陆成功，跳转主页面
                                    Intent intent = new Intent(AcLogin.this, MainActivity.class);
                                    startActivity(intent);

                                }
                                //登录错误处理
                                else if (response.code() == 400) {
                                    Toasty.warning(AcLogin.this, "密码错误!").show();
                                } else if (response.code() == 404) {
                                    Toasty.warning(AcLogin.this, "账号不存在!").show();
                                } else {
                                    Toasty.error(AcLogin.this, "请求错误!"+response.code()).show();
                                }
                            }

                            @Override
                            public void onError(com.lzy.okgo.model.Response<String> response) {
                                Toasty.error(AcLogin.this, "账号不存在!").show();
                            }
                        });
            }
        });
    }
}
