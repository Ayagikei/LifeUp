package net.sarasarasa.lifeup.adapters

import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.ToDoItemConstants
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.dao.TaskTargetDAO
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.utils.DateUtil
import net.sarasarasa.lifeup.utils.DensityUtil
import java.text.SimpleDateFormat
import java.util.*


class ToDoItemAdapter(layoutResId: Int, data: List<TaskModel>) : BaseQuickAdapter<TaskModel, BaseViewHolder>(layoutResId, data) {

    private val taskTargetDAO = TaskTargetDAO()

    override fun convert(helper: BaseViewHolder, item: TaskModel) {

        val tw = helper.getView(R.id.tw_name) as TextView
        val cw = helper.getView(R.id.constraintLayout2) as ConstraintLayout
        cw.post {
            val newMaxWidth = cw.width - DensityUtil.dp2px(134f)
            if (newMaxWidth > 0) {
                tw.maxWidth = newMaxWidth
            }
        }

        val cal = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val dateAndTimeFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        val isTeamTask = when (item.teamId) {
            -1L -> false
            else -> true
        }

        var itemTitle = item.content
        if (item.taskTargetId != null && item.taskFrequency != 0) {
            val taskTarget = taskTargetDAO.getTaskTargetById(item.taskTargetId!!)

            if (taskTarget != null && taskTarget.targetTimes != 0) {
                itemTitle = "${item.content} （${item.currentTimes}/${taskTarget.targetTimes}）"
            }
        }

        helper.setText(R.id.tw_name, itemTitle)
                .setText(R.id.tv_remark, item.remark)
                .setText(R.id.tv_exp, "${item.expReward}经验值")
                .setImageResource(R.id.iv_iconSkillFrist, getAbbrIconDrawable(item.relatedAttribute1))
                .setImageResource(R.id.iv_iconSkillSecond, getAbbrIconDrawable(item.relatedAttribute2))
                .setImageResource(R.id.iv_iconSkillThird, getAbbrIconDrawable(item.relatedAttribute3))
                .addOnClickListener(R.id.av_checkBtn)

        if (item.remark.isEmpty()) {
            helper.setGone(R.id.tv_remark, false)
        } else {
            helper.setGone(R.id.tv_remark, true)
        }

        if (item.taskFrequency == 1 && item.isIgnoreDayOfWeek.contains(1)) {
            helper.setText(R.id.tv_headerText,
                    "周期任务-" + TodoItemConverter.iFrequencyWithIgnoreToNormalString(item.isIgnoreDayOfWeek.toIntArray()))
        } else {
            helper.setText(R.id.tv_headerText, TodoItemConverter.iFrequencyToTitleString(isTeamTask, item.taskFrequency))
        }

        if (item.enableEbbinghausMode) {
            helper.setText(R.id.tv_headerText,
                    "艾宾浩斯记忆法-${item.taskFrequency}天")
        }

        if (item.priority == 1) {
            helper.setVisible(R.id.iv_top, true)
        } else {
            helper.setVisible(R.id.iv_top, false)
        }

        if (cal.timeInMillis < item.startTime.time) {
            //还没到开始时间的时候
            with(helper) {
                getView<CardView>(R.id.TodolistHeaderCardView).setCardBackgroundColor(getUnableColor())
                setTextColor(R.id.tw_name, getUnableColor())
                setTextColor(R.id.tv_time, getUnableColor())
                setText(R.id.tv_time, dateToStringWithTime(item.startTime) + "开始")
                setVisible(R.id.iv_timeIcon, true)
                setVisible(R.id.tv_time, true)
            }
        } else {
            //设置频次标识的颜色
            if (item.enableEbbinghausMode) {
                helper.getView<CardView>(R.id.TodolistHeaderCardView).setCardBackgroundColor(getThemeColor(0))
                helper.setTextColor(R.id.tw_name, getThemeColor(0))
            } else {
                helper.getView<CardView>(R.id.TodolistHeaderCardView).setCardBackgroundColor(getThemeColor(item.taskFrequency))
                helper.setTextColor(R.id.tw_name, getThemeColor(item.taskFrequency))
            }

            helper.setTextColor(R.id.tv_time, getNormalTimeColor())

            if (item.taskExpireTime != null) {
                if (item.teamId != -1L) {
                    helper.setText(R.id.tv_time, dateToStringWithTime(item.endTime) + "期限")
                } else {
                    if (item.isUseSpecificExpireTime)
                        helper.setText(R.id.tv_time, dateToStringWithTime(item.taskExpireTime!!) + "期限")
                    else helper.setText(R.id.tv_time, dateToStringWithoutTime(item.taskExpireTime!!) + "期限")
                }

                helper.setVisible(R.id.iv_timeIcon, true)
                        .setVisible(R.id.tv_time, true)
            } else {
                helper.setVisible(R.id.iv_timeIcon, false)
                        .setVisible(R.id.tv_time, false)
            }
        }


        with(helper.getView<LottieAnimationView>(R.id.av_checkBtn)) {
            if (item.taskStatus == ToDoItemConstants.COMPLETED) {
                progress = 1.0f
                isClickable = false
            } else if (item.taskStatus == ToDoItemConstants.UNCOMPLETED) {
                progress = 0.0f
            }
        }


    }

    /** 获得[abbr]属性图标的[Drawable Id] **/
    private fun getAbbrIconDrawable(abbr: String?): Int {
        return TodoItemConverter.strAbbrToDrawableId(abbr)
    }

    /** 根据[taskFrequency: String]获得[color]主题色 **/
    private fun getThemeColor(taskFrequency: Int): Int {
        return ContextCompat.getColor(mContext, TodoItemConverter.strFrequencyToColorId(taskFrequency))
    }

    private fun getUnableColor(): Int {
        return ContextCompat.getColor(mContext, R.color.color_to_do_item_unable)
    }

    private fun getNormalTimeColor(): Int {
        return ContextCompat.getColor(mContext, R.color.color_to_do_item_time)
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