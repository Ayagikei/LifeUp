package net.sarasarasa.lifeup.application

import android.app.Application
import android.content.Context
import com.mob.MobSDK
import net.sarasarasa.lifeup.utils.DensityUtil
import net.sarasarasa.lifeup.utils.ToastUtils
import org.litepal.LitePal


class LifeUpApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        MobSDK.init(this)
        LitePal.initialize(this)
        ToastUtils.init(this)
        DensityUtil.init(this)

        val applicationCrashHandler = ApplicationCrashHandler()
        applicationCrashHandler.init(this)

        instance = this

    }

    companion object {
        private var instance: LifeUpApplication? = null

        fun getLifeUpApplication(): Context {
            return instance as Context
        }
    }

}