package net.sarasarasa.lifeup.service

import android.app.IntentService
import android.content.Intent
import android.os.Handler
import net.sarasarasa.lifeup.application.LifeUpApplication
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.network.impl.TeamNetworkImpl
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.utils.WidgetUtils
import net.sarasarasa.lifeup.vo.ActivityVO

class FinishTaskIntentService : IntentService("FinishTaskIntentService") {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->
        if (msg.what == NetworkConstants.MSG_FINISH_TEAM_TASK) {
            ToastUtils.showShortToast("成功完成事项", LifeUpApplication.getLifeUpApplication())
        }

        return@Callback true
    }

    private val todoService = TodoServiceImpl()
    private val teamNetworkImpl = TeamNetworkImpl(uiHandler)


    override fun onHandleIntent(intent: Intent?) {
        val extras = intent?.extras

        if (extras != null) {
            if (extras.getBoolean("canBeFinish", false)) {
                val taskId = extras.getLong("taskId")
                val teamId = extras.getLong("teamId")
                val item = todoService.getATodoItem(taskId)

                if (teamId == -1L) {
                    if (todoService.finishTodoItem(taskId)) {
                        if (!item?.completeReward.isNullOrEmpty()) {
                            ToastUtils.showLongToast("成功完成事项并且获得奖励：${item?.completeReward}！", LifeUpApplication.getLifeUpApplication())
                        } else ToastUtils.showShortToast("成功完成事项", LifeUpApplication.getLifeUpApplication())

                        if (item?.taskFrequency != 0)
                            todoService.repeatTask(taskId)
                    }
                } else {
                    val activityVO = ActivityVO()
                    item?.let {
                        teamNetworkImpl.finishTeamTask(it, activityVO)

                    }
                }
            } else {
                ToastUtils.showShortToast("尚未到开始时间", LifeUpApplication.getLifeUpApplication())
            }

            WidgetUtils.updateWidgets(LifeUpApplication.getLifeUpApplication())
        }
    }


}