package com.example.HomeworkOne;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class TestFragment extends Fragment {
	private TextView textView;
	private ImageButton testButton;
	private int photo;
	private String content;
	public static final int  REQUEST_TESTDETAIL = 222;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.factivity, container, false);
		textView=(TextView) v.findViewById(R.id.textView1);
		testButton=(ImageButton) v.findViewById(R.id.imageButton1);
		testButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), TestDetail.class);
				startActivityForResult(intent,REQUEST_TESTDETAIL);
			}
		});
		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode){
			case REQUEST_TESTDETAIL:
				if(resultCode == TestDetail.TEST_RESULT_CODE){
					content = data.getStringExtra("content");
					photo = data.getIntExtra("photo",0);
					showResult();
				}
				break;
			default:
				break;
		}
	}

	private void showResult(){
		switch (photo) {
			case 1:
				testButton.setImageResource(R.drawable.thin);
				break;
			case 2:
				testButton.setImageResource(R.drawable.good);
				break;
			case 3:
				testButton.setImageResource(R.drawable.fast);
				break;
			case 4:
				testButton.setImageResource(R.drawable.veryfast);
				break;
			case 5:
				testButton.setImageResource(R.drawable.veryveryfast);
				break;
			case 6:
				testButton.setImageResource(R.drawable.female_thin);
				break;
			case 7:
				testButton.setImageResource(R.drawable.female_good);
				break;
			case 8:
				testButton.setImageResource(R.drawable.female_fat);
				break;
			case 9:
				testButton.setImageResource(R.drawable.female_vfat);
				break;
			case 10:
				testButton.setImageResource(R.drawable.female_vvfat);
				break;
			default:
				break;
		}
		textView.setText(content);
	}
}
