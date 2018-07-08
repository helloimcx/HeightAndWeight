package com.example.HomeworkOne;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.HomeworkOne.BaseActivity.BaseActivity;
import com.example.HomeworkOne.globalConfig.MyApplication;
import com.gc.materialdesign.views.ButtonRectangle;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import Utils.JsonUserBean;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import es.dmoral.toasty.Toasty;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Created by kafca on 18-7-05.
 */

public class AcForgetPwd extends BaseActivity{
    @Bind(R.id.edit_phone)
    EditText phone;
    @Bind(R.id.edit_code)
    EditText code;
    @Bind(R.id.verify_code)
    ButtonRectangle get_verify_code;
    @Bind(R.id.submit_btn)
    ButtonRectangle submit_btn;
    @Bind(R.id.ivToolbarNavigation)
    ImageView goback;

    private String phoneStr;
    private String codeStr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_forget_pwd);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initListener() {
        // 获取验证码
        get_verify_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int phone_length = phone.getText().length();
                if (phone_length == 11){
                    phoneStr = phone.getText().toString();
                    Log.e("token",gen_token(phoneStr));
                    sendCode("86", phoneStr);
                }
                else
                    showWarningToast("请输入正确的手机号码");
            }
        });

        // 提交验证码
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int code_length = code.getText().length();
                if (code_length > 0)
                    codeStr = code.getText().toString();
                else {
                    showWarningToast("请输入验证码");
                    return;
                }
                submitCode("86", phoneStr, codeStr);
            }
        });

        // 返回
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcForgetPwd.this.finish();
            }
        });
    }


    public void sendCode(String country, String phone) {
        // 注册一个事件回调，用于处理发送验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showSuccessToast("验证码发送成功");
                        }
                    });
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWarningToast("验证码发送失败，请稍后再试");
                        }
                    });
                }
                SMSSDK.unregisterAllEventHandler();
            }
        });
        // 触发操作
        SMSSDK.getVerificationCode(country, phone);
    }


    // 提交验证码，其中的code表示验证码，如“1357”
    public void submitCode(String country, final String phone, String code) {
        // 注册一个事件回调，用于处理提交验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    MyApplication myApplication = (MyApplication) getApplication();
                    final SharedPreferences sharedPreferences = myApplication.getShare();
                    final String token = gen_token(phoneStr);
                    OkGo.<String>post(myApplication.getHost() + "/android_account/auth/")
                            .headers("Authorization", "phone "+ token)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                                    if(response.code() == 200){
                                        Gson gson = new Gson();
                                        JsonUserBean jsonUserBean = gson.fromJson(response.body(),
                                                JsonUserBean.class);
                                        int id = jsonUserBean.getId();
                                        String phone = jsonUserBean.getPhone();
                                        String email = jsonUserBean.getEmail();
                                        String username = jsonUserBean.getUsername();
                                        String sex = jsonUserBean.getSex();
                                        String header = jsonUserBean.getHeader();
                                        String token = jsonUserBean.getToken();
                                        sex = (sex.equals("M")) ? "男" : "女";
                                        SharedPreferences.Editor edit = sharedPreferences.edit();
                                        edit.putInt("user_id", id);
                                        edit.putString("phone", phone);
                                        edit.putString("email", email);
                                        edit.putString("username", username);
                                        edit.putString("sex", sex);
                                        edit.putString("header", header);
                                        edit.putString("token", token);
                                        edit.apply();

                                        // 转到输入密码
                                        Intent intent = new Intent(AcForgetPwd.this, AcResetPwd.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        Toasty.warning(AcForgetPwd.this, "该手机号尚未注册",
                                                Toast.LENGTH_SHORT, true).show();
                                    }
                                }

                                @Override
                                public void onError(com.lzy.okgo.model.Response<String> response) {
                                    Toasty.error(AcForgetPwd.this, "请求错误").show();
                                }
                            });
                } else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showErrorToast("验证码错误");
                        }
                    });
                }
                SMSSDK.unregisterAllEventHandler();
            }
        });
        // 触发操作
        SMSSDK.submitVerificationCode(country, phone, code);
    }

    private String gen_token(String phone){
        Map<String, Object> map = new HashMap<>();
        map.put("typ", "JWT");
        map.put("alg", "HS256");
        return Jwts.builder()
                .setHeader(map)
                .claim("phone", phone)
                .signWith(SignatureAlgorithm.HS256, "SEVJR0hUQU5EV0VJR0hU")
                .compact();
    }

    protected void onDestroy() {
        super.onDestroy();
        //用完回调要注销掉，否则可能会出现内存泄露
        SMSSDK.unregisterAllEventHandler();
    }

}
