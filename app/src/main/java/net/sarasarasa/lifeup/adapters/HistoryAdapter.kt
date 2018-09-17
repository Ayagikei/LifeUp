package net.sarasarasa.lifeup.adapters

import android.text.format.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.ToDoItemConstants
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.models.TaskModel
import java.text.SimpleDateFormat
import java.util.*


class HistoryAdapter(layoutResId: Int, data: List<TaskModel>) : BaseQuickAdapter<TaskModel, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TaskModel) {

        val timeFormat = SimpleDateFormat("yyyy-MM-dd\nHH:mm:ss", Locale.getDefault())

        val endDate = timeFormat.format(checkNotNull(item.endDate))

        helper.setText(R.id.tv_nickname, item.content)
                .setText(R.id.tv_btn, endDate)
                .setText(R.id.tv_headerText, TodoItemConverter.iFrequencyToTitleString(item.taskFrequency))
                .setImageResource(R.id.iv_iconSkillFrist, getAbbrIconDrawable(item.relatedAttribute1))
                .setImageResource(R.id.iv_iconSkillSecond, getAbbrIconDrawable(item.relatedAttribute2))
                .setImageResource(R.id.iv_iconSkillThird, getAbbrIconDrawable(item.relatedAttribute3))
                .setImageResource(R.id.iv_avatar, getStatusIconDrawable(item.taskStatus))
                .addOnClickListener(R.id.tv_btn)

        if (DateUtils.isToday(checkNotNull(item.endDate).time)
                && item.taskFrequency == 0
                && item.taskStatus == ToDoItemConstants.COMPLETED) {

            helper.setVisible(R.id.btn_undo, true)
                    .addOnClickListener(R.id.btn_undo)
        } else {
            helper.setVisible(R.id.btn_undo, false)
        }

    }

    /** 获得[abbr]属性图标的[Drawable Id] **/
    private fun getAbbrIconDrawable(abbr: String?): Int {
        return TodoItemConverter.strAbbrToDrawableId(abbr)
    }

    /** 获得[taskStatus]属性图标的[Drawable Id] **/
    private fun getStatusIconDrawable(status: Int): Int {
        return TodoItemConverter.strStatusToDrawableId(status)
    }

}