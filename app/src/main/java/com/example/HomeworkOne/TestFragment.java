package com.example.HomeworkOne;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import MyInterface.InitView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class TestFragment extends Fragment implements InitView{
	private int photo;
	private String content;
	public static final int  REQUEST_TESTDETAIL = 222;
	@Bind(R.id.image_test)
	ImageView image_test;
	@Bind(R.id.textView1)
	TextView textView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.fm_test, container, false);
		return v;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		ButterKnife.bind(this,view);
		initView();
		initListener();
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
				Picasso.with(getActivity()).load(R.drawable.thin).fit().centerInside().into(image_test);
				break;
			case 2:
				Picasso.with(getActivity()).load(R.drawable.good).fit().centerInside().into(image_test);
				break;
			case 3:
				Picasso.with(getActivity()).load(R.drawable.fast).fit().centerInside().into(image_test);
				break;
			case 4:
				Picasso.with(getActivity()).load(R.drawable.veryfast).fit().centerInside().into(image_test);
				break;
			case 5:
				Picasso.with(getActivity()).load(R.drawable.veryveryfast).fit().centerInside().into(image_test);
				break;
			case 6:
				Picasso.with(getActivity()).load(R.drawable.female_thin).fit().centerInside().into(image_test);
				break;
			case 7:
				Picasso.with(getActivity()).load(R.drawable.female_good).fit().centerInside().into(image_test);
				break;
			case 8:
				Picasso.with(getActivity()).load(R.drawable.female_fat).fit().centerInside().into(image_test);
				break;
			case 9:
				Picasso.with(getActivity()).load(R.drawable.female_vfat).fit().centerInside().into(image_test);
				break;
			case 10:
				Picasso.with(getActivity()).load(R.drawable.female_vvfat).fit().centerInside().into(image_test);
				break;
			default:
				break;
		}
		textView.setText(content);
	}

	@Override
	public void initView() {
		Picasso.with(getActivity()).load(R.drawable.test_button).fit().centerCrop().into(image_test);
	}

	@Override
	public void initListener() {

		image_test.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getActivity(), TestDetail.class);
				startActivityForResult(intent,REQUEST_TESTDETAIL);
			}
		});
	}
}
