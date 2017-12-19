package com.example.HomeworkOne.BaseActivity

import MyInterface.InitView
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

    override fun initView() {
        client = OkHttpClient()
    }

    override fun initListener() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    open fun httpGet(url: String){
        val request: Request = Request.Builder().url(url).build()
        call = client.newCall(request)
    }
}