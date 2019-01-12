package net.sarasarasa.lifeup.models

import org.litepal.crud.LitePalSupport

data class ExpModel(
        var value: Int,
        var content: String
) : LitePalSupport() {
    var id: Long? = null
    var relatedAttribute = ArrayList<String>()


}