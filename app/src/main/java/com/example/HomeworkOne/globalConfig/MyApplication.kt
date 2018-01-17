package com.example.HomeworkOne.globalConfig

import android.app.Application

/**
 * Author: kafca
 * Date: 2017/12/6
 * Description: »´æ÷≈‰÷√
 */

class MyApplication : Application() {
    lateinit var host: String
    override fun onCreate() {
        host = "http://120.78.199.2:8000"
        super.onCreate()
    }
}