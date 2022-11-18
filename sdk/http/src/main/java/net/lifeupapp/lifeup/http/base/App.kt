package net.lifeupapp.lifeup.http.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import net.lifeupapp.lifeup.http.service.notification.NotificationChannels

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        app = this
        appCtx = this
        runStartupTasks()
    }

    private fun runStartupTasks() {
        NotificationChannels.createChannels(this)
    }
}

lateinit var app: Application
    private set

@SuppressLint("StaticFieldLeak")
lateinit var appCtx: Context
    private set