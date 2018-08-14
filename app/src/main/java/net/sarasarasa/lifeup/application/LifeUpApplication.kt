package net.sarasarasa.lifeup.application

import android.app.Application
import com.mob.MobSDK
import org.litepal.LitePal

class LifeUpApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        MobSDK.init(this)
        LitePal.initialize(this);

    }
}