package com.example.HomeworkOne;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import com.bm.library.PhotoView;
import Utils.PopupWindowUtils;

import com.example.HomeworkOne.BaseActivity.BaseActivity;
import com.example.HomeworkOne.globalConfig.MyApplication;
import com.lqr.imagepicker.ui.ImageGridActivity;
import com.lqr.imagepicker.*;
import com.lqr.imagepicker.view.CropImageView;
import com.lqr.optionitemview.OptionItemView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.ArrayList;
import Utils.TimeUtils;


public class UserHeader extends BaseActivity{
    @Bind(R.id.pv)
    PhotoView header;
    @Bind(R.id.ivToolbarNavigation)
    ImageView goback;
    @Bind(R.id.ibToolbarMore)
    ImageButton more;

    public static final String BROADCAST_ACTION = "CHANGE_HEADER";
    private PopupWindow mPopupWindow;
    public static final int REQUEST_IMAGE_PICKER = 1000;
    private View menu;
    private String header_url;
    private OptionItemView album;
    private OptionItemView camera;
    private OptionItemView cancel;
    private MyApplication myApplication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_show_big_image);
        initView();
        initListener();
    }

    @SuppressLint("InflateParams")
    @Override
    public void initView() {
        LayoutInflater factory = LayoutInflater.from(this);
        menu = factory.inflate(R.layout.popup_header, null);
        album = menu.findViewById(R.id.choose_from_album);
        camera = menu.findViewById(R.id.choose_from_camera);
        cancel = menu.findViewById(R.id.header_cancel);
        ButterKnife.bind(this);
        more.setVisibility(View.VISIBLE);
        initHeader();
        myApplication = (MyApplication) getApplication();

        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setMultiMode(false);
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(1);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(500);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(500);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(500);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(500);//保存文件的高度。单位像素
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
                        SharedPreferences sharedPreferences = getSharedPreferences("Session",MODE_PRIVATE);
                        String email = sharedPreferences.getString("email","null");
                        ArrayList<com.lqr.imagepicker.bean.ImageItem> images = (ArrayList<com.lqr.imagepicker.bean.ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                        if (images != null && images.size() > 0) {
                            com.lqr.imagepicker.bean.ImageItem imageItem = images.get(0);
                            //name the header
                            String objectKey = "header"+email+ TimeUtils.getCurrentTime() +".png";
                            myApplication.putToOss(objectKey,imageItem.path);
                            header_url = "http://ht-data.oss-cn-shenzhen.aliyuncs.com/"
                                    +objectKey;
                                    //+"?x-oss-process=image/resize,m_fixed,h_50,w_50";
                            setUserHeader(header_url);

                            //发送广播让其他组件修改头像
                            sendBroadcast();
                        }
                    }
                }
        }
    }


    private void setUserHeader(final String url){
        //上传用户头像url到服务器上
        final MyApplication myApplication = (MyApplication) getApplication();
        String host = myApplication.getHost();

        JSONObject param = new JSONObject();
        try {
            param.put("header", url);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<String>post(host+"/android_account/set-header/")
                .headers(myApplication.header())
                .upJson(param)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        if (response.code() == 200) {
                            //把用户头像信息更新到SharedPreferences
                            SharedPreferences.Editor editor = myApplication.getShare().edit();
                            editor.putString("header",url);
                            editor.apply();
                            showSuccessToast("设置成功");
                            initHeader();
                        }
                        else
                            showErrorToast("请求失败");
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        showErrorToast("网络开小差");
                    }
                });
    }

    private void initHeader(){
        // 用户头像
        SharedPreferences sharedPreferences = getSharedPreferences("Session",MODE_PRIVATE);
        String header_str = sharedPreferences.getString("header","null");
        Uri header_uri = Uri.parse(header_str);
        Picasso.with(UserHeader.this).load(header_uri).into(header);
        header.enable();
    }

    //发送广播通知其他组件修改头像
    private void sendBroadcast(){
        Intent intent = new Intent();
        intent.setAction(BROADCAST_ACTION);
        intent.putExtra("header_url",header_url);
        sendBroadcast(intent);
    }
}
