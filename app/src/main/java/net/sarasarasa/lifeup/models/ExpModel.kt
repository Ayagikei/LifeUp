package net.sarasarasa.lifeup.models

import org.litepal.crud.LitePalSupport
import java.util.*

data class ExpModel(
        var value: Int,
        var content: String,
        var createTime: Date?,
        var isDecrease: Boolean,
        var totalValue: Int,
        var amountOfAttribute: Int
) : LitePalSupport() {
    var id: Long? = null
    var relatedAttribute = ArrayList<String>()
}