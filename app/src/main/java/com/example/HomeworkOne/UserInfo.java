package com.example.HomeworkOne;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.example.HomeworkOne.globalConfig.MyApplication;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLinearLayout;
import com.lqr.optionitemview.OptionItemView;
import java.io.IOException;


public class UserInfo extends Activity {
    @Bind(R.id.llHeader)
    AutoLinearLayout header;
    @Bind(R.id.nickName)
    OptionItemView nickName;
    @Bind(R.id.myEmail)
    OptionItemView email;
    @Bind(R.id.myRegion)
    OptionItemView region;
    @Bind(R.id.myGender)
    OptionItemView gender;
    @Bind(R.id.mySignature)
    OptionItemView signature;
    @Bind(R.id.logout)
    OptionItemView logout;
    @Bind(R.id.ivToolbarNavigation)
    ImageView goback;
    @Bind(R.id.ivHeader)
    ImageView user_header;

    private String user_header_str;
    private  MyBroadCastReceiver myBroadCastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_my_info);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    private void initView() {
        SharedPreferences share = getSharedPreferences("Session", MODE_PRIVATE);
        String email_str = share.getString("email", "null");
        String nickname_str = share.getString("username", "null");
        String gender_str = share.getString("sex", "null");
        user_header_str = share.getString("header","null");
        Uri header_uri = Uri.parse(user_header_str);
        email.setRightText(email_str);
        nickName.setRightText(nickname_str);
        gender.setRightText(gender_str);
        Picasso.with(UserInfo.this).load(header_uri).placeholder(R.mipmap.default_header).fit().centerCrop().into(user_header);

        //注册BroadcastReceiver
        registerBroadcastReceiver();
    }

    private void initListener() {
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfo.this, UserHeader.class);
                startActivity(intent);
            }
        });
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInfo.this.finish();
            }
        });

        /*
        退出登录
         */
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication myApplication = (MyApplication) getApplication();
                String host = myApplication.getHost();

                OkGo.<String>get(host+"/android_account/sign-out/")
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                                SharedPreferences sharedPreferences =
                                        getSharedPreferences("Session",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear().apply();

                                //关闭这几个Activity
                                UserInfo.this.finish();

                                //转到登录界面
                                Intent intent = new Intent(UserInfo.this, AcLogin.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onError(com.lzy.okgo.model.Response<String> response) {
                                Toasty.error(UserInfo.this, "请求超时").show();
                            }
                });
            }
        });
    }

    //BroadcastReceiver
    class MyBroadCastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            user_header_str = intent.getStringExtra("header_url");
            Uri header_uri = Uri.parse(user_header_str);
            Picasso.with(UserInfo.this).load(header_uri).fit().centerCrop().into(user_header);
        }
    }

    //注册BroadcastReceiver
    private void registerBroadcastReceiver(){
        myBroadCastReceiver = new MyBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UserHeader.BROADCAST_ACTION);
        registerReceiver(myBroadCastReceiver, intentFilter);
    }

    //重写onDestory（）撤销BroadcastReceiver的注册
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadCastReceiver);
    }
}
