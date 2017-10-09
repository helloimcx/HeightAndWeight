package com.example.HomeworkOne;

import android.app.Fragment;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.apache.commons.logging.Log;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.String;
import android.os.Handler;
import android.os.Message;
import com.squareup.okhttp.*;


/**
 * Created by kafca on 17-9-28.
 */

public class RegisterFragment extends android.support.v4.app.Fragment{
    private EditText email;
    private EditText username;
    private EditText password;
    private EditText passwordRepeat;
    private Button registerButton;
    private RadioButton male;
    private String emailStr;
    private String usernameStr;
    private String passwordStr;
    private String passwordRepeatStr;
    private String usernameRegister;
    private String sex="F";
    private Handler handler;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private boolean register_flag=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.register, container, false);

        email=(EditText)view.findViewById(R.id.email);
        username=(EditText)view.findViewById(R.id.username);
        password=(EditText)view.findViewById(R.id.Password);
        passwordRepeat=(EditText)view.findViewById(R.id.PasswordRepeat);
        registerButton=(Button)view.findViewById(R.id.RegisterButton);
        male=(RadioButton) view.findViewById(R.id.btnMan);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    emailStr=email.getText().toString();
                    usernameStr=username.getText().toString();
                    passwordStr=password.getText().toString();
                    passwordRepeatStr=passwordRepeat.getText().toString();
                    if(male.isChecked()){
                        sex="M";
                    }

                }catch (Exception e){
                    Toast.makeText(getActivity(), "请输入正确的信息！",
                            Toast.LENGTH_SHORT).show();
                }
                if(passwordStr.equals(passwordRepeatStr)){
/**
 * 网络操作相关的子线程
 */
                    Runnable networkTask = new Runnable() {

                        @Override
                        public void run() {
                            // TODO
                            // 在这里进行 http request.网络请求相关操作

                            try {
                                OkHttpClient okHttpClient = new OkHttpClient();
                                JSONObject param = new JSONObject();
                                param.put("email", emailStr);
                                param.put("username",usernameStr);
                                param.put("sex", sex);
                                param.put("password", passwordStr);
                                RequestBody requestBody = RequestBody.create(JSON,param.toString());
                                Request request = new Request.Builder()
                                        .url("http://120.78.67.135:8000/androidaccount/register")
                                        .post(requestBody)
                                        .build();
                                Response response=okHttpClient.newCall(request).execute();
                                if(response.isSuccessful()){
                                    //打印服务端返回结果
                                    register_flag=true;
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }
                    };

                    new Thread(networkTask).start();
                    if(register_flag){
                        Toast.makeText(getActivity(), "注册成功!",
                                Toast.LENGTH_SHORT).show();
                        switchFragment(new LoginFragment());
                    }

                }
                else{
                    Toast.makeText(getActivity(), "输入密码不一致!",
                            Toast.LENGTH_SHORT).show();
                }


            }
        });


        return view;
    }
    private void switchFragment(android.support.v4.app.Fragment targetFragment) {
        MainActivity.tab04=targetFragment;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction
                    .hide(RegisterFragment.this)
                    .add(R.id.id_content, MainActivity.tab04)
                    .commit();
        } else {
            transaction
                    .hide(RegisterFragment.this)
                    .show( MainActivity.tab04)
                    .commit();
        }

    }
}
