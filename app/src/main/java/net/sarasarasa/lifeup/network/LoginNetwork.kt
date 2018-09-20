package net.sarasarasa.lifeup.network

import net.sarasarasa.lifeup.vo.MobVO
import net.sarasarasa.lifeup.vo.ResultVO
import net.sarasarasa.lifeup.vo.SignUpVO
import retrofit2.Call
import retrofit2.http.*

interface LoginNetwork {

    @GET("/auth/yb")
    fun getYBLoginUrl(): Call<ResultVO<String>>

    @FormUrlEncoded
    @POST("/auth/yb/login")
    fun getYBLoginInfo(@Field("code") code: String): Call<ResultVO<String>>

    @POST("/auth/code/login")
    fun loginOrSignUpBySMS(@Body mobVO: MobVO): Call<ResultVO<String>>

    @POST("/auth/qq/login")
    fun loginOrSignUpByQQ(@Body signUpVO: SignUpVO): Call<ResultVO<String>>

    @POST("/auth/register")
    fun registerByPhone(@Body signUpVO: SignUpVO): Call<ResultVO<String>>

    @POST("/auth/phone/login")
    fun loginByPhone(@Body signUpVO: SignUpVO): Call<ResultVO<String>>
}