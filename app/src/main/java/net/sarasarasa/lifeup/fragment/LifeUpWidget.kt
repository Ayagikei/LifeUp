package net.sarasarasa.lifeup.fragment

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.widget.RemoteViews
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.AddToDoItemActivity
import net.sarasarasa.lifeup.activities.MainActivity
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.network.impl.TeamNetworkImpl
import net.sarasarasa.lifeup.service.LifeUpRemoteViewsService
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.utils.WidgetUtils
import net.sarasarasa.lifeup.vo.ActivityVO


/**
 * Implementation of App Widget functionality.
 */
class LifeUpWidget : AppWidgetProvider() {


    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            NetworkConstants.INVALID_TOKEN -> {
                ToastUtils.showShortToast("请重新登陆")
            }
            NetworkConstants.MSG_FINISH_TEAM_TASK -> {
                //团队事项完成
                ToastUtils.showShortToast("成功完成事项")
            }
            else -> {

            }

        }

        return@Callback true
    }

    private val teamNetworkImpl = TeamNetworkImpl(uiHandler)


    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.hasExtra(WIDGET_IDS_KEY)) {
            val ids = intent.extras.getIntArray(WIDGET_IDS_KEY)
            this.onUpdate(context, AppWidgetManager.getInstance(context), ids)

            if (intent.getBooleanExtra("isShowToast", false))
                ToastUtils.showShortToast("成功刷新")
        } else if (intent.action == FINISH_TASK) {
            val extras = intent.extras
            if (extras != null) {

                if (extras.getBoolean("canBeFinish", false)) {
                    val taskId = extras.getLong("taskId")
                    val teamId = extras.getLong("teamId")
                    val item = todoService.getATodoItem(taskId)

                    if (teamId == -1L) {
                        todoService.finishTodoItem(taskId)
                        ToastUtils.showShortToast("成功完成事项")

                        if (item?.taskFrequency != 0)
                            todoService.repeatTask(taskId)
                    } else {
                        val activityVO = ActivityVO()
                        item?.let {
                            teamNetworkImpl.finishTeamTask(it, activityVO)
                            ToastUtils.showShortToast("成功完成事项")
                        }
                    }

                } else {
                    ToastUtils.showShortToast("尚未到开始时间")
                }

                WidgetUtils.updateWidgets(context)
/*                // Notify the widget that the list view needs to be updated.
                val mgr = AppWidgetManager.getInstance(context)
                val cn = ComponentName(context, LifeUpWidget::class.java)
                mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),
                        R.id.widget_list)*/
            }
        }
    }

    companion object {

        const val WIDGET_IDS_KEY = "lifeupwidgetidskey"
        const val FINISH_TASK = "net.sarasarasa.lifeup.action.FINISH_TASK"
        private val todoService = TodoServiceImpl()

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {


            // Construct the RemoteViews object


            val views = RemoteViews(context.packageName, getMainLayoutByPreferences(context))
            views.setTextViewText(R.id.appwidget_text, "今日事项 0/0")

            val intent = Intent(context, LifeUpRemoteViewsService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            views.setRemoteAdapter(R.id.widget_list, intent)
            views.setEmptyView(R.id.widget_list, R.id.tv_empty)

            val finishTaskIntent = Intent(FINISH_TASK)
            finishTaskIntent.setClass(context, LifeUpWidget::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 200, finishTaskIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(R.id.widget_list, pendingIntent)

            val startActivityIntent = Intent(context, MainActivity::class.java)
            val startActivityPendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.iv_home, startActivityPendingIntent)

            val addItemIntent = Intent(context, AddToDoItemActivity::class.java)
            val addItemPendingIntent = PendingIntent.getActivity(context, 0, addItemIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.iv_add, addItemPendingIntent)

            val man = AppWidgetManager.getInstance(context)

            val ids = man.getAppWidgetIds(
                    ComponentName(context, LifeUpWidget::class.java))
            val refreshIntent = Intent()
            refreshIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            refreshIntent.putExtra(LifeUpWidget.WIDGET_IDS_KEY, ids)
            refreshIntent.putExtra("isShowToast", true)
            val refreshPendingIntent = PendingIntent.getBroadcast(context, 199, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.iv_refresh, refreshPendingIntent)

            val finishCnt = todoService.getTodayFinishCount()
            val taskCnt = todoService.getTodayTaskCount()
            views.setTextViewText(R.id.appwidget_text, "今日事项 ${finishCnt}/${taskCnt}")


            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getMainLayoutByPreferences(context: Context): Int {
            val sharedPreferences = context.getSharedPreferences("options", Context.MODE_PRIVATE)
            val isWidgetDarkTheme = sharedPreferences.getBoolean("isWidgetDarkTheme", false)

            return if (isWidgetDarkTheme)
                R.layout.life_up_widget_dark_theme
            else R.layout.life_up_widget

        }



    }
}

