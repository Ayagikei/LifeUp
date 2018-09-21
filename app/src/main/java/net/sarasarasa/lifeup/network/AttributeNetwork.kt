package net.sarasarasa.lifeup.network

import net.sarasarasa.lifeup.vo.AttributionVO
import net.sarasarasa.lifeup.vo.ResultVO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AttributeNetwork {

    @GET("/user/attribute")
    fun getAttribute(@Header("authenticity-token") token: String): Call<ResultVO<AttributionVO>>

    @POST("/user/attribute")
    fun updateAttribute(@Header("authenticity-token") token: String, @Body attributionVO: AttributionVO): Call<ResultVO<AttributionVO>>

}