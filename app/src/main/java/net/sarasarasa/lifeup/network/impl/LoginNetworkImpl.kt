package net.sarasarasa.lifeup.network.impl

import android.os.Handler
import android.os.Message
import android.util.Log
import net.sarasarasa.lifeup.base.BaseNetwork
import net.sarasarasa.lifeup.constants.LoginConstants
import net.sarasarasa.lifeup.network.LoginNetwork
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.vo.MobVO
import net.sarasarasa.lifeup.vo.ResultVO
import net.sarasarasa.lifeup.vo.SignUpVO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginNetworkImpl(var uiHandler: Handler.Callback) : BaseNetwork() {

    val userService = UserServiceImpl()

    fun getYBLoginUrl(): String {

        Log.i("LifeUp 登陆模块", "执行[获取易班登录URL]操作")

        val network = retrofit.create(LoginNetwork::class.java)
        val call = network.getYBLoginUrl()

        var str = ""

        call.enqueue(object : Callback<ResultVO<String>> {
            override fun onFailure(call: Call<ResultVO<String>>?, t: Throwable?) {
                val message = Message()
                message.what = LoginConstants.MSG_URL_FAILED
                uiHandler.handleMessage(message)
                Log.i("LifeUp 登陆模块", "[获取易班登录URL]请求返回错误")
            }

            override fun onResponse(call: Call<ResultVO<String>>, response: Response<ResultVO<String>>) {
                val url = response.body()?.data

                Log.i("LifeUp", response.message())

                val message = Message()
                message.what = LoginConstants.MSG_URL_SUCCESS
                message.obj = url
                uiHandler.handleMessage(message)

                Log.i("LifeUp 登陆模块", "[获取易班登录URL]请求成功")
            }

        })

        return str
    }

    fun getYBLoginInfo(code: String) {
        Log.i("LifeUp 登陆模块", "执行[发送易班授权CODE]操作")

        val network = retrofit.create(LoginNetwork::class.java)
        val call = network.getYBLoginInfo(code)

        var str: String

        call.enqueue(object : Callback<ResultVO<String>> {
            override fun onFailure(call: Call<ResultVO<String>>?, t: Throwable?) {
                Log.e("LifeUp 登陆模块", "[发送易班授权CODE]返回错误: ${t.toString()}")
            }

            override fun onResponse(call: Call<ResultVO<String>>?, response: Response<ResultVO<String>>?) {
                val resultVO = response?.body()
                Log.e("Profile", resultVO.toString())



                if (resultVO?.data != null) {
                    Log.i("LifeUp 登陆模块", "[发送易班授权CODE]请求成功")
                    str = resultVO.data
                    userService.saveToken(str)
                    val message = Message()
                    message.what = LoginConstants.MSG_YB_LOGIN_SUCCESS
                    uiHandler.handleMessage(message)
                }
            }
        })
    }

    fun loginOrSignUpBySMS(mobVO: MobVO) {

        Log.i("LifeUp 登陆模块", "执行[使用短信验证登录]操作")

        val network = retrofit.create(LoginNetwork::class.java)
        val call = network.loginOrSignUpBySMS(mobVO)

        var str: String

        call.enqueue(object : Callback<ResultVO<String>> {
            override fun onFailure(call: Call<ResultVO<String>>?, t: Throwable?) {
                Log.e("LifeUp 登陆模块", "[使用短信验证登录]返回错误: ${t.toString()}")
            }

            override fun onResponse(call: Call<ResultVO<String>>?, response: Response<ResultVO<String>>?) {
                val resultVO = response?.body()
                Log.e("Profile", resultVO.toString())

                if (resultVO?.data != null) {
                    Log.i("LifeUp 登陆模块", "[使用短信验证登录]请求成功")
                    str = resultVO.data
                    userService.saveToken(str)
                    val message = Message()
                    message.what = LoginConstants.MSG_QQ_LOGIN_SUCCESS
                    uiHandler.handleMessage(message)
                }
            }
        })
    }

    fun loginOrSignUpByQQ(signUpVO: SignUpVO) {

        Log.i("LifeUp 登陆模块", "执行[使用QQ授权登录或注册]操作")

        val network = retrofit.create(LoginNetwork::class.java)
        val call = network.loginOrSignUpByQQ(signUpVO)

        var str: String

        call.enqueue(object : Callback<ResultVO<String>> {
            override fun onFailure(call: Call<ResultVO<String>>?, t: Throwable?) {
                Log.e("LifeUp 登陆模块", "[使用QQ授权登录或注册]返回错误: ${t.toString()}")
            }

            override fun onResponse(call: Call<ResultVO<String>>?, response: Response<ResultVO<String>>?) {
                val resultVO = response?.body()
                Log.e("Profile", resultVO.toString())

                if (resultVO?.data != null) {
                    Log.i("LifeUp 登陆模块", "[使用QQ授权登录或注册]请求成功")
                    str = resultVO.data
                    userService.saveToken(str)
                    val message = Message()
                    message.what = LoginConstants.MSG_QQ_LOGIN_SUCCESS
                    uiHandler.handleMessage(message)
                }
            }
        })
    }

    fun registerByPhone(signUpVO: SignUpVO) {

        Log.i("LifeUp 登录模块", "执行[使用手机号注册]操作")

        val network = retrofit.create(LoginNetwork::class.java)
        val call = network.registerByPhone(signUpVO)

        var str: String

        call.enqueue(object : Callback<ResultVO<String>> {
            override fun onFailure(call: Call<ResultVO<String>>?, t: Throwable?) {
                Log.e("LifeUp 登陆模块", "[使用手机号注册]返回错误: ${t.toString()}")
            }

            override fun onResponse(call: Call<ResultVO<String>>?, response: Response<ResultVO<String>>?) {
                val resultVO = response?.body()
                Log.e("Profile", resultVO.toString())
                val message = Message()
                message.obj = resultVO?.msg

                if (resultVO?.data != null) {
                    Log.i("LifeUp 登陆模块", "[使用手机号注册]请求成功")
                    str = resultVO.data
                    userService.saveToken(str)

                    message.what = LoginConstants.MSG_PHONE_REGISTER_SUCCESS
                }
                uiHandler.handleMessage(message)
            }
        })
    }

    fun loginByPhone(signUpVO: SignUpVO) {

        Log.i("LifeUp 登录模块", "执行[使用手机号登录]操作")

        val network = retrofit.create(LoginNetwork::class.java)
        val call = network.loginByPhone(signUpVO)

        var str: String

        call.enqueue(object : Callback<ResultVO<String>> {
            override fun onFailure(call: Call<ResultVO<String>>?, t: Throwable?) {
                Log.e("LifeUp 登陆模块", "[使用手机号登录]返回错误: ${t.toString()}")
            }

            override fun onResponse(call: Call<ResultVO<String>>?, response: Response<ResultVO<String>>?) {
                val resultVO = response?.body()

                val message = Message()
                message.obj = resultVO?.msg

                if (resultVO?.data != null) {
                    Log.i("LifeUp 登陆模块", "[使用手机号登录]请求成功")
                    str = resultVO.data
                    userService.saveToken(str)

                    message.what = LoginConstants.MSG_PHONE_REGISTER_SUCCESS
                    uiHandler.handleMessage(message)
                }
            }
        })
    }

}