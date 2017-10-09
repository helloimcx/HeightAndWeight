package com.example.HomeworkOne;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.R.integer;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SActivity extends Activity{
	private Button button1;
	private EditText editText1;
	private EditText editText2;
	private EditText input_name;
	private String content="你好";
	private boolean sex=true;                  //'false' represents male,'true' represents female  
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sactivity);
		button1=(Button) findViewById(R.id.button1);
		editText1=(EditText) findViewById(R.id.editText1);
		editText2=(EditText) findViewById(R.id.editText2);
		input_name=(EditText) findViewById(R.id.input_name);
		button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent data=new Intent();
				try{
				String user_name=input_name.getText().toString();
				double height=Double.parseDouble(editText1.getText().toString());
				double Mheight=height/100;
				double weight=Double.parseDouble(editText2.getText().toString());
				double BMI=weight/(Mheight*Mheight);
				RadioButton male=(RadioButton) findViewById(R.id.btnMan);
				if(male.isChecked()){
				     sex=false;
				}
				int sex_flag=(sex)?1:0;
			    int photo=0;                           // '1-5' for male , '6-10' for female
			    if(sex==false){
				    if(BMI<18.5){
						content="太瘦了！赶紧吃起来!";
						photo=1;
					}
					else if(BMI<=23.9){
						content="你的身材太棒了！请继续保持！";
						photo=2;
					}
					else if(BMI<=27){
						content="有点胖了！多多运动吧！";
						photo=3;
					}
					else if(BMI<=32){
						content="胖子！赶紧减肥吧！";
						photo=4;
					}
					else{
						content="终极胖子！能不能少吃点？";
						photo=5;
					}
			    }
			    else {
			    	if(BMI<18.5){
						content="太瘦了！赶紧吃起来!";
						photo=6;
					}
					else if(BMI<=23.9){
						content="你的身材太棒了！请继续保持！";
						photo=7;
					}
					else if(BMI<=27){
						content="有点胖了！多多运动吧！";
						photo=8;
					}
					else if(BMI<=32){
						content="胖妞！赶紧减肥吧！";
						photo=9;
					}
					else{
						content="终极胖妞！能不能少吃点？";
						photo=10;
					}
				}
				data.putExtra("data",content);
				data.putExtra("photo", photo);
				setResult(2, data);
                
				Calendar c = Calendar.getInstance();
				int year = c.get(Calendar.YEAR);  
			    int month= c.get(Calendar.MONTH)+1;  
			    int day = c.get(Calendar.DAY_OF_MONTH);  
				
				SQLiteDatabase db = openOrCreateDatabase("user.db", MODE_PRIVATE, null);
				db.execSQL("create table if not exists records(_id integer primary key autoincrement,name text not null,Height double not null,"
						+ "Weight double not null,BMI double not null,currDate date not null,sex integer)");
				ContentValues values = new ContentValues();
				values.put("name", user_name);
				values.put("Height", height);
				values.put("Weight", weight);
				DecimalFormat df = new DecimalFormat("#.0");
				values.put("BMI", df.format(BMI));
				values.put("currDate", year+"-"+month+"-"+day);
				values.put("sex", sex_flag);
				db.insert("records", null, values);
				values.clear();
				
				finish();
				}catch(NumberFormatException e){
					Toast.makeText(getApplicationContext(), "请输入正确的身高值和体重值！",
						     Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		
		
	}
}