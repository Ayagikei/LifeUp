package net.sarasarasa.lifeup.network

import net.sarasarasa.lifeup.VO.ResultVO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginNetwork {

    @GET("/oauth/yb")
    fun getYBLoginUrl(): Call<ResultVO<String>>

    @GET("/oauth/yb/login")
    fun getYBLoginInfo(@Query("code") code: String): Call<ResultVO<String>>
}