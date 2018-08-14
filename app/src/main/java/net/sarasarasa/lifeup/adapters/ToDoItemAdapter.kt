package net.sarasarasa.lifeup.adapters

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.datas.TaskData

class ToDoItemAdapter(layoutResId: Int, data: List<TaskData>) : BaseQuickAdapter<TaskData, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TaskData) {
        helper.setText(R.id.tw_name, item.content).setIsRecyclable(false)
    }

}