package com.example.HomeworkOne;

import com.example.HomeworkOne.MyWeb.webViewClient;

import android.R.string;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebFragment extends android.support.v4.app.Fragment{
	private WebView webview;
	public static String myurl;
    @SuppressLint("SetJavaScriptEnabled")  
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	View v=inflater.inflate(R.layout.webview, container, false); 
        webview = (WebView) v.findViewById(R.id.webview);  
        WebSettings webSettings = webview.getSettings();  
        //设置WebView属性，能够执行Javascript脚本    
        webSettings.setJavaScriptEnabled(true);    
        //设置可以访问文件  
        webSettings.setAllowFileAccess(true);  
         //设置支持缩放  
        webSettings.setBuiltInZoomControls(true);  
        //加载需要显示的网页    
        webview.loadUrl(myurl);    
        //设置Web视图    
        webview.setWebViewClient(new WebViewClient(){
            @Override
         public boolean shouldOverrideUrlLoading(WebView view, String url) {
             // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
              view.loadUrl(url);
             return true;
         }
        }); 
    	return v;
    	
    }
    
     
   
  
}
