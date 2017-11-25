package com.example.HomeworkOne;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

import MyInterface.InitView;

public class Access extends Activity implements InitView{
	@Bind(R.id.img_welcome)
	ImageView img_welcome;

	static Access instance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.access);
		initView();
		initListener();
	}

	//转到哪一个Activity
	private void whereToGo(){
		//尝试获取sessionid
		SharedPreferences sharedPreferences = getSharedPreferences("Session",MODE_PRIVATE);
		String sessionid = sharedPreferences.getString("sessionid","null");

		//若用户已经登录，则进入MainActivity
		if(!sessionid.equals("null")){
			Intent intent = new Intent(Access.this, MainActivity.class);
			startActivity(intent);
		}
		//否则转到登陆
		else {
			Intent intent = new Intent(Access.this, AcLogin.class);
			startActivity(intent);
		}

	}

	@Override
	public void initView() {
		ButterKnife.bind(this);
		Picasso.with(this).load(R.drawable.pirlo).fit().into(img_welcome);

		//方便其他Activity关闭当前Activity
		instance = this;

		new Handler(){
			public void handleMessage(android.os.Message msg) {
				whereToGo();
			};
		}.sendEmptyMessageDelayed(0, 1900);
	}

	@Override
	public void initListener() {
		img_welcome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				whereToGo();
			}
		});
	}
}
