package net.sarasarasa.lifeup.network

import net.sarasarasa.lifeup.vo.ResultVO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadNetwork {

    @Multipart
    @POST("/upload/image/activity")
    fun uploadImages(@Header("authenticity-token") token: String, @Part body: List<MultipartBody.Part>): Call<ResultVO<List<String>>>

}