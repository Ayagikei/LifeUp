package net.sarasarasa.lifeup.utils

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import net.sarasarasa.lifeup.fragment.LifeUpWidget


class WidgetUtils {
    companion object {
        fun updateWidgets(context: Context) {
            val man = AppWidgetManager.getInstance(context)
            val ids = man.getAppWidgetIds(
                    ComponentName(context, LifeUpWidget::class.java))
            val updateIntent = Intent()
            updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            updateIntent.putExtra(LifeUpWidget.WIDGET_IDS_KEY, ids)
            // updateIntent.putExtra(MyWidgetProvider.WIDGET_DATA_KEY, data)
            context.sendBroadcast(updateIntent)
        }
    }
}