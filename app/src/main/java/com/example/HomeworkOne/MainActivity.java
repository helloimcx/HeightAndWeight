package com.example.HomeworkOne;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends FragmentActivity implements OnClickListener{
	//底部的4个导航控件
	private LinearLayout mTabTest;
	private LinearLayout mTabDiscover;
	private LinearLayout mTabAccount;
	//底部4个导航控件中的图片按钮
	private ImageButton mImgTest;
	private ImageButton mImgDiscover;
	private ImageButton mImgAccount;
	//初始化4个Fragment
	private Fragment tab01;
	private Fragment tab02;
	private Fragment tab03;
	public static Fragment tab_transfer;
	public static OkHttpClient okHttpClient;
	public static String sessionid;

	@Bind(R.id.ivToolbarNavigation)
	ImageView goback;
	@Bind(R.id.id_text_test)
	TextView test_tv;
	@Bind(R.id.id_text_discover)
	TextView discover_tv;
	@Bind(R.id.id_text_account)
	TextView account_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initView();//初始化所有的view
		initEvents();
		setSelect(0);//默认显示测试界面
		createHttpClient();
		SharedPreferences share = getSharedPreferences("Session", MODE_PRIVATE);
		sessionid = share.getString("sessionid","null");
		if(sessionid.equals("null")){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(true);
			builder.setTitle("登录").setMessage("登陆后可以在云端保存您的测试记录哦，立即登录？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface Arg, int arg) {
							// TODO Auto-generated method stub
							resetImgAndText();
							setSelect(3);
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
				}
			});
			builder.create().show();
		}
	}

	private void initEvents() {
		mTabTest.setOnClickListener(this);
		mTabDiscover.setOnClickListener(this);
		mTabAccount.setOnClickListener(this);
	}

	private void initView() {
		ButterKnife.bind(this);
		goback.setVisibility(View.GONE);
		mTabTest = (LinearLayout)findViewById(R.id.id_tab_test);
		mTabDiscover = (LinearLayout)findViewById(R.id.id_tab_discover);
		mTabAccount = (LinearLayout)findViewById(R.id.id_tab_account);
		mImgTest = (ImageButton)findViewById(R.id.id_tab_test_img);
		mImgDiscover = (ImageButton)findViewById(R.id.id_tab_discover_img);
		mImgAccount = (ImageButton)findViewById(R.id.id_tab_account_img);
	}

	@Override
	public void onClick(View v) {
		resetImgAndText();
		switch (v.getId()) {
		case R.id.id_tab_test://当点击测试按钮时，切换图片为亮色，切换fragment为微信聊天界面
			setSelect(0);
			break;
		case R.id.id_tab_discover:
			setSelect(1);
			break;
		case R.id.id_tab_account:
			setSelect(2);
			break;
		default:
			break;
		}
	}

	/*
	 * 将图片设置为亮色的；切换显示内容的fragment
	 * */
	private void setSelect(int i) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();//创建一个事务
		hideFragment(transaction);//我们先把所有的Fragment隐藏了，然后下面再开始处理具体要显示的Fragment
		switch (i) {
		case 0:
			if (tab01 == null) {
				tab01 = new TestFragment();
				transaction.add(R.id.id_content, tab01);
			}else {
				transaction.show(tab01);
			}
			mImgTest.setImageResource(R.drawable.test_pressed);
			test_tv.setTextColor(getResources().getColor(R.color.blue));
			break;
		case 1:
			MyWebFragment.myurl="http://www.baidu.com";
			tab02 = new MyWebFragment();
			transaction.add(R.id.id_content, tab02);
			transaction.show(tab02);
			mImgDiscover.setImageResource(R.drawable.discover_pressed);
			discover_tv.setTextColor(getResources().getColor(R.color.blue));
			break;
		case 2:
			if (!sessionid.equals("null")) {
				tab03 = new AccountFragment();
			}
			else {
				tab03 = new LoginFragment();
			}
			transaction.add(R.id.id_content,tab03);
			mImgAccount.setImageResource(R.drawable.account_pressed);
			account_tv.setTextColor(getResources().getColor(R.color.blue));
			break;
		default:
			break;
		}
		transaction.commit();
	}

	/*
	 * 隐藏所有的Fragment
	 * */
	private void hideFragment(FragmentTransaction transaction) {
		if (tab01 != null) {
			transaction.hide(tab01);
		}
		if (tab02 != null) {
			transaction.hide(tab02);
		}
		if (tab03 != null) {
			transaction.hide(tab03);
		}
		if (tab_transfer != null) {
			transaction.hide(tab_transfer);
		}
	}

	private void resetImgAndText() {
		mImgTest.setImageResource(R.drawable.test_normal);
		mImgDiscover.setImageResource(R.drawable.discover_normal);
		mImgAccount.setImageResource(R.drawable.account_normal);
		test_tv.setTextColor(getResources().getColor(R.color.gray0));
		discover_tv.setTextColor(getResources().getColor(R.color.gray0));
		account_tv.setTextColor(getResources().getColor(R.color.gray0));
	}



		 void  createHttpClient() {
			okHttpClient = new OkHttpClient.Builder().cookieJar(new CookieJar() {
				private final HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();
				@Override
				public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
					cookieStore.put(url.host(), cookies);
				}

				@Override
				public List<Cookie> loadForRequest(HttpUrl url) {
					List<Cookie> cookies = cookieStore.get(url.host());
					return cookies != null ? cookies : new ArrayList<Cookie>();
				}
			}).connectTimeout(5, TimeUnit.SECONDS)
					.readTimeout(10, TimeUnit.SECONDS)
					.build();
		}
}