package net.sarasarasa.lifeup.network.impl

import android.os.Handler
import android.os.Message
import android.util.Log
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.application.LifeUpApplication
import net.sarasarasa.lifeup.base.BaseNetwork
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_REPORT_TYPE_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_REPORT_TYPE_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_REPORT_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_REPORT_SUCCESS
import net.sarasarasa.lifeup.network.ReportNetwork
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.ReportDetailVO
import net.sarasarasa.lifeup.vo.ReportTypeVO
import net.sarasarasa.lifeup.vo.ResultVO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportNetworkImpl(var uiHandler: Handler.Callback) : BaseNetwork() {

    val userService = UserServiceImpl()
    val network: ReportNetwork = retrofit.create(ReportNetwork::class.java)

    fun getReportType() {
        Log.i("LifeUp 举报模块", "执行[获取举报类型]操作")

        val call = network.getReportType(userService.getToken())
        call.enqueue(object : Callback<ResultVO<ArrayList<ReportTypeVO>>> {
            override fun onFailure(call: Call<ResultVO<ArrayList<ReportTypeVO>>>?, t: Throwable?) {
                Log.e("LifeUp 举报模块", "[获取举报类型]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_GET_REPORT_TYPE_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<ArrayList<ReportTypeVO>>>, response: Response<ResultVO<ArrayList<ReportTypeVO>>>) {
                val responseBody = response.body()

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 举报模块", "[获取举报类型]请求失败：错误或失效TOKEN")
                    ToastUtils.showShortToast(LifeUpApplication.getLifeUpApplication().getString(R.string.network_login_invaild))
                    message.what = MSG_GET_REPORT_TYPE_FAILED
                    userService.saveToken("")
                    message.obj = responseBody.msg
                } else {
                    message.what = MSG_GET_REPORT_TYPE_SUCCESS
                    val returnList = response.body()?.data
                    message.obj = returnList

                    Log.i("LifeUp 举报模块", "[获取举报类型]请求成功 " + returnList.toString())
                }
                uiHandler.handleMessage(message)
            }
        })
    }


    fun report(reportDetailVO: ReportDetailVO) {
        Log.i("LifeUp 举报模块", "执行[举报]操作")

        val call = network.report(userService.getToken(),reportDetailVO)
        call.enqueue(object : Callback<ResultVO<Any>> {
            override fun onFailure(call: Call<ResultVO<Any>>?, t: Throwable?) {
                Log.e("LifeUp 举报模块", "[举报]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_REPORT_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<Any>>, response: Response<ResultVO<Any>>) {
                val responseBody = response.body()

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 举报模块", "[举报]请求失败：错误或失效TOKEN")
                    ToastUtils.showShortToast(LifeUpApplication.getLifeUpApplication().getString(R.string.network_login_invaild))
                    message.what = MSG_REPORT_FAILED
                    userService.saveToken("")
                    message.obj = responseBody.msg
                } else {
                    message.what = MSG_REPORT_SUCCESS
                    val returnList = response.body()?.data

                    Log.i("LifeUp 举报模块", "[举报]请求成功 " + returnList.toString())
                }
                uiHandler.handleMessage(message)
            }
        })
    }

}