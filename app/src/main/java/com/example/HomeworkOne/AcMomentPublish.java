package com.example.HomeworkOne;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import com.example.HomeworkOne.globalConfig.MyApplication;
import com.gc.materialdesign.views.CheckBox;
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
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import static com.example.HomeworkOne.AcLogin.JSON;
import Utils.TimeUtils;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by mac on 2017/11/23.
 */

public class AcMomentPublish extends Activity implements InitView{
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
        ButterKnife.bind(this);
        moment_public.setVisibility(View.VISIBLE);

        //��ȡҪ�ϴ�����Ƭ��Ϣ
        Intent data = getIntent();
        image_path = data.getStringExtra("path");
        image_name = data.getStringExtra("name");
        Picasso.with(this).load(new File(image_path)).fit().centerCrop().into(image);

        //��ȡ�û���Ϣ
        SharedPreferences sharedPreferences = getSharedPreferences("Session", MODE_PRIVATE);
        email = sharedPreferences.getString("email","email");

        //��ʼ��Oss
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

        //�ϴ�moment��Ƭ�ͷ���moment
        moment_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //name the moment picture
                final String objectKey = "moment"+email+TimeUtils.getCurrentTime()+".jpg";

                // ѹ��ͼƬ
                Luban.with(AcMomentPublish.this)
                        .load(new File(image_path))                     // ����Ҫѹ����ͼƬ�б�
                        .ignoreBy(100)                                  // ���Բ�ѹ��ͼƬ�Ĵ�С
                        .setCompressListener(new OnCompressListener() { //���ûص�
                            @Override
                            public void onStart() {
                                Toasty.info(AcMomentPublish.this, "�����ϴ�").show();
                            }

                            @Override
                            public void onSuccess(File file) {
                                putToOss(objectKey, file.getPath());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toasty.error(AcMomentPublish.this, "ѹ��ʧ��").show();
                            }
                        }).launch();    //����ѹ��

                String content = moment_content.getText().toString();
                boolean is_public = checkBox.isCheck();
                String url = "http://ht-data.oss-cn-shenzhen.aliyuncs.com/"+objectKey;

                //��������
                OkHttpClient okHttpClient = new OkHttpClient();
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
                RequestBody requestBody = RequestBody.create(JSON, param.toString());
                MyApplication myApplication = (MyApplication) getApplication();
                Request request = new Request.Builder()
                        .url(myApplication.getHost()+"/moment/")
                        .addHeader("cookie", MainActivity.sessionid)
                        .post(requestBody)
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        AcMomentPublish.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AcMomentPublish.this,"���������ƺ���С����...",Toast.LENGTH_SHORT)
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
                // ����Ҫ����������OSS�涨��ǩ���㷨��ʵ�ּ�ǩһ���ַ����ݣ����ѵõ���ǩ����ƴ����AccessKeyId�󷵻�
                // һ��ʵ���ǣ����ַ�����post������ҵ���������Ȼ�󷵻�ǩ��
                // �����Ϊĳ��ԭ���ǩʧ�ܣ�����error��Ϣ�󣬷���nil
                // �������ñ����㷨���е���ʾ
                //return "OSS " + AccessKeyId + ":" + base64(hmac-sha1(AccessKeySecret, content));
                SharedPreferences sharedPreferences = getSharedPreferences("Session",MODE_PRIVATE);
                String sessionid = sharedPreferences.getString("sessionid","null");
                MyApplication myApplication = (MyApplication) getApplication();
                String url = myApplication.getHost()+"/oss/android_signature/";
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
        // �ļ�Ԫ��Ϣ�������ǿ�ѡ��
        // ObjectMetadata metadata = new ObjectMetadata();
        // metadata.setContentType("application/octet-stream"); // ����content-type
        // metadata.setContentMD5(BinaryUtil.calculateBase64Md5(uploadFilePath)); // У��MD5
        // put.setMetadata(metadata);
        try {
            PutObjectResult putResult = oss.putObject(put);
            Log.d("PutObject", "UploadSuccess");
            Log.d("ETag", putResult.getETag());
            Log.d("RequestId", putResult.getRequestId());
        } catch (ClientException e) {
            // �����쳣�������쳣��
            e.printStackTrace();
        } catch (ServiceException e) {
            // �����쳣
            Log.e("RequestId", e.getRequestId());
            Log.e("ErrorCode", e.getErrorCode());
            Log.e("HostId", e.getHostId());
            Log.e("RawMessage", e.getRawMessage());
        }
    }
}