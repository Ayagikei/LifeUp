package net.sarasarasa.lifeup.network

import net.sarasarasa.lifeup.vo.ResultVO
import net.sarasarasa.lifeup.vo.VersionVO
import retrofit2.Call
import retrofit2.http.GET

interface VersionNetwork {

    @GET("/version")
    fun checkUpdate(): Call<ResultVO<VersionVO>>

}

