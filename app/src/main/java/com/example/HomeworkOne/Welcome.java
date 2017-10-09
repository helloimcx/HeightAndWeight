package com.example.HomeworkOne;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class Welcome extends Activity{
	protected LinearLayout leftLayout;
	protected LinearLayout rightLayout;
	protected LinearLayout animLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.welcome);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		leftLayout=(LinearLayout) findViewById(R.id.leftLayout);
		rightLayout=(LinearLayout) findViewById(R.id.rightLayout);
		animLayout=(LinearLayout) findViewById(R.id.animLayout);
		animLayout.setBackgroundResource(R.drawable.main_bg);
		
		Animation leftOutAnimation=AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.translate_left);
		Animation rightOutAnimation=AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.translate_right);
		
		leftLayout.setAnimation(leftOutAnimation);
		rightLayout.setAnimation(rightOutAnimation);
		
		leftOutAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				leftLayout.setVisibility(View.GONE);
				rightLayout.setVisibility(View.GONE);
				Intent intent = new Intent();
				intent.setClass(Welcome.this,Access.class);
				startActivity(intent);
				overridePendingTransition(0,0);
				Welcome.this.finish();
			}
		});
		
	}

}
