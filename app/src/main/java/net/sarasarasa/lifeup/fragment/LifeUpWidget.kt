package net.sarasarasa.lifeup.fragment

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.AddToDoItemActivity
import net.sarasarasa.lifeup.activities.MainActivity
import net.sarasarasa.lifeup.service.LifeUpRemoteViewsService
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.utils.ToastUtils


/**
 * Implementation of App Widget functionality.
 */
class LifeUpWidget : AppWidgetProvider() {

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
                ToastUtils.showShortToast("成功刷新", context)
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

