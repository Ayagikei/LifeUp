package net.sarasarasa.lifeup.adapters

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.datas.ToDo

class ToDoItemAdapter(layoutResId: Int, data: List<ToDo>) : BaseQuickAdapter<ToDo, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ToDo) {
        helper.setText(R.id.tw_name, item.name)
    }

}