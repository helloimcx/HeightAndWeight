package com.example.HomeworkOne;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordDetail extends Activity{
	private TextView showName,showSex,showHeight,showWeight,showBMI,showDate;
	private ImageView imageView;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.item);
	showName=(TextView) findViewById(R.id.showname);
	showSex=(TextView) findViewById(R.id.showSex);
	showHeight=(TextView) findViewById(R.id.showheight);
	showWeight=(TextView) findViewById(R.id.showweight);
	showBMI=(TextView) findViewById(R.id.showBMI);
	showDate=(TextView) findViewById(R.id.showDate);
	imageView=(ImageView) findViewById(R.id.resultImage);
	
	Intent intent=getIntent();
	Bundle bundle=intent.getExtras();
	int sex = bundle.getInt("sex");
	
	showName.setText(bundle.getString("name"));
	showHeight.setText(bundle.getDouble("height")+"");
	showWeight.setText(bundle.getDouble("weight")+"");
	showBMI.setText(bundle.getDouble("BMI")+"");
	showDate.setText(bundle.getString("date"));
	showSex.setText((sex==0)?"ÄÐ":"Å®");
	
	double BMI=Double.parseDouble(showBMI.getText()+"");
	
	if(sex==0){
			if(BMI<18.5){
				imageView.setImageResource(R.drawable.thin);
			}
			else if(BMI<=23.9){
				imageView.setImageResource(R.drawable.good);
			}
			else if(BMI<=27){
				imageView.setImageResource(R.drawable.fast);
			}
			else if(BMI<=32){
				imageView.setImageResource(R.drawable.veryfast);
			}
			else{
				imageView.setImageResource(R.drawable.veryveryfast);
			}
	}
	else {
			if(BMI<18.5){
				imageView.setImageResource(R.drawable.female_thin);
			}
			else if(BMI<=23.9){
				imageView.setImageResource(R.drawable.female_good);
			}
			else if(BMI<=27){
				imageView.setImageResource(R.drawable.female_fat);
			}
			else if(BMI<=32){
				imageView.setImageResource(R.drawable.female_vfat);
			}
			else{
				imageView.setImageResource(R.drawable.female_vvfat);
			}
	}
}
}
