package com.example.HomeworkOne;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.HomeworkOne.globalConfig.MyApplication;
import com.gc.materialdesign.views.ButtonRectangle;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import java.io.IOException;
/**
 * Created by mac on 2017/10/29.
 */

public class ContactDeveloper extends Activity {
    @Bind(R.id.contact_title)
    EditText title;
    @Bind(R.id.contact_content)
    EditText content;
    @Bind(R.id.contact_button)
    ButtonRectangle button;
    @Bind(R.id.ivToolbarNavigation)
    ImageView goback;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private RequestBody requestBody;
    private int response_code;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.contact_developer);
        ButterKnife.bind(this);
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
                int user_id = bundle.getInt("user_id");
                String sessionid = bundle.getString("sessionid");
                String title_str = title.getText().toString();
                String content_str = content.getText().toString();
                MyApplication myApplication = (MyApplication) getApplication();
                String host = myApplication.getHost();
                String url = host+"/android_health_test/email_developer/"+user_id+"/";
                OkHttpClient okHttpClient = new OkHttpClient();
                try{
                    JSONObject param = new JSONObject();
                    param.put("title", title_str);
                    param.put("content", content_str);
                    requestBody = RequestBody.create(JSON, param.toString());
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("cookie", sessionid)
                            .post(requestBody)
                            .build();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            ContactDeveloper.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ContactDeveloper.this,"网络似乎开了小差...",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            ContactDeveloper.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    title.setText("");
                                    content.setText("");
                                    Toast.makeText(ContactDeveloper.this,"感谢您的建议，我们将尽快回复！",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
