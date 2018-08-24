package net.sarasarasa.lifeup.DAO

import net.sarasarasa.lifeup.models.AttributeModel
import org.litepal.LitePal

class AttributeDAO {

    fun saveAttribute(attributeModel: AttributeModel) {
        attributeModel.save()
    }

    fun getFirstAttribute(): AttributeModel? {
        return LitePal.findFirst(AttributeModel::class.java)
    }
}