package com.example.HomeworkOne;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

    private String email_str;
    private String nickname_str;
    private String gender_str;
    private String user_header_str;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    private void initView() {
        SharedPreferences share = getSharedPreferences("Session", MODE_PRIVATE);
        email_str = share.getString("email", "null");
        nickname_str = share.getString("username", "null");
        gender_str = share.getString("sex", "null");
        user_header_str = share.getString("header","null");
        Uri header_uri = Uri.parse(user_header_str);
        email.setRightText(email_str);
        nickName.setRightText(nickname_str);
        gender.setRightText(gender_str);
        Picasso.with(UserInfo.this).load(header_uri).into(user_header);
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
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request =new Request.Builder().url("http://120.78.67.135:8000/android_account/logout")
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        UserInfo.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserInfo.this);
                                builder.setMessage("«Î«Û≥¨ ±!");
                                builder.setCancelable(true);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        UserInfo.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences sharedPreferences =
                                        getSharedPreferences("Session",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear().apply();
                                MainActivity.sessionid = "null";
                                UserInfo.this.finish();
                            }
                        });
                    }
                });
            }
        });
    }
}
