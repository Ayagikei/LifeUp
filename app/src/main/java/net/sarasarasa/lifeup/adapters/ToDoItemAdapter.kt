package net.sarasarasa.lifeup.adapters

import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.ToDoItemConstants
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.dao.TaskTargetDAO
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.utils.DateUtil
import net.sarasarasa.lifeup.utils.DensityUtil
import net.sarasarasa.lifeup.utils.ToastUtils
import java.text.SimpleDateFormat
import java.util.*


class ToDoItemAdapter(layoutResId: Int, data: List<TaskModel>) : BaseItemDraggableAdapter<TaskModel, BaseViewHolder>(layoutResId, data) {

    private val taskTargetDAO = TaskTargetDAO()

    override fun convert(helper: BaseViewHolder, item: TaskModel) {

        val tw = helper.getView(R.id.tw_name) as TextView
        val cw = helper.getView(R.id.constraintLayout2) as ConstraintLayout
        cw.post {
            val newMaxWidth = cw.width - DensityUtil.dp2px(138f)
            if (newMaxWidth > 0) {
                tw.maxWidth = newMaxWidth
            }
        }

        val cal = Calendar.getInstance()
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
                .setText(R.id.tv_exp, item.expReward.toString() + mContext.getString(R.string.exp))
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
                    mContext.getString(R.string.repeat_task) + TodoItemConverter.iFrequencyWithIgnoreToNormalString(item.isIgnoreDayOfWeek.toIntArray()))
        } else {
            helper.setText(R.id.tv_headerText, TodoItemConverter.iFrequencyToTitleString(isTeamTask, item.taskFrequency))
        }

        if (item.enableEbbinghausMode) {
            if (item.taskFrequency == 0) {
                helper.setText(R.id.tv_headerText,
                        mContext.getString(R.string.ebbinghaus_the_last_day))
            } else {
                helper.setText(R.id.tv_headerText,
                        "${mContext.getString(R.string.ebbinghaus)}-${item.taskFrequency}${mContext.getString(R.string.day)}")
            }
        }

        if (item.priority == 1) {
            helper.setVisible(R.id.iv_top, true)
        } else {
            helper.setVisible(R.id.iv_top, false)
        }

        if (cal.timeInMillis < item.startTime.time) {
            //还没到开始时间的时候
            helper.apply {
                getView<CardView>(R.id.TodolistHeaderCardView).setCardBackgroundColor(getUnableColor())
                setTextColor(R.id.tw_name, getUnableColor())
                setTextColor(R.id.tv_time, getUnableColor())
                setTextColor(R.id.tv_exp, getUnableColor())
                setText(R.id.tv_time, dateToStringWithTime(item.startTime) + mContext.getString(R.string.start))
                setVisible(R.id.iv_timeIcon, true)
                setVisible(R.id.tv_time, true)
                getView<ImageView>(R.id.iv_expIcon).setColorFilter(getUnableColor())
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

            // 设置已经开始事项的正常颜色
            helper.apply {
                setTextColor(R.id.tv_time, getNormalTimeColor())
                getView<ImageView>(R.id.iv_expIcon).colorFilter = null
                setTextColor(R.id.tv_exp, getNormalExpColor())
            }


            if (item.taskExpireTime != null) {
                if (item.teamId != -1L) {
                    helper.setText(R.id.tv_time, dateToStringWithTime(item.endTime) + mContext.getString(R.string.deadline))
                } else {
                    if (item.isUseSpecificExpireTime)
                        helper.setText(R.id.tv_time, dateToStringWithTime(item.taskExpireTime!!) + mContext.getString(R.string.deadline))
                    else helper.setText(R.id.tv_time, dateToStringWithoutTime(item.taskExpireTime!!) + mContext.getString(R.string.deadline))
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
        return ContextCompat.getColor(mContext, TodoItemConverter.strFrequencyToColorId(taskFrequency, checkPreferences = true))
    }

    private fun getUnableColor(): Int {
        return ContextCompat.getColor(mContext, R.color.color_to_do_item_unable)
    }

    private fun getNormalTimeColor(): Int {
        return ContextCompat.getColor(mContext, R.color.color_to_do_item_time)
    }

    private fun getNormalExpColor(): Int {
        return ContextCompat.getColor(mContext, R.color.color_to_do_item_exp)
    }

    private fun dateToStringWithTime(date: Date): String {
        if (DateUtil.isToday(date.time)) {
            val cal = Calendar.getInstance()
            cal.time = date

            if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0) {
                val formatter = SimpleDateFormat(mContext.getString(R.string.today), Locale.getDefault())
                return formatter.format(date)
            } else {
                val formatter = SimpleDateFormat("${mContext.getString(R.string.today)} HH:mm ", Locale.getDefault())
                return formatter.format(date)
            }
        } else if (DateUtil.isTomorrow(date.time)) {
            val cal = Calendar.getInstance()
            cal.time = date

            if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0) {
                val formatter = SimpleDateFormat(mContext.getString(R.string.tomorrow), Locale.getDefault())
                return formatter.format(date)
            } else {
                val formatter = SimpleDateFormat("${mContext.getString(R.string.tomorrow)} HH:mm ", Locale.getDefault())
                return formatter.format(date)
            }
        } else {
            val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm ", Locale.getDefault())
            return formatter.format(date)
        }
    }

    private fun dateToStringWithoutTime(date: Date): String {
        if (DateUtil.isToday(date.time)) {
            val formatter = SimpleDateFormat(mContext.getString(R.string.today), Locale.getDefault())
            return formatter.format(date)
        } else if (DateUtil.isTomorrow(date.time)) {
            val formatter = SimpleDateFormat(mContext.getString(R.string.tomorrow), Locale.getDefault())
            return formatter.format(date)
        } else {
            val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            return formatter.format(date)
        }
    }

    override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder) {
        val pos = this.getViewHolderPosition(viewHolder)
        if (this.inRange(pos)) {
            val cal = Calendar.getInstance()
            if (cal.time < this.mData.get(pos).startTime) {
                ToastUtils.showShortToast(mContext.getString(R.string.to_do_adapter_not_start_yet))
                this.notifyItemChanged(pos + 1)
            } else {
                this.mData.removeAt(pos)
                this.notifyItemRemoved(viewHolder.adapterPosition)

                if (this.mOnItemSwipeListener != null && this.itemSwipeEnabled) {
                    this.mOnItemSwipeListener.onItemSwiped(viewHolder, this.getViewHolderPosition(viewHolder))
                }
            }
        }

    }

    private fun inRange(position: Int): Boolean {
        return position >= 0 && position < this.mData.size
    }
}