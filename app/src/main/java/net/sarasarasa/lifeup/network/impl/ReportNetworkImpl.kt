package net.sarasarasa.lifeup.network.impl

import android.os.Handler
import android.os.Message
import android.util.Log
import net.sarasarasa.lifeup.base.BaseNetwork
import net.sarasarasa.lifeup.constants.AttributeConstants
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_CONNECT_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_FOLLOW_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_FOLLOW_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_MOMENTS_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_PROFILE_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_REPORT_TYPE_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_REPORT_TYPE_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_USER_ACTIVITIES_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_USER_DETAIL_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_USER_TEAM_LIST_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_REPORT_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_REPORT_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UNFOLLOW_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UNFOLLOW_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UPDATE_AVATAR_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UPDATE_AVATAR_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UPDATE_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UPDATE_PROFILE_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_YB_LOGIN_CONNECT_FAILED
import net.sarasarasa.lifeup.constants.ToDoItemConstants.Companion.USER_ME
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.network.ReportNetwork
import net.sarasarasa.lifeup.network.UploadNetwork
import net.sarasarasa.lifeup.network.UserNetwork
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.vo.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

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
                    message.what = MSG_GET_REPORT_TYPE_FAILED
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
                    message.what = MSG_REPORT_FAILED
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