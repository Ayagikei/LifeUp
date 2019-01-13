package net.sarasarasa.lifeup.network.impl

import android.os.Handler
import android.os.Message
import android.util.Log
import net.sarasarasa.lifeup.base.BaseNetwork
import net.sarasarasa.lifeup.constants.AttributeConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_PHONE_REGISTER_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_QQ_LOGIN_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_QQ_LOGIN_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_URL_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_URL_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_YB_LOGIN_SUCCESS
import net.sarasarasa.lifeup.network.LoginNetwork
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.MobVO
import net.sarasarasa.lifeup.vo.ResultVO
import net.sarasarasa.lifeup.vo.SignUpVO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginNetworkImpl(var uiHandler: Handler.Callback) : BaseNetwork() {

    val userService = UserServiceImpl()
    val network: LoginNetwork = retrofit.create(LoginNetwork::class.java)

    fun getYBLoginUrl(): String {

        Log.i("LifeUp 登录模块", "执行[获取易班登录URL]操作")

        val call = network.getYBLoginUrl()
        var str = ""

        call.enqueue(object : Callback<ResultVO<String>> {
            override fun onFailure(call: Call<ResultVO<String>>?, t: Throwable?) {
                val message = Message()
                message.what = MSG_URL_FAILED
                uiHandler.handleMessage(message)
                Log.i("LifeUp 登录模块", "[获取易班登录URL]请求返回错误")
            }

            override fun onResponse(call: Call<ResultVO<String>>, response: Response<ResultVO<String>>) {
                val url = response.body()?.data

                Log.i("LifeUp", response.message())

                val message = Message()
                message.what = MSG_URL_SUCCESS
                message.obj = url
                uiHandler.handleMessage(message)

                Log.i("LifeUp 登录模块", "[获取易班登录URL]请求成功")
            }

        })

        return str
    }

    fun getYBLoginInfo(code: String) {
        Log.i("LifeUp 登录模块", "执行[发送易班授权CODE]操作")

        val call = network.getYBLoginInfo(code)
        var str: String

        call.enqueue(object : Callback<ResultVO<String>> {
            override fun onFailure(call: Call<ResultVO<String>>?, t: Throwable?) {
                Log.e("LifeUp 登录模块", "[发送易班授权CODE]返回错误: ${t.toString()}")
            }

            override fun onResponse(call: Call<ResultVO<String>>?, response: Response<ResultVO<String>>?) {
                val resultVO = response?.body()
                Log.e("Profile", resultVO.toString())



                if (resultVO?.data != null) {
                    Log.i("LifeUp 登录模块", "[发送易班授权CODE]请求成功")
                    str = resultVO.data
                    userService.saveToken(str)
                    val message = Message()
                    message.what = MSG_YB_LOGIN_SUCCESS
                    uiHandler.handleMessage(message)
                }
            }
        })
    }

    fun loginOrSignUpBySMS(mobVO: MobVO) {
        Log.i("LifeUp 登录模块", "执行[使用短信验证登录]操作")

        val call = network.loginOrSignUpBySMS(mobVO)
        var str: String

        call.enqueue(object : Callback<ResultVO<String>> {
            override fun onFailure(call: Call<ResultVO<String>>?, t: Throwable?) {
                Log.e("LifeUp 登录模块", "[使用短信验证登录]返回错误: ${t.toString()}")
            }

            override fun onResponse(call: Call<ResultVO<String>>?, response: Response<ResultVO<String>>?) {
                val resultVO = response?.body()
                Log.e("Profile", resultVO.toString())

                if (resultVO?.data != null) {
                    Log.i("LifeUp 登录模块", "[使用短信验证登录]请求成功")
                    str = resultVO.data
                    userService.saveToken(str)

                    val message = Message()
                    message.what = MSG_QQ_LOGIN_SUCCESS
                    uiHandler.handleMessage(message)
                }
            }
        })
    }

    fun loginOrSignUpByQQ(signUpVO: SignUpVO) {
        Log.i("LifeUp 登录模块", "执行[使用QQ授权登录或注册]操作")

        val call = network.loginOrSignUpByQQ(signUpVO)
        var str: String

        call.enqueue(object : Callback<ResultVO<String>> {
            override fun onFailure(call: Call<ResultVO<String>>?, t: Throwable?) {
                Log.e("LifeUp 登录模块", "[使用QQ授权登录或注册]返回错误: ${t.toString()}")
                ToastUtils.showShortToast("[使用QQ授权登录或注册]返回错误:" + t.toString())

                val message = Message()
                message.what = AttributeConstants.MSG_CONNECT_FAILED
                uiHandler.handleMessage(message)
            }

            override fun onResponse(call: Call<ResultVO<String>>?, response: Response<ResultVO<String>>?) {
                val resultVO = response?.body()

                if (resultVO != null)
                    Log.e("Profile", resultVO.toString())

                val message = Message()
                if (resultVO?.data != null) {
                    Log.i("LifeUp 登录模块", "[使用QQ授权登录或注册]请求成功")
                    str = resultVO.data
                    userService.saveToken(str)
                    message.what = MSG_QQ_LOGIN_SUCCESS

                } else {
                    message.what = MSG_QQ_LOGIN_FAILED
                    message.obj = resultVO?.msg
                }

                uiHandler.handleMessage(message)
            }
        })
    }

    fun registerByPhone(signUpVO: SignUpVO) {
        Log.i("LifeUp 登录模块", "执行[使用手机号注册]操作")

        val call = network.registerByPhone(signUpVO)
        var str: String

        call.enqueue(object : Callback<ResultVO<String>> {
            override fun onFailure(call: Call<ResultVO<String>>?, t: Throwable?) {
                Log.e("LifeUp 登录模块", "[使用手机号注册]返回错误: ${t.toString()}")
            }

            override fun onResponse(call: Call<ResultVO<String>>?, response: Response<ResultVO<String>>?) {
                val resultVO = response?.body()
                if (resultVO != null)
                    Log.e("Profile", resultVO.toString())

                val message = Message()
                message.obj = resultVO?.msg

                if (resultVO?.data != null) {
                    Log.i("LifeUp 登录模块", "[使用手机号注册]请求成功")
                    str = resultVO.data
                    userService.saveToken(str)

                    message.what = MSG_PHONE_REGISTER_SUCCESS
                }
                uiHandler.handleMessage(message)
            }
        })
    }

    fun loginByPhone(signUpVO: SignUpVO) {
        Log.i("LifeUp 登录模块", "执行[使用手机号登录]操作")

        val call = network.loginByPhone(signUpVO)
        var str: String

        call.enqueue(object : Callback<ResultVO<String>> {
            override fun onFailure(call: Call<ResultVO<String>>?, t: Throwable?) {
                Log.e("LifeUp 登录模块", "[使用手机号登录]返回错误: ${t.toString()}")
            }

            override fun onResponse(call: Call<ResultVO<String>>?, response: Response<ResultVO<String>>?) {
                val resultVO = response?.body()

                val message = Message()
                message.obj = resultVO?.msg

                if (resultVO?.data != null) {
                    Log.i("LifeUp 登录模块", "[使用手机号登录]请求成功")
                    str = resultVO.data
                    userService.saveToken(str)

                    message.what = MSG_PHONE_REGISTER_SUCCESS

                }

                uiHandler.handleMessage(message)
            }
        })
    }

}