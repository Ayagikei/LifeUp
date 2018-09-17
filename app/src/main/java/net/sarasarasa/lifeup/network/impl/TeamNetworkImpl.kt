package net.sarasarasa.lifeup.network.impl

import android.os.Handler
import android.os.Message
import android.util.Log
import net.sarasarasa.lifeup.base.BaseNetwork
import net.sarasarasa.lifeup.constants.AttributeConstants.Companion.MSG_CONNECT_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.network.TeamNetwork
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.vo.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamNetworkImpl(var uiHandler: Handler.Callback) : BaseNetwork() {

    val userService = UserServiceImpl()

    fun getTeamList(pageVO: PageVO<TeamListVO>) {
        Log.i("LifeUp 团队模块", "执行[查询团队列表]操作")

        val network = retrofit.create(TeamNetwork::class.java)
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
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<PageVO<TeamListVO>>>, response: Response<ResultVO<PageVO<TeamListVO>>>) {
                val responseBody = response.body()
                Log.i("LifeUp", responseBody?.msg)

                val message = Message()

                if (responseBody?.code == NetworkConstants.INVAILD_TOKEN) {
                    Log.i("LifeUp 团队模块", "[查询团队列表]请求失败：错误或失效TOKEN")
                    message.what = NetworkConstants.INVAILD_TOKEN
                } else {
                    message.what = 300
                    val list = responseBody?.data
                    message.obj = list
                    Log.i("LifeUp 团队模块", "[查询团队列表]请求成功：${list}")
                }
                uiHandler.handleMessage(message)
            }
        })
    }


    fun getTeamMembersList(pageVO: PageVO<TeamMembaerListVO>, teamId: Long) {
        Log.i("LifeUp 团队模块", "执行[查询团队成员列表]操作")

        val network = retrofit.create(TeamNetwork::class.java)
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
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<PageVO<TeamMembaerListVO>>>, response: Response<ResultVO<PageVO<TeamMembaerListVO>>>) {
                val responseBody = response.body()
                Log.i("LifeUp", responseBody?.msg)

                val message = Message()

                if (responseBody?.code == NetworkConstants.INVAILD_TOKEN) {
                    Log.i("LifeUp 团队模块", "[查询团队成员列表]请求失败：错误或失效TOKEN")
                    message.what = NetworkConstants.INVAILD_TOKEN
                } else {
                    message.what = 113
                    val list = responseBody?.data
                    message.obj = list
                    Log.i("LifeUp 团队模块", "[查询团队成员列表]请求成功：${list}")
                }
                uiHandler.handleMessage(message)
            }
        })
    }

    fun getTeamActivityList(pageVO: PageVO<TeamActivityListVO>, teamId: Long) {
        Log.i("LifeUp 团队模块", "执行[查询团队动态列表]操作")

        val network = retrofit.create(TeamNetwork::class.java)
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
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<PageVO<TeamActivityListVO>>>, response: Response<ResultVO<PageVO<TeamActivityListVO>>>) {
                val responseBody = response.body()
                Log.i("LifeUp", responseBody?.msg)

                val message = Message()

                if (responseBody?.code == NetworkConstants.INVAILD_TOKEN) {
                    Log.i("LifeUp 团队模块", "[查询团队动态列表]请求失败：错误或失效TOKEN")
                    message.what = NetworkConstants.INVAILD_TOKEN
                } else {
                    message.what = 400
                    val list = responseBody?.data
                    message.obj = list
                    Log.i("LifeUp 团队模块", "[查询团队动态列表]请求成功：${list}")
                }
                uiHandler.handleMessage(message)
            }
        })
    }


    fun addTeam(teamVO: TeamVO) {
        Log.i("LifeUp 团队模块", "执行[新建团队]操作")

        val network = retrofit.create(TeamNetwork::class.java)
        Log.i("Token", userService.getToken())
        val call = network.addTeam(userService.getToken(), teamVO)

        call.enqueue(object : Callback<ResultVO<TeamTaskVO>> {
            override fun onFailure(call: Call<ResultVO<TeamTaskVO>>?, t: Throwable?) {
                Log.e("LifeUp 团队模块", "[新建团队]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<TeamTaskVO>>, response: Response<ResultVO<TeamTaskVO>>) {
                val responseBody = response.body()
                Log.i("LifeUp", responseBody?.msg)

                val message = Message()

                if (responseBody?.code == NetworkConstants.INVAILD_TOKEN) {
                    Log.i("LifeUp 团队模块", "[新建团队]请求失败：错误或失效TOKEN")
                    message.what = NetworkConstants.INVAILD_TOKEN
                } else {
                    message.what = 200
                    val teamTaskVO = responseBody?.data
                    message.obj = teamTaskVO
                    Log.i("LifeUp 团队模块", "[新建团队]请求成功：${teamTaskVO}")
                }
                uiHandler.handleMessage(message)
            }
        })
    }

    fun getTeamDetail(teamId: Long) {
        Log.i("LifeUp 团队模块", "执行[团队信息]操作")

        val network = retrofit.create(TeamNetwork::class.java)
        Log.i("Token", userService.getToken())
        val call = network.getTeamDetail(userService.getToken(), teamId)

        call.enqueue(object : Callback<ResultVO<TeamDetailVO>> {
            override fun onFailure(call: Call<ResultVO<TeamDetailVO>>?, t: Throwable?) {
                Log.e("LifeUp 团队模块", "[团队信息]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<TeamDetailVO>>, response: Response<ResultVO<TeamDetailVO>>) {
                val responseBody = response.body()
                Log.i("LifeUp", responseBody?.msg)

                val message = Message()
                if (responseBody?.code == NetworkConstants.INVAILD_TOKEN) {
                    Log.i("LifeUp 团队模块", "[团队信息]请求失败：错误或失效TOKEN")
                    message.what = NetworkConstants.INVAILD_TOKEN
                } else {
                    message.what = 200
                    val teamDetailVO = responseBody?.data
                    message.obj = teamDetailVO
                    Log.i("LifeUp 团队模块", "[团队信息]请求成功：${teamDetailVO}")
                }
                uiHandler.handleMessage(message)
            }
        })
    }


}