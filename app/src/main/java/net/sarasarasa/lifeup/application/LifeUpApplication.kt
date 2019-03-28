package net.sarasarasa.lifeup.application

import android.app.Application
import android.content.Context
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.utils.DensityUtil
import net.sarasarasa.lifeup.utils.SharedPreferencesUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import org.acra.ACRA
import org.acra.BuildConfig
import org.acra.annotation.AcraCore
import org.acra.annotation.AcraDialog
import org.acra.annotation.AcraMailSender

import org.acra.data.StringFormat
import org.litepal.LitePal

@AcraCore(buildConfigClass = BuildConfig::class, reportFormat = StringFormat.JSON)
/*@AcraNotification(resChannelName = R.string.AcraChannel,
        resTitle =  R.string.AcraTitle,
        resText = R.string.AcraText)*/
@AcraDialog(resCommentPrompt = R.string.AcraCommend,
        resTitle = R.string.AcraTitle,
        resText = R.string.AcraText)
@AcraMailSender(mailTo = "ayagikei@163.com")
class LifeUpApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LitePal.initialize(this)
        ToastUtils.init(this)
        DensityUtil.init(this)
        SharedPreferencesUtils.init(this)
        instance = this
    }

    companion object {
        private var instance: LifeUpApplication? = null

        fun getLifeUpApplication(): Context {
            return instance as Context
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        ACRA.init(this)
    }

}