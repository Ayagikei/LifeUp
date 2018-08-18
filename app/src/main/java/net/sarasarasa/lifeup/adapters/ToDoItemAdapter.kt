package net.sarasarasa.lifeup.adapters

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.models.TaskModel


class ToDoItemAdapter(layoutResId: Int, data: List<TaskModel>) : BaseQuickAdapter<TaskModel, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TaskModel) {

        helper.setText(R.id.tw_name, item.content)
                .setText(R.id.tw_contentTitle, item.remark)
                .setImageResource(R.id.iv_iconSkillFrist, getAbbrIconDrawable(item.relatedAttribute1))
                .setImageResource(R.id.iv_iconSkillSecond, getAbbrIconDrawable(item.relatedAttribute2))
                .setImageResource(R.id.iv_iconSkillThird, getAbbrIconDrawable(item.relatedAttribute3))

    }

    /** 获得[abbr]属性图标的[Drawable Id] **/
    private fun getAbbrIconDrawable(abbr: String?): Int {
        return TodoItemConverter.stringToDrawableId(abbr)
    }

}