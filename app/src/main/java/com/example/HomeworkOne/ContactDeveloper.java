package com.example.HomeworkOne;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.HomeworkOne.BaseActivity.BaseActivity;
import com.example.HomeworkOne.globalConfig.MyApplication;
import com.gc.materialdesign.views.ButtonRectangle;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by mac on 2017/10/29.
 */

public class ContactDeveloper extends BaseActivity {
    @Bind(R.id.contact_title)
    EditText title;
    @Bind(R.id.contact_content)
    EditText content;
    @Bind(R.id.contact_button)
    ButtonRectangle button;
    @Bind(R.id.ivToolbarNavigation)
    ImageView goback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityState();
        setContentView(R.layout.contact_developer);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        super.initView();
        ButterKnife.bind(this);
    }

    @Override
    public void initListener() {
        super.initListener();
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactDeveloper.this.finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = getIntent().getExtras();
                assert bundle != null;
                String title_str = title.getText().toString();
                String content_str = content.getText().toString();
                if (title_str.length() * content_str.length() == 0){
                    showWarningToast("请输入完整的信息");
                    return;
                }
                MyApplication myApplication = (MyApplication) getApplication();
                String host = myApplication.getHost();
                String url = host+"/android_health_test/email/";

                JSONObject param = new JSONObject();
                try {
                    param.put("title", title_str);
                    param.put("content", content_str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                OkGo.<String>post(url)
                        .headers(myApplication.header())
                        .upJson(param)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                                if (response.code() == 200){
                                    title.setText("");
                                    content.setText("");
                                    //showSuccessToast(ContactDeveloper.this, "发送成功，我们会尽快回复！");
                                    showSuccessToast("发送成功，我们会尽快回复");
                                }
                                else
                                    showWarningToast("请求错误");
                            }

                            @Override
                            public void onError(com.lzy.okgo.model.Response<String> response) {
                                showErrorToast("网络开小差了");
                            }
                        });
            }
        });
    }
}
