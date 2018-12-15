package net.sarasarasa.lifeup.network

import net.sarasarasa.lifeup.vo.*
import retrofit2.Call
import retrofit2.http.*

interface TeamNetwork {

    @GET("/teams")
    fun getTeamList(@Header("authenticity-token") token: String, @Query("currentPage") currentPage: Long, @Query("size") size: Long): Call<ResultVO<PageVO<TeamListVO>>>

    @GET("/teams")
    fun searchTeamList(@Header("authenticity-token") token: String, @Query("currentPage") currentPage: Long, @Query("size") size: Long, @Query("teamTitle") teamTitle: String): Call<ResultVO<PageVO<TeamListVO>>>

    @GET("/teams/{teamId}/members")
    fun getTeamMembers(@Header("authenticity-token") token: String, @Path("teamId") teamId: Long, @Query("currentPage") currentPage: Long, @Query("size") size: Long): Call<ResultVO<PageVO<TeamMembaerListVO>>>

    @DELETE("/teams/{teamId}/members/quit")
    fun quitTeam(@Header("authenticity-token") token: String, @Path("teamId") teamId: Long): Call<ResultVO<Any>>

    @POST("/teams/{teamId}/end")
    fun endTeam(@Header("authenticity-token") token: String, @Path("teamId") teamId: Long): Call<ResultVO<Any>>

    @GET("/teams/{teamId}/records")
    fun getTeamActivity(@Header("authenticity-token") token: String, @Path("teamId") teamId: Long, @Query("currentPage") currentPage: Long, @Query("size") size: Long): Call<ResultVO<PageVO<TeamActivityListVO>>>

    @POST("/teams/new")
    fun addTeam(@Header("authenticity-token") token: String, @Body teamVO: TeamVO): Call<ResultVO<TeamTaskVO>>

    @PUT("/teams/{teamId}")
    fun editTeam(@Header("authenticity-token") token: String, @Body teamEditVO: TeamEditVO, @Path("teamId") teamId: Long): Call<ResultVO<Any>>

    @GET("/teams/{teamId}")
    fun getTeamDetail(@Header("authenticity-token") token: String, @Path("teamId") teamId: Long): Call<ResultVO<TeamDetailVO>>

    @POST("/teams/{teamId}")
    fun joinTheTeam(@Header("authenticity-token") token: String, @Path("teamId") teamId: Long): Call<ResultVO<TeamTaskVO>>

    @GET("/teams/{teamId}/next_sign")
    fun getNextTeamTask(@Header("authenticity-token") token: String, @Path("teamId") teamId: Long): Call<ResultVO<TeamTaskVO>>

    @POST("/teams/{teamId}/sign")
    fun finishTeamTask(@Header("authenticity-token") token: String, @Path("teamId") teamId: Long, @Body activityVO: ActivityVO): Call<ResultVO<TeamTaskVO>>

}