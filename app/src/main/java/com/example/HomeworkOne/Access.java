package com.example.HomeworkOne;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Button;

public class Access extends Activity{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.access);
		
		new Handler(){
            public void handleMessage(android.os.Message msg) {
            	Intent intent=null;
				intent=new Intent(Access.this,MainActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.slide_in_left,
						android.R.anim.slide_out_right);
            };
        }.sendEmptyMessageDelayed(0, 900);
		
	}

}
