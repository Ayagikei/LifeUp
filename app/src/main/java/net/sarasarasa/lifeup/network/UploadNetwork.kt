package net.sarasarasa.lifeup.network

import net.sarasarasa.lifeup.vo.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface UploadNetwork {

    @Multipart
    @POST("/upload/image/activity")
    fun uploadImages(@Header("authenticity-token") token: String, @Part body: List<MultipartBody.Part>): Call<ResultVO<List<String>>>

}