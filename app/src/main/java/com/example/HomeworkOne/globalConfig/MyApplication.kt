package com.example.HomeworkOne.globalConfig

import android.app.Application
import android.content.SharedPreferences
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheEntity
import com.lzy.okgo.cookie.CookieJarImpl
import com.lzy.okgo.cookie.store.SPCookieStore
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import com.lzy.okgo.https.HttpsUtils
import com.lzy.okgo.model.HttpHeaders


/**
 * Author: kafca
 * Date: 2017/12/6
 * Description: 全局配置
 */

class MyApplication : Application() {
    lateinit var host: String
    lateinit var share: SharedPreferences
    override fun onCreate() {
        host = "https://api.mochuxian.top"
        share = getSharedPreferences("Session", MODE_PRIVATE)
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor("OkGo")
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY)
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO)
        //全局的读取超时时间
        builder.readTimeout(10000, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(10000, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(10000, TimeUnit.MILLISECONDS);
        builder.addInterceptor(loggingInterceptor)
        builder.cookieJar(CookieJarImpl(SPCookieStore(this)));
        val sslParams1 = HttpsUtils.getSslSocketFactory()
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        OkGo.getInstance().init(this)                       //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置将使用默认的
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
        super.onCreate()
    }

    fun header(): HttpHeaders {
        val token = share.getString("token", "null")
        val headers = HttpHeaders()
        headers.put("Authorization", "Token $token")
        return headers
    }
}