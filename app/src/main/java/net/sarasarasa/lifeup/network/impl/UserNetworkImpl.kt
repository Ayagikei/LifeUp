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
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_USER_ACTIVITIES_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_USER_DETAIL_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_USER_TEAM_LIST_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UNFOLLOW_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UNFOLLOW_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UPDATE_AVATAR_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UPDATE_AVATAR_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UPDATE_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UPDATE_PROFILE_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_YB_LOGIN_CONNECT_FAILED
import net.sarasarasa.lifeup.constants.ToDoItemConstants.Companion.USER_ME
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

class UserNetworkImpl(var uiHandler: Handler.Callback) : BaseNetwork() {

    val userService = UserServiceImpl()
    val network: UserNetwork = retrofit.create(UserNetwork::class.java)

    fun getUserProfile() {
        Log.i("LifeUp 用户模块", "执行[获取用户信息]操作 Token = " + userService.getToken())

        val call = network.getUserProfile(userService.getToken())

        call.enqueue(object : Callback<ResultVO<ProfileVO>> {
            override fun onFailure(call: Call<ResultVO<ProfileVO>>?, t: Throwable?) {
                Log.e("LifeUp 用户模块", "[获取用户信息]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_YB_LOGIN_CONNECT_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<ProfileVO>>, response: Response<ResultVO<ProfileVO>>) {
                val responseBody = response.body()

                Log.i("LifeUp 用户模块", "[获取用户信息]请求成功：" + responseBody?.msg)

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 用户模块", "[获取用户信息]请求失败：错误或失效TOKEN")
                    message.what = NetworkConstants.INVALID_TOKEN
                    message.obj = responseBody.msg

                } else {
                    message.what = MSG_GET_PROFILE_SUCCESS
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
                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 用户模块", "[更新用户信息]请求失败：错误或失效TOKEN")
                    message.what = MSG_UPDATE_FAILED
                    message.obj = responseBody.msg
                } else {

                    message.what = MSG_UPDATE_PROFILE_SUCCESS
                    val profileVO = responseBody?.data
                    Log.i("LifeUp 用户模块", "[更新用户信息]请求成功：${profileVO}")
                    if (profileVO != null)
                        userService.saveMine(profileVO)
                }
                uiHandler.handleMessage(message)
            }
        })
    }

    fun updateAvatar(file: File) {
        Log.i("LifeUp 用户模块", "执行[更新用户头像]操作")

        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("avatarImage", file.name, requestFile)

        val call = network.updateAvatar(userService.getToken(), body)



        call.enqueue(object : Callback<ResultVO<String>> {
            override fun onFailure(call: Call<ResultVO<String>>?, t: Throwable?) {
                Log.e("LifeUp 用户模块", "[更新用户头像]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_UPDATE_AVATAR_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<String>>, response: Response<ResultVO<String>>) {
                val responseBody = response.body()

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 用户模块", "[更新用户头像]请求失败：错误或失效TOKEN")
                    message.what = MSG_UPDATE_AVATAR_FAILED
                    message.obj = responseBody.msg
                } else {
                    message.what = MSG_UPDATE_AVATAR_SUCCESS
                    val userMine = userService.getMine()
                    userMine.userHead = responseBody?.data
                    userMine.save()

                    Log.i("LifeUp 用户模块", "[更新用户头像]请求成功 " + responseBody?.data)
                }
                uiHandler.handleMessage(message)
            }
        })
    }

