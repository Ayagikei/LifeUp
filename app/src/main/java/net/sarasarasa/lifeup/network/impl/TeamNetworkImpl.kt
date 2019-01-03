package net.sarasarasa.lifeup.network.impl

import android.os.Handler
import android.os.Message
import android.util.Log
import net.sarasarasa.lifeup.base.BaseNetwork
import net.sarasarasa.lifeup.constants.AttributeConstants.Companion.MSG_CONNECT_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_ADD_TEAM_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_ADD_TEAM_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_EDIT_TEAM_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_EDIT_TEAM_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_END_TEAM_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_FINISH_TEAM_TASK
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_NEXT_TEAM_ACTIVITIES_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_TEAM_ACTIVITIES_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_TEAM_DETAIL_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_TEAM_LIST_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_TEAM_MEMBER_LIST_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_JOIN_TEAM_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_QUIT_TEAM_SUCCESS
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.network.TeamNetwork
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamNetworkImpl(var uiHandler: Handler.Callback?) : BaseNetwork() {

    val userService = UserServiceImpl()
    val todoService = TodoServiceImpl()
    val network: TeamNetwork = retrofit.create(TeamNetwork::class.java)

    fun getTeamList(pageVO: PageVO<TeamListVO>) {
        Log.i("LifeUp 团队模块", "执行[查询团队列表]操作" + userService.getToken())

        val currentPage = pageVO.currentPage ?: 0
        val size = pageVO.size ?: 0

        if (currentPage == 0L || size == 0L)
            return


        val call = network.getTeamList(userService.getToken(), currentPage, size)

        call.enqueue(object : Callback<ResultVO<PageVO<TeamListVO>>> {
            override fun onFailure(call: Call<ResultVO<PageVO<TeamListVO>>>?, t: Throwable?) {
                Log.e("LifeUp 团队模块", "[查询团队列表]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler?.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<PageVO<TeamListVO>>>, response: Response<ResultVO<PageVO<TeamListVO>>>) {
                val responseBody = response.body()
                Log.i("LifeUp", responseBody?.msg)

                val message = Message()

                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 团队模块", "[查询团队列表]请求失败：错误或失效TOKEN")
                    ToastUtils.showShortToast("登陆已失效，请重新登陆！")
                    userService.saveToken("")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {
                    message.what = MSG_GET_TEAM_LIST_SUCCESS
                    val list = responseBody?.data
                    message.obj = list
                    Log.i("LifeUp 团队模块", "[查询团队列表]请求成功：${list}")
                }
                uiHandler?.handleMessage(message)
            }
        })
    }

    fun getTeamList(pageVO: PageVO<TeamListVO>, searchText: String) {
        Log.i("LifeUp 团队模块", "执行[搜索团队列表]操作" + userService.getToken())

        val currentPage = pageVO.currentPage ?: 0
        val size = pageVO.size ?: 0

        if (currentPage == 0L || size == 0L)
            return


        val call = if (searchText.isBlank())
            network.getTeamList(userService.getToken(), currentPage, size)
        else network.searchTeamList(userService.getToken(), currentPage, size, searchText.trim())

        call.enqueue(object : Callback<ResultVO<PageVO<TeamListVO>>> {
            override fun onFailure(call: Call<ResultVO<PageVO<TeamListVO>>>?, t: Throwable?) {
                Log.e("LifeUp 团队模块", "[搜索团队列表]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler?.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<PageVO<TeamListVO>>>, response: Response<ResultVO<PageVO<TeamListVO>>>) {
                val responseBody = response.body()
                Log.i("LifeUp", responseBody?.msg)

                val message = Message()

                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 团队模块", "[搜索团队列表]请求失败：错误或失效TOKEN")
                    ToastUtils.showShortToast("登陆已失效，请重新登陆！")
                    userService.saveToken("")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {
                    message.what = MSG_GET_TEAM_LIST_SUCCESS
                    val list = responseBody?.data
                    message.obj = list
                    Log.i("LifeUp 团队模块", "[搜索团队列表]请求成功：${list}")
                }
                uiHandler?.handleMessage(message)
            }
        })
    }


    fun getTeamMembersList(pageVO: PageVO<TeamMembaerListVO>, teamId: Long) {
        Log.i("LifeUp 团队模块", "执行[查询团队成员列表]操作")

        val currentPage = pageVO.currentPage ?: 0
        val size = pageVO.size ?: 0

        if (currentPage == 0L || size == 0L)
            return

        val call = network.getTeamMembers(userService.getToken(), teamId, currentPage, size)

        call.enqueue(object : Callback<ResultVO<PageVO<TeamMembaerListVO>>> {
            override fun onFailure(call: Call<ResultVO<PageVO<TeamMembaerListVO>>>?, t: Throwable?) {
                Log.e("LifeUp 团队模块", "[查询团队成员列表]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler?.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<PageVO<TeamMembaerListVO>>>, response: Response<ResultVO<PageVO<TeamMembaerListVO>>>) {
                val responseBody = response.body()
                Log.i("LifeUp", responseBody?.msg)

                val message = Message()

                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 团队模块", "[查询团队成员列表]请求失败：错误或失效TOKEN")
                    ToastUtils.showShortToast("登陆已失效，请重新登陆！")
                    userService.saveToken("")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {
                    message.what = MSG_GET_TEAM_MEMBER_LIST_SUCCESS
                    val list = responseBody?.data
                    message.obj = list
                    Log.i("LifeUp 团队模块", "[查询团队成员列表]请求成功：${list}")
                }
                uiHandler?.handleMessage(message)
            }
        })
    }

    fun quitTeam(teamId: Long) {
        Log.i("LifeUp 团队模块", "执行[退出团队]操作")

        val call = network.quitTeam(userService.getToken(), teamId)

        call.enqueue(object : Callback<ResultVO<Any>> {
            override fun onFailure(call: Call<ResultVO<Any>>?, t: Throwable?) {
                Log.e("LifeUp 团队模块", "[退出团队]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler?.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<Any>>, response: Response<ResultVO<Any>>) {
                val responseBody = response.body()
                val message = Message()

                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 团队模块", "[退出团队]请求失败：错误或失效TOKEN")
                    ToastUtils.showShortToast("登陆已失效，请重新登陆！")
                    userService.saveToken("")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {
                    message.what = MSG_QUIT_TEAM_SUCCESS

                    todoService.deleteTeamTaskByTeamId(teamId)
                    Log.i("LifeUp 团队模块", "[退出团队]请求成功")
                }
                uiHandler?.handleMessage(message)
            }
        })
    }

    fun endTeam(teamId: Long) {
        Log.i("LifeUp 团队模块", "执行[终止团队]操作")

        val call = network.endTeam(userService.getToken(), teamId)

        call.enqueue(object : Callback<ResultVO<Any>> {
            override fun onFailure(call: Call<ResultVO<Any>>?, t: Throwable?) {
                Log.e("LifeUp 团队模块", "[终止团队]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler?.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<Any>>, response: Response<ResultVO<Any>>) {
                val responseBody = response.body()
                val message = Message()

                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 团队模块", "[终止团队]请求失败：错误或失效TOKEN")
                    ToastUtils.showShortToast("登陆已失效，请重新登陆！")
                    userService.saveToken("")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {
                    message.what = MSG_END_TEAM_SUCCESS

                    todoService.deleteTeamTaskByTeamId(teamId)
                    Log.i("LifeUp 团队模块", "[终止团队]请求成功")
                }
                uiHandler?.handleMessage(message)
            }
        })
    }

    fun getTeamActivityList(pageVO: PageVO<TeamActivityListVO>, teamId: Long) {
        Log.i("LifeUp 团队模块", "执行[查询团队动态列表]操作")

        val currentPage = pageVO.currentPage ?: 0
        val size = pageVO.size ?: 0

        if (currentPage == 0L || size == 0L)
            return

        val call = network.getTeamActivity(userService.getToken(), teamId, currentPage, size)

        call.enqueue(object : Callback<ResultVO<PageVO<TeamActivityListVO>>> {
            override fun onFailure(call: Call<ResultVO<PageVO<TeamActivityListVO>>>?, t: Throwable?) {
                Log.e("LifeUp 团队模块", "[查询团队动态列表]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler?.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<PageVO<TeamActivityListVO>>>, response: Response<ResultVO<PageVO<TeamActivityListVO>>>) {
                val responseBody = response.body()
                Log.i("LifeUp", responseBody?.msg)

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 团队模块", "[查询团队动态列表]请求失败：错误或失效TOKEN")
                    ToastUtils.showShortToast("登陆已失效，请重新登陆！")
                    userService.saveToken("")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {
                    message.what = MSG_GET_TEAM_ACTIVITIES_SUCCESS
                    val list = responseBody?.data
                    message.obj = list
                    Log.i("LifeUp 团队模块", "[查询团队动态列表]请求成功：${list}")
                }
                uiHandler?.handleMessage(message)
            }
        })
    }


    fun addTeam(teamVO: TeamVO) {
        Log.i("LifeUp 团队模块", "执行[新建团队]操作")

        if (teamVO.teamHead.isNullOrEmpty()) {
            teamVO.teamHead = userService.getMine().userHead
        }

        val call = network.addTeam(userService.getToken(), teamVO)

        call.enqueue(object : Callback<ResultVO<TeamTaskVO>> {
            override fun onFailure(call: Call<ResultVO<TeamTaskVO>>?, t: Throwable?) {
                Log.e("LifeUp 团队模块", "[新建团队]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler?.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<TeamTaskVO>>, response: Response<ResultVO<TeamTaskVO>>) {
                val responseBody = response.body()
                Log.i("LifeUp", responseBody?.msg)

                val message = Message()

                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 团队模块", "[新建团队]请求失败：错误或失效TOKEN")
                    ToastUtils.showShortToast("登陆已失效，请重新登陆！")
                    userService.saveToken("")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {

                    val teamTaskVO = responseBody?.data

                    if (teamTaskVO != null) {
                        message.what = MSG_ADD_TEAM_SUCCESS
                        message.obj = teamTaskVO
                        todoService.addOrUpdateTeamTask(teamTaskVO)
                    } else {
                        message.what = MSG_ADD_TEAM_FAILED
                        message.obj = responseBody?.msg
                    }

                    Log.i("LifeUp 团队模块", "[新建团队]请求成功：${teamTaskVO}")
                }
                uiHandler?.handleMessage(message)
            }
        })
    }

    fun editTeam(teamEditVO: TeamEditVO) {
        Log.i("LifeUp 团队模块", "执行[修改团队]操作")

        if (teamEditVO.teamId == null || teamEditVO.teamId == -1L)
            return

        ToastUtils.showLongToast(teamEditVO.toString())

        val call = network.editTeam(userService.getToken(), teamEditVO, teamEditVO.teamId!!)

        call.enqueue(object : Callback<ResultVO<Any>> {
            override fun onFailure(call: Call<ResultVO<Any>>?, t: Throwable?) {
                Log.e("LifeUp 团队模块", "[修改团队]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler?.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<Any>>, response: Response<ResultVO<Any>>) {
                val responseBody = response.body()
                Log.i("LifeUp", responseBody?.msg)

                val message = Message()

                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 团队模块", "[修改团队]请求失败：错误或失效TOKEN")
                    ToastUtils.showShortToast("登陆已失效，请重新登陆！")
                    userService.saveToken("")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {

                    val messageResponse = responseBody?.msg

                    if (messageResponse.equals("success")) {
                        message.what = MSG_EDIT_TEAM_SUCCESS
                    } else {
                        message.what = MSG_EDIT_TEAM_FAILED
                        message.obj = responseBody?.msg
                    }

                    Log.i("LifeUp 团队模块", "[修改团队]请求成功：${messageResponse}")
                }
                uiHandler?.handleMessage(message)
            }
        })
    }

    fun getTeamDetail(teamId: Long) {
        Log.i("LifeUp 团队模块", "执行[团队信息]操作")

        val call = network.getTeamDetail(userService.getToken(), teamId)

        call.enqueue(object : Callback<ResultVO<TeamDetailVO>> {
            override fun onFailure(call: Call<ResultVO<TeamDetailVO>>?, t: Throwable?) {
                Log.e("LifeUp 团队模块", "[团队信息]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler?.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<TeamDetailVO>>, response: Response<ResultVO<TeamDetailVO>>) {
                val responseBody = response.body()

                if (responseBody?.msg != null)
                    Log.i("LifeUp", responseBody.msg)

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 团队模块", "[团队信息]请求失败：错误或失效TOKEN")
                    ToastUtils.showShortToast("登陆已失效，请重新登陆！")
                    userService.saveToken("")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {
                    message.what = MSG_GET_TEAM_DETAIL_SUCCESS
                    val teamDetailVO = responseBody?.data
                    message.obj = teamDetailVO
                    Log.i("LifeUp 团队模块", "[团队信息]请求成功：${teamDetailVO}")
                }
                uiHandler?.handleMessage(message)
            }
        })
    }


    fun joinTheTeam(teamDetailVO: TeamDetailVO) {
        Log.i("LifeUp 团队模块", "执行[加入团队]操作")

        val call = network.joinTheTeam(userService.getToken(), teamDetailVO.teamId ?: -1)

        call.enqueue(object : Callback<ResultVO<TeamTaskVO>> {
            override fun onFailure(call: Call<ResultVO<TeamTaskVO>>?, t: Throwable?) {
                Log.e("LifeUp 团队模块", "[加入团队]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler?.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<TeamTaskVO>>, response: Response<ResultVO<TeamTaskVO>>) {
                val responseBody = response.body()

                if (responseBody?.msg != null)
                    Log.i("LifeUp", responseBody.msg)

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 团队模块", "[加入团队]请求失败：错误或失效TOKEN")
                    ToastUtils.showShortToast("登陆已失效，请重新登陆！")
                    userService.saveToken("")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {
                    message.what = MSG_JOIN_TEAM_SUCCESS
                    val teamTaskVO = responseBody?.data
                    message.obj = teamTaskVO

                    if (teamTaskVO != null) {
                        todoService.addOrUpdateTeamTask(teamTaskVO)
                    }

                    Log.i("LifeUp 团队模块", "[加入团队]请求成功：${teamTaskVO}")
                }
                uiHandler?.handleMessage(message)
            }
        })
    }


    fun getNextTeamTask(teamId: Long) {
        Log.i("LifeUp 团队模块", "执行[领取团队事项]操作")

        val call = network.getNextTeamTask(userService.getToken(), teamId)

        call.enqueue(object : Callback<ResultVO<TeamTaskVO>> {
            override fun onFailure(call: Call<ResultVO<TeamTaskVO>>?, t: Throwable?) {
                Log.e("LifeUp 团队模块", "[领取团队事项]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler?.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<TeamTaskVO>>, response: Response<ResultVO<TeamTaskVO>>) {
                val responseBody = response.body()

                if (responseBody?.msg != null)
                    Log.i("LifeUp", responseBody.msg)

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 团队模块", "[领取团队事项]请求失败：错误或失效TOKEN")
                    ToastUtils.showShortToast("登陆已失效，请重新登陆！")
                    userService.saveToken("")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {
                    message.what = MSG_GET_NEXT_TEAM_ACTIVITIES_SUCCESS
                    val teamTaskVO = responseBody?.data

                    if (teamTaskVO != null) {
                        //添加新的事项
                        if (todoService.addOrUpdateTeamTask(teamTaskVO))
                            message.obj = "成功领取事项"
                        else message.obj = "事项已领取，请在事项逾期的情况下领取！"
                    }

                    Log.i("LifeUp 团队模块", "[领取团队事项]请求成功：${teamTaskVO}")
                }
                uiHandler?.handleMessage(message)
            }
        })
    }


    fun finishTeamTask(item: TaskModel, activityVO: ActivityVO) {
        Log.i("LifeUp 团队模块", "执行[完成团队事项]操作")

        val call = network.finishTeamTask(userService.getToken(), item.teamId, activityVO)

        call.enqueue(object : Callback<ResultVO<TeamTaskVO>> {
            override fun onFailure(call: Call<ResultVO<TeamTaskVO>>?, t: Throwable?) {
                Log.e("LifeUp 团队模块", "[完成团队事项]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler?.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<TeamTaskVO>>, response: Response<ResultVO<TeamTaskVO>>) {
                val responseBody = response.body()

                if (responseBody?.msg != null)
                    Log.i("LifeUp", responseBody.msg)

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 团队模块", "[完成团队事项]请求失败：错误或失效TOKEN")
                    ToastUtils.showShortToast("登陆已失效，请重新登陆！")
                    userService.saveToken("")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {
                    message.what = MSG_FINISH_TEAM_TASK
                    val teamTaskVO = responseBody?.data

                    if (teamTaskVO != null) {
                        //完成事项
                        todoService.finishTodoItem(item.id)
                        //添加新的事项
                        todoService.addOrUpdateTeamTask(teamTaskVO)
                    }

                    Log.i("LifeUp 团队模块", "[完成团队事项]请求成功：${teamTaskVO}")
                }
                uiHandler?.handleMessage(message)
            }
        })
    }


}