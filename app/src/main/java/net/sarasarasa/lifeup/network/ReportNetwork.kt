package net.sarasarasa.lifeup.network

import net.sarasarasa.lifeup.vo.ReportDetailVO
import net.sarasarasa.lifeup.vo.ReportTypeVO
import net.sarasarasa.lifeup.vo.ResultVO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ReportNetwork {

    @GET("/report/types")
    fun getReportType(@Header("authenticity-token") token: String): Call<ResultVO<ArrayList<ReportTypeVO>>>

    @POST("/report/records/new")
    fun report(@Header("authenticity-token") token: String,@Body reportDetailVO: ReportDetailVO): Call<ResultVO<Any>>

}

