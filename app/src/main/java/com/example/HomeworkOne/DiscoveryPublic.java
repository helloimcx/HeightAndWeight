package com.example.HomeworkOne;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import Utils.OssSecretBean;
import butterknife.Bind;
import butterknife.ButterKnife;
import MyInterface.InitView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import static com.example.HomeworkOne.AcLogin.JSON;

/**
 * Created by mac on 2017/11/23.
 */

public class DiscoveryPublic extends Activity implements InitView{
    private String signature;
    private OSS oss;
    private RequestBody requestBody;
    private String image_path;
    private String image_name;
    private String email;
    @Bind(R.id.moment_public)
    TextView moment_public;
    @Bind(R.id.ivToolbarNavigation)
    ImageView goback;
    @Bind(R.id.moment_image)
    ImageView image;
    @Bind(R.id.moment_content)
    EditText moment_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_moment);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        moment_public.setVisibility(View.VISIBLE);

        //获取要上传的照片信息
        Intent data = getIntent();
        image_path = data.getStringExtra("path");
        image_name = data.getStringExtra("name");
        Picasso.with(this).load(new File(image_path)).fit().centerCrop().into(image);

        //获取用户信息
        SharedPreferences sharedPreferences = getSharedPreferences("Session", MODE_PRIVATE);
        email = sharedPreferences.getString("email","email");

        //初始化Oss
        initOss();
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
                //用户上传的照片以"moment"+email+image_name命名
                String objectKey = "moment"+email+image_name;
                putToOss(objectKey, image_path);
                String content = moment_content.getText().toString();
                String url = "http://ht-data.oss-cn-shenzhen.aliyuncs.com/"+objectKey;

                //发布分享
                OkHttpClient okHttpClient = new OkHttpClient();
                JSONObject param = new JSONObject();
                try {
                    param.put("moment_content", content);
                    param.put("moment_url", url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(JSON, param.toString());
                Request request = new Request.Builder()
                        .url("http://120.78.67.135:8000/moment/")
                        .addHeader("cookie", MainActivity.sessionid)
                        .post(requestBody)
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        DiscoveryPublic.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DiscoveryPublic.this,"您的网络似乎开小差了...",Toast.LENGTH_SHORT)
                                .show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        finish();
                    }
                });
            }
        });
    }

    private void initOss(){
        String endpoint = "http://oss-cn-shenzhen.aliyuncs.com";
        OSSCustomSignerCredentialProvider credentialProvider = new OSSCustomSignerCredentialProvider() {
            @Override
            public String signContent(String content) {
                // 您需要在这里依照OSS规定的签名算法，实现加签一串字符内容，并把得到的签名传拼接上AccessKeyId后返回
                // 一般实现是，将字符内容post到您的业务服务器，然后返回签名
                // 如果因为某种原因加签失败，描述error信息后，返回nil
                // 以下是用本地算法进行的演示
                //return "OSS " + AccessKeyId + ":" + base64(hmac-sha1(AccessKeySecret, content));
                SharedPreferences sharedPreferences = getSharedPreferences("Session",MODE_PRIVATE);
                String sessionid = sharedPreferences.getString("sessionid","null");
                String url = "http://120.78.67.135:8000/oss/android_signature/";
                OkHttpClient okHttpClient = new OkHttpClient();
                try {
                    JSONObject param = new JSONObject();
                    param.put("content", content);
                    requestBody = RequestBody.create(JSON, param.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
                Request request = new Request.Builder().url(url).addHeader("cookie",sessionid)
                        .post(requestBody).build();
                Call call = okHttpClient.newCall(request);
                try{
                    Response response = call.execute();
                    Gson gson = new Gson();
                    OssSecretBean ossSecretBean = gson.fromJson(response.body().string(),
                            OssSecretBean.class);
                    signature = ossSecretBean.getAuthorization();
                    //Log.e("signature",signature);
                    return signature;
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                return "null";
            }
        };
        oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
    }

    private void putToOss(String objectKey, String path){
        PutObjectRequest put = new PutObjectRequest("ht-data", objectKey, path);
        // 文件元信息的设置是可选的
        // ObjectMetadata metadata = new ObjectMetadata();
        // metadata.setContentType("application/octet-stream"); // 设置content-type
        // metadata.setContentMD5(BinaryUtil.calculateBase64Md5(uploadFilePath)); // 校验MD5
        // put.setMetadata(metadata);
        try {
            PutObjectResult putResult = oss.putObject(put);
            Log.d("PutObject", "UploadSuccess");
            Log.d("ETag", putResult.getETag());
            Log.d("RequestId", putResult.getRequestId());
        } catch (ClientException e) {
            // 本地异常如网络异常等
            e.printStackTrace();
        } catch (ServiceException e) {
            // 服务异常
            Log.e("RequestId", e.getRequestId());
            Log.e("ErrorCode", e.getErrorCode());
            Log.e("HostId", e.getHostId());
            Log.e("RawMessage", e.getRawMessage());
        }
    }
}
