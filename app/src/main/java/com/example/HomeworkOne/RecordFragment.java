package com.example.HomeworkOne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class RecordFragment extends Fragment {
	private ListView listView;
	private SimpleAdapter simpleAdapter;
	private List<Map<String, Object>>dataList;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//引入我们的布局
		View view=inflater.inflate(R.layout.record_list, container, false);
		dataList=new ArrayList<Map<String, Object>>();
		listView=(ListView) view.findViewById(R.id.record_list);
		simpleAdapter=new SimpleAdapter(getActivity(), getData(),R.layout.title,
	             new String[]{"name","date"} , new int[]{R.id.shownameTitle,
			     R.id.showDateTitle});
	    listView.setAdapter(simpleAdapter);
	   
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
		        .setPositiveButton("确定", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface Arg, int arg) {
						// TODO Auto-generated method stub
						 arg1.setVisibility(View.GONE);
						 String name = (String) map.get("name");
						 double BMI = (Double) map.get("BMI");
						 SQLiteDatabase db = getActivity().openOrCreateDatabase("user.db", getActivity().MODE_PRIVATE, null);
						 db.execSQL("delete from records where name="+"'"+name+"'"+" and BMI="+BMI);
						 db.close();
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
				 String name = (String) map.get("name");
				 double height = (Double) map.get("height");
				 double weight = (Double) map.get("weight");
				 double BMI = (Double) map.get("BMI");
				 String date=(String) map.get("date");
				 int sex = (Integer) map.get("sex");
				 Intent intent=new Intent(getActivity(),RecordDetail.class);
				 Bundle bundle=new Bundle();
				 bundle.putString("name", name);
				 bundle.putDouble("height", height);
				 bundle.putDouble("weight", weight);
				 bundle.putDouble("BMI", BMI);
				 bundle.putString("date", date);
				 bundle.putInt("sex", sex);
				 
				 intent.putExtras(bundle);
				 startActivity(intent);
			}
		});
	    
		return view;
	}
	
	private List<Map<String, Object>> getData() {
		// TODO Auto-generated method stub
		SQLiteDatabase db = getActivity().openOrCreateDatabase("user.db", getActivity().MODE_PRIVATE, null);
		try{
		Cursor c = db.rawQuery("select * from records order by currDate", null);
		if(c!=null){
			String []cols=c.getColumnNames();
			while(c.moveToNext()){
				Map<String, Object>map=new HashMap<String, Object>();
		    	map.put("name", c.getString(c.getColumnIndex(cols[1])));
		    	map.put("height",c.getDouble(c.getColumnIndex(cols[2])) );
		    	map.put("weight",c.getDouble(c.getColumnIndex(cols[3])) );
		    	map.put("BMI", c.getDouble(c.getColumnIndex(cols[4])));
		    	map.put("date", c.getString(c.getColumnIndex(cols[5])));
		    	map.put("sex", c.getInt(c.getColumnIndex(cols[6])));
		    	dataList.add(map);
			}
		}
		}catch (Exception e){
			Toast.makeText(getActivity(), "还没有任何数据哦！",
				     Toast.LENGTH_SHORT).show();
		}
		return dataList;
	} 

}
