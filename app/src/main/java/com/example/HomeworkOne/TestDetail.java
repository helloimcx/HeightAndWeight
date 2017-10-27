package com.example.HomeworkOne;

import java.text.DecimalFormat;
import java.util.Calendar;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import Utils.JsonUserBean;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.HomeworkOne.LoginFragment.JSON;

public class TestDetail extends Fragment {
	private Button button1;
	private EditText editText1;
	private EditText editText2;
	private View v;
	private double height;
	private double Mheight;
	private double weight;
	private double BMI;
	private int post_flag;
	public static Response response;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v=inflater.inflate(R.layout.test_detail, container, false);

		button1=(Button) v.findViewById(R.id.testButton);
		editText1=(EditText) v.findViewById(R.id.height);
		editText2=(EditText) v.findViewById(R.id.weight);
		button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				try{
					height=Double.parseDouble(editText1.getText().toString());
					Mheight=height/100;
					weight=Double.parseDouble(editText2.getText().toString());
					BMI=weight/(Mheight*Mheight);

					SharedPreferences share = getActivity().getSharedPreferences("Session", MODE_PRIVATE);
					String sex = share.getString("sex","null");

					// '1-5' for male , '6-10' for female
					if(sex.equals("M")){
						if(BMI<18.5){
							TestFragment.content="太瘦了！赶紧吃起来!";
							TestFragment.photo=1;
						}
						else if(BMI<=23.9){
							TestFragment.content="你的身材太棒了！请继续保持！";
							TestFragment.photo=2;
						}
						else if(BMI<=27){
							TestFragment.content="有点胖了！多多运动吧！";
							TestFragment.photo=3;
						}
						else if(BMI<=32){
							TestFragment.content="胖子！赶紧减肥吧！";
							TestFragment.photo=4;
						}
						else{
							TestFragment.content="终极胖子！能不能少吃点？";
							TestFragment.photo=5;
						}
					}
					else {
						if(BMI<18.5){
							TestFragment.content="太瘦了！赶紧吃起来!";
							TestFragment.photo=6;
						}
						else if(BMI<=23.9){
							TestFragment.content="你的身材太棒了！请继续保持！";
							TestFragment.photo=7;
						}
						else if(BMI<=27){
							TestFragment.content="有点胖了！多多运动吧！";
							TestFragment.photo=8;
						}
						else if(BMI<=32){
							TestFragment.content="胖妞！赶紧减肥吧！";
							TestFragment.photo=9;
						}
						else{
							TestFragment.content="终极胖妞！能不能少吃点？";
							TestFragment.photo=10;
						}
					}
				}catch(NumberFormatException e){
					Toast.makeText(getActivity(), "请输入正确的身高值和体重值！",
						     Toast.LENGTH_SHORT).show();
				}
				final Handler handler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						Bundle data = msg.getData();
						int status = data.getInt("status");
						//Log.i("status",status+"");
						switch(status){
							case 200:
								switchFragment(new TestFragment());
								break;
							case 400:
								break;
							default:
								break;
						}

					}
				};

				Runnable networkTask = new Runnable() {
					@Override
					public void run() {
						// TODO
						// 在这里进行 http request.网络请求相关操作
						try {
							SharedPreferences share = getActivity().getSharedPreferences("Session", MODE_PRIVATE);
							int user_id = share.getInt("user_id",0);
							OkHttpClient okHttpClient = MainActivity.okHttpClient;
							JSONObject param = new JSONObject();
							param.put("android_account_id", user_id);
							param.put("height", height);
							param.put("weight", weight);
							RequestBody requestBody = RequestBody.create(JSON, param.toString());
							Request request = new Request.Builder()
									.url("http://120.78.67.135:8000/android_health_test/")
									.addHeader("cookie", MainActivity.sessionid)
									.post(requestBody)
									.build();
							response = okHttpClient.newCall(request).execute();
							post_flag = response.code();
						} catch (Exception e) {
							e.printStackTrace();
						}
						Message msg = new Message();
						Bundle data = new Bundle();
						data.putInt("status", post_flag);
						msg.setData(data);
						handler.sendMessage(msg);
					}
				};
				if (!MainActivity.sessionid.equals("null")) {
					new Thread(networkTask).start();
				}
				else{
					switchFragment(new TestFragment());
				}
			}
		});

		return v;
	}

	private void switchFragment(android.support.v4.app.Fragment targetFragment) {
		MainActivity.tab_transfer=targetFragment;
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if (!targetFragment.isAdded()) {
			transaction
					.hide(TestDetail.this)
					.add(R.id.id_content, MainActivity.tab_transfer)
					.commit();
		} else {
			transaction
					.hide(TestDetail.this)
					.show( MainActivity.tab_transfer)
					.commit();
		}

	}

}
