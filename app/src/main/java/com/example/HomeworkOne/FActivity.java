package com.example.HomeworkOne;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.platformtools.BackwardSupportUtil.BitmapFactory;
import com.tencent.mm.sdk.platformtools.Util;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FActivity extends Activity{
	private Button button;
	private TextView textView;
	private ImageView imageView;
	private ImageView mImageView;
	private Button record;
	private Button help;
	private ImageButton shareToFriends;
	private ImageButton shareToMoments;
	private IWXAPI api;
	private static final String APP_ID="wxac23c0af2a986db5";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.factivity);
		reToWx();
		button=(Button) findViewById(R.id.button1);
		shareToFriends=(ImageButton) findViewById(R.id.shareToFriends);
		shareToMoments=(ImageButton) findViewById(R.id.shareToMoments);
		textView=(TextView) findViewById(R.id.textView1);

		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(FActivity.this,SActivity.class);
				startActivityForResult(intent, 1);
			}
		});
		
         record.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(FActivity.this,RecordActivity.class);
				startActivity(intent);
			}
		});
         
         help.setOnClickListener(new OnClickListener() {
 			
 			@Override
 			public void onClick(View arg0) {
 				AlertDialog.Builder builder=new AlertDialog.Builder(FActivity.this);
 				builder.setTitle("帮助");
 				builder.setMessage("身体质量指数（BMI，Body Mass Index）是国际上常用的衡量人体肥胖程度和是否健康的重要标准，主要用于统计分析。"
 						+ "肥胖程度的判断不能采用体重的绝对值，它天然与身高有关。因此，BMI 通过人体体重和身高两个数值获得相对客观的参数，"
 						+ "并用这个参数所处范围衡量身体质量。\n"
 						+ "成人的BMI数值:\n过轻：低于18.5\n正常：18.5-23.9\n过重：24-27\n"
 						+"肥胖：28-32\n"
 						+"非常肥胖: 高于32");
                                                                                                                                       
 				builder.create().show();
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
	}
	
	private void reToWx(){
		api=WXAPIFactory.createWXAPI(getApplicationContext(),APP_ID,true);
		api.registerApp(APP_ID);
	}
	
	private static String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1&&resultCode==2){
			String contentString=data.getStringExtra("data");
			int photo=data.getIntExtra("photo",1);
			textView.setText(contentString);
			switch (photo) {
			case 1:
				imageView.setImageResource(R.drawable.thin);
				break;
			case 2:
				imageView.setImageResource(R.drawable.good);
				break;
			case 3:
				imageView.setImageResource(R.drawable.fast);
				break;
			case 4:
				imageView.setImageResource(R.drawable.veryfast);
				break;
			case 5:
				imageView.setImageResource(R.drawable.veryveryfast);
				break;
			case 6:
				imageView.setImageResource(R.drawable.female_thin);
				break;
			case 7:
				imageView.setImageResource(R.drawable.female_good);
				break;
			case 8:
				imageView.setImageResource(R.drawable.female_fat);
				break;
			case 9:
				imageView.setImageResource(R.drawable.female_vfat);
				break;

			default:
				imageView.setImageResource(R.drawable.female_vvfat);
				break;
			}
			
		}
	}

}