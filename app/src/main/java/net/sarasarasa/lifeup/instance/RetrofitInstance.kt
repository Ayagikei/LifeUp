package net.sarasarasa.lifeup.instance

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInstance {
    companion object {
        val gson: Gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create()


        private val retrofitInstance = Retrofit.Builder().baseUrl("http://hdonghong.top").addConverterFactory(GsonConverterFactory.create(gson)).build()

        fun getInstance(): Retrofit {
            return retrofitInstance
        }
    }
}