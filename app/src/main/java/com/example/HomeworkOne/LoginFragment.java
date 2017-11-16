package com.example.HomeworkOne;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import Utils.JsonUserBean;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.List;
import org.json.JSONObject;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import static android.content.Context.MODE_PRIVATE;


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
    private static Response response;

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

                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        Bundle data = msg.getData();
                        int status = data.getInt("status");
                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                        switch(status){
                            case 200:
                                String sessionid = data.getString("sessionid");
                                int user_id = data.getInt("user_id");
                                String email = data.getString("email");
                                String username = data.getString("username");
                                String sex = data.getString("sex");

                                SharedPreferences share = getActivity().getSharedPreferences("Session", MODE_PRIVATE);
                                SharedPreferences.Editor edit = share.edit();
                                edit.putString("sessionid", sessionid);
                                MainActivity.sessionid=sessionid; //每次登录都要更新sessionid
                                edit.putInt("user_id",user_id);
                                edit.putString("email", email);
                                edit.putString("username", username);
                                edit.putString("sex", sex);
                                edit.commit();  //保存数据信息

                                builder.setMessage("登录成功!");
                                switchFragment(new AccountFragment());
                                break;
                            case 400:
                                builder.setMessage("密码错误!");
                                break;
                            case 500:
                                builder.setMessage("账号不存在!");
                                break;
                            default:
                                builder.setMessage(login_flag+"");
                        }
                        builder.setCancelable(true);
                        AlertDialog dialog=builder.create();
                        dialog.show();
                    }
                };


                    Runnable networkTask = new Runnable() {
                        @Override
                        public void run() {
                            // TODO
                            // 在这里进行 http request.网络请求相关操作

                            try {
                                OkHttpClient okHttpClient = MainActivity.okHttpClient;
                                JSONObject param = new JSONObject();
                                param.put("email", emailStr);
                                param.put("password", passwordStr);
                                RequestBody requestBody = RequestBody.create(JSON, param.toString());
                                Request request = new Request.Builder()
                                        .url("http://120.78.67.135:8000/android_account/login")
                                        .post(requestBody)
                                        .build();
                                response = okHttpClient.newCall(request).execute();
                                login_flag = response.code();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Headers headers = response.headers();
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            if(login_flag==200){
                                List<String> cookies = headers.values("Set-Cookie");
                                String session = cookies.get(0);
                                String sessionid = session.substring(0, session.indexOf(";"));
                                data.putString("sessionid",sessionid);
                                Gson gson = new Gson();
                                try {
                                    JsonUserBean jsonUserBean = gson.fromJson(response.body().string(),
                                            JsonUserBean.class);
                                    data.putInt("user_id",jsonUserBean.get_id());
                                    data.putString("email",jsonUserBean.get_email());
                                    data.putString("username",jsonUserBean.get_username());
                                    data.putString("sex",jsonUserBean.get_sex());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            data.putInt("status", login_flag);
                            msg.setData(data);
                            handler.sendMessage(msg);
                        }
                    };
                new Thread(networkTask).start();
                }
        });

        return view;
    }
    private void switchFragment(android.support.v4.app.Fragment targetFragment) {
        MainActivity.tab_transfer=targetFragment;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction
                    .hide(LoginFragment.this)
                    .add(R.id.id_content, MainActivity.tab_transfer)
                    .commit();
        } else {
            transaction
                    .hide(LoginFragment.this)
                    .show( MainActivity.tab_transfer)
                    .commit();
        }

    }
}
