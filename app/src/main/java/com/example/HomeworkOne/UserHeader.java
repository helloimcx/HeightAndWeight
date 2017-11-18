package com.example.HomeworkOne;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bm.library.PhotoView;
import MyInterface.InitView;
import Utils.PopupWindowUtils;
import com.google.gson.Gson;
import com.lqr.imagepicker.ui.ImageGridActivity;
import com.lqr.imagepicker.*;
import com.lqr.imagepicker.view.CropImageView;
import com.lqr.optionitemview.OptionItemView;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.io.IOException;
import java.util.ArrayList;
import Utils.PicassoImageLoader;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import Utils.OssSecretBean;

import static com.example.HomeworkOne.LoginFragment.JSON;

/**
 * Created by mac on 2017/11/3.
 */

public class UserHeader extends Activity implements InitView{
    @Bind(R.id.pv)
    PhotoView header;
    @Bind(R.id.ivToolbarNavigation)
    ImageView goback;
    @Bind(R.id.ibToolbarMore)
    ImageButton more;

    public static Context context;
    private PopupWindow mPopupWindow;
    public static final int REQUEST_IMAGE_PICKER = 1000;
    private ImagePicker imagePicker;
    private View menu;
    private String signature;
    private OSS oss;
    private OptionItemView album;
    private OptionItemView camera;
    private OptionItemView cancel;
    private RequestBody requestBody;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_big_image);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        context = getApplicationContext();
        LayoutInflater factory = LayoutInflater.from(this);
        menu = factory.inflate(R.layout.popup_header, null);
        album = (OptionItemView) menu.findViewById(R.id.choose_from_album);
        camera = (OptionItemView) menu.findViewById(R.id.choose_from_camera);
        cancel = (OptionItemView) menu.findViewById(R.id.header_cancel);
        ButterKnife.bind(this);
        more.setVisibility(View.VISIBLE);
        initHeader();

        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new PicassoImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(1);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    @Override
    public void initListener() {
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserHeader.this.finish();
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initOss();
                showPopupMenu();
            }
        });
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHeader.this, ImageGridActivity.class);
                startActivityForResult(intent,REQUEST_IMAGE_PICKER );
                mPopupWindow.dismiss();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHeader.this, ImageGridActivity.class);
                startActivityForResult(intent,REQUEST_IMAGE_PICKER );
                mPopupWindow.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });
    }

    private void showPopupMenu() {
        mPopupWindow = PopupWindowUtils.getPopupWindowAtLocation(
               menu, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                PopupWindowUtils.makeWindowLight(UserHeader.this);
            }
        });
        PopupWindowUtils.makeWindowDark(UserHeader.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_PICKER:
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    if (data != null) {
                        ArrayList<com.lqr.imagepicker.bean.ImageItem> images = (ArrayList<com.lqr.imagepicker.bean.ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                        if (images != null && images.size() > 0) {
                            com.lqr.imagepicker.bean.ImageItem imageItem = images.get(0);
                            putToOss(imageItem.name,imageItem.path);
                            String url = "http://ht-data.oss-cn-shenzhen.aliyuncs.com/"
                                    +imageItem.name;
                                    //+"?x-oss-process=image/resize,m_fixed,h_50,w_50";
                            setUserHeader(url);
                        }
                    }
                }
        }
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

    private void setUserHeader(final String url){
        //上传用户头像url
        final SharedPreferences sharedPreferences = getSharedPreferences("Session",MODE_PRIVATE);
        int user_id = sharedPreferences.getInt("user_id",0);
        OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject param = new JSONObject();
        try {
            param.put("header", url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(JSON, param.toString());
        Request request = new Request.Builder()
                .url("http://120.78.67.135:8000/android_account/header/"+user_id+"/")
                .addHeader("cookie", MainActivity.sessionid)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UserHeader.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserHeader.this,"您的网络似乎出了小差...",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                UserHeader.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("header",url);
                        editor.apply();
                        Toast.makeText(UserHeader.this,"设置成功！",Toast.LENGTH_SHORT).show();
                        initHeader();
                    }
                });
            }
        });
    }

    private void initHeader(){
        //用户头像
        SharedPreferences sharedPreferences = getSharedPreferences("Session",MODE_PRIVATE);
        String header_str = sharedPreferences.getString("header","null");
        Uri header_uri = Uri.parse(header_str);
        Picasso.with(UserHeader.this).load(header_uri).into(header);
        header.enable();
    }

}
