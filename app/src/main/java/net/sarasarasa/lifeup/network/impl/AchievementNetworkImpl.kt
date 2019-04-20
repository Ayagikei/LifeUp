package net.sarasarasa.lifeup.network.impl

import android.os.Handler
import android.os.Message
import android.util.Log
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.application.LifeUpApplication
import net.sarasarasa.lifeup.base.BaseNetwork
import net.sarasarasa.lifeup.constants.AttributeConstants
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.network.AchievementNetwork
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.PageVO
import net.sarasarasa.lifeup.vo.ResultVO
import net.sarasarasa.lifeup.vo.TeamMembaerListVO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AchievementNetworkImpl(var uiHandler: Handler.Callback) : BaseNetwork() {

    val userService = UserServiceImpl()
    val network: AchievementNetwork = retrofit.create(AchievementNetwork::class.java)

    fun getTeamMembersList(pageVO: PageVO<TeamMembaerListVO>) {
        Log.i("LifeUp 成就模块", "执行[查询排行榜]操作")

        val currentPage = pageVO.currentPage ?: 0
        val size = pageVO.size ?: 0

        if (currentPage == 0L || size == 0L)
            return

        val call = network.getLeaderboard(userService.getToken(), currentPage, size)

        call.enqueue(object : Callback<ResultVO<PageVO<TeamMembaerListVO>>> {
            override fun onFailure(call: Call<ResultVO<PageVO<TeamMembaerListVO>>>?, t: Throwable?) {
                Log.e("LifeUp 成就模块", "[查询排行榜]返回错误: ${t.toString()}")
                val message = Message()
                message.what = AttributeConstants.MSG_CONNECT_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<PageVO<TeamMembaerListVO>>>, response: Response<ResultVO<PageVO<TeamMembaerListVO>>>) {
                val responseBody = response.body()

                if (responseBody?.msg != null)
                    Log.i("LifeUp", responseBody.msg)

                val message = Message()

                if (responseBody?.code == NetworkConstants.INVALID_TOKEN) {
                    Log.i("LifeUp 成就模块", "[查询排行榜]请求失败：错误或失效TOKEN")
                    ToastUtils.showShortToast(LifeUpApplication.getLifeUpApplication().getString(R.string.network_login_invaild))
                    userService.saveToken("")
                    message.what = NetworkConstants.INVALID_TOKEN
                } else {
                    message.what = NetworkConstants.MSG_GET_TEAM_MEMBER_LIST_SUCCESS
                    val list = responseBody?.data
                    message.obj = list
                    Log.i("LifeUp 成就模块", "[查询排行榜]请求成功：${list}")
                }
                uiHandler.handleMessage(message)
            }
        })
    }


}