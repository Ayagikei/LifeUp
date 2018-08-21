package net.sarasarasa.lifeup.network.impl

import android.os.Handler
import android.os.Message
import android.util.Log
import com.google.gson.internal.LinkedTreeMap
import net.sarasarasa.lifeup.VO.ResultVO
import net.sarasarasa.lifeup.base.BaseNetwork
import net.sarasarasa.lifeup.constants.LoginConstants
import net.sarasarasa.lifeup.network.LoginNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginNetworkImpl(var uiHandler: Handler.Callback) : BaseNetwork() {

    fun getYBLoginUrl(): String {
        val network = retrofit.create(LoginNetwork::class.java)

        val call = network.getYBLoginUrl()

        var str = ""

        call.enqueue(object : Callback<ResultVO<String>> {
            override fun onFailure(call: Call<ResultVO<String>>?, t: Throwable?) {
                val message = Message()
                message.what = LoginConstants.MSG_URL_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<String>>, response: Response<ResultVO<String>>) {
                val url = response.body()?.data as String?

                val message = Message()
                message.what = LoginConstants.MSG_URL_SUCCESS
                message.obj = url
                uiHandler.handleMessage(message)
            }

        })

        return str
    }

    fun getYBLoginInfo(code: String) {
        val network = retrofit.create(LoginNetwork::class.java)

        val call = network.getYBLoginInfo(code)

        var str: LinkedTreeMap<*, *>

        call.enqueue(object : Callback<ResultVO<String>> {
            override fun onFailure(call: Call<ResultVO<String>>?, t: Throwable?) {
                Log.e("NETWORKERROR", t.toString())
            }

            override fun onResponse(call: Call<ResultVO<String>>?, response: Response<ResultVO<String>>?) {
                val resultVO = response?.body()
                Log.e("YBinfo", resultVO?.data.toString())

                if (resultVO != null)
                    str = resultVO.data as LinkedTreeMap<*, *>

                Log.e("YBinfo", resultVO?.data.toString())
            }

        })

    }


}