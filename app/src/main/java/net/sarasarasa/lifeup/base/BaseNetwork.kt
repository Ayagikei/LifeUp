package net.sarasarasa.lifeup.base

import net.sarasarasa.lifeup.instance.RetrofitInstance

open class BaseNetwork {
    protected val retrofit = RetrofitInstance.getInstance()
}