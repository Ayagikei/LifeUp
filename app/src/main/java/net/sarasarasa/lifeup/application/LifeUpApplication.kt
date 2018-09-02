package net.sarasarasa.lifeup.application

import android.app.Application
import android.content.Context
import com.mob.MobSDK
import org.litepal.LitePal

class LifeUpApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        MobSDK.init(this)
        LitePal.initialize(this)
        instance = this

    }

    companion object {
        private var instance: LifeUpApplication? = null

        fun getLifeUpApplication(): Context {
            return instance as Context
        }
    }

}