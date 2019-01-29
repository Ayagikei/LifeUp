package net.sarasarasa.lifeup.models

import org.litepal.crud.LitePalSupport

data class CategoryModel(var categoryName: String,
                         var isDelete: Boolean) : LitePalSupport() {
    var id: Long? = null
}