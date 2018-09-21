package net.sarasarasa.lifeup.instance

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInstance {
    companion object {
        val gson: Gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create()

        var client = OkHttpClient.Builder()
                .build()

        private val retrofitInstance = Retrofit.Builder().baseUrl("http://hdonghong.top").client(client).addConverterFactory(GsonConverterFactory.create(gson)).build()

        fun getInstance(): Retrofit {
            return retrofitInstance
        }
    }
}