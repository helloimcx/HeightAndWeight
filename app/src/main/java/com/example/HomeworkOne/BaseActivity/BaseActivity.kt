package com.example.HomeworkOne.BaseActivity

import MyInterface.InitView
import Utils.LoadingDialog
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Window
import com.example.HomeworkOne.R
import es.dmoral.toasty.Toasty


/**
 * Author: kafca
 * Date: 2018/5/5
 * Description: BaseActivity
 */
abstract class BaseActivity : Activity(), InitView {

    lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialog = LoadingDialog(this, R.style.MyDialogStyle)
    }

    fun setActivityState() {
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }


    fun showSuccessToast (str: String) {
        Toasty.success(this, str).show()
    }

    fun showErrorToast (str: String) {
        Toasty.error(this, str).show()
    }

    fun showWarningToast (str: String) {
        Toasty.warning(this, str).show()
    }

    fun showInfoToast (str: String) {
        Toasty.info(this, str).show()
    }

    fun showLoading() {
        loadingDialog.show()
    }

    fun hideLoading() {
        loadingDialog.dismiss()
    }

    override fun initView() {}

    override fun initListener() {}

}