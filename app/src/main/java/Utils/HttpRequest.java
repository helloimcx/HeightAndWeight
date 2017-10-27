package Utils;

/**
 * Created by mac on 2017/10/14.
 */
import android.app.Activity;
import android.content.SharedPreferences;

import com.example.HomeworkOne.MainActivity;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HttpRequest{
    public static String get_method_result;
    public static int response_code;

    public static void get(String url) {
        OkHttpClient okHttpClient = MainActivity.okHttpClient;
        //构建一个请求对象
        Request request = new Request.Builder().url(url).addHeader("cookie", MainActivity.sessionid).build();
        //发送请求
        try {
            Response response = okHttpClient.newCall(request).execute();
            get_method_result=response.body().string();
            response_code=response.code();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(String url){
        OkHttpClient okHttpClient = MainActivity.okHttpClient;
        //构建一个请求对象
        Request request = new Request.Builder().url(url).addHeader("cookie", MainActivity.sessionid)
                .delete().build();
        //发送请求
        try {
            Response response = okHttpClient.newCall(request).execute();
            get_method_result=response.body().string();
            response_code=response.code();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


