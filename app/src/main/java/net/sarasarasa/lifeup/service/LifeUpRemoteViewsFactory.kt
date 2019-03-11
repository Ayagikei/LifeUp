package net.sarasarasa.lifeup.service

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.dao.TaskTargetDAO
import net.sarasarasa.lifeup.fragment.LifeUpWidget
import net.sarasarasa.lifeup.fragment.LifeUpWidget.Companion.FINISH_TASK
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.utils.DateUtil
import java.text.SimpleDateFormat
import java.util.*


class LifeUpRemoteViewsFactory(context: Context, intent: Intent?) : RemoteViewsService.RemoteViewsFactory {

    private val mList = ArrayList<TaskModel>()
    private val mContext = context

    private val todoService = TodoServiceImpl()
    private val taskTargetDAO = TaskTargetDAO()

    override fun onCreate() {
        mList.clear()
        mList.addAll(getListByPreferences())
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
        mList.clear()
        mList.addAll(getListByPreferences())
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if (position < 0 || position >= mList.size)
            return null

        val taskModel = mList[position]
        val rv = RemoteViews(mContext.packageName, getListViewLayoutByPreferences(mContext))

        var canBeFinish = true

        var itemTitle = taskModel.content
        if (taskModel.taskTargetId != null && taskModel.taskFrequency != 0) {
            val taskTarget = taskTargetDAO.getTaskTargetById(taskModel.taskTargetId!!)

            if (taskTarget != null && taskTarget.targetTimes != 0) {
                itemTitle = "${taskModel.content} （${taskModel.currentTimes}/${taskTarget.targetTimes}）"
            }
        }

        rv.setTextViewText(R.id.tv_title, itemTitle)
        rv.setTextViewText(R.id.tv_exp, taskModel.expReward.toString() + "经验值")


        val sharedPreferences = mContext.getSharedPreferences("options", Context.MODE_PRIVATE)
        val isWidgetDarkTheme = sharedPreferences.getBoolean("isWidgetDarkTheme", false)
        val isWidgetDarkThemeWhiteIconAndFonts = sharedPreferences.getBoolean("isWidgetDarkThemeWhiteIconAndFonts", false)

        if (isWidgetDarkTheme && isWidgetDarkThemeWhiteIconAndFonts) {
            rv.setTextColor(R.id.tv_exp, ContextCompat.getColor(mContext, R.color.white))
            rv.setTextColor(R.id.tv_time, ContextCompat.getColor(mContext, R.color.white))
            rv.setImageViewResource(R.id.imageView4, R.drawable.ic_award_exp_white)
            rv.setImageViewResource(R.id.iv_time, R.drawable.ic_time_white)
        } else {
            rv.setTextColor(R.id.tv_exp, Color.parseColor("#FF9100"))
            rv.setTextColor(R.id.tv_time, Color.parseColor("#FF8A80"))
            rv.setImageViewResource(R.id.imageView4, R.drawable.ic_award_exp)
            rv.setImageViewResource(R.id.iv_time, R.drawable.ic_time)
        }


        val cal = Calendar.getInstance()

        val isTeamTask = when (taskModel.teamId) {
            -1L -> false
            else -> true
        }

        if (cal.timeInMillis < taskModel.startTime.time) {
            //还没到开始时间的时候
            rv.setViewVisibility(R.id.tv_time, View.VISIBLE)
            rv.setViewVisibility(R.id.iv_time, View.VISIBLE)
            rv.setTextViewText(R.id.tv_time, dateToStringWithTime(taskModel.startTime) + "开始  #" + TodoItemConverter.iFrequencyToTitleString(isTeamTask, taskModel.taskFrequency))
            canBeFinish = false
        } else {
            //设置频次标识的颜色

            if (taskModel.taskExpireTime != null) {
                rv.setViewVisibility(R.id.tv_time, View.VISIBLE)
                rv.setViewVisibility(R.id.iv_time, View.VISIBLE)

                if (taskModel.teamId != -1L) {
                    rv.setTextViewText(R.id.tv_time, dateToStringWithTime(taskModel.endTime) + "期限  #" + TodoItemConverter.iFrequencyToTitleString(isTeamTask, taskModel.taskFrequency))
                } else rv.setTextViewText(R.id.tv_time, dateToStringWithoutTime(taskModel.taskExpireTime!!) + "期限  #" + TodoItemConverter.iFrequencyToTitleString(isTeamTask, taskModel.taskFrequency))
            } else {
                rv.setViewVisibility(R.id.tv_time, View.INVISIBLE)
                rv.setViewVisibility(R.id.iv_time, View.INVISIBLE)
            }
        }

/*        val extras = Bundle()
        taskModel.id?.let { extras.putLong("taskId", it) }
        val finishTaskIntent = Intent()
        finishTaskIntent.action = LifeUpWidget.FINISH_TASK
        finishTaskIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.btn,finishTaskIntent)*/
        val extras = Bundle()
        taskModel.id?.let { extras.putLong("taskId", it) }
        extras.putLong("teamId", taskModel.teamId)
        extras.putBoolean("canBeFinish", canBeFinish)
        val fillInIntent = Intent(FINISH_TASK)
        fillInIntent.putExtra("NUMBER", position)
        fillInIntent.setClass(mContext, LifeUpWidget::class.java)
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.btn, fillInIntent)




        return rv
    }

    override fun getCount(): Int {
        return mList.size
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun onDestroy() {
        mList.clear()
    }

    private fun getListViewLayoutByPreferences(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("options", Context.MODE_PRIVATE)
        val isWidgetDarkTheme = sharedPreferences.getBoolean("isWidgetDarkTheme", false)

        return if (isWidgetDarkTheme)
            R.layout.item_widget_list_dark_theme
        else R.layout.item_widget_list

    }

    private fun getListByPreferences(): List<TaskModel> {
        val sharedPreferences = mContext.getSharedPreferences("options", Context.MODE_PRIVATE)
        val isHideNotBegunItem = sharedPreferences.getBoolean("isHideNotBegunItem", false)

        return if (isHideNotBegunItem)
            todoService.getAllUncompletedTodoListWhichHaveBegun(false)
        else todoService.getAllUncompletedTodoList(false)
    }

    private fun dateToStringWithTime(date: Date): String {
        if (DateUtil.isToday(date.time)) {
            val cal = Calendar.getInstance()
            cal.time = date

            if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0) {
                val formatter = SimpleDateFormat("今天", Locale.getDefault())
                return formatter.format(date)
            } else {
                val formatter = SimpleDateFormat("今天 HH:mm ", Locale.getDefault())
                return formatter.format(date)
            }
        } else if (DateUtil.isTomorrow(date.time)) {
            val cal = Calendar.getInstance()
            cal.time = date

            if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0) {
                val formatter = SimpleDateFormat("明天", Locale.getDefault())
                return formatter.format(date)
            } else {
                val formatter = SimpleDateFormat("明天 HH:mm ", Locale.getDefault())
                return formatter.format(date)
            }
        } else {
            val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm ", Locale.getDefault())
            return formatter.format(date)
        }
    }

    private fun dateToStringWithoutTime(date: Date): String {
        if (DateUtil.isToday(date.time)) {
            val formatter = SimpleDateFormat("今天", Locale.getDefault())
            return formatter.format(date)
        } else if (DateUtil.isTomorrow(date.time)) {
            val formatter = SimpleDateFormat("明天", Locale.getDefault())
            return formatter.format(date)
        } else {
            val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            return formatter.format(date)
        }
    }


}