package net.sarasarasa.lifeup.network

import net.sarasarasa.lifeup.vo.PageVO
import net.sarasarasa.lifeup.vo.ResultVO
import net.sarasarasa.lifeup.vo.TeamMembaerListVO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface AchievementNetwork {

    @GET("/achieve/rank/following")
    fun getLeaderboard(@Header("authenticity-token") token: String, @Query("currentPage") currentPage: Long, @Query("size") size: Long): Call<ResultVO<PageVO<TeamMembaerListVO>>>

}