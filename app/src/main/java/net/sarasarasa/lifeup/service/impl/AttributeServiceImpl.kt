package net.sarasarasa.lifeup.service.impl

import net.sarasarasa.lifeup.dao.AttributeDAO
import net.sarasarasa.lifeup.models.AttributeModel
import net.sarasarasa.lifeup.service.AttributeService
import net.sarasarasa.lifeup.vo.AttributionVO

class AttributeServiceImpl : AttributeService {

    private val attributeDAO = AttributeDAO()


    override fun initAttribute() {
        if (attributeDAO.getFirstAttribute() == null) {
            val attributeModel = AttributeModel(0, 0, 0, 0, 0, 0, 0)
            attributeDAO.saveAttribute(attributeModel)
        }
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

    override fun decreaseExp(abbr: String, exp: Int): Boolean {

        val attributeModel = getAttribute()

        when (abbr) {
            "strength" -> {
                attributeModel.strengthAttribute -= exp
                if (attributeModel.strengthAttribute < 0)
                    attributeModel.strengthAttribute = 0
            }
            "learning" -> {
                attributeModel.knowledgeAttribute -= exp
                if (attributeModel.knowledgeAttribute < 0)
                    attributeModel.knowledgeAttribute = 0
            }
            "charm" -> {
                attributeModel.charmAttribute -= exp
                if (attributeModel.charmAttribute < 0)
                    attributeModel.charmAttribute = 0
            }
            "endurance" -> {
                attributeModel.enduranceAttribute -= exp
                if (attributeModel.enduranceAttribute < 0)
                    attributeModel.enduranceAttribute = 0
            }
            "vitality" -> {
                attributeModel.energyAttribute -= exp
                if (attributeModel.energyAttribute < 0)
                    attributeModel.energyAttribute = 0
            }
            "creative" -> {
                attributeModel.creativity -= exp
                if (attributeModel.creativity < 0)
                    attributeModel.creativity = 0
            }
            else -> return false
        }

        attributeModel.gradeAttribute -= exp / 5
        if (attributeModel.gradeAttribute < 0)
            attributeModel.gradeAttribute = 0

        return attributeModel.save()
    }

    override fun getAttributeVO(): AttributionVO {
        val attributeModel = getAttribute()
        val attributionVO = AttributionVO()
        with(attributionVO) {
            attributionVO.userExp = attributeModel.gradeAttribute
            attributionVO.attributeStrength = attributeModel.strengthAttribute
            attributionVO.attributeKnowledge = attributeModel.knowledgeAttribute
            attributionVO.attributeCharm = attributeModel.charmAttribute
            attributionVO.attributeEndurance = attributeModel.enduranceAttribute
            attributionVO.attributeEnergy = attributeModel.energyAttribute
            attributionVO.attributeCreativity = attributeModel.creativity
        }

        return attributionVO
    }

    override fun getTotalAttrExp(): Int {
        val attributeModel = getAttribute()
        return attributeModel.strengthAttribute +
                attributeModel.charmAttribute +
                attributeModel.knowledgeAttribute +
                attributeModel.energyAttribute +
                attributeModel.enduranceAttribute +
                attributeModel.creativity
    }

}