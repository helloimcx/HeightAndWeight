package com.example.HomeworkOne;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.platformtools.Util;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class TestFragment extends Fragment {
	private TextView textView;
	private ImageButton shareToFriends;
	private ImageButton shareToMoments;
	private ImageButton testButton;
	private IWXAPI api;
	private static final String APP_ID="wxac23c0af2a986db5";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//引入我们的布局
		View v=inflater.inflate(R.layout.factivity, container, false);
		reToWx();
		shareToFriends=(ImageButton) v.findViewById(R.id.shareToFriends);
		shareToMoments=(ImageButton) v.findViewById(R.id.shareToMoments);
		textView=(TextView) v.findViewById(R.id.textView1);
		testButton=(ImageButton) v.findViewById(R.id.imageButton1);
		testButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(getActivity(),SActivity.class);
				startActivityForResult(intent, 1);
			}
		});
		
        
        
         shareToFriends.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				WXWebpageObject webpageObject=new WXWebpageObject();
				webpageObject.webpageUrl="http://kafca.baiduux.com/h5/00995a8c-3555-895e-e141-c20faeb69724.html";
				
				WXMediaMessage msg=new WXMediaMessage(webpageObject);
				msg.title="体型测试";
				msg.description="用户输入自己的身高和体重，应用会根据输入值计算用户的BMI指数（身体质量指数），衡量人体的肥胖程度。";
				
				Bitmap thump=android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.logo);
				msg.thumbData=Util.bmpToByteArray(thump,true);
				
				SendMessageToWX.Req req=new SendMessageToWX.Req();
				req.transaction =buildTransaction("webpage");
				req.message=msg;
				req.scene = SendMessageToWX.Req.WXSceneSession;
			    api.sendReq(req);
				
			}
		});
         shareToMoments.setOnClickListener(new OnClickListener() {
 			
 			@Override
 			public void onClick(View arg0) {
 				// TODO Auto-generated method stub
 				WXWebpageObject webpageObject=new WXWebpageObject();
				webpageObject.webpageUrl="http://kafca.baiduux.com/h5/00995a8c-3555-895e-e141-c20faeb69724.html";
				
				WXMediaMessage msg=new WXMediaMessage(webpageObject);
				msg.title="体型测试";
				msg.description="用户输入自己的身高和体重，应用会根据输入值计算用户的BMI指数（身体质量指数），衡量人体的肥胖程度。";
				
				Bitmap thump=android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.logo);
				msg.thumbData=Util.bmpToByteArray(thump,true);
				
				SendMessageToWX.Req req=new SendMessageToWX.Req();
				req.transaction =buildTransaction("webpage");
				req.message=msg;
				req.scene = SendMessageToWX.Req.WXSceneTimeline;
			    api.sendReq(req);
 			}
 		});
		return v;
	}
	
	private void reToWx(){
		api=WXAPIFactory.createWXAPI(getActivity().getApplicationContext(),APP_ID,true);
		api.registerApp(APP_ID);
	}
	
	private static String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1&&resultCode==2){
			String contentString=data.getStringExtra("data");
			int photo=data.getIntExtra("photo",1);
			textView.setText(contentString);
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

			default:
				testButton.setImageResource(R.drawable.female_vvfat);
				break;
			}
			
		}
	}
}
