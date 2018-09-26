package net.sarasarasa.lifeup.application

import android.content.Context
import net.sarasarasa.lifeup.utils.ToastUtils

class ApplicationCrashHandler : Thread.UncaughtExceptionHandler {


    companion object {
        val TAG = "ApplicationCrashHandler"
    }

    private lateinit var context: Context
    private var defalutHandler = Thread.getDefaultUncaughtExceptionHandler()


    fun init(context: Context) {
        this.context = context
        Thread.setDefaultUncaughtExceptionHandler(this)
    }


    override fun uncaughtException(thread: Thread, ex: Throwable) {
        ToastUtils.showLongToast("「人升」遇到了异常：" + ex.printStackTrace())
    }


}
