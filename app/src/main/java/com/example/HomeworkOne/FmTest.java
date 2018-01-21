package com.example.HomeworkOne;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqr.optionitemview.OptionItemView;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

import MyInterface.InitView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class FmTest extends Fragment implements InitView{
	private int photo;
	private double weight;
	private double bmi;
	private String content;
	public static final int  REQUEST_TESTDETAIL = 222;
	@Bind(R.id.image_test)
	ImageView image_test;
	@Bind(R.id.textView1)
	TextView textView;
	@Bind(R.id.compare_with_last_record)
	OptionItemView compare_last;
	@Bind(R.id.show_bmi)
	OptionItemView show_bmi;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fm_test, container, false);
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
				if(resultCode == AcTest.TEST_RESULT_CODE){
					compare_last.setVisibility(View.VISIBLE);
					show_bmi.setVisibility(View.VISIBLE);
					content = data.getStringExtra("content");
					photo = data.getIntExtra("photo",0);
					setWeight(data.getDoubleExtra("weight",0));
					setBmi(data.getDoubleExtra("bmi",0));
					showResult();
				}
				break;
			default:
				break;
		}
	}

	private void showResult(){
		/**
		* @Method: showResult
		* @Params: []
		* @Return: void
		* @Description: show the test result
		*/

		switch (photo) {
			case 1:
				Picasso.with(getActivity()).load(R.drawable.thin).fit().centerInside().into(image_test);
				break;
			case 2:
				Picasso.with(getActivity()).load(R.drawable.good).fit().centerInside().into(image_test);
				break;
			case 3:
				Picasso.with(getActivity()).load(R.drawable.fat).fit().centerInside().into(image_test);
				break;
			case 4:
				Picasso.with(getActivity()).load(R.drawable.veryfat).fit().centerInside().into(image_test);
				break;
			case 5:
				Picasso.with(getActivity()).load(R.drawable.veryveryfat).fit().centerInside().into(image_test);
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
		BigDecimal bg = new BigDecimal(getBmi());
		double bmi = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		show_bmi.setRightText(bmi+"");
	}

	@Override
	public void initView() {
		Picasso.with(getActivity()).load(R.drawable.cover_man).fit().centerCrop().into(image_test);
	}

	@Override
	public void initListener() {

		// Jump to AcTest
		image_test.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getActivity(), AcTest.class);
				startActivityForResult(intent,REQUEST_TESTDETAIL);
			}
		});

		// Jump to AcCompareLast
		compare_last.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getActivity(),AcCompareLast.class);
				intent.putExtra("weight",getWeight());
				startActivity(intent);
			}
		});
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getBmi() {
		return bmi;
	}

	public void setBmi(double bmi) {
		this.bmi = bmi;
	}
}
