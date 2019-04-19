package net.sarasarasa.lifeup.service

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import net.sarasarasa.lifeup.application.LifeUpApplication
import net.sarasarasa.lifeup.network.impl.TeamNetworkImpl
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.utils.WidgetUtils
import net.sarasarasa.lifeup.vo.ActivityVO


class FinishTaskIntentService : IntentService("FinishTaskIntentService") {

    private val todoService = TodoServiceImpl()
    private val teamNetworkImpl = TeamNetworkImpl(null)

    override fun onCreate() {
        super.onCreate()
        Log.i("FinishTaskIntentService", "onCreate()")
    }


    override fun onHandleIntent(intent: Intent?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NOTIFICATION_CHANNEL_ID = "net.sarasarasa.lifeup"
            val channelName = "FinishTaskIntentService"
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName,
                    NotificationManager.IMPORTANCE_MIN)

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            val notification = Notification.Builder(applicationContext, NOTIFICATION_CHANNEL_ID).build()

            startForeground(1001, notification)        //context.startForeground(SERVICE_ID, builder.getNotification());
        }


        val extras = intent?.extras

        if (extras != null) {
            if (extras.getBoolean("canBeFinish", false)) {
                val taskId = extras.getLong("taskId")
                val teamId = extras.getLong("teamId")
                val item = todoService.getATodoItem(taskId)

                if (teamId == -1L) {
                    if (todoService.finishTodoItem(taskId)) {
                        if (!item?.completeReward.isNullOrEmpty()) {
                            Handler(Looper.getMainLooper()).post { ToastUtils.showLongToast("成功完成事项并且获得奖励：${item?.completeReward}！", LifeUpApplication.getLifeUpApplication()) }
                        } else {
                            Handler(Looper.getMainLooper()).post { ToastUtils.showLongToast("成功完成事项", LifeUpApplication.getLifeUpApplication()) }
                        }

                        if (item?.taskFrequency != 0)
                            todoService.repeatTask(taskId)
                    }
                } else {
                    val activityVO = ActivityVO()
                    item?.let {
                        teamNetworkImpl.finishTeamTask(it, activityVO, true)

                    }
                }
            } else {
                Handler(Looper.getMainLooper()).post { ToastUtils.showLongToast("尚未到开始时间", LifeUpApplication.getLifeUpApplication()) }
            }

            WidgetUtils.updateWidgets(LifeUpApplication.getLifeUpApplication())
        }
    }


}