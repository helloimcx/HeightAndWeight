package com.example.HomeworkOne;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;


/**
 * Created by kafca on 17-9-28.
 */

public class LoginFragment extends android.support.v4.app.Fragment{
    private EditText email;
    private EditText password;
    private Button registerButton;
    private Button LoginButton;
    private String emailStr;
    private String passwordStr;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private int login_flag;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.login, container, false);

        email=(EditText)view.findViewById(R.id.email);
        password=(EditText)view.findViewById(R.id.Password);
        registerButton=(Button)view.findViewById(R.id.RegisterButton);
        LoginButton=(Button)view.findViewById(R.id.LoginButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(new RegisterFragment());
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    emailStr=email.getText().toString();
                    passwordStr=password.getText().toString();


                }catch (Exception e){
                    Toast.makeText(getActivity(), "请输入正确的信息！",
                            Toast.LENGTH_SHORT).show();
                }


                    Runnable networkTask = new Runnable() {

                        @Override
                        public void run() {
                            // TODO
                            // 在这里进行 http request.网络请求相关操作

                            try {
                                OkHttpClient okHttpClient = new OkHttpClient();
                                JSONObject param = new JSONObject();
                                param.put("email", emailStr);
                                param.put("password", passwordStr);
                                RequestBody requestBody = RequestBody.create(JSON,param.toString());
                                Request request = new Request.Builder()
                                        .url("http://120.78.67.135:8000/androidaccount/login")
                                        .post(requestBody)
                                        .build();
                                Response response=okHttpClient.newCall(request).execute();
                                    //打印服务端返回结果
                                login_flag=response.code();
                                Log.i("STATUS",response.code()+"");

                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }
                    };

                new Thread(networkTask).start();
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

                switch(login_flag){
                    case 200:
                        builder.setMessage("登录成功!");

                        break;
                    case 400:
                        builder.setMessage("密码错误!");
                        break;
                    case 404:
                        builder.setMessage("账号不存在!");
                        break;
                    default:
                        builder.setMessage("账号或密码错误!");
                }
                builder.setCancelable(true);
                AlertDialog dialog=builder.create();
                dialog.show();

                }
        });


        return view;
    }
    private void switchFragment(android.support.v4.app.Fragment targetFragment) {
        MainActivity.tab04=targetFragment;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction
                    .hide(LoginFragment.this)
                    .add(R.id.id_content, MainActivity.tab04)
                    .commit();
        } else {
            transaction
                    .hide(LoginFragment.this)
                    .show( MainActivity.tab04)
                    .commit();
        }

    }
}
