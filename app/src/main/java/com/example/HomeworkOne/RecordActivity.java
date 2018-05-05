package com.example.HomeworkOne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.example.HomeworkOne.BaseActivity.BaseActivity;
import com.example.HomeworkOne.globalConfig.MyApplication;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import Utils.JsonRecordBean;


public class RecordActivity extends BaseActivity {
	@Bind(R.id.record_list)
	ListView listView;
	@Bind(R.id.ivToolbarNavigation)
	ImageView goback;

	private SimpleAdapter simpleAdapter;
	private List<Map<String, Object>> dataList;
	private int count_record;
	private int result_label;
	private String host;
	private MyApplication myApplication;
	private ArrayList<JsonRecordBean.RecordBean> records;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.record_list);
		initView();
		initListener();
	}

	private void getData() {
		OkGo.<String>get(host + "/android_health_test/user/")
				.headers(myApplication.header())
				.execute(new StringCallback() {
					@Override
					public void onSuccess(com.lzy.okgo.model.Response<String> response) {
						if (response.code() == 200) {
							Gson gson = new Gson();
							try {
								JsonRecordBean jsonRecordBean = gson.fromJson(response.body(),
										JsonRecordBean.class);
								count_record = jsonRecordBean.get_count_record();
								records = jsonRecordBean.get_records();
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (count_record == 0) {
								showInfoToast("还没有任何记录哦");
								hideLoading();
							} else {
								for (int i = count_record - 1; i >= 0; i--) {
									String result;
									double height = records.get(i).get_height() / 100;
									double weight = records.get(i).get_weight();
									double BMI = weight / (height * height);
									if (BMI < 18.5) {
										result = "偏瘦";
										result_label = R.mipmap.thin_label;
									} else if (BMI <= 23.9) {
										result = "正常";
										result_label = R.mipmap.fitness;
									} else if (BMI <= 27) {
										result = "微胖";
										result_label = R.mipmap.fat_label;
									} else if (BMI <= 32) {
										result = "偏胖";
										result_label = R.mipmap.fat_label;
									} else {
										result = "肥胖";
										result_label = R.mipmap.fat_label;
									}
									Map<String, Object> map = new HashMap<>();
									map.put("record_id", records.get(i).get_record_id());
									map.put("height", height * 100);
									map.put("weight", weight);
									map.put("BMI", BMI);
									map.put("date", records.get(i).get_record_time().substring(0,10)+ ' '+
											records.get(i).get_record_time().substring(11,19));
									map.put("result", result);
									map.put("result_label",result_label);
									dataList.add(map);
								}
								simpleAdapter = new SimpleAdapter(RecordActivity.this, dataList, R.layout.title,
										new String[]{"result", "result_label", "date"}, new int[]{R.id.show_result_title,
										R.id.result_label,
										R.id.show_date_title});
								listView.setAdapter(simpleAdapter);
							}
						}
						else
							showWarningToast("请求错误");
						hideLoading();
					}

					@Override
					public void onError(com.lzy.okgo.model.Response<String> response) {
						showErrorToast("网络开小差");
						hideLoading();
					}
				});
	}

	@Override
	public void initView() {
		ButterKnife.bind(this);
		showLoading();
		dataList = new ArrayList<>();
		myApplication = (MyApplication) getApplication();
		host = myApplication.getHost();
		getData();
	}

	@Override
	public void initListener() {
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
										   final int arg2, long arg3) {
				ListView listView = (ListView) arg0;
				final HashMap<String, Object> map = (HashMap<String, Object>) listView.getItemAtPosition(arg2);
				AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
				builder.setCancelable(false);
				builder.setTitle("删除记录").setMessage("确定要删除这条记录吗？")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface Arg, int arg) {
								final int record_id = (Integer) map.get("record_id");

								OkGo.<String>delete(host + "/android_health_test/one/"
										+ record_id + "/")
										.headers(myApplication.header())
										.execute(new StringCallback() {
											@Override
											public void onSuccess(com.lzy.okgo.model.Response<String> response) {
												if (response.code() == 200) {
													showSuccessToast("删除成功");
													dataList.remove(map);
													simpleAdapter.notifyDataSetChanged();
												}
												else
													showErrorToast("删除失败");
											}

											@Override
											public void onError(com.lzy.okgo.model.Response<String> response) {
												showErrorToast("网络开小差");
											}
										});
							}
						})
						.setNegativeButton("取消", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
							}
						});
				builder.setCancelable(true);
				AlertDialog dialog=builder.create();
				dialog.show();
				return true;
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				ListView listView = (ListView) arg0;
				HashMap<String, Object> map = (HashMap<String, Object>) listView.getItemAtPosition(arg2);
				Intent intent = new Intent(RecordActivity.this,RecordDetail.class);
				intent.putExtra("height", (Double) map.get("height"));
				intent.putExtra("weight", (Double) map.get("weight"));
				intent.putExtra("bmi", map.get("BMI").toString().substring(0,4));
				intent.putExtra("date", (String) map.get("date"));
				intent.putExtra("result", (String) map.get("result"));
				startActivity(intent);
			}
		});
		goback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}
}
