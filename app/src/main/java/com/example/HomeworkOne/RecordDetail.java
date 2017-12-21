package com.example.HomeworkOne;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import MyInterface.InitView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class RecordDetail extends Activity implements InitView{
	private EditText record_height, record_weight, record_bmi, record_result, record_time;
	private ImageView imageView;
	public static double height, weight, BMI;
	public static String result, time;
	@Bind(R.id.ivToolbarNavigation)
	ImageView goback;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_detail);
		initView();
		initListener();
	}


	@Override
	public void initView() {
		ButterKnife.bind(this);
		record_height = (EditText) findViewById(R.id.record_height);
		record_weight = (EditText) findViewById(R.id.record_weight);
		record_bmi = (EditText) findViewById(R.id.record_bmi);
		record_result = (EditText) findViewById(R.id.record_result);
		record_time = (EditText) findViewById(R.id.record_time);
		imageView = (ImageView) findViewById(R.id.record_image);

		String height_str = height + "  cm";
		String weight_str = weight + "  kg";
		String bmi_str = BMI + "";
		record_height.setText(height_str);
		record_weight.setText(weight_str);
		record_bmi.setText(bmi_str);
		record_result.setText(result);
		record_time.setText(time);

		SharedPreferences share = getSharedPreferences("Session", MODE_PRIVATE);
		String sex = share.getString("sex", "null");
		if (sex.equals("ÄÐ")) {
			if (BMI < 18.5) {
				imageView.setImageResource(R.drawable.thin);
			} else if (BMI <= 23.9) {
				imageView.setImageResource(R.drawable.good);
			} else if (BMI <= 27) {
				imageView.setImageResource(R.drawable.fat);
			} else if (BMI <= 32) {
				imageView.setImageResource(R.drawable.veryfat);
			} else {
				imageView.setImageResource(R.drawable.veryveryfat);
			}
		} else {
			if (BMI < 18.5) {
				imageView.setImageResource(R.drawable.female_thin);
			} else if (BMI <= 23.9) {
				imageView.setImageResource(R.drawable.female_good);
			} else if (BMI <= 27) {
				imageView.setImageResource(R.drawable.female_fat);
			} else if (BMI <= 32) {
				imageView.setImageResource(R.drawable.female_vfat);
			} else {
				imageView.setImageResource(R.drawable.female_vvfat);
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

