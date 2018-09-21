package net.sarasarasa.lifeup.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class LifeUpNetCallback<T> : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}