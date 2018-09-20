package net.sarasarasa.lifeup.network

import net.sarasarasa.lifeup.vo.*
import retrofit2.Call
import retrofit2.http.*

interface TeamNetwork {

    @GET("/teams")
    fun getTeamList(@Header("AUTHENTICITY_TOKEN") token: String, @Query("currentPage") currentPage: Long, @Query("size") size: Long): Call<ResultVO<PageVO<TeamListVO>>>

    @GET("/teams/{teamId}/members")
    fun getTeamMembers(@Header("AUTHENTICITY_TOKEN") token: String, @Path("teamId") teamId: Long, @Query("currentPage") currentPage: Long, @Query("size") size: Long): Call<ResultVO<PageVO<TeamMembaerListVO>>>

    @GET("/teams/{teamId}/records")
    fun getTeamActivity(@Header("AUTHENTICITY_TOKEN") token: String, @Path("teamId") teamId: Long, @Query("currentPage") currentPage: Long, @Query("size") size: Long): Call<ResultVO<PageVO<TeamActivityListVO>>>

    @POST("/teams/new")
    fun addTeam(@Header("AUTHENTICITY_TOKEN") token: String, @Body teamVO: TeamVO): Call<ResultVO<TeamTaskVO>>

    @GET("/teams/{teamId}")
    fun getTeamDetail(@Header("AUTHENTICITY_TOKEN") token: String, @Path("teamId") teamId: Long): Call<ResultVO<TeamDetailVO>>

    @POST("/teams/{teamId}")
    fun joinTheTeam(@Header("AUTHENTICITY_TOKEN") token: String, @Path("teamId") teamId: Long): Call<ResultVO<TeamTaskVO>>

    @POST("/teams/{teamId}/sign")
    fun finishTeamTask(@Header("AUTHENTICITY_TOKEN") token: String, @Path("teamId") teamId: Long, @Body activityVO: ActivityVO): Call<ResultVO<TeamTaskVO>>

}