package net.sarasarasa.lifeup.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import com.airbnb.lottie.LottieAnimationView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.ToDoItemConstants
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.models.TaskModel
import java.text.SimpleDateFormat
import java.util.*


class ToDoItemAdapter(layoutResId: Int, data: List<TaskModel>) : BaseQuickAdapter<TaskModel, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TaskModel) {


        helper.setText(R.id.tw_name, item.content)
                .setText(R.id.tw_contentTitle, item.remark)
                .setText(R.id.tv_headerText, TodoItemConverter.iFrequencyToString(item.taskFrequency))
                .setText(R.id.tv_exp, "${item.expReward}经验值")
                .setImageResource(R.id.iv_iconSkillFrist, getAbbrIconDrawable(item.relatedAttribute1))
                .setImageResource(R.id.iv_iconSkillSecond, getAbbrIconDrawable(item.relatedAttribute2))
                .setImageResource(R.id.iv_iconSkillThird, getAbbrIconDrawable(item.relatedAttribute3))
                .setTextColor(R.id.tw_name, getThemeColor(item.taskFrequency))
                .addOnClickListener(R.id.av_checkBtn)

        //设置频次标识的颜色
        helper.getView<CardView>(R.id.TodolistHeaderCardView).setCardBackgroundColor(getThemeColor(item.taskFrequency))

        if (item.taskExpireTime != null) {
            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            helper.setText(R.id.tv_time, simpleDateFormat.format(item.taskExpireTime))
                    .setVisible(R.id.iv_timeIcon, true)
                    .setVisible(R.id.tv_time, true)
        } else {
            helper.setVisible(R.id.iv_timeIcon, false)
                    .setVisible(R.id.tv_time, false)
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


}