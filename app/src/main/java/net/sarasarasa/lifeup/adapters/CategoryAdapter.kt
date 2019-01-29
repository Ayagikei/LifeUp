package net.sarasarasa.lifeup.adapters

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.models.CategoryModel


class CategoryAdapter(layoutResId: Int, data: List<CategoryModel>) : BaseQuickAdapter<CategoryModel, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CategoryModel) {
        helper.setText(R.id.tv_category_name, item.categoryName)
    }

}