    fun getUserActivities(pageVO: PageVO<TeamActivityListVO>, userId: Long) {
        Log.i("LifeUp 团队模块", "执行[查询团队列表]操作" + userService.getToken())

        val currentPage = pageVO.currentPage ?: 1
        val size = pageVO.size ?: 0

        if (currentPage == 0L || size == 0L)
            return


        val call = when (userId) {
            USER_ME -> network.getMineUserActivities(userService.getToken(), currentPage, size)
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

                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 用户模块", "[查询用户动态列表]请求失败：错误或失效TOKEN")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {
                    message.what = MSG_GET_USER_ACTIVITIES_SUCCESS
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

        val call = when (userId) {
            USER_ME -> network.getMineUserDetail(userService.getToken())
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
                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 用户模块", "[查询用户详情]请求失败：错误或失效TOKEN")
                    message.what = MSG_UPDATE_FAILED
                    message.obj = responseBody.msg
                } else {

                    message.what = MSG_GET_USER_DETAIL_SUCCESS
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

        val currentPage = pageVO.currentPage ?: 0
        val size = pageVO.size ?: 0

        if (currentPage == 0L || size == 0L)
            return


        val call = when (userId) {
            USER_ME -> network.getUserTeamList(userService.getToken(), currentPage, size)
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

                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 用户模块", "[查询用户加入团队列表]请求失败：错误或失效TOKEN")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {
                    message.what = MSG_GET_USER_TEAM_LIST_SUCCESS
                    val list = responseBody?.data
                    message.obj = list
                    Log.i("LifeUp 用户模块", "[查询用户加入团队列表]请求成功：${list}")
                }
                uiHandler.handleMessage(message)
            }
        })
    }


    fun followUserById(userId: Long) {
        Log.i("LifeUp 用户模块", "执行[关注用户]操作")

        val call = network.followUserById(userService.getToken(), userId)

        call.enqueue(object : Callback<ResultVO<Any>> {
            override fun onFailure(call: Call<ResultVO<Any>>?, t: Throwable?) {
                Log.e("LifeUp 用户模块", "[关注用户]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_FOLLOW_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<Any>>, response: Response<ResultVO<Any>>) {
                val responseBody = response.body()

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 用户模块", "[关注用户]请求失败：错误或失效TOKEN")
                    message.what = MSG_FOLLOW_FAILED
                    message.obj = responseBody.msg
                } else {
                    message.what = MSG_FOLLOW_SUCCESS
                    Log.i("LifeUp 用户模块", "[关注用户]请求成功")
                }
                uiHandler.handleMessage(message)
            }
        })
    }


    fun unfollowUserById(userId: Long) {
        Log.i("LifeUp 用户模块", "执行[取消关注用户]操作")

        val call = network.unfollowUserById(userService.getToken(), userId)

        call.enqueue(object : Callback<ResultVO<Any>> {
            override fun onFailure(call: Call<ResultVO<Any>>?, t: Throwable?) {
                Log.e("LifeUp 用户模块", "[取消关注用户]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_UNFOLLOW_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<Any>>, response: Response<ResultVO<Any>>) {
                val responseBody = response.body()

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 用户模块", "[取消关注用户]请求失败：错误或失效TOKEN")
                    message.what = MSG_UNFOLLOW_FAILED
                    message.obj = responseBody.msg
                } else {
                    message.what = MSG_UNFOLLOW_SUCCESS
                    Log.i("LifeUp 用户模块", "[取消关注用户]请求成功")
                }
                uiHandler.handleMessage(message)
            }
        })
    }


    fun getUserFollower(pageVO: PageVO<TeamMembaerListVO>, userId: Long) {
        Log.i("LifeUp 用户模块", "执行[查询用户粉丝列表]操作")

        val currentPage = pageVO.currentPage ?: 0
        val size = pageVO.size ?: 0

        if (currentPage == 0L || size == 0L)
            return

        val call = when (userId) {
            USER_ME -> network.getUserFollower(userService.getToken(), currentPage, size)
            else -> network.getUserFollower(userService.getToken(), userId, currentPage, size)
        }

        call.enqueue(object : Callback<ResultVO<PageVO<TeamMembaerListVO>>> {
            override fun onFailure(call: Call<ResultVO<PageVO<TeamMembaerListVO>>>?, t: Throwable?) {
                Log.e("LifeUp 用户模块", "[查询用户粉丝列表]返回错误: ${t.toString()}")
                val message = Message()
                message.what = AttributeConstants.MSG_CONNECT_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<PageVO<TeamMembaerListVO>>>, response: Response<ResultVO<PageVO<TeamMembaerListVO>>>) {
                val responseBody = response.body()
                Log.i("LifeUp", responseBody?.msg)

                val message = Message()

                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 用户模块", "[查询用户粉丝列表]请求失败：错误或失效TOKEN")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {
                    message.what = NetworkConstants.MSG_GET_TEAM_MEMBER_LIST_SUCCESS
                    val list = responseBody?.data
                    message.obj = list
                    Log.i("LifeUp 用户模块", "[查询用户粉丝列表]请求成功：${list}")
                }
                uiHandler.handleMessage(message)
            }
        })
    }

    fun getUserFollowing(pageVO: PageVO<TeamMembaerListVO>, userId: Long) {
        Log.i("LifeUp 用户模块", "执行[查询用户关注列表]操作")

        val currentPage = pageVO.currentPage ?: 0
        val size = pageVO.size ?: 0

        if (currentPage == 0L || size == 0L)
            return

        val call = when (userId) {
            USER_ME -> network.getUserFollowing(userService.getToken(), currentPage, size)
            else -> network.getUserFollowing(userService.getToken(), userId, currentPage, size)
        }

        call.enqueue(object : Callback<ResultVO<PageVO<TeamMembaerListVO>>> {
            override fun onFailure(call: Call<ResultVO<PageVO<TeamMembaerListVO>>>?, t: Throwable?) {
                Log.e("LifeUp 用户模块", "[查询用户关注列表]返回错误: ${t.toString()}")
                val message = Message()
                message.what = AttributeConstants.MSG_CONNECT_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<PageVO<TeamMembaerListVO>>>, response: Response<ResultVO<PageVO<TeamMembaerListVO>>>) {
                val responseBody = response.body()
                Log.i("LifeUp", responseBody?.msg)

                val message = Message()

                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 用户模块", "[查询用户关注列表]请求失败：错误或失效TOKEN")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {
                    message.what = NetworkConstants.MSG_GET_TEAM_MEMBER_LIST_SUCCESS
                    val list = responseBody?.data
                    message.obj = list
                    Log.i("LifeUp 用户模块", "[查询用户关注列表]请求成功：${list}")
                }
                uiHandler.handleMessage(message)
            }
        })
    }


    fun getMoments(pageVO: PageVO<TeamActivityListVO>) {
        Log.i("LifeUp 成员模块", "执行[查询朋友圈]操作" + userService.getToken())

        val currentPage = pageVO.currentPage ?: 1
        val size = pageVO.size ?: 0

        if (currentPage == 0L || size == 0L)
            return


        val call = network.getMoments(userService.getToken(), currentPage, size)

        call.enqueue(object : Callback<ResultVO<PageVO<TeamActivityListVO>>> {
            override fun onFailure(call: Call<ResultVO<PageVO<TeamActivityListVO>>>?, t: Throwable?) {
                Log.e("LifeUp 用户模块", "[查询朋友圈]返回错误: ${t.toString()}")
                val message = Message()
                message.what = AttributeConstants.MSG_CONNECT_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<PageVO<TeamActivityListVO>>>, response: Response<ResultVO<PageVO<TeamActivityListVO>>>) {
                val responseBody = response.body()
                Log.i("LifeUp", responseBody?.msg)

                val message = Message()

                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 用户模块", "[查询朋友圈]请求失败：错误或失效TOKEN")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {
                    message.what = MSG_GET_MOMENTS_SUCCESS
                    val list = responseBody?.data
                    message.obj = list
                    Log.i("LifeUp 用户模块", "[查询朋友圈]请求成功：${list}")
                }
                uiHandler.handleMessage(message)
            }
        })
    }
}