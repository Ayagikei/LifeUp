package net.sarasarasa.lifeup.adapters

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.models.TaskModel

class ToDoItemAdapter(layoutResId: Int, data: List<TaskModel>) : BaseQuickAdapter<TaskModel, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TaskModel) {
        helper.setText(R.id.tw_name, item.content).setIsRecyclable(false)
    }

}