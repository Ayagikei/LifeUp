package net.sarasarasa.lifeup.network

import net.sarasarasa.lifeup.vo.ResultVO
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginNetwork {

    @GET("/auth/yb")
    fun getYBLoginUrl(): Call<ResultVO<String>>

    @FormUrlEncoded
    @POST("/auth/yb/login")
    fun getYBLoginInfo(@Field("code") code: String): Call<ResultVO<String>>



}