package net.sarasarasa.lifeup.service.impl

import net.sarasarasa.lifeup.DAO.AttributeDAO
import net.sarasarasa.lifeup.models.AttributeModel
import net.sarasarasa.lifeup.service.AttributeService

class AttributeServiceImpl : AttributeService {

    private val attributeDAO = AttributeDAO()

    override fun initAttribute() {
        val attributeModel = AttributeModel(1, 0, 0, 0, 0, 0, 0)
        attributeDAO.saveAttribute(attributeModel)
    }

    override fun getAttribute(): AttributeModel {
        val ret = attributeDAO.getFirstAttribute()
        if (ret == null) {
            initAttribute()
            return getAttribute()
        }
        return ret
    }

    override fun getAttributeExpByString(attribute: String): Int {
        return when (attribute) {
            "strength" -> getAttribute().strengthAttribute
            "learning" -> getAttribute().knowledgeAttribute
            "charm" -> getAttribute().charmAttribute
            "endurance" -> getAttribute().enduranceAttribute
            "vitality" -> getAttribute().energyAttribute
            "creative" -> getAttribute().creativity
            else -> return -1
        }
    }

    override fun increaseExp(abbr: String, exp: Int): Boolean {

        val attributeModel = getAttribute()

        when (abbr) {
            "strength" -> attributeModel.strengthAttribute += exp
            "learning" -> attributeModel.knowledgeAttribute += exp
            "charm" -> attributeModel.charmAttribute += exp
            "endurance" -> attributeModel.enduranceAttribute += exp
            "vitality" -> attributeModel.energyAttribute += exp
            "creative" -> attributeModel.creativity += exp
            else -> return false
        }

        attributeModel.gradeAttribute += exp / 5

        return attributeModel.save()
    }

}