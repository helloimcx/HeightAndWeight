package com.example.HomeworkOne;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import MyInterface.InitView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import butterknife.Bind;
import butterknife.ButterKnife;
import static com.example.HomeworkOne.AcLogin.JSON;

public class TestDetail extends Activity implements InitView{
	@Bind(R.id.testButton)
	Button button1;
	@Bind(R.id.height)
	EditText editText1;
	@Bind(R.id.weight)
	EditText editText2;
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
		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SharedPreferences share = getSharedPreferences("Session", MODE_PRIVATE);
				try {
					height = Double.parseDouble(editText1.getText().toString());
					Mheight = height / 100;
					weight = Double.parseDouble(editText2.getText().toString());
					BMI = weight / (Mheight * Mheight);
					String sex = share.getString("sex", "null");
					// '1-5' for male , '6-10' for female
					if (sex.equals("男")) {
						if (BMI < 18.5) {
							content = "太瘦了！赶紧吃起来!";
							photo = 1;
						} else if (BMI <= 23.9) {
						    content = "你的身材太棒了！请继续保持！";
							photo = 2;
						} else if (BMI <= 27) {
							content = "有点胖了！多多运动吧！";
							photo = 3;
						} else if (BMI <= 32) {
							content = "胖子！赶紧减肥吧！";
							photo = 4;
						} else {
							content = "终极胖子！能不能少吃点？";
							photo = 5;
						}
					} else {
						if (BMI < 18.5) {
							content = "太瘦了！赶紧吃起来!";
							photo = 6;
						} else if (BMI <= 23.9) {
							content = "你的身材太棒了！请继续保持！";
							photo = 7;
						} else if (BMI <= 27) {
						    content = "有点胖了！多多运动吧！";
							photo = 8;
						} else if (BMI <= 32) {
							content = "胖妞！赶紧减肥吧！";
							photo = 9;
						} else {
							content = "终极胖妞！能不能少吃点？";
							photo = 10;
						}
					}
				} catch (NumberFormatException e) {
					Toast.makeText(TestDetail.this, "请输入正确的身高值和体重值！",
							Toast.LENGTH_SHORT).show();
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
				Request request = new Request.Builder()
						.url("http://120.78.67.135:8000/android_health_test/")
						.addHeader("cookie", MainActivity.sessionid)
						.post(requestBody)
						.build();
				Call call = okHttpClient.newCall(request);
				call.enqueue(new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						TestDetail.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(TestDetail.this, "您的网络似乎开小差了...",
										Toast.LENGTH_SHORT).show();
							}
						});
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						TestDetail.this.runOnUiThread(new Runnable() {
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

