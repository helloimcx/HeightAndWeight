package com.example.HomeworkOne;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.HomeworkOne.globalConfig.MyApplication;
import com.gc.materialdesign.views.ButtonRectangle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import MyInterface.InitView;
import butterknife.Bind;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.HomeworkOne.AcLogin.JSON;

public class AcTest extends Activity implements InitView{
	@Bind(R.id.testButton)
	ButtonRectangle testButton;
	@Bind(R.id.height)
	EditText edit_height;
	@Bind(R.id.weight)
	EditText edit_weight;
	@Bind(R.id.ivToolbarNavigation)
	ImageView goback;

	private double height;
	private double Mheight;
	private double weight;
	private double BMI;
	private String content;
	private int photo;
	public static final int TEST_RESULT_CODE = 223;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.test_detail);
		initView();
		initListener();
	}

	@Override
	public void initView() {
		ButterKnife.bind(this);
	}

	@Override
	public void initListener() {
		testButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SharedPreferences share = getSharedPreferences("Session", MODE_PRIVATE);
				try{
					height = Double.parseDouble(edit_height.getText().toString());
					Mheight = height / 100;
					weight = Double.parseDouble(edit_weight.getText().toString());
				}catch (Exception e){
					Toasty.error(AcTest.this,"请输入正确的身高和体重值！"
							,Toast.LENGTH_SHORT,true).show();
					return;
				}
				if(height<=120||weight<=30||weight>=250){
					Toasty.warning(AcTest.this,"请输入正确的身高和体重值！"
							,Toast.LENGTH_SHORT,true).show();
					return;
				}
				else {
					BMI = weight / (Mheight * Mheight);
					String sex = share.getString("sex", "null");
					// '1-5' for male , '6-10' for female
					if (sex.equals("男")) {
						if (BMI < 18.5) {
							content = "太瘦了!";
							photo = 1;
						} else if (BMI <= 23.9) {
							content = "你的身材太棒了!";
							photo = 2;
						} else if (BMI <= 27) {
							content = "有点胖了!";
							photo = 3;
						} else if (BMI <= 32) {
							content = "赶紧减肥吧！";
							photo = 4;
						} else {
							content = "能不能少吃点？";
							photo = 5;
						}
					} else {
						if (BMI < 18.5) {
							content = "太瘦了!";
							photo = 6;
						} else if (BMI <= 23.9) {
							content = "你的身材太棒了!";
							photo = 7;
						} else if (BMI <= 27) {
							content = "有点胖了！";
							photo = 8;
						} else if (BMI <= 32) {
							content = "赶紧减肥吧！";
							photo = 9;
						} else {
							content = "能不能少吃点？";
							photo = 10;
						}
					}
				}
				int user_id = share.getInt("user_id", 0);
				OkHttpClient okHttpClient = new OkHttpClient();
				JSONObject param = new JSONObject();
				try {
					param.put("android_account_id", user_id);
					param.put("height", height);
					param.put("weight", weight);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				RequestBody requestBody = RequestBody.create(JSON, param.toString());
				MyApplication myApplication = (MyApplication) getApplication();
				String host = myApplication.getHost();
				Request request = new Request.Builder()
						.url(host+"/android_health_test/")
						.addHeader("cookie", MainActivity.sessionid)
						.post(requestBody)
						.build();
				Call call = okHttpClient.newCall(request);
				call.enqueue(new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						AcTest.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(AcTest.this, "您的网络似乎开小差了...",
										Toast.LENGTH_SHORT).show();
							}
						});
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						AcTest.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Intent intent = new Intent();
								intent.putExtra("content",content);
								intent.putExtra("photo",photo);
								intent.putExtra("weight",weight);
								intent.putExtra("bmi",BMI);
								setResult(TEST_RESULT_CODE,intent);
								finish();
							}
						});
					}
				});

			}
		});
		goback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}
}

