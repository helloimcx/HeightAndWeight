package com.example.HomeworkOne.BaseActivity

import MyInterface.InitView
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Author: kafca
 * Date: 2017/12/19
 * Description: a util class for httpRequest
 */
open class AcHttpRequest : AppCompatActivity(),InitView {
    private lateinit var client: OkHttpClient
    lateinit var call: Call
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var session_id: String

    override fun initView() {
        client = OkHttpClient()
        sharedPreference = getSharedPreferences("Session", Context.MODE_PRIVATE)
        session_id = sharedPreference.getString("sessionid",null)
    }

    override fun initListener() {}

    open fun httpGet(url: String){
        val request: Request = Request.Builder().url(url).addHeader("cookie",session_id).build()
        call = client.newCall(request)
    }
}