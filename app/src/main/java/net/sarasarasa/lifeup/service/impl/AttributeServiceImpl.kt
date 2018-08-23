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
        return attributeDAO.getFirstAttribute()
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
        }

        attributeModel.gradeAttribute += exp / 5

        return attributeModel.save()
    }

}