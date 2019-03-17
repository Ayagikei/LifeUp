package net.sarasarasa.lifeup.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import net.sarasarasa.lifeup.activities.AddToDoItemActivity
import net.sarasarasa.lifeup.activities.MainActivity

private const val CHANNEL_ID = "net.sarasarasa.lifeup.channelId"

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationIntent = Intent(context, AddToDoItemActivity::class.java)

        val id = intent.getIntExtra("id", 0)
        val content = intent.getStringExtra("content")

/*        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(notificationIntent)

        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)*/

        val msgIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, msgIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val builder = NotificationCompat.Builder(context, CHANNEL_ID).setAutoCancel(true)

        val notification = builder.setContentTitle("人升")
                .setContentText("你还有待办事项需要完成：$content")
                .setTicker("待办事项提醒")
                //.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setSmallIcon(context.applicationInfo.icon)
                .setContentIntent(pendingIntent).build()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    CHANNEL_ID,
                    "NotificationLifeup",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        NotificationManagerCompat.from(context).notify(id, notification)

        //notificationManager.notify(0, notification)
    }
}