package com.example.HomeworkOne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.google.gson.Gson;

import Utils.HttpRequest;
import Utils.JsonRecordBean;

import static android.content.Context.MODE_PRIVATE;

public class RecordFragment extends Fragment {
	private ListView listView;
	private SimpleAdapter simpleAdapter;
	private List<Map<String, Object>>dataList;
	private int user_id;
	private int count_record;
	private ArrayList<JsonRecordBean.RecordBean> records;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//引入我们的布局
		View view=inflater.inflate(R.layout.record_list, container, false);
		dataList=new ArrayList<Map<String, Object>>();
		listView=(ListView) view.findViewById(R.id.record_list);
		getData();

	    listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
				ListView listView = (ListView)arg0;
				final HashMap<String, Object> map = (HashMap<String, Object>) listView.getItemAtPosition(arg2);
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setCancelable(false);
		        builder.setTitle("删除记录").setMessage("确定要删除这条记录吗？")
		        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface Arg, int arg) {
						// TODO Auto-generated method stub
						final int record_id = (Integer) map.get("record_id");

						final Handler handler = new Handler() {
							@Override
							public void handleMessage(Message msg) {
								super.handleMessage(msg);
								Bundle data = msg.getData();
								int status = data.getInt("status");
								AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
								if(status==200){
									arg1.setVisibility(View.GONE);
									builder.setMessage("删除成功!");
								}
								else {
									builder.setMessage("请求超时!");
								}
								builder.setCancelable(true);
								AlertDialog dialog=builder.create();
								dialog.show();
							}
						};

						Runnable networkTask = new Runnable() {
							@Override
							public void run() {
								// TODO
								try {
									HttpRequest.delete("http://120.78.67.135:8000/android_health_test/record/"
											+ record_id + "/");
								} catch (Exception e) {
									e.printStackTrace();
								}
								Message msg = new Message();
								Bundle data = new Bundle();
								data.putInt("status", HttpRequest.response_code);
								msg.setData(data);
								handler.sendMessage(msg);
							}
						};
						new Thread(networkTask).start();
					}
		        }).setNegativeButton("取消", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
					}
		        });
				builder.create().show();
				return true;
			}
		});
	    
	    listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub	
				 ListView listView = (ListView)arg0;
				 HashMap<String, Object> map = (HashMap<String, Object>) listView.getItemAtPosition(arg2);
				 RecordDetail.height = (Double) map.get("height");
				 RecordDetail.weight = (Double) map.get("weight");
				 RecordDetail.BMI = (Double) map.get("BMI");
				 RecordDetail.time=(String) map.get("date");
				 RecordDetail.result=(String) map.get("result");
				 switchFragment(new RecordDetail());
			}
		});
	    
		return view;
	}
	
	private void getData() {
		// TODO Auto-generated method stub
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle data = msg.getData();
				int status = data.getInt("status");
				Gson gson = new Gson();
				try {
					JsonRecordBean jsonRecordBean = gson.fromJson(HttpRequest.get_method_result,
							JsonRecordBean.class);
					count_record=jsonRecordBean.get_count_record();
					records = jsonRecordBean.get_records();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(count_record == 0){
					AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
					builder.setMessage("还没有任何记录哦!");
					builder.setCancelable(true);
					AlertDialog dialog=builder.create();
					dialog.show();
				}
				else {
					for (int i=count_record-1;i>=0;i--){
						String result="";
						double height = records.get(i).get_height()/100;
						double weight = records.get(i).get_weight();
						double BMI=weight/(height*height);
						if(BMI<18.5){
							result="偏瘦";
						}
						else if(BMI<=23.9){
							result="正常";
						}
						else if(BMI<=27){
							result="微胖";
						}
						else if(BMI<=32){
							result="偏胖";
						}
						else{
							result="肥胖";
						}
						Map<String, Object>map=new HashMap<String, Object>();
						map.put("record_id",records.get(i).get_record_id());
						map.put("height",height*100);
						map.put("weight",weight);
						map.put("BMI", BMI);
						map.put("date", records.get(i).get_record_time());
						map.put("result",result);
						dataList.add(map);
					}
					simpleAdapter=new SimpleAdapter(getActivity(),dataList,R.layout.title,
							new String[]{"result","date"} , new int[]{R.id.show_result_title,
							R.id.show_date_title});
					listView.setAdapter(simpleAdapter);
				}
			}
		};

		Runnable networkTask = new Runnable() {
			@Override
			public void run() {
				// TODO
				// 在这里进行 http request.网络请求相关操作
				SharedPreferences share = getActivity().getSharedPreferences("Session", MODE_PRIVATE);
				user_id = share.getInt("user_id",0);
				try {
					HttpRequest.get("http://120.78.67.135:8000/android_health_test/record/user/"
							+user_id+"/");
				} catch (Exception e) {
					e.printStackTrace();
				}

				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("status", HttpRequest.response_code);
				msg.setData(data);
				handler.sendMessage(msg);
			}
		};
		if(!MainActivity.sessionid.equals("null")){
			new Thread(networkTask).start();
		}
	}

	private void switchFragment(android.support.v4.app.Fragment targetFragment) {
		MainActivity.tab_transfer=targetFragment;
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if (!targetFragment.isAdded()) {
			transaction
					.hide(RecordFragment.this)
					.add(R.id.id_content, MainActivity.tab_transfer)
					.commit();
		} else {
			transaction
					.hide(RecordFragment.this)
					.show( MainActivity.tab_transfer)
					.commit();
		}

	}
}
