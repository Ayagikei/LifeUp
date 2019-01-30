package net.sarasarasa.lifeup.dao

import net.sarasarasa.lifeup.models.CategoryModel
import org.litepal.LitePal

class CategoryDAO {
    fun listCategory(): List<CategoryModel> {
        return LitePal.where("isDelete = ? or isDelete is null", "0").find(CategoryModel::class.java)
    }

    fun getOneCategoryById(categoryId: Long): CategoryModel? {
        return LitePal.find(CategoryModel::class.java, categoryId)
    }
}