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
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;


import MyInterface.InitView;
import butterknife.Bind;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

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

		/*
		创建测试记录
		 */
		testButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MyApplication myApplication = (MyApplication) getApplication();
				SharedPreferences share = myApplication.getShare();
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

				JSONObject param = new JSONObject();
				try {
					param.put("height", height);
					param.put("weight", weight);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				OkGo.<String>post(myApplication.getHost() + "/android_health_test/create/")
						.headers(myApplication.header())
						.upJson(param)
						.execute(new StringCallback() {
							@Override
							public void onSuccess(com.lzy.okgo.model.Response<String> response) {
								if (response.code() == 201) {
									Intent intent = new Intent();
									intent.putExtra("content", content);
									intent.putExtra("photo", photo);
									intent.putExtra("weight", weight);
									intent.putExtra("bmi", BMI);
									setResult(TEST_RESULT_CODE, intent);
									finish();
								} else {
									Toasty.error(AcTest.this, "请求错误!").show();
								}
							}

							@Override
							public void onError(com.lzy.okgo.model.Response<String> response) {
								Toasty.error(AcTest.this, "请求错误!").show();
							}
						});

			}
		});

		/*
		返回
		 */
		goback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}
}
