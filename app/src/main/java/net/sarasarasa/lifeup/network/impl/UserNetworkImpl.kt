package net.sarasarasa.lifeup.network.impl

import android.os.Handler
import android.os.Message
import android.util.Log
import net.sarasarasa.lifeup.base.BaseNetwork
import net.sarasarasa.lifeup.constants.LoginConstants
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.UserConstants.Companion.MSG_UPDATE_FAILED
import net.sarasarasa.lifeup.constants.UserConstants.Companion.MSG_UPDATE_SUCCESS
import net.sarasarasa.lifeup.network.UserNetwork
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.vo.ProfileVO
import net.sarasarasa.lifeup.vo.ResultVO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserNetworkImpl(var uiHandler: Handler.Callback) : BaseNetwork() {

    val userService = UserServiceImpl()

    fun getUserProfile() {
        Log.i("LifeUp 用户模块", "执行[获取用户信息]操作")

        val network = retrofit.create(UserNetwork::class.java)
        val call = network.getUserProfile(userService.getToken())

        call.enqueue(object : Callback<ResultVO<ProfileVO>> {
            override fun onFailure(call: Call<ResultVO<ProfileVO>>?, t: Throwable?) {
                Log.e("LifeUp 用户模块", "[获取用户信息]返回错误: ${t.toString()}")
                val message = Message()
                message.what = LoginConstants.MSG_YB_LOGIN_CONNECT_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<ProfileVO>>, response: Response<ResultVO<ProfileVO>>) {
                val responseBody = response.body()

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVAILD_TOKEN) {
                    Log.i("LifeUp 用户模块", "[获取用户信息]请求失败：错误或失效TOKEN")
                    message.what = NetworkConstants.INVAILD_TOKEN
                    message.obj = responseBody.msg

                } else {
                    message.what = LoginConstants.MSG_GET_PROFILE_SUCCESS
                    val profileVO = responseBody?.data
                    Log.i("LifeUp 用户模块", "[获取用户信息]请求成功：${profileVO}")
                    if (profileVO != null)
                        userService.saveMine(profileVO)
                }
                uiHandler.handleMessage(message)
            }
        })
    }

    fun updateUserProfile(profileVO: ProfileVO) {
        Log.i("LifeUp 登陆模块", "执行[更新用户信息]操作")

        val network = retrofit.create(UserNetwork::class.java)
        val call = network.updateUserProfile(userService.getToken(), profileVO)

        call.enqueue(object : Callback<ResultVO<ProfileVO>> {
            override fun onFailure(call: Call<ResultVO<ProfileVO>>?, t: Throwable?) {
                Log.e("LifeUp 登陆模块", "[更新用户信息]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_UPDATE_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<ProfileVO>>, response: Response<ResultVO<ProfileVO>>) {
                val responseBody = response.body()

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVAILD_TOKEN) {
                    Log.i("LifeUp 用户模块", "[更新用户信息]请求失败：错误或失效TOKEN")
                    message.what = MSG_UPDATE_FAILED
                    message.obj = responseBody.msg
                } else {

                    message.what = MSG_UPDATE_SUCCESS
                    val profileVO = responseBody?.data
                    Log.i("LifeUp 用户模块", "[更新用户信息]请求成功：${profileVO}")
                    if (profileVO != null)
                        userService.saveMine(profileVO)
                }
                uiHandler.handleMessage(message)
            }
        })
    }


}