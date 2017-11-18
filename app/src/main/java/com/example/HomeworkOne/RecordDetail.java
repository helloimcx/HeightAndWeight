package com.example.HomeworkOne;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import static android.content.Context.MODE_PRIVATE;

public class RecordDetail extends android.support.v4.app.Fragment {
	private EditText record_height, record_weight, record_bmi, record_result, record_time;
	private ImageView imageView;
	private Button goBack;
	public static double height, weight, BMI;
	public static String result, time;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.record_detail, container, false);

		record_height = (EditText) view.findViewById(R.id.record_height);
		record_weight = (EditText) view.findViewById(R.id.record_weight);
		record_bmi = (EditText) view.findViewById(R.id.record_bmi);
		record_result = (EditText) view.findViewById(R.id.record_result);
		record_time = (EditText) view.findViewById(R.id.record_time);
		imageView = (ImageView) view.findViewById(R.id.record_image);
		goBack = (Button) view.findViewById(R.id.record_go_back);

		String height_str = height + "  cm";
		String weight_str = weight + "  kg";
		String bmi_str = BMI + "";
		record_height.setText(height_str);
		record_weight.setText(weight_str);
		record_bmi.setText(bmi_str);
		record_result.setText(result);
		record_time.setText(time);

		SharedPreferences share = getActivity().getSharedPreferences("Session", MODE_PRIVATE);
		String sex = share.getString("sex", "null");
		if (sex.equals("M")) {
			if (BMI < 18.5) {
				imageView.setImageResource(R.drawable.thin);
			} else if (BMI <= 23.9) {
				imageView.setImageResource(R.drawable.good);
			} else if (BMI <= 27) {
				imageView.setImageResource(R.drawable.fast);
			} else if (BMI <= 32) {
				imageView.setImageResource(R.drawable.veryfast);
			} else {
				imageView.setImageResource(R.drawable.veryveryfast);
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

		goBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});

		return view;
	}

	private void switchFragment(android.support.v4.app.Fragment targetFragment) {
		MainActivity.tab_transfer = targetFragment;
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if (!targetFragment.isAdded()) {
			transaction
					.hide(RecordDetail.this)
					.add(R.id.id_content, MainActivity.tab_transfer)
					.commit();
		} else {
			transaction
					.hide(RecordDetail.this)
					.show(MainActivity.tab_transfer)
					.commit();
		}
	}
}

