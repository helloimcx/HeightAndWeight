package com.example.HomeworkOne;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.gson.Gson;
import MyInterface.InitView;
import Utils.JsonRecordBean;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecordActivity extends Activity implements InitView {
	@Bind(R.id.record_list)
	ListView listView;
	private SimpleAdapter simpleAdapter;
	private List<Map<String, Object>> dataList;
	private int user_id;
	private int count_record;
	private ArrayList<JsonRecordBean.RecordBean> records;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_list);
		initView();
		initListener();
	}

	private void getData() {
		SharedPreferences share = getSharedPreferences("Session", MODE_PRIVATE);
		user_id = share.getInt("user_id", 0);
		OkHttpClient okHttpClient = new OkHttpClient();
		Request request = new Request.Builder().url("http://120.78.67.135:8000/android_health_test/record/user/"
				+ user_id + "/")
				.addHeader("cookie",MainActivity.sessionid)
				.build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				RecordActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(RecordActivity.this,
								"您的网络似乎开了小差...", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				RecordActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Gson gson = new Gson();
						try {
							JsonRecordBean jsonRecordBean = gson.fromJson(response.body().string(),
									JsonRecordBean.class);
							count_record = jsonRecordBean.get_count_record();
							records = jsonRecordBean.get_records();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (count_record == 0) {
							AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
							builder.setMessage("还没有任何记录哦!");
							builder.setCancelable(true);
							AlertDialog dialog = builder.create();
							dialog.show();
						} else {
							for (int i = count_record - 1; i >= 0; i--) {
								String result = "";
								double height = records.get(i).get_height() / 100;
								double weight = records.get(i).get_weight();
								double BMI = weight / (height * height);
								if (BMI < 18.5) {
									result = "偏瘦";
								} else if (BMI <= 23.9) {
									result = "正常";
								} else if (BMI <= 27) {
									result = "微胖";
								} else if (BMI <= 32) {
									result = "偏胖";
								} else {
									result = "肥胖";
								}
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("record_id", records.get(i).get_record_id());
								map.put("height", height * 100);
								map.put("weight", weight);
								map.put("BMI", BMI);
								map.put("date", records.get(i).get_record_time());
								map.put("result", result);
								dataList.add(map);
							}
							simpleAdapter = new SimpleAdapter(RecordActivity.this, dataList, R.layout.title,
									new String[]{"result", "date"}, new int[]{R.id.show_result_title,
									R.id.show_date_title});
							listView.setAdapter(simpleAdapter);
						}
					}
				});
			}
		});

	}

	@Override
	public void initView() {
		ButterKnife.bind(this);
		dataList = new ArrayList<Map<String, Object>>();
		getData();
	}

	@Override
	public void initListener() {
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
										   int arg2, long arg3) {
				ListView listView = (ListView) arg0;
				final HashMap<String, Object> map = (HashMap<String, Object>) listView.getItemAtPosition(arg2);
				AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
				builder.setCancelable(false);
				builder.setTitle("删除记录").setMessage("确定要删除这条记录吗？")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface Arg, int arg) {
								// TODO Auto-generated method stub
								final int record_id = (Integer) map.get("record_id");
								OkHttpClient okHttpClient = new OkHttpClient();
								Request request = new Request.Builder().url("http://120.78.67.135:8000/" +
										"android_health_test/record/"
										+ record_id + "/").addHeader("cookie", MainActivity.sessionid)
										.delete().build();
								Call call = okHttpClient.newCall(request);
								call.enqueue(new Callback() {
									@Override
									public void onFailure(Call call, IOException e) {
										RecordActivity.this.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												Toast.makeText(RecordActivity.this,
														"您的网络似乎开了小差...", Toast.LENGTH_SHORT).show();
											}
										});
									}

									@Override
									public void onResponse(Call call, Response response) throws IOException {
										RecordActivity.this.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												arg1.setVisibility(View.GONE);
												Toast.makeText(RecordActivity.this,
														"删除成功！", Toast.LENGTH_SHORT).show();
											}
										});
									}
								});
							}
						})
						.setNegativeButton("取消", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
				ListView listView = (ListView) arg0;
				HashMap<String, Object> map = (HashMap<String, Object>) listView.getItemAtPosition(arg2);
				RecordDetail.height = (Double) map.get("height");
				RecordDetail.weight = (Double) map.get("weight");
				RecordDetail.BMI = (Double) map.get("BMI");
				RecordDetail.time = (String) map.get("date");
				RecordDetail.result = (String) map.get("result");
			}
		});
	}
}
