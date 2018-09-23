package net.sarasarasa.lifeup.network.impl

import android.os.Handler
import android.os.Message
import android.util.Log
import net.sarasarasa.lifeup.base.BaseNetwork
import net.sarasarasa.lifeup.constants.AttributeConstants
import net.sarasarasa.lifeup.constants.LoginConstants
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_CONNECT_FAILED
import net.sarasarasa.lifeup.constants.UserConstants.Companion.MSG_UPDATE_FAILED
import net.sarasarasa.lifeup.network.UserNetwork
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.vo.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserNetworkImpl(var uiHandler: Handler.Callback) : BaseNetwork() {

    val userService = UserServiceImpl()

    fun getUserProfile() {
        Log.i("LifeUp 用户模块", "执行[获取用户信息]操作 Token = " + userService.getToken())

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

                Log.i("LifeUp 用户模块", "[获取用户信息]请求成功：" + responseBody?.msg)

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

                    message.what = 266
                    val profileVO = responseBody?.data
                    Log.i("LifeUp 用户模块", "[更新用户信息]请求成功：${profileVO}")
                    if (profileVO != null)
                        userService.saveMine(profileVO)
                }
                uiHandler.handleMessage(message)
            }
        })
    }


    fun getUserActivities(pageVO: PageVO<TeamActivityListVO>, userId: Long) {
        Log.i("LifeUp 团队模块", "执行[查询团队列表]操作" + userService.getToken())

        val network = retrofit.create(UserNetwork::class.java)
        val currentPage = pageVO.currentPage ?: 1
        val size = pageVO.size ?: 0

        if (currentPage == 0L || size == 0L)
            return


        val call = when (userId) {
            -1L -> network.getMineUserActivities(userService.getToken(), currentPage, size)
            else -> network.getUserActivities(userService.getToken(), userId, currentPage, size)
        }

        call.enqueue(object : Callback<ResultVO<PageVO<TeamActivityListVO>>> {
            override fun onFailure(call: Call<ResultVO<PageVO<TeamActivityListVO>>>?, t: Throwable?) {
                Log.e("LifeUp 用户模块", "[查询用户动态列表]返回错误: ${t.toString()}")
                val message = Message()
                message.what = AttributeConstants.MSG_CONNECT_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<PageVO<TeamActivityListVO>>>, response: Response<ResultVO<PageVO<TeamActivityListVO>>>) {
                val responseBody = response.body()
                Log.i("LifeUp", responseBody?.msg)

                val message = Message()

                if (responseBody?.code == NetworkConstants.INVAILD_TOKEN) {
                    Log.i("LifeUp 用户模块", "[查询用户动态列表]请求失败：错误或失效TOKEN")
                    message.what = NetworkConstants.INVAILD_TOKEN
                } else {
                    message.what = 333
                    val list = responseBody?.data
                    message.obj = list
                    Log.i("LifeUp 用户模块", "[查询用户动态列表]请求成功：${list}")
                }
                uiHandler.handleMessage(message)
            }
        })
    }


    fun getUserDetail(userId: Long) {
        Log.i("LifeUp 用户模块", "执行[查询用户详情]操作")

        val network = retrofit.create(UserNetwork::class.java)

        val call = when (userId) {
            -1L -> network.getMineUserDetail(userService.getToken())
            else -> network.getUserDetail(userService.getToken(), userId)
        }

        call.enqueue(object : Callback<ResultVO<UserDetailVO>> {
            override fun onFailure(call: Call<ResultVO<UserDetailVO>>?, t: Throwable?) {
                Log.e("LifeUp 用户模块", "[查询用户详情]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_UPDATE_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<UserDetailVO>>, response: Response<ResultVO<UserDetailVO>>) {
                val responseBody = response.body()

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVAILD_TOKEN) {
                    Log.i("LifeUp 用户模块", "[查询用户详情]请求失败：错误或失效TOKEN")
                    message.what = MSG_UPDATE_FAILED
                    message.obj = responseBody.msg
                } else {

                    message.what = 334
                    val userDetailVO = responseBody?.data
                    Log.i("LifeUp 用户模块", "[查询用户详情]请求成功：${userDetailVO}")
                    message.obj = userDetailVO
                }
                uiHandler.handleMessage(message)
            }
        })
    }


    fun getUserTeamList(pageVO: PageVO<TeamListVO>, userId: Long) {
        Log.i("LifeUp 用户模块", "执行[查询用户加入团队列表]操作" + userService.getToken())

        val network = retrofit.create(UserNetwork::class.java)
        val currentPage = pageVO.currentPage ?: 0
        val size = pageVO.size ?: 0

        if (currentPage == 0L || size == 0L)
            return


        val call = when (userId) {
            -1L -> network.getUserTeamList(userService.getToken(), currentPage, size)
            else -> network.getUserTeamList(userService.getToken(), userId, currentPage, size)
        }


        call.enqueue(object : Callback<ResultVO<PageVO<TeamListVO>>> {
            override fun onFailure(call: Call<ResultVO<PageVO<TeamListVO>>>?, t: Throwable?) {
                Log.e("LifeUp 用户模块", "[查询用户加入团队列表]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<PageVO<TeamListVO>>>, response: Response<ResultVO<PageVO<TeamListVO>>>) {
                val responseBody = response.body()
                Log.i("LifeUp", responseBody?.msg)

                val message = Message()

                if (responseBody?.code == NetworkConstants.INVAILD_TOKEN) {
                    Log.i("LifeUp 用户模块", "[查询用户加入团队列表]请求失败：错误或失效TOKEN")
                    message.what = NetworkConstants.INVAILD_TOKEN
                } else {
                    message.what = 888
                    val list = responseBody?.data
                    message.obj = list
                    Log.i("LifeUp 用户模块", "[查询用户加入团队列表]请求成功：${list}")
                }
                uiHandler.handleMessage(message)
            }
        })
    }

}