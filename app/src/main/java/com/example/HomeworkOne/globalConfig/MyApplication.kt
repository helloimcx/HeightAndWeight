package com.example.HomeworkOne.globalConfig

import Utils.OssSecretBean
import Utils.PicassoImageLoader
import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.OSS
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.google.gson.Gson
import com.lqr.imagepicker.ImagePicker
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
import okhttp3.Response
import org.json.JSONObject


/**
 * Author: kafca
 * Date: 2017/12/6
 * Description: 全局配置
 */

class MyApplication : Application() {
    lateinit var host: String
    lateinit var share: SharedPreferences
    private lateinit var oss: OSS
    override fun onCreate() {
        host = "https://api.mochuxian.top"
        share = getSharedPreferences("Session", MODE_PRIVATE)
        initOkGo()
        intiOss()
        initImagePicker()
        super.onCreate()
    }

    private fun initOkGo() {
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor("OkGo")
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY)
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO)
        //全局的读取超时时间
        builder.readTimeout(10000, TimeUnit.MILLISECONDS)
        //全局的写入超时时间
        builder.writeTimeout(10000, TimeUnit.MILLISECONDS)
        //全局的连接超时时间
        builder.connectTimeout(10000, TimeUnit.MILLISECONDS)
        builder.addInterceptor(loggingInterceptor)
        builder.cookieJar(CookieJarImpl(SPCookieStore(this)))
        val sslParams1 = HttpsUtils.getSslSocketFactory()
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager)
        OkGo.getInstance().init(this)                       //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置将使用默认的
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE).retryCount = 3
    }

    fun header(): HttpHeaders {
        val token = share.getString("token", "null")
        val headers = HttpHeaders()
        headers.put("Authorization", "Token $token")
        return headers
    }

    private fun intiOss() {
        val endpoint = "http://oss-cn-shenzhen.aliyuncs.com"
        val credentialProvider = object : OSSCustomSignerCredentialProvider() {
            override fun signContent(content: String): String {
                // 您需要在这里依照OSS规定的签名算法，实现加签一串字符内容，并把得到的签名传拼接上AccessKeyId后返回
                // 一般实现是，将字符内容post到您的业务服务器，然后返回签名
                // 如果因为某种原因加签失败，描述error信息后，返回nil
                // 以下是用本地算法进行的演示
                //return "OSS " + AccessKeyId + ":" + base64(hmac-sha1(AccessKeySecret, content));
                val url = "$host/oss/signature/"
                val param = JSONObject()
                param.put("content", content)
                var signature = ""
                val response: Response  = OkGo.post<String>(url).headers(header()).upJson(param).execute()
                if (response.code() == 200) {
                    val gson = Gson()
                    val ossSecretBean = gson.fromJson(response.body()!!.string()!!,
                            OssSecretBean::class.java)
                    signature = ossSecretBean.authorization
                }
                return signature
            }
        }
        oss = OSSClient(applicationContext, endpoint, credentialProvider)
    }

    fun putToOss(objectKey: String, path: String) {
        val put = PutObjectRequest("ht-data", objectKey, path)
        // 文件元信息的设置是可选的
        // ObjectMetadata metadata = new ObjectMetadata();
        // metadata.setContentType("application/octet-stream"); // 设置content-type
        // metadata.setContentMD5(BinaryUtil.calculateBase64Md5(uploadFilePath)); // 校验MD5
        // put.setMetadata(metadata);
        try {
            val putResult = oss.putObject(put)
            Log.d("PutObject", "UploadSuccess")
            Log.d("ETag", putResult.eTag)
            Log.d("RequestId", putResult.requestId)
        } catch (e: ClientException) {
            // 本地异常如网络异常等
            e.printStackTrace()
        } catch (e: ServiceException) {
            // 服务异常
            Log.e("RequestId", e.requestId)
            Log.e("ErrorCode", e.errorCode)
            Log.e("HostId", e.hostId)
            Log.e("RawMessage", e.rawMessage)
        }
    }

    private fun initImagePicker() {
        val imagePicker = ImagePicker.getInstance()
        imagePicker.imageLoader = PicassoImageLoader()   //设置图片加载器
        imagePicker.isMultiMode = true
        imagePicker.isShowCamera = true  //显示拍照按钮
        imagePicker.selectLimit = 1    //选中数量限制
        imagePicker.isCrop = false        //允许裁剪（单选才有效）
    }

}