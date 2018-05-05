package com.example.HomeworkOne;

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

import com.example.HomeworkOne.BaseActivity.BaseActivity;
import com.example.HomeworkOne.globalConfig.MyApplication;
import com.gc.materialdesign.views.CheckBox;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import Utils.TimeUtils;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by mac on 2017/11/23.
 */

public class AcMomentPublish extends BaseActivity{
    private String image_path;
    private String email;
    @Bind(R.id.moment_public)
    TextView moment_public;
    @Bind(R.id.ivToolbarNavigation)
    ImageView goback;
    @Bind(R.id.moment_image)
    ImageView image;
    @Bind(R.id.moment_content)
    EditText moment_content;
    @Bind(R.id.checkBox)
    CheckBox checkBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.public_moment);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        loadingDialog.setMessage("正在上传");
        ButterKnife.bind(this);
        moment_public.setVisibility(View.VISIBLE);

        //获取要上传的照片信息
        Intent data = getIntent();
        image_path = data.getStringExtra("path");
        Picasso.with(this).load(new File(image_path)).fit().centerCrop().into(image);

        //获取用户信息
        SharedPreferences sharedPreferences = getSharedPreferences("Session", MODE_PRIVATE);
        email = sharedPreferences.getString("email","email");
    }

    @Override
    public void initListener() {
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //上传moment照片和发表moment
        moment_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoading();
                final MyApplication myApplication = (MyApplication) getApplication();
                //name the moment picture
                final String objectKey = "moment"+email+TimeUtils.getCurrentTime()+".jpg";

                // 压缩图片
                Luban.with(AcMomentPublish.this)
                        .load(new File(image_path))                     // 传人要压缩的图片列表
                        .ignoreBy(100)                                  // 忽略不压缩图片的大小
                        .setCompressListener(new OnCompressListener() { //设置回调
                            @Override
                            public void onStart() {}

                            @Override
                            public void onSuccess(File file) {
                                myApplication.putToOss(objectKey, file.getPath());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toasty.error(AcMomentPublish.this, "压缩失败").show();
                            }
                        }).launch();    //启动压缩

                String content = moment_content.getText().toString();
                boolean is_public = checkBox.isCheck();
                String url = "http://ht-data.oss-cn-shenzhen.aliyuncs.com/"+objectKey;

                //发布分享
                JSONObject param = new JSONObject();
                try {
                    if(!content.trim().isEmpty()){
                        param.put("moment_content", content);
                    }
                    param.put("moment_url", url);
                    param.put("is_public", is_public);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                OkGo.<String>post(myApplication.getHost()+"/moment/list/")
                        .headers(myApplication.header())
                        .upJson(param)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                                if (response.code() == 201)
                                    finish();
                                else
                                    Toast.makeText(AcMomentPublish.this,"您的网络似乎开小差了...",
                                            Toast.LENGTH_SHORT).show();
                                hideLoading();
                            }

                            @Override
                            public void onError(com.lzy.okgo.model.Response<String> response) {
                                Toast.makeText(AcMomentPublish.this,"您的网络似乎开小差了...",
                                        Toast.LENGTH_SHORT).show();
                                hideLoading();
                            }
                        });
            }
        });
    }
}
