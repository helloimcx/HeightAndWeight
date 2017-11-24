package com.example.HomeworkOne;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.String;

import MyInterface.InitView;
import okhttp3.*;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.CheckBox;
import com.gc.materialdesign.widgets.SnackBar;


/**
 * Created by kafca on 17-9-28.
 */

public class AcRegister extends Activity implements InitView{
    @Bind(R.id.edit_email)
    EditText email;
    @Bind(R.id.edit_username)
    EditText username;
    @Bind(R.id.edit_password)
    EditText password;
    @Bind(R.id.btn_register)
    ButtonRectangle registerButton;
    @Bind(R.id.cbx_male)
    CheckBox male;
    @Bind(R.id.cbx_female)
    CheckBox female;
    @Bind(R.id.tv_has_account)
    TextView has_account;
    @Bind(R.id.ivToolbarNavigation)
    ImageView goback;
    private String emailStr;
    private String usernameStr;
    private String passwordStr;
    private String sex="F";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private int register_flag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_register);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initListener() {

        //返回登录页面
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //选男不选女，选女不选男
        male.setOncheckListener(new CheckBox.OnCheckListener() {
            @Override
            public void onCheck(CheckBox checkBox, boolean b) {
                if(b){
                    female.setChecked(false);
                }
            }
        });
        female.setOncheckListener(new CheckBox.OnCheckListener() {
            @Override
            public void onCheck(CheckBox checkBox, boolean b) {
                if(b){
                    male.setChecked(false);
                }
            }
        });

        //注册
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //获取注册信息
                try{
                    emailStr=email.getText().toString();
                    usernameStr=username.getText().toString();
                    passwordStr=password.getText().toString();
                    if(male.isCheck()){
                        sex = "M";
                    }
                }catch (Exception e){
                    final SnackBar snackbar = new SnackBar(AcRegister.this,
                            "请输入正确的注册信息...", null, null);
                    snackbar.show();
                }

                OkHttpClient okHttpClient = new OkHttpClient();
                JSONObject param = new JSONObject();
                try {
                    param.put("email", emailStr);
                    param.put("username", usernameStr);
                    param.put("sex", sex);
                    param.put("password", passwordStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(JSON, param.toString());
                Request request = new Request.Builder()
                        .url("http://120.78.67.135:8000/android_account/register")
                        .post(requestBody)
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        AcRegister.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final SnackBar snackbar = new SnackBar(AcRegister.this,
                                        "您的网络似乎开小差了...", null, null);
                                snackbar.show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        AcRegister.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final SnackBar snackbar = new SnackBar(AcRegister.this,
                                        "注册成功!", null, null);
                                snackbar.show();

                                //转到登陆
                                Intent intent = new Intent(AcRegister.this, AcLogin.class);
                                intent.putExtra("email", emailStr);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        });

        //已有账号,转到登陆
        has_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AcRegister.this, AcLogin.class);
                startActivity(intent);
            }
        });

    }
}
