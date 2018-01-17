package com.example.HomeworkOne.Activity

import MyInterface.InitView
import android.app.Activity
import android.os.Bundle
import com.example.HomeworkOne.R

/**
 * Author: kafca
 * Date: 2018/1/17
 * Description: Show personal records graph
 */
class RecordGraph : Activity(), InitView{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_record_graph)
    }
    override fun initListener() {
    }

    override fun initView() {
    }

}