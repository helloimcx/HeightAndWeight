package com.example.HomeworkOne;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class TestFragment extends Fragment {
	private TextView textView;
	private ImageButton testButton;
	public static int photo;
	public static String content = "µã»÷Í¼Æ¬¿ªÊ¼²âÊÔ";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.factivity, container, false);
		textView=(TextView) v.findViewById(R.id.textView1);
		testButton=(ImageButton) v.findViewById(R.id.imageButton1);
		init();
		testButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				switchFragment(new TestDetail());
			}
		});
		return v;
	}

	private void init(){
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

	private void switchFragment(android.support.v4.app.Fragment targetFragment) {
		MainActivity.tab_transfer=targetFragment;
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if (!targetFragment.isAdded()) {
			transaction
					.hide(TestFragment.this)
					.add(R.id.id_content, MainActivity.tab_transfer)
					.commit();
		} else {
			transaction
					.hide(TestFragment.this)
					.show( MainActivity.tab_transfer)
					.commit();
		}

	}
}
