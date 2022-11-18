package net.lifeupapp.lifeup.http.service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import net.lifeupapp.lifeup.http.R
import net.lifeupapp.lifeup.http.base.appCtx


object NotificationChannels {
    const val CHANNEL_NOTIFICATION = "net.lifeupapp.lifeup.http.server"

    private val notificationManager =
        appCtx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ArrayList<NotificationChannel>().apply {
                add(
                    NotificationChannel(
                        CHANNEL_NOTIFICATION,
                        context.getString(R.string.notification_channel_server),
                        NotificationManager.IMPORTANCE_LOW
                    )
                )
            }.also {
                notificationManager.createNotificationChannels(it)
            }
        }
    }
}
