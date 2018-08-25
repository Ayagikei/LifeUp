package net.sarasarasa.lifeup.network

import net.sarasarasa.lifeup.vo.ProfileVO
import net.sarasarasa.lifeup.vo.ResultVO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface UserNetwork {

    @GET("/user/profile")
    fun getUserProfile(@Header("AUTHENTICITY_TOKEN") token: String): Call<ResultVO<ProfileVO>>

    @PUT("/user/profile")
    fun updateUserProfile(@Header("AUTHENTICITY_TOKEN") token: String, @Body profileVO: ProfileVO): Call<ResultVO<ProfileVO>>

}