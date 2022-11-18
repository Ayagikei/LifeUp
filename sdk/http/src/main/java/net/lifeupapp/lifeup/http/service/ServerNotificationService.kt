package net.lifeupapp.lifeup.http.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import net.lifeupapp.lifeup.http.MainActivity
import net.lifeupapp.lifeup.http.R
import net.lifeupapp.lifeup.http.service.notification.NotificationChannels
import net.lifeupapp.lifeup.http.utils.compactAndFlagImmutable

class ServerNotificationService : Service() {

    override fun onCreate() {
    }

    override fun onDestroy() {
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("toActivity", 0)
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, compactAndFlagImmutable(PendingIntent.FLAG_UPDATE_CURRENT)
        )

        var notificationBuilder: Notification.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(this, NotificationChannels.CHANNEL_NOTIFICATION)
            } else {
                Notification.Builder(this)
            }

        notificationBuilder = notificationBuilder
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(this, R.color.md_theme_light_primary))
            .setGroup(GROUP_KEY_QUICK_ADD)
            .setOngoing(true)
            .setShowWhen(false)


        notificationBuilder.setContentIntent(pendingIntent)
            .setContentTitle(getString(R.string.notification_channel_server))
            .setSound(null)
            .setVibrate(longArrayOf(0))

        val notification = notificationBuilder
            .build()

        // Clearing any previous notifications.
        NotificationManagerCompat
            .from(this)
            .cancel(TASK_INFORMATION_NOTIFICATION_ID)
        startForeground(TASK_INFORMATION_NOTIFICATION_ID, notification)
        return START_REDELIVER_INTENT
    }

    companion object {

        const val TASK_INFORMATION_NOTIFICATION_ID = 416
        const val GROUP_KEY_QUICK_ADD = "quick_add"

        fun start(context: Context) {
            val serviceIntent =
                Intent(context, ServerNotificationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(
                serviceIntent
            ) else context.startService(serviceIntent)
        }

        fun cancel(context: Context) {
            try {
                val serviceIntent =
                    Intent(context, ServerNotificationService::class.java)
                context.stopService(serviceIntent)
                // Clearing any previous notifications.
                NotificationManagerCompat
                    .from(context)
                    .cancel(TASK_INFORMATION_NOTIFICATION_ID)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }
}