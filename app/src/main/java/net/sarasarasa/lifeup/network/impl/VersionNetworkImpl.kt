package net.sarasarasa.lifeup.network.impl

import android.os.Handler
import android.os.Message
import android.util.Log
import net.sarasarasa.lifeup.base.BaseNetwork
import net.sarasarasa.lifeup.constants.AttributeConstants.Companion.MSG_CONNECT_FAILED
import net.sarasarasa.lifeup.constants.VersionConstants.Companion.MSG_NEW_VERSION
import net.sarasarasa.lifeup.constants.VersionConstants.Companion.MSG_NO_NEW_VERSION
import net.sarasarasa.lifeup.network.VersionNetwork
import net.sarasarasa.lifeup.vo.ResultVO
import net.sarasarasa.lifeup.vo.VersionVO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VersionNetworkImpl(var uiHandler: Handler.Callback) : BaseNetwork() {

    val network: VersionNetwork = retrofit.create(VersionNetwork::class.java)

    fun checkUpdate(localVersion: Int) {
        Log.i("LifeUp 版本模块", "执行[检测版本]操作")

        val call = network.checkUpdate()

        call.enqueue(object : Callback<ResultVO<VersionVO>> {
            override fun onFailure(call: Call<ResultVO<VersionVO>>?, t: Throwable?) {
                Log.e("LifeUp 版本模块", "[检测版本]返回错误: ${t.toString()}")
                val message = Message()
                message.what = MSG_CONNECT_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<VersionVO>>, response: Response<ResultVO<VersionVO>>) {
                val responseBody = response.body()
                val message = Message()
                val versionVO = responseBody?.data
                val remoteVersion = versionVO?.newVersion

                Log.i("LifeUp 版本模块", "[检测版本]请求成功：${versionVO}")


                if (remoteVersion != null && remoteVersion > localVersion) {
                    message.what = MSG_NEW_VERSION
                    message.obj = versionVO.downloadUrl
                } else {
                    message.what = MSG_NO_NEW_VERSION
                }

                uiHandler.handleMessage(message)
            }
        })
    }

}