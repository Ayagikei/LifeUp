package net.sarasarasa.lifeup.adapters

import com.airbnb.lottie.LottieAnimationView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.AddToDoItemConstants
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.models.TaskModel


class ToDoItemAdapter(layoutResId: Int, data: List<TaskModel>) : BaseQuickAdapter<TaskModel, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TaskModel) {

        helper.setText(R.id.tw_name, item.content)
                .setText(R.id.tw_contentTitle, item.remark)
                .setImageResource(R.id.iv_iconSkillFrist, getAbbrIconDrawable(item.relatedAttribute1))
                .setImageResource(R.id.iv_iconSkillSecond, getAbbrIconDrawable(item.relatedAttribute2))
                .setImageResource(R.id.iv_iconSkillThird, getAbbrIconDrawable(item.relatedAttribute3))
                .addOnClickListener(R.id.av_checkBtn)

        if (item.taskStatus == AddToDoItemConstants.COMPLETED) {
            with(helper.getView<LottieAnimationView>(R.id.av_checkBtn)) {
                progress = 1.0f
                isClickable = false
            }
        }

    }

    /** 获得[abbr]属性图标的[Drawable Id] **/
    private fun getAbbrIconDrawable(abbr: String?): Int {
        return TodoItemConverter.stringToDrawableId(abbr)
    }

}