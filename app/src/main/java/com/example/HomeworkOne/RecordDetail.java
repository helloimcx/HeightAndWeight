package com.example.HomeworkOne;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import MyInterface.InitView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class RecordDetail extends Activity implements InitView{
	@Bind(R.id.ivToolbarNavigation)
	ImageView goback;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.record_detail);
		initView();
		initListener();
	}


	@Override
	public void initView() {
		ButterKnife.bind(this);
		TextView record_height = findViewById(R.id.record_height);
		TextView record_weight = findViewById(R.id.record_weight);
		TextView record_bmi = findViewById(R.id.record_bmi);
		TextView record_result = findViewById(R.id.record_result);
		TextView record_time = findViewById(R.id.record_time);
		ImageView imageView = findViewById(R.id.record_image);

		Intent data = getIntent();
		double height = data.getDoubleExtra("height", 0);
		double weight = data.getDoubleExtra("weight", 0);
		Double BMI = Double.valueOf(data.getStringExtra("bmi"));
		String result = data.getStringExtra("result");
		String date = data.getStringExtra("date");

		String height_str = height + "  cm";
		String weight_str = weight + "  kg";
		String bmi_str = BMI + "";
		record_height.setText(height_str);
		record_weight.setText(weight_str);
		record_bmi.setText(bmi_str);
		record_result.setText(result);
		record_time.setText(date);

		SharedPreferences share = getSharedPreferences("Session", MODE_PRIVATE);
		String sex = share.getString("sex", "null");
		if (sex.equals("ÄÐ")) {
			if (BMI < 18.5) {
				Picasso.with(this).load(R.drawable.thin).fit().centerInside().into(imageView);
			} else if (BMI <= 23.9) {
				Picasso.with(this).load(R.drawable.good).fit().centerInside().into(imageView);
			} else if (BMI <= 27) {
				Picasso.with(this).load(R.drawable.fat).fit().centerInside().into(imageView);
			} else if (BMI <= 32) {
				Picasso.with(this).load(R.drawable.veryfat).fit().centerInside().into(imageView);
			} else {
				Picasso.with(this).load(R.drawable.veryveryfat).fit().centerInside().into(imageView);
			}
		} else {
			if (BMI < 18.5) {
				Picasso.with(this).load(R.drawable.female_thin).fit().centerInside().into(imageView);
			} else if (BMI <= 23.9) {
				Picasso.with(this).load(R.drawable.female_good).fit().centerInside().into(imageView);
			} else if (BMI <= 27) {
				Picasso.with(this).load(R.drawable.female_fat).fit().centerInside().into(imageView);
			} else if (BMI <= 32) {
				Picasso.with(this).load(R.drawable.female_vfat).fit().centerInside().into(imageView);
			} else {
				Picasso.with(this).load(R.drawable.female_vvfat).fit().centerInside().into(imageView);
			}
		}
	}

	@Override
	public void initListener() {
		goback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}
}

