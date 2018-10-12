package net.sarasarasa.lifeup.network

import net.sarasarasa.lifeup.vo.*
import retrofit2.Call
import retrofit2.http.*

interface UserNetwork {

    @GET("/user/profile")
    fun getUserProfile(@Header("authenticity-token") token: String): Call<ResultVO<ProfileVO>>

    @PUT("/user/profile")
    fun updateUserProfile(@Header("authenticity-token") token: String, @Body profileVO: ProfileVO): Call<ResultVO<ProfileVO>>

    @GET("/user/{userId}/activities")
    fun getUserActivities(@Header("authenticity-token") token: String, @Path("userId") userId: Long, @Query("currentPage") currentPage: Long, @Query("size") size: Long): Call<ResultVO<PageVO<TeamActivityListVO>>>

    @GET("/user/activities")
    fun getMineUserActivities(@Header("authenticity-token") token: String, @Query("currentPage") currentPage: Long, @Query("size") size: Long): Call<ResultVO<PageVO<TeamActivityListVO>>>

    @GET("/user/{userId}/detail")
    fun getUserDetail(@Header("authenticity-token") token: String, @Path("userId") userId: Long): Call<ResultVO<UserDetailVO>>

    @GET("/user/detail")
    fun getMineUserDetail(@Header("authenticity-token") token: String): Call<ResultVO<UserDetailVO>>

    @GET("/user/{userId}/teams")
    fun getUserTeamList(@Header("authenticity-token") token: String, @Path("userId") userId: Long, @Query("currentPage") currentPage: Long, @Query("size") size: Long): Call<ResultVO<PageVO<TeamListVO>>>

    @GET("/user/teams")
    fun getUserTeamList(@Header("authenticity-token") token: String, @Query("currentPage") currentPage: Long, @Query("size") size: Long): Call<ResultVO<PageVO<TeamListVO>>>

    @POST("/user/following/{userId}")
    fun followUserById(@Header("authenticity-token") token: String, @Path("userId") userId: Long): Call<ResultVO<Any>>

    @DELETE("/user/following/{userId}")
    fun unfollowUserById(@Header("authenticity-token") token: String, @Path("userId") userId: Long): Call<ResultVO<Any>>

    @GET("/user/{userId}/follower")
    fun getUserFollower(@Header("authenticity-token") token: String, @Path("userId") userId: Long, @Query("currentPage") currentPage: Long, @Query("size") size: Long): Call<ResultVO<PageVO<TeamMembaerListVO>>>

    @GET("/user/follower")
    fun getUserFollower(@Header("authenticity-token") token: String, @Query("currentPage") currentPage: Long, @Query("size") size: Long): Call<ResultVO<PageVO<TeamMembaerListVO>>>

    @GET("/user/{userId}/following")
    fun getUserFollowing(@Header("authenticity-token") token: String, @Path("userId") userId: Long, @Query("currentPage") currentPage: Long, @Query("size") size: Long): Call<ResultVO<PageVO<TeamMembaerListVO>>>

    @GET("/user/following")
    fun getUserFollowing(@Header("authenticity-token") token: String, @Query("currentPage") currentPage: Long, @Query("size") size: Long): Call<ResultVO<PageVO<TeamMembaerListVO>>>
